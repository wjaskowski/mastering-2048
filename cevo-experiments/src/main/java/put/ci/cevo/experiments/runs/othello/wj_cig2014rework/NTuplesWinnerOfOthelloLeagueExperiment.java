package put.ci.cevo.experiments.runs.othello.wj_cig2014rework;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import put.ci.cevo.experiments.ntuple.NTuplesDoubleVectorAdapter;
import put.ci.cevo.experiments.ntuple.OthelloNTuplesAllStraightFactory;
import put.ci.cevo.experiments.othello.OthelloInteractionDomain;
import put.ci.cevo.experiments.wpc.othello.mappers.NTuplesOthelloPlayerMapper;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.OnePopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.PerformanceMeasureIndividualEvaluator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.measures.AgainstTeamPerformanceMeasure;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.framework.operators.mutation.GaussianMutation;
import put.ci.cevo.framework.operators.mutation.MutationAdapter;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.MeasuredIndividual;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.othello.DoubleOthello;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.published.OthelloStandardWPCHeuristicPlayer;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.util.TableUtil;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class NTuplesWinnerOfOthelloLeagueExperiment implements Experiment, EvolutionStateListener {
	private static final Configuration config = Configuration.getConfiguration();

	// Technical params
	private static final long SEED = config.getInt(new ConfKey("seed"), 123);
	private static final int NUM_THREADS = config.getInt(new ConfKey("threads"), getRuntime().availableProcessors());

	// Major params
	private static final int MU = config.getInt(new ConfKey("es.mu"), 10);
	private static final int LAMBDA = config.getInt(new ConfKey("es.lambda"), 90);
	private static final int NUM_GENERATIONS = config.getInt(new ConfKey("es.generations"), Integer.MAX_VALUE);

	private static final int NUM_INTERACTIONS_FOR_FITNESS = config.getInt(new ConfKey("fitness_interactions"), 1000);

	private static final BoardEvaluationType BOARD_EVALUATION_TYPE = config.getEnumValue(new ConfKey(
		"board_evaluation_type"), BoardEvaluationType.OUTPUT_NEGATION);

	private static final IndividualFactory<NTuples> INITIAL_INDIVIDUAL_FACTORY = config.getObject(new ConfKey(
		"ntuple_factory"), new OthelloNTuplesAllStraightFactory(1, -0.1, 0.1));

	private static final int POPULATION_SIZE = MU + LAMBDA;
	private static final double FORCED_RANDOM_MOVE_PROBABILITY = 0.1;

	// Statistics params
	private static final int EVERY_GENERATIONS = config.getInt(new ConfKey("every_generations"), 10);
	private static final int NUM_INTERACTIONS_FOR_PERFORMANCE_MEASURE = config.getInt(new ConfKey(
		"perf_measure_interactions"), 50000);
	private static final File RESULTS_DIR = config.getFile(new ConfKey("results_dir"), new File("tmp"));

	private final ThreadedContext context = new ThreadedContext(SEED, NUM_THREADS);
	private final Logger logger = Logger.getLogger(NTuplesWinnerOfOthelloLeagueExperiment.class);

	// Interaction environment
	private final OthelloInteractionDomain domain = new OthelloInteractionDomain(
		new DoubleOthello(FORCED_RANDOM_MOVE_PROBABILITY)
	);

	// Fitness is defined as average interaction result against Standard WPC Heuristic
	final PerformanceMeasure<OthelloPlayer> fitnessMeasure =
		new AgainstTeamPerformanceMeasure<>(
			domain,
			new OthelloStandardWPCHeuristicPlayer().create(),
			NUM_INTERACTIONS_FOR_FITNESS
		);

	// Objective external performance measure is the same as fitness but more interactions
	final PerformanceMeasure<OthelloPlayer> externalPerformanceMeasure =
		new AgainstTeamPerformanceMeasure<>(
			domain,
			new OthelloStandardWPCHeuristicPlayer().create(),
			NUM_INTERACTIONS_FOR_PERFORMANCE_MEASURE
		);

	final GenotypePhenotypeMapper<NTuples, OthelloPlayer> genotypeToPhenotypeMapper =
		new NTuplesOthelloPlayerMapper(BOARD_EVALUATION_TYPE);

	@Override
	public void run(String[] args) {
		setupLogger();

		MutationOperator<NTuples> gaussianMutation = new MutationAdapter<>(
			new GaussianMutation(1.0, 1.0), new NTuplesDoubleVectorAdapter());

		EvolutionModel<NTuples> muPlusLambda = new MuPlusLambdaEvolutionModel<>(MU, LAMBDA, gaussianMutation);

		PopulationFactory<NTuples> initialPopulationFactory = new UniformRandomPopulationFactory<>(
			INITIAL_INDIVIDUAL_FACTORY);

		logSomeInitialStatistics(initialPopulationFactory);

		Species<NTuples> species = new Species<>(muPlusLambda, initialPopulationFactory, POPULATION_SIZE);

		GenerationalOptimizationAlgorithm rsel = new OnePopulationEvolutionaryAlgorithm<>(
			species,
			new PerformanceMeasureIndividualEvaluator<>(
				genotypeToPhenotypeMapper,
				fitnessMeasure
			));

		rsel.addNextGenerationListener(this);

		rsel.evolve(new GenerationsTarget(NUM_GENERATIONS), context);

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
	private final TableBuilder tableBuilder = new TableBuilder("gen", "eff", "time", "best_perf");

	private final SerializationManager serializer = SerializationManagerFactory.create();

	@Override
	public void onNextGeneration(EvolutionState state) {
		logger.info(format("Generation %04d", state.getGeneration()));
		if (state.getGeneration() % EVERY_GENERATIONS != 0) {
			return;
		}

		NTuples individual = state.<NTuples> getBestSolution().getIndividual();
		OthelloPlayer player = genotypeToPhenotypeMapper.getPhenotype(individual, context.getRandomForThread());
		double performance = externalPerformanceMeasure.measure(player, context).stats().getMean();
		MeasuredIndividual<NTuples> bestOfGeneration = new MeasuredIndividual<>(individual, performance);
		if (bestOfGeneration.isBetterThan(bestOfRun)) {
			bestOfRun = bestOfGeneration;
			// Save the best
			serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS_DIR, "best.dump"));
			logger.info(format("Found better. Perf = %5.3f", bestOfRun.getPerformance()));
		}
		tableBuilder.addRow(state.getGeneration(), state.getTotalEffort(), state.getElapsedTime(),
			bestOfGeneration.getPerformance());
		TableUtil.saveTableAsCSV(tableBuilder.build(), new File(RESULTS_DIR, "results.csv"));

		// TODO: This should be only onLastGeneration
		serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS_DIR, "last.dump"));
	}

	public static void main(String[] args) {
		new NTuplesWinnerOfOthelloLeagueExperiment().run(args);
	}
}
