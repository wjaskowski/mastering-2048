package put.ci.cevo.experiments.connect4.experiments;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import put.ci.cevo.experiments.connect4.Connect4Interaction;
import put.ci.cevo.experiments.connect4.mappers.DoubleNTuplesConnect4PlayerMapper;
import put.ci.cevo.experiments.connect4.measures.Connect4PerfectPlayerPerformanceMeasure;
import put.ci.cevo.experiments.connect4.measures.Connect4RandomPlayerPerformanceMeasure;
import put.ci.cevo.experiments.connect4.ntuples.Connect4NTuplePopulationFactory;
import put.ci.cevo.experiments.ntuple.DoubleNTuplesDoubleVectorAdapter;
import put.ci.cevo.experiments.ntuple.DoubleNTuplesFactory;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.HansenCMAESAlgorithm;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.evaluators.coev.OnePopulationCoevolutionEvaluator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.MeasuredIndividual;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.state.listeners.LastGenerationListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.connect4.players.Connect4PerfectPlayer;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.connect4.thill.c4.AlphaBetaAgent;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
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

import static java.lang.Integer.MAX_VALUE;
import static java.lang.String.format;
import static put.ci.cevo.framework.algorithms.HansenCMAESAlgorithm.DEFAULT_SIGMA;
import static put.ci.cevo.util.TimeUtils.millisToHMS;

/**
 * Evaluates CMA-ES learning {@link DoubleNTuples} on the {@link put.ci.cevo.games.connect4.Connect4} game.
 *
 * Uses Thill's perfect alpha-beta player {@link Connect4PerfectPlayer} {@link AlphaBetaAgent} to objectively measure
 * the performance of evolved candidate solutions.
 */
public class Connect4DoubleNTuplesCMAESExperiment implements Experiment, EvolutionStateListener, LastGenerationListener {

	private static final Logger logger = Logger.getLogger(Connect4DoubleNTuplesCMAESExperiment.class);
	private static final Configuration config = Configuration.getConfiguration();

	// Technical params
	private static final int SEED = config.getInt(new ConfKey("seed"), 1);
	private static final int NUM_THREADS = config.getInt(new ConfKey("threads"), 1);

	// Major params
	private static final int LAMBDA = config.getInt(new ConfKey("es.lambda"), 400);
	private static final int NUM_GENERATIONS = config.getInt(new ConfKey("es.generations"), MAX_VALUE);
	private static final int TUPLE_SIZE = config.getInt(new ConfKey("tuple.size"), 4);
	private static final boolean DIAGONAL_COVARIANCE = config.getBoolean(new ConfKey("diagonal.covariance"), true);

	// Statistics params
	private static final int EVERY_GENERATIONS = config.getInt(new ConfKey("every_generations"), 5);
	private static final File RESULTS = config.getFile(new ConfKey("results_dir"), new File("."));

	private final ThreadedContext context = new ThreadedContext(SEED, NUM_THREADS);

	// Interaction environment
	private final InteractionDomain<Connect4Player, Connect4Player> c4 = new Connect4Interaction();

	private final DoubleNTuplesConnect4PlayerMapper mapper = new DoubleNTuplesConnect4PlayerMapper();

	// Performance measures
	private static final boolean OBJECTIVE_EVAL = config.getBoolean(new ConfKey("eval.objective"), false);

	private final PerformanceMeasure<Connect4Player> objectiveMeasure = new Connect4PerfectPlayerPerformanceMeasure(c4, 200);
//	private final PerformanceMeasure<Connect4Player> objectiveMeasure = new LegacyConnect4PerfectPlayerPerformanceMeasure();
	private final PerformanceMeasure<Connect4Player> randomPlayerMeasure = new Connect4RandomPlayerPerformanceMeasure(
		new Connect4Interaction(), 10000);


	public void run(String[] args) {
		setupLogger();

		IndividualFactory<DoubleNTuples> factory = new DoubleNTuplesFactory(
				new Connect4NTuplePopulationFactory(TUPLE_SIZE, -0.1, 0.1));
		logSomeInitialStatistics(factory);

		DoubleNTuples initial = factory.createRandomIndividual(context.getRandomForThread());
		PopulationEvaluator<DoubleNTuples> populationEvaluator = new OnePopulationCoevolutionEvaluator<>(c4, mapper);

		GenerationalOptimizationAlgorithm cel = new HansenCMAESAlgorithm<>(LAMBDA, populationEvaluator, initial,
				new DoubleNTuplesDoubleVectorAdapter(), DEFAULT_SIGMA, DIAGONAL_COVARIANCE, true);
		
		cel.addNextGenerationListener(this);
		cel.addLastGenerationListener(this);

		cel.evolve(new GenerationsTarget(NUM_GENERATIONS), context);
		logger.info("FINISHED");
	}

	private static void setupLogger() {
		try {
			FileUtils.forceMkdir(RESULTS);
			FileWriter fileWriter = new FileWriter(new File(RESULTS, "run.log"));
			PatternLayout logPattern = new PatternLayout("%-10r [%-5p] [%t] (%F:%L) -- %m%n");
			WriterAppender logAppender = new WriterAppender(logPattern, fileWriter);
			Logger.getRootLogger().addAppender(logAppender);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void logSomeInitialStatistics(IndividualFactory<DoubleNTuples> initialPopulationFactory) {
		try {
			logger.info("hostname = " + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		logger.info("threads = " + NUM_THREADS);
		logger.info("seed = " + SEED);
		logger.info("performance measure = " + (OBJECTIVE_EVAL ? objectiveMeasure.getClass().getSimpleName()
				: randomPlayerMeasure.getClass().getSimpleName()));
		DoubleNTuples sample = initialPopulationFactory.createRandomIndividual(new RandomDataGenerator());
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
		if (state.getGeneration() % EVERY_GENERATIONS != 0) {
			return;
		}

		DoubleNTuples individual = state.<DoubleNTuples>getBestSolution().getIndividual();
		Connect4Player player = mapper.getPhenotype(individual, context.getRandomForThread());

		double performance = OBJECTIVE_EVAL ? objectiveMeasure.measure(player, context.singleThreaded()).stats().getMean() :
				randomPlayerMeasure.measure(player, context).stats().getMean();

		MeasuredIndividual<DoubleNTuples> bestOfGeneration = new MeasuredIndividual<>(individual, performance);

		if (bestOfGeneration.isBetterThan(bestOfRun)) {
			bestOfRun = bestOfGeneration;
			serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS, "best.dump"));
			logger.info(format("Found better. Perf = %5.3f", bestOfRun.getPerformance()));
		}

		tableBuilder.addRow(state.getGeneration(), state.getTotalEffort(), millisToHMS(state.getElapsedTime()),
				bestOfGeneration.getPerformance());
		TableUtil.saveTableAsCSV(tableBuilder.build(), new File(RESULTS, "results.csv"));
	}

	@Override
	public void onLastGeneration(EvolutionState state) {
		serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS, "last.dump"));
	}

	public static void main(String[] args) {
		new Connect4DoubleNTuplesCMAESExperiment().run(args);
	}

}
