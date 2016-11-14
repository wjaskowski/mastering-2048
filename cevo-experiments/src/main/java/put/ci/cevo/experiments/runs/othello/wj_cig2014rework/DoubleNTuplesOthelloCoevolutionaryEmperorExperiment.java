package put.ci.cevo.experiments.runs.othello.wj_cig2014rework;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import put.ci.cevo.experiments.ntuple.DoubleNTuplesDoubleVectorAdapter;
import put.ci.cevo.experiments.ntuple.DoubleNTuplesFactory;
import put.ci.cevo.experiments.ntuple.OthelloNTuplesAllStraightFactory;
import put.ci.cevo.experiments.othello.OthelloInteractionDomain;
import put.ci.cevo.experiments.othello.RepeatedInteractionDomain;
import put.ci.cevo.experiments.wpc.othello.mappers.DoubleNTuplesOthelloPlayerMapper;
import put.ci.cevo.framework.algorithms.ApacheCMAESAlgorithm;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.evaluators.coev.OnePopulationCoevolutionEvaluator;
import put.ci.cevo.framework.evaluators.coev.TwoPopulationCoevolutionEvaluator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.MeasuredIndividual;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
import put.ci.cevo.games.othello.DoubleOthello;
import put.ci.cevo.games.othello.LucasInitialOthelloStates;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.util.TableUtil;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class DoubleNTuplesOthelloCoevolutionaryEmperorExperiment implements Experiment, EvolutionStateListener {
	private static final Configuration config = Configuration.getConfiguration();

	// Technical params
	private static final long SEED = config.getInt(new ConfKey("seed"));
	private static final int NUM_THREADS = config.getInt(new ConfKey("threads"), getRuntime().availableProcessors());

	// Major params
	// TODO: I think I need some diversification in the population
	private static final int LAMBDA = config.getInt(new ConfKey("es.lambda"), 50);
	private static final int ARCHIVE_SIZE = config.getInt(new ConfKey("archive_size"), 500);
	private static final int NUM_GENERATIONS = config.getInt(new ConfKey("es.generations"), Integer.MAX_VALUE);

	private static final int NUM_RANDOM_BOARDS = config.getInt(new ConfKey("random_boards"), 1);

	private static final EvolType EVOL_TYPE = config.getEnumValue(EvolType.class, new ConfKey("evol_type"),
			EvolType.COEVOLUTION);

	private static final IndividualFactory<DoubleNTuples> INITIAL_INDIVIDUAL_FACTORY =
			new DoubleNTuplesFactory(config.getObject(new ConfKey("ntuple_factory"),
					new OthelloNTuplesAllStraightFactory(1, -0.1, 0.1)));

	private static final double FORCE_RANDOM_MOVE_PROBABILITY_FOR_LEARNING =
			config.getDouble(new ConfKey("epsilon"), 0.00);

	// Statistics params
	private static final int COMPUTE_STATS_GENERATIONS_FREQUENCY = config.getInt(new ConfKey("every_generations"), 10);
	private static final File RESULTS_DIR = config.getFile(new ConfKey("results_dir"));
	private static final int SAVE_POPULATION_GENERATIONS_FREQUENCY =
			config.getInt(new ConfKey("save_population_every"), 0);

	private enum EvolType {
		COEVOLUTION, COEVOLUTION_WITH_PARENT_CHILD_AVERAGING, AGAINST_FIXED_OPPONENTS, CMA_ES_COEVOLUTION, CMA_ES_2COEVOLUTION_NORMAL, CMA_ES_2COEVOLUTION
	}

	private final ThreadedContext context = new ThreadedContext(SEED, NUM_THREADS);
	private final Logger logger = Logger.getLogger(DoubleNTuplesOthelloCoevolutionaryEmperorExperiment.class);

	private DoubleNTuplesOthelloPlayerMapper genotypeToPhenotypeMapper = new DoubleNTuplesOthelloPlayerMapper();

	// Interaction domain
	private final InteractionDomain<OthelloPlayer, OthelloPlayer> domain = new RepeatedInteractionDomain<>(
			new OthelloInteractionDomain(
					new DoubleOthello(FORCE_RANDOM_MOVE_PROBABILITY_FOR_LEARNING),
					new LucasInitialOthelloStates().boards()),
			NUM_RANDOM_BOARDS);

	private final PerformanceMeasure<OthelloPlayer> externalObjectivePerfMeasure =
			new AgainstPublishedOthelloPlayersOnLucasBoardsPerformanceMeasure();

	@Override
	public void run(String[] args) {
		setupLogger();

		PopulationFactory<DoubleNTuples> initialPopulationFactory = new UniformRandomPopulationFactory<>(
				INITIAL_INDIVIDUAL_FACTORY);
		logSomeInitialStatistics(initialPopulationFactory);

		GenerationalOptimizationAlgorithm alg;
		switch (EVOL_TYPE) {
		case CMA_ES_COEVOLUTION:
			alg = new ApacheCMAESAlgorithm<>(
					LAMBDA,
					new OnePopulationCoevolutionEvaluator<>(
							domain, genotypeToPhenotypeMapper
					), INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(context.getRandomForThread()),
					new DoubleNTuplesDoubleVectorAdapter(), 1.0,
					false //TODO
			);
			break;

		case CMA_ES_2COEVOLUTION:
			alg = new HybridCMAESAlgorithm<>(
					LAMBDA,
					ARCHIVE_SIZE,
					1.0,
					new HybridCoevolutionaryTwoPopulationEvaluator<>(
							domain,
							genotypeToPhenotypeMapper
					),
					INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(context.getRandomForThread()),
					new DoubleNTuplesDoubleVectorAdapter()
			);
			break;

		// Will work well with epsilon>0, because we do not use any symmetry for playing games
		case CMA_ES_2COEVOLUTION_NORMAL:
			alg = new HybridCMAESAlgorithm<>(
					LAMBDA,
					ARCHIVE_SIZE,
					1.0,
					new TwoPopulationCoevolutionEvaluator<>(
							domain, genotypeToPhenotypeMapper,
							genotypeToPhenotypeMapper
					),
					INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(context.getRandomForThread()),
					new DoubleNTuplesDoubleVectorAdapter()
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

	private void logSomeInitialStatistics(PopulationFactory<DoubleNTuples> initialPopulationFactory) {
		try {
			logger.info("hostname = " + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		logger.info("threads = " + NUM_THREADS);
		logger.info("seed = " + SEED);
		DoubleNTuples sample = initialPopulationFactory.createPopulation(1, new RandomDataGenerator()).get(0);
		logger.info("main tuples = " + sample.first().getMain().size());
		logger.info("all tuples = " + sample.first().getAll().size());
		logger.info("weights = " + sample.totalWeights());
	}

	private MeasuredIndividual<DoubleNTuples> bestOfRun = MeasuredIndividual.createNull();
	private final TableBuilder tableBuilder = new TableBuilder("gen", "eff", "time", "best_perf");

	private final SerializationManager serializer = SerializationManagerFactory.create();

	@Override
	public void onNextGeneration(EvolutionState state) {
		logger.info(format("Generation %04d", state.getGeneration()));
		if (state.getGeneration() % COMPUTE_STATS_GENERATIONS_FREQUENCY == 0) {

			DoubleNTuples individual = state.<DoubleNTuples>getBestSolution().getIndividual();
			OthelloPlayer player = genotypeToPhenotypeMapper.getPhenotype(individual, context.getRandomForThread());
			double performance = externalObjectivePerfMeasure.measure(player, context).stats().getMean();
			MeasuredIndividual<DoubleNTuples> bestOfGeneration = new MeasuredIndividual<>(individual, performance);
			logger.info(format("Best-of-current-generation performance = %5.3f", bestOfGeneration.getPerformance()));
			if (bestOfGeneration.isBetterThan(bestOfRun)) {
				bestOfRun = bestOfGeneration;
				// Save the best
				serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS_DIR, "best.dump"));
				logger.info(format("  Found best-to-date with performance = %5.3f", bestOfRun.getPerformance()));
			}
			tableBuilder.addRow(state.getGeneration(), state.getTotalEffort(), state.getElapsedTime(),
					bestOfGeneration.getPerformance());
			TableUtil.saveTableAsCSV(tableBuilder.build(), new File(RESULTS_DIR, "results.csv"));

			// TODO: This should be only onLastGeneration
			serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS_DIR, "last.dump"));
		}
		if (SAVE_POPULATION_GENERATIONS_FREQUENCY > 0
				&& state.getGeneration() % SAVE_POPULATION_GENERATIONS_FREQUENCY == 0) {
			List<DoubleNTuples> population = seq(state.<DoubleNTuples>getEvaluatedSolutions()).map(
					EvaluatedIndividual.<DoubleNTuples>toIndividual()).toList();
			serializer.serializeWrapExceptions(population, new File(RESULTS_DIR, "pop.dump"));
		}
	}

	public static void main(String[] args) {
		new DoubleNTuplesOthelloCoevolutionaryEmperorExperiment().run(args);
	}
}
