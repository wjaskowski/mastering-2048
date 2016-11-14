package put.ci.cevo.experiments.runs.othello.wj_cig2014rework;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static put.ci.cevo.framework.interactions.SwissTournament.SwissStrategy;
import static put.ci.cevo.util.sequence.Sequences.seq;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import put.ci.cevo.experiments.ntuple.NTuplesDoubleVectorAdapter;
import put.ci.cevo.experiments.othello.*;
import put.ci.cevo.experiments.wpc.othello.mappers.NTuplesOthelloPlayerMapper;
import put.ci.cevo.framework.algorithms.*;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.OneByOnePopulationEvaluator;
import put.ci.cevo.framework.evaluators.PerformanceMeasureIndividualEvaluator;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.evaluators.coev.OnePopulationCoevolutionEvaluator;
import put.ci.cevo.framework.evaluators.coev.TournamentCoevolutionEvaluator;
import put.ci.cevo.framework.evaluators.coev.TwoPopulationCoevolutionEvaluator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.interactions.*;
import put.ci.cevo.framework.measures.AgainstTeamPerformanceMeasure;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.MuCommaLambdaEvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.framework.operators.mutation.GaussianMutation;
import put.ci.cevo.framework.operators.mutation.MutationAdapter;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.MeasuredIndividual;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.othello.DoubleOthello;
import put.ci.cevo.games.othello.LucasInitialOthelloStates;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.published.OthelloStandardWPCHeuristicPlayer;
import put.ci.cevo.games.othello.players.published.PublishedPlayers;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.util.TableUtil;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;
import put.ci.cevo.util.vectors.DoubleVector;

public class NTuplesOthelloCoevolutionaryEmperorExperiment implements Experiment, EvolutionStateListener {
	private static final Configuration config = Configuration.getConfiguration();

	// Technical params
	private static final long SEED = config.getInt(new ConfKey("seed"));
	private static final int NUM_THREADS = config.getInt(new ConfKey("threads"), getRuntime().availableProcessors());

	// Major params
	// TODO: I think I need some diversification in the population
	private static final int MU = config.getInt(new ConfKey("es.mu"), 5);
	private static final int LAMBDA = config.getInt(new ConfKey("es.lambda"), 50);
	private static final int ARCHIVE_SIZE = config.getInt(new ConfKey("archive_size"), 500);
	private static final int NUM_GENERATIONS = config.getInt(new ConfKey("es.generations"), Integer.MAX_VALUE);

	private static final int NUM_RANDOM_BOARDS = config.getInt(new ConfKey("random_boards"), 1);
	private static final boolean CONSTANT_SIGMA = config.getBoolean(new ConfKey("const_sigma"), false);

	private boolean LEARN_FROM_LUCAS_STATES = config.getBoolean(new ConfKey("lucas_states"), true);

	private static final BoardEvaluationType BOARD_EVALUATION_TYPE = config.getEnumValue(new ConfKey(
			"board_evaluation_type"), BoardEvaluationType.BOARD_INVERSION);

	private static final EvolType EVOL_TYPE = config.getEnumValue(EvolType.class, new ConfKey("evol_type"),
			EvolType.COEVOLUTION);
	private static final EvolModel EVOL_MODEL = config.getEnumValue(EvolModel.class, new ConfKey("evol_model"),
			EvolModel.MU_PLUS_LAMBDA);
	private static final EvaluatorType EVALUATOR_TYPE = config.getEnumValue(EvaluatorType.class, new ConfKey(
					"evaluator_type"),
			EvaluatorType.ROUND_ROBIN);

	// Just for evolution against the set of fixed opponents
	private static final int NUM_REPEATS = config.getInt(new ConfKey("num_repeats"), 91);

	MutationOperator<DoubleVector> MUTATION = config.getObject(new ConfKey("mutation"), new GaussianMutation(1.0, 1.0));

