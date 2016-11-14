package put.ci.cevo.experiments.gecco2015tetris;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static put.ci.cevo.util.sequence.Sequences.seq;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import put.ci.cevo.experiments.ntuple.NTuplesDoubleVectorAdapter;
import put.ci.cevo.experiments.tetris.NTuplesToTetrisAgentMapper;
import put.ci.cevo.experiments.tetris.TetrisExpectedUtility;
import put.ci.cevo.experiments.tetris.TetrisNTuplesSystematicFactory;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.HansenCMAESAlgorithm;
import put.ci.cevo.framework.algorithms.SharkVDCMAES;
import put.ci.cevo.framework.algorithms.SzitaCEMAlgorithm;
import put.ci.cevo.framework.evaluators.OneByOnePopulationEvaluator;
import put.ci.cevo.framework.evaluators.PerformanceMeasureIndividualEvaluator;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.MeasuredIndividual;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.tetris.Tetris;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.util.TableUtil;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class NTuplesTetrisExperiment implements Experiment, EvolutionStateListener {
	private static final Configuration config = Configuration.getConfiguration();

	// Technical params
	private static final long SEED = config.getInt(new ConfKey("seed"));
	private static final int NUM_THREADS = config.getInt(new ConfKey("threads"), getRuntime().availableProcessors());

	// Major params
	private static final int LAMBDA = config.getInt(new ConfKey("es.lambda"), 100);
	private static final int NUM_GENERATIONS = config.getInt(new ConfKey("es.generations"), Integer.MAX_VALUE);

	private static final boolean CONSTANT_SIGMA = config.getBoolean(new ConfKey("const_sigma"), false);

	private static final IndividualFactory<NTuples> INITIAL_INDIVIDUAL_FACTORY =
			config.getObject(new ConfKey("ntuple_factory"), new TetrisNTuplesSystematicFactory(-0.1, 0.1, "11111"));

	private static final double CMAES_SIGMA = config.getDouble(new ConfKey("sigma"), 1.0);

	private static final int EVALUATION_GAMES = config.getInt(new ConfKey("evaluation_games"), 100);

	private enum CMAVersion {STANDARD, SEP, VD, STANDARD_APACHE, CEM}

	private static final CMAVersion CMA_VERSION = config.getEnumValue(CMAVersion.class, new ConfKey("cma_version"),
			CMAVersion.STANDARD);

	// Statistics params
	private static final int MEASURE_GAMES = config.getInt(new ConfKey("stats.measure_games"), 100);
	private static final int MEASURE_GENERATIONS_FREQUENCY = config.getInt(new ConfKey("stats.generations_every"), 10);
	private static final File RESULTS_DIR = config.getFile(new ConfKey("results_dir"), new File(
			"cevo-experiments/tmp"));
	private static final int SAVE_POPULATION_GENERATIONS_FREQUENCY = config.getInt(new ConfKey(
			"stats.save_population_every"), 100);

	private final ThreadedContext context = new ThreadedContext(SEED, NUM_THREADS);
	private final Logger logger = Logger.getLogger(NTuplesTetrisExperiment.class);

	private final Tetris tetris = Tetris.newSZTetris();

	private NTuplesToTetrisAgentMapper genotypeToPhenotypeMapper = new NTuplesToTetrisAgentMapper();

	private PerformanceMeasure<Agent<TetrisState, TetrisAction>> fitnessMeasure = new TetrisExpectedUtility(tetris, EVALUATION_GAMES);

	private PerformanceMeasure<Agent<TetrisState, TetrisAction>> externalObjectiveMeasure = new TetrisExpectedUtility(tetris, MEASURE_GAMES);

	@Override
	public void run(String[] args) {
		setupLogger();

		PopulationFactory<NTuples> initialPopulationFactory = new UniformRandomPopulationFactory<>(
				INITIAL_INDIVIDUAL_FACTORY);
		logSomeInitialStatistics(initialPopulationFactory);

		PopulationEvaluator<NTuples> evaluator = new OneByOnePopulationEvaluator<>(
				new PerformanceMeasureIndividualEvaluator<>(genotypeToPhenotypeMapper, fitnessMeasure));

		GenerationalOptimizationAlgorithm alg = getOptimizationAlgorithm(evaluator);

		alg.addNextGenerationListener(this);

		alg.evolve(new GenerationsTarget(NUM_GENERATIONS), context);

		logger.info("FINISHED");
	}

	private GenerationalOptimizationAlgorithm getOptimizationAlgorithm(PopulationEvaluator<NTuples> evaluator) {
		switch (CMA_VERSION) {
		case STANDARD:
		case SEP:
			return new HansenCMAESAlgorithm<>(
					LAMBDA,
					evaluator, INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(context.getRandomForThread()),
					new NTuplesDoubleVectorAdapter(), CMAES_SIGMA,
					CMA_VERSION == CMAVersion.SEP,
					CONSTANT_SIGMA);
		case VD:
			return new SharkVDCMAES<>(
					LAMBDA,
					evaluator, INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(context.getRandomForThread()),
					new NTuplesDoubleVectorAdapter(), CMAES_SIGMA);
		case CEM:
			return new SzitaCEMAlgorithm<>(LAMBDA, evaluator, INITIAL_INDIVIDUAL_FACTORY.createRandomIndividual(
					context.getRandomForThread()), new NTuplesDoubleVectorAdapter());
		}
		throw new IllegalStateException();
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
		logger.info(format("Generation %04d. Best fitness: %5.3f", state.getGeneration(),
				state.getBestSolution().getFitness()));
		if (state.getGeneration() % MEASURE_GENERATIONS_FREQUENCY == 0) {

			NTuples individual = state.<NTuples>getBestSolution().getIndividual();
			Agent<TetrisState, TetrisAction> agent = genotypeToPhenotypeMapper.getPhenotype(individual, context.getRandomForThread());
			double performance = externalObjectiveMeasure.measure(agent, context).stats().getMean();
			MeasuredIndividual<NTuples> bestOfGeneration = new MeasuredIndividual<>(individual, performance);
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
		if (state.getGeneration() % SAVE_POPULATION_GENERATIONS_FREQUENCY == 0) {
			List<NTuples> population = seq(state.<NTuples>getEvaluatedSolutions()).map(
					EvaluatedIndividual.<NTuples>toIndividual()).toList();
			serializer.serializeWrapExceptions(population, new File(RESULTS_DIR, "pop.dump"));
		}
	}

	public static void main(String[] args) {
		new NTuplesTetrisExperiment().run(args);
	}
}
