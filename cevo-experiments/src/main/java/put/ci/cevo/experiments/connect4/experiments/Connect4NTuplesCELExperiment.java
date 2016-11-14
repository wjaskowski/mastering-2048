package put.ci.cevo.experiments.connect4.experiments;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import put.ci.cevo.experiments.connect4.Connect4Interaction;
import put.ci.cevo.experiments.connect4.mappers.NTuplesConnect4PlayerMapper;
import put.ci.cevo.experiments.connect4.measures.Connect4PerfectPlayerPerformanceMeasure;
import put.ci.cevo.experiments.connect4.ntuples.Connect4NTuplePopulationFactory;
import put.ci.cevo.experiments.ntuple.NTuplesDoubleVectorAdapter;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.OnePopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.evaluators.coev.OnePopulationCoevolutionEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.framework.operators.mutation.GaussianMutation;
import put.ci.cevo.framework.operators.mutation.MutationAdapter;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.MeasuredIndividual;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.state.listeners.LastGenerationListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.encodings.ntuple.NTuples;
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
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static put.ci.cevo.util.TimeUtils.millisToHMS;

/**
 * Evaluates CMA-ES learning {@link NTuples} on the {@link put.ci.cevo.games.connect4.Connect4} game.
 *
 * Uses Thill's perfect alpha-beta player {@link put.ci.cevo.games.connect4.players.Connect4PerfectPlayer} {@link put.ci.cevo.games.connect4.thill.c4.AlphaBetaAgent} to objectively measure
 * the performance of evolved candidate solutions.
 */
public class Connect4NTuplesCELExperiment implements Experiment, EvolutionStateListener, LastGenerationListener {

	private static final Logger logger = Logger.getLogger(Connect4NTuplesCELExperiment.class);
	private static final Configuration config = Configuration.getConfiguration();

	// Technical params
	private static final int SEED = config.getInt(new ConfKey("seed"));
	private static final long RUN = config.getInt(new ConfKey("run"));
	private static final int NUM_THREADS = config.getInt(new ConfKey("threads"), getRuntime().availableProcessors());

	// Major params
	private static final int MU = config.getInt(new ConfKey("es.mu"), 20);
	private static final int LAMBDA = config.getInt(new ConfKey("es.lambda"), 180);
	private static final int NUM_GENERATIONS = config.getInt(new ConfKey("es.generations"), MAX_VALUE);
	private static final int TUPLE_SIZE = config.getInt(new ConfKey("tuple.size"), 4);

	private static final int POPULATION_SIZE = MU + LAMBDA;

	// Statistics params
	private static final int EVERY_GENERATIONS = config.getInt(new ConfKey("every_generations"), 5);
	private static final File RESULTS = config.getFile(new ConfKey("results_dir"));

	private final ThreadedContext context = new ThreadedContext(SEED);

	// Interaction environment
	private final InteractionDomain<Connect4Player, Connect4Player> c4 = new Connect4Interaction();
	// private final InteractionDomain<Connect4Player, Connect4Player> c4random = new Connect4Interaction();

	private final NTuplesConnect4PlayerMapper mapper = new NTuplesConnect4PlayerMapper();

	// Performance measures
	// private final PerformanceMeasure<Connect4Player> randomPlayerMeasure = new Connect4RandomPlayerPerformanceMeasure(c4random, 10000);
	private final PerformanceMeasure<Connect4Player> minimax = new Connect4PerfectPlayerPerformanceMeasure(c4, 50);

	@Override
	public void run(String[] args) {
		setupLogger();

		MutationOperator<NTuples> gaussianMutation = new MutationAdapter<>(
				new GaussianMutation(1.0, 1.0), new NTuplesDoubleVectorAdapter());

		EvolutionModel<NTuples> muPlusLambda = new MuPlusLambdaEvolutionModel<>(MU, LAMBDA, gaussianMutation);
		PopulationFactory<NTuples> initialPopulationFactory = new Connect4NTuplePopulationFactory(TUPLE_SIZE, -0.1, 0.1);
		logSomeInitialStatistics(initialPopulationFactory);

		Species<NTuples> species = new Species<>(muPlusLambda, initialPopulationFactory, POPULATION_SIZE);
		PopulationEvaluator<NTuples> evaluator = new OnePopulationCoevolutionEvaluator<>(c4, mapper);
		GenerationalOptimizationAlgorithm cel = new OnePopulationEvolutionaryAlgorithm<>(species, evaluator);

		cel.addNextGenerationListener(this);
		cel.addLastGenerationListener(this);

		cel.evolve(new GenerationsTarget(NUM_GENERATIONS), context);
		logger.info("FINISHED");
	}

	private static void setupLogger() {
		try {
			FileUtils.forceMkdir(RESULTS);
			FileWriter fileWriter = new FileWriter(new File(RESULTS, "run-" + RUN + ".log"));
			PatternLayout logPattern = new PatternLayout("%-10r [%-5p] [%t] (%F:%L) -- %m%n");
			WriterAppender logAppender = new WriterAppender(logPattern, fileWriter);
			Logger.getRootLogger().addAppender(logAppender);
		} catch (IOException e) {
			throw new RuntimeException(e);
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

		NTuples individual = state.<NTuples>getBestSolution().getIndividual();
		Connect4Player player = mapper.getPhenotype(individual, context.getRandomForThread());

		double performance = minimax.measure(player, context).stats().getMean();
		MeasuredIndividual<NTuples> bestOfGeneration = new MeasuredIndividual<>(individual, performance);

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
		new Connect4NTuplesCELExperiment().run(args);
	}

}