	private static final IndividualFactory<NTuples> INITIAL_INDIVIDUAL_FACTORY =
			config.getObject(new ConfKey("ntuple_factory"),
					new OthelloNTuplesSystematicFactory(-0.1, 0.1, "1"));
	//new NTuplesOthelloAllStraightFactory(1, -0.1, 0.1));

	private double CMAES_SIGMA = config.getDouble(new ConfKey("sigma"), 1.0);

	private static final double FORCE_RANDOM_MOVE_PROBABILITY_FOR_LEARNING =
			config.getDouble(new ConfKey("epsilon"), 0.00);
	private static final int NUM_FORCED_RANDOM_MOVES_FOR_LEARNING = config.getInt(new ConfKey("random_moves"),
			Integer.MAX_VALUE);

	// Only for COEVOLUTION_WITH_PARENT_CHILD_AVERAGING
	private static final double BETA = config.getDouble(new ConfKey("beta"), 0.05);

	// Statistics params
	private static final int COMPUTE_STATS_GENERATIONS_FREQUENCY = config.getInt(new ConfKey("every_generations"), 10);
	private static final File RESULTS_DIR = config.getFile(new ConfKey("results_dir"));
	private static final int SAVE_POPULATION_GENERATIONS_FREQUENCY =
			config.getInt(new ConfKey("save_population_every"), 100);
	private static final boolean COMPUTE_STATS_AGAINST_RANDOM = config.getBoolean(new ConfKey("stats_against_random"),
			false);

	private static final ExternalObjectiveType EXTERNAL_OBJECTIVE_MEASURE = config.getEnumValue(
			ExternalObjectiveType.class, new ConfKey("objective_measure"), ExternalObjectiveType.LUCAS_ELEVEN);

	private enum ExternalObjectiveType {
		LUCAS_ELEVEN, SWH, THIRTEEN_EPS
	}

	private enum CMAVersion {
		STANDARD,
		SEP,
		VD
	}

	private static final CMAVersion CMA_VERSION = config.getEnumValue(CMAVersion.class, new ConfKey("cma_version"),
			CMAVersion.STANDARD);

	private enum EvolType {
		COEVOLUTION, COEVOLUTION_WITH_PARENT_CHILD_AVERAGING, AGAINST_FIXED_OPPONENTS, CMA_ES_COEVOLUTION,
		CMA_ES_2COEVOLUTION_NORMAL, CMA_ES_COEVOLUTION_2, CMA_ES_AGAINST_SWH, CMA_ES_2COEVOLUTION
	}

	private enum EvolModel {
		MU_PLUS_LAMBDA, MU_COMMA_LAMBDA
	}

	private enum EvaluatorType {
		K_RANDOM_OPPONENTS, DANISH_TOURNAMENT, ROUND_ROBIN, SWISS_TOURNAMENT_WEIGHTED, SWISS_TOURNAMENT
	}

	private final ThreadedContext context = new ThreadedContext(SEED, NUM_THREADS);
	private final Logger logger = Logger.getLogger(NTuplesOthelloCoevolutionaryEmperorExperiment.class);

	private final List<OthelloPlayer> OPPONENTS_FOR_LEARNING = PublishedPlayers.eleven();

	private NTuplesOthelloPlayerMapper genotypeToPhenotypeMapper =
			new NTuplesOthelloPlayerMapper(BOARD_EVALUATION_TYPE);

	// Interaction domain
	private final InteractionDomain<OthelloPlayer, OthelloPlayer> learningDomain = new RepeatedInteractionDomain<>(
			new OthelloInteractionDomain(
					new DoubleOthello(FORCE_RANDOM_MOVE_PROBABILITY_FOR_LEARNING, NUM_FORCED_RANDOM_MOVES_FOR_LEARNING),
					LEARN_FROM_LUCAS_STATES ? new LucasInitialOthelloStates().boards() : Arrays.asList(
							new OthelloState())),
			NUM_RANDOM_BOARDS);

	private PerformanceMeasure<OthelloPlayer> externalObjectiveMeasure;

