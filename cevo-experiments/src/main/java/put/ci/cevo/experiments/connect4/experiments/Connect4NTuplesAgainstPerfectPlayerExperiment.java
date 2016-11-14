package put.ci.cevo.experiments.connect4.experiments;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import put.ci.cevo.experiments.connect4.PerfectPlayerPopulationEvaluator;
import put.ci.cevo.experiments.connect4.mappers.NTuplesConnect4PlayerMapper;
import put.ci.cevo.experiments.connect4.ntuples.Connect4NTuplePopulationFactory;
import put.ci.cevo.experiments.ntuple.NTuplesDoubleVectorAdapter;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.OnePopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.framework.operators.mutation.GaussianMutation;
import put.ci.cevo.framework.operators.mutation.MutationAdapter;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.MeasuredIndividual;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.state.listeners.LastGenerationListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.connect4.thill.c4.AlphaBetaAgent;
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
 * Uses perfectly playing {@link AlphaBetaAgent} to evaluate evolving population and guide the evolutionary search.
 */
public class Connect4NTuplesAgainstPerfectPlayerExperiment
		implements Experiment, EvolutionStateListener, LastGenerationListener {

	private static final Logger logger = Logger.getLogger(Connect4NTuplesAgainstPerfectPlayerExperiment.class);
	private static final Configuration config = Configuration.getConfiguration();

	// Technical params
	private static final int SEED = config.getInt(new ConfKey("seed"));
	private static final long RUN = config.getInt(new ConfKey("run"));
	private static final int NUM_THREADS = config.getInt(new ConfKey("threads"), getRuntime().availableProcessors());

	// Major params
	private static final int MU = config.getInt(new ConfKey("es.mu"), 2);
	private static final int LAMBDA = config.getInt(new ConfKey("es.lambda"), 2);
	private static final int POPULATION_SIZE = MU + LAMBDA;
	private static final int NUM_GENERATIONS = config.getInt(new ConfKey("es.generations"), MAX_VALUE);
	private static final int TUPLE_SIZE = config.getInt(new ConfKey("tuple.size"), 3);

	// Statistics params
	private static final int EVERY_GENERATIONS = config.getInt(new ConfKey("every_generations"), 10);
	private static final int NUM_INTERACTIONS_FOR_FITNESS = config.getInt(new ConfKey("fitness_interactions"), 200);

	private static final File RESULTS = config.getFile(new ConfKey("results_dir"));

	private final ThreadedContext context = new ThreadedContext(SEED, NUM_THREADS);

	private final TableBuilder tableBuilder = new TableBuilder("gen", "eff", "time", "best_perf");
	private final SerializationManager serializer = SerializationManagerFactory.create();

	private MeasuredIndividual<NTuples> bestOfRun = MeasuredIndividual.<NTuples>createNull();

	public static void main(String[] args) {
		new Connect4NTuplesAgainstPerfectPlayerExperiment().run(args);
	}

	@Override
	public void run(String[] args) {
		setupLogger();

		PopulationFactory<NTuples> populationFactory = new Connect4NTuplePopulationFactory(TUPLE_SIZE, -0.1, 0.1);
		logSomeInitialStatistics(populationFactory);

		MutationOperator<NTuples> gaussianMutation = new MutationAdapter<>(new GaussianMutation(1.0, 1.0),
				new NTuplesDoubleVectorAdapter());
		EvolutionModel<NTuples> muPlusLambda = new MuPlusLambdaEvolutionModel<>(MU, LAMBDA, gaussianMutation);
		Species<NTuples> species = new Species<>(muPlusLambda, populationFactory, POPULATION_SIZE);

		PopulationEvaluator<NTuples> evaluator = new PerfectPlayerPopulationEvaluator<>(
				new NTuplesConnect4PlayerMapper(), NUM_INTERACTIONS_FOR_FITNESS);
		GenerationalOptimizationAlgorithm rsel = new OnePopulationEvolutionaryAlgorithm<>(species, evaluator);

		rsel.addNextGenerationListener(this);
		rsel.addLastGenerationListener(this);

		rsel.evolve(new GenerationsTarget(NUM_GENERATIONS), context);
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

	private static void logSomeInitialStatistics(PopulationFactory<NTuples> initialPopulationFactory) {
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

	@Override
	public void onNextGeneration(EvolutionState state) {
		logPopulationPerformance(state);
		if (state.getGeneration() % EVERY_GENERATIONS != 0) {
			return;
		}

		EvaluatedIndividual<NTuples> individual = state.<NTuples>getBestSolution();
		MeasuredIndividual<NTuples> bestOfGeneration = new MeasuredIndividual<>(
				individual.getIndividual(), individual.getFitness());

		if (bestOfGeneration.isBetterThan(bestOfRun)) {
			bestOfRun = bestOfGeneration;
			serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS, "best-" + RUN + ".dump"));
			logger.info(format("Found better. Perf = %5.3f", bestOfRun.getPerformance()));
		}

		tableBuilder.addRow(state.getGeneration(), state.getTotalEffort(), millisToHMS(state.getElapsedTime()),
				bestOfGeneration.getPerformance());
		TableUtil.saveTableAsCSV(tableBuilder.build(), new File(RESULTS, "results-" + RUN + ".csv"));
	}

	private void logPopulationPerformance(EvolutionState state) {
		SummaryStatistics stats = new SummaryStatistics();
		for (EvaluatedIndividual<?> ind : state.getEvaluatedSolutions()) {
			stats.addValue(ind.getFitness());
		}
		logger.info(String.format("gen = %3d, eff = %10d, pop = (%.3f,%.3f,%.3f), obj_best = %.5f",
			state.getGeneration(), state.getTotalEffort(), stats.getMin(), stats.getMean(), stats.getMax(),
			bestOfRun.getPerformance()));
	}

	@Override
	public void onLastGeneration(EvolutionState state) {
		serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS, "last-" + RUN + ".dump"));
	}

}