	private final PerformanceMeasure<OthelloPlayer> againstRandomPlayer =
			new AgainstOthelloRandomPlayerPerformanceMeasure(10000);

	@Override
	public void run(String[] args) {
		setupLogger();

		MutationOperator<NTuples> gaussianMutation = new MutationAdapter<>(
				MUTATION, new NTuplesDoubleVectorAdapter());

		PopulationFactory<NTuples> initialPopulationFactory = new UniformRandomPopulationFactory<>(
				INITIAL_INDIVIDUAL_FACTORY);
		logSomeInitialStatistics(initialPopulationFactory);

		PopulationEvaluator<NTuples> evaluator;

		switch (EXTERNAL_OBJECTIVE_MEASURE) {
		case LUCAS_ELEVEN:
			externalObjectiveMeasure = new AgainstPublishedOthelloPlayersOnLucasBoardsPerformanceMeasure();
			break;

		case THIRTEEN_EPS:
			externalObjectiveMeasure = new AgainstOthelloTeamPerformanceMeasure(
					new OthelloPlayerStateOpponentInteractionDomain(new DoubleOthello(0.1)),
					PublishedPlayers.published(), 1000);
			break;

		case SWH:
			externalObjectiveMeasure = new OthelloLeaguePerformanceMeasure(100000);
			break;
		}

		Species<NTuples> species = null;
		if (EVOL_TYPE == EvolType.COEVOLUTION || EVOL_TYPE == EvolType.AGAINST_FIXED_OPPONENTS) {
			switch (EVOL_MODEL) {
			case MU_PLUS_LAMBDA:
				EvolutionModel<NTuples> evolutionModel = new MuPlusLambdaEvolutionModel<>(MU, LAMBDA, gaussianMutation);
				species = new Species<>(evolutionModel, initialPopulationFactory, MU + LAMBDA);
				break;
			case MU_COMMA_LAMBDA:
				evolutionModel = new MuCommaLambdaEvolutionModel<>(MU, LAMBDA, gaussianMutation);
				species = new Species<>(evolutionModel, initialPopulationFactory, LAMBDA);
				break;
			default:
				throw new NotImplementedException();
			}
		}

		GenerationalOptimizationAlgorithm alg = null;
		switch (EVOL_TYPE) {
		case COEVOLUTION:
			alg = new OnePopulationEvolutionaryAlgorithm<>(
					species,
					new OnePopulationCoevolutionEvaluator<>(
							learningDomain, genotypeToPhenotypeMapper
					)
			);
			break;
		case COEVOLUTION_WITH_PARENT_CHILD_AVERAGING:
			alg = new ParentChildAveragingEvolutionaryStrategy<>(
					MU, LAMBDA, BETA,
					initialPopulationFactory,
					new OnePopulationCoevolutionEvaluator<>(
							learningDomain, genotypeToPhenotypeMapper
					),
					gaussianMutation,
					new NTuplesDoubleVectorAdapter()
			);
			break;

		case AGAINST_FIXED_OPPONENTS:
			alg = new OnePopulationEvolutionaryAlgorithm<NTuples>(
					species,
					new OneByOnePopulationEvaluator<>(
							new PerformanceMeasureIndividualEvaluator<>(
									genotypeToPhenotypeMapper,
									new AgainstTeamPerformanceMeasure<>(learningDomain, OPPONENTS_FOR_LEARNING,
											NUM_REPEATS)
							)
					)
			);
			break;

		case CMA_ES_AGAINST_SWH:
			PerformanceMeasure<OthelloPlayer> fitnessMeasure = new AgainstTeamPerformanceMeasure<>(learningDomain,
					new OthelloStandardWPCHeuristicPlayer().create(), NUM_REPEATS);
			evaluator = new OneByOnePopulationEvaluator<>(
					new PerformanceMeasureIndividualEvaluator<>(genotypeToPhenotypeMapper, fitnessMeasure));
			if (CMA_VERSION == CMAVersion.STANDARD || CMA_VERSION == CMAVersion.SEP) {
				alg = new HansenCMAESAlgorithm<>(
						LAMBDA,
						evaluator, INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(context.getRandomForThread()),
						new NTuplesDoubleVectorAdapter(), CMAES_SIGMA,
						CMA_VERSION == CMAVersion.SEP,
						CONSTANT_SIGMA);
			} else if (CMA_VERSION == CMAVersion.VD) {
				alg = new SharkVDCMAES<>(
						LAMBDA,
						evaluator, INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(context.getRandomForThread()),
						new NTuplesDoubleVectorAdapter(), CMAES_SIGMA);
			}
			break;

		case CMA_ES_COEVOLUTION:
			switch (EVALUATOR_TYPE) {
			case ROUND_ROBIN:
				evaluator = new OnePopulationCoevolutionEvaluator<>(learningDomain, genotypeToPhenotypeMapper);
				break;
			case K_RANDOM_OPPONENTS:
				evaluator = new TournamentCoevolutionEvaluator<>(new KRandomOpponentsTournament<>(learningDomain, MU,
						KRandomOpponentsMatch.OpponentsStrategy.FIXED_OPPONENTS),
						genotypeToPhenotypeMapper);
				break;
			case DANISH_TOURNAMENT:
				evaluator = new TournamentCoevolutionEvaluator<>(new DanishTournament<>(learningDomain, MU),
						genotypeToPhenotypeMapper);
				break;
			case SWISS_TOURNAMENT:
				evaluator = new TournamentCoevolutionEvaluator<>(new SwissTournament<>(learningDomain, MU,
						SwissStrategy.SECONDARY_POINTS),
						genotypeToPhenotypeMapper);
				break;
			case SWISS_TOURNAMENT_WEIGHTED:
				evaluator = new TournamentCoevolutionEvaluator<>(new SwissTournament<>(learningDomain, MU,
						SwissStrategy.WEIGHTED),
						genotypeToPhenotypeMapper);
				break;
			default:
				throw new RuntimeException();
			}
			if (CMA_VERSION == CMAVersion.STANDARD || CMA_VERSION == CMAVersion.SEP) {
				alg = new HansenCMAESAlgorithm<>(
					LAMBDA,
					evaluator, INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(context.getRandomForThread()),
					new NTuplesDoubleVectorAdapter(), CMAES_SIGMA,
					CMA_VERSION == CMAVersion.SEP,
					CONSTANT_SIGMA);
			} else if (CMA_VERSION == CMAVersion.VD) {
				alg = new SharkVDCMAES<>(
						LAMBDA,
						evaluator, INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(context.getRandomForThread()),
						new NTuplesDoubleVectorAdapter(), CMAES_SIGMA);
			}
			break;

		case CMA_ES_2COEVOLUTION:
			alg = new HybridCMAESAlgorithm<>(
					LAMBDA,
					ARCHIVE_SIZE,
					CMAES_SIGMA,
					new HybridCoevolutionaryTwoPopulationEvaluator<>(
							learningDomain,
							genotypeToPhenotypeMapper
					),
					INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(context.getRandomForThread()),
					new NTuplesDoubleVectorAdapter()
			);
			break;

		// Will work well with epsilon>0, because we do not use any symmetry for playing games
		case CMA_ES_2COEVOLUTION_NORMAL:
			alg = new HybridCMAESAlgorithm<>(
					LAMBDA,
					ARCHIVE_SIZE,
					CMAES_SIGMA,
					new TwoPopulationCoevolutionEvaluator<>(
							learningDomain, genotypeToPhenotypeMapper,
							genotypeToPhenotypeMapper
					),
					INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(context.getRandomForThread()),
					new NTuplesDoubleVectorAdapter()
			);
			break;

		default:
			throw new NotImplementedException();
		}

		alg.addNextGenerationListener(this);

		alg.evolve(new GenerationsTarget(NUM_GENERATIONS), context);

		logger.info("FINISHED");
	}

	private static void setupLogger() {
		try {
			FileUtils.forceMkdir(RESULTS_DIR);
			FileWriter fileWriter = new FileWriter(new File(RESULTS_DIR, "run.log"));
			PatternLayout logPattern = new PatternLayout("%-10r [%-5p] [%t] (%F:%L) -- %m%n");
			WriterAppender logAppender = new WriterAppender(logPattern, fileWriter);
			Logger.getRootLogger().addAppender(logAppender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void logSomeInitialStatistics(PopulationFactory<NTuples> initialPopulationFactory) {
		try {
			logger.info("hostname = " + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		logger.info("threads = " + NUM_THREADS);
		logger.info("seed = " + SEED);
		NTuples sample = initialPopulationFactory.createPopulation(1, new RandomDataGenerator()).get(0);
		logger.info("main tuples = " + sample.getMain().size());
		logger.info("all tuples = " + sample.getAll().size());
		logger.info("weights = " + sample.totalWeights());
	}

	private MeasuredIndividual<NTuples> bestOfRun = MeasuredIndividual.createNull();
	private MeasuredIndividual<NTuples> bestOfRunAgainstRandom = MeasuredIndividual.createNull();
	private final TableBuilder tableBuilder = new TableBuilder("gen", "eff", "time", "best_perf", "rand_perf");

	private final SerializationManager serializer = SerializationManagerFactory.create();

	@Override
	public void onNextGeneration(EvolutionState state) {
		logger.info(format("Generation %04d", state.getGeneration()));
		if (state.getGeneration() % COMPUTE_STATS_GENERATIONS_FREQUENCY == 0) {

			NTuples individual = state.<NTuples>getBestSolution().getIndividual();
			OthelloPlayer player = genotypeToPhenotypeMapper.getPhenotype(individual, context.getRandomForThread());
			double performance = externalObjectiveMeasure.measure(player, context).stats().getMean();
			MeasuredIndividual<NTuples> bestOfGeneration = new MeasuredIndividual<>(individual, performance);
			logger.info(format("Best-of-current-generation performance = %5.3f", bestOfGeneration.getPerformance()));
			if (bestOfGeneration.isBetterThan(bestOfRun)) {
				bestOfRun = bestOfGeneration;
				// Save the best
				serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS_DIR, "best.dump"));
				logger.info(format("  Found best-to-date with performance = %5.3f", bestOfRun.getPerformance()));
			}

			double randPerf = 0.0;
			if (COMPUTE_STATS_AGAINST_RANDOM) {
				Measurement againstRandomPerformance = againstRandomPlayer.measure(player, context);
				if (againstRandomPerformance.stats().getMean() > bestOfRunAgainstRandom.getPerformance()) {
					bestOfRunAgainstRandom = new MeasuredIndividual<>(individual,
							againstRandomPerformance.stats().getMean());
					serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS_DIR,
							"best_rand.dump"));
				}
				randPerf = againstRandomPerformance.stats().getMean();
			}

			tableBuilder.addRow(state.getGeneration(), state.getTotalEffort(), state.getElapsedTime(),
					bestOfGeneration.getPerformance(), randPerf);
			TableUtil.saveTableAsCSV(tableBuilder.build(), new File(RESULTS_DIR, "results.csv"));

			// TODO: This should be only onLastGeneration
			serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS_DIR, "last.dump"));
		}
		if (state.getGeneration() % SAVE_POPULATION_GENERATIONS_FREQUENCY == 0) {
			List<NTuples> population = seq(state.<NTuples>getEvaluatedSolutions()).map(
					EvaluatedIndividual.<NTuples>toIndividual()).toList();
			serializer.serializeWrapExceptions(population, new File(RESULTS_DIR, "pop.dump"));
		}
	}

	public static void main(String[] args) {
		new NTuplesOthelloCoevolutionaryEmperorExperiment().run(args);
	}
}
