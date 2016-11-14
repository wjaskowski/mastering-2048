package put.ci.cevo.experiments.runs.othello;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.wpc.WPCGaussianMutation;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.experiments.wpc.WPCPopulationFactory;
import put.ci.cevo.framework.algorithms.coevolution.random.RandomSamplingEvolutionaryLearning;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.fitness.SimpleAverageFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;

import static java.lang.Runtime.getRuntime;

public class WPCBestMEUExperiment implements Experiment, EvolutionStateListener {

	private static final int THREADS = getRuntime().availableProcessors();
	private static final int NUM_GAMES_FOR_PERFORMANCE_MEASURE = 50000;
	private static final int POPULATION_SIZE = 200;
	private static final int MU = 50;
	private static final int LAMBDA = POPULATION_SIZE - MU;
	private static final int NUM_EVALUATIONS = 2500;

	private EvaluatedIndividual<WPC> bestOfRun = new EvaluatedIndividual<>(null, 0);

	private final ThreadedContext context = new ThreadedContext(1234, THREADS);
	Logger logger = Logger.getLogger(WPCBestMEUExperiment.class);

	// Interaction environment
	final InteractionDomain<WPC, WPC> othello = new OthelloWPCInteraction(true);

	final PopulationFactory<WPC> wpcFactory = new UniformRandomPopulationFactory<>(new WPCIndividualFactory(
		OthelloBoard.NUM_FIELDS, -1.0, 1.0));

	// Measure is a number of games against Standard WPC Heuristic
	final PerformanceMeasure<WPC> meu = new ExpectedUtility<>(
		othello, wpcFactory, NUM_GAMES_FOR_PERFORMANCE_MEASURE, context);

	@Override
	public void onNextGeneration(EvolutionState state) {
		EvaluatedIndividual<WPC> bestOfGeneration = state.getBestSolution();
		if (bestOfGeneration.getFitness() > bestOfRun.getFitness()) {
			double objectiveFitness = meu.measure(bestOfGeneration.getIndividual(), context).stats().getMean();
			if (objectiveFitness > bestOfRun.getFitness()) {
				bestOfRun = new EvaluatedIndividual<>(bestOfGeneration.getIndividual(), objectiveFitness);

				// Save the best
				try {
					File file = new File(String.format("best_wpc_%04d.dump", state.getGeneration()));
					SerializationManagerFactory.create().serialize(bestOfRun, file);
				} catch (SerializationException e) {
					throw new RuntimeException(e.getMessage(), e);
				}

				// Actually we use here marginWPC only because it knows the dimensions of the board. And WPC does not.
				// (TODO: Make it better)
				logger.info("\n" + OthelloWPCPlayer.createMarginWPC(bestOfRun.getIndividual()));
			}
		}

		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (EvaluatedIndividual<WPC> ind : state.<WPC> getEvaluatedSolutions()) {
			stats.addValue(ind.getFitness());
		}
		logger.info(String.format("gen = %3d, eff = %10d, pop = (%.3f,%.3f,%.3f), obj_best = %.5f",
			state.getGeneration(), state.getTotalEffort(), stats.getMin(), stats.getMean(), stats.getMax(),
			bestOfRun.getFitness()));
	}

	public WPCBestMEUExperiment() {
	}

	@Override
	public void run(String[] args) {
		Logger.getRootLogger().setLevel(Level.INFO);

		MutationOperator<WPC> gaussianMutation = new WPCGaussianMutation(0.1, 0.1, 0);

		EvolutionModel<WPC> muPlusLambda = new MuPlusLambdaEvolutionModel<>(MU, LAMBDA, gaussianMutation);
		PopulationFactory<WPC> initialPopulationFactory = new WPCPopulationFactory(new WPCIndividualFactory(
			OthelloBoard.NUM_FIELDS, -0.1, 0.1));
		Species<WPC> species = new Species<>(muPlusLambda, initialPopulationFactory, POPULATION_SIZE);

		RandomSamplingEvolutionaryLearning<WPC, WPC> rsel = new RandomSamplingEvolutionaryLearning<>(
			species, new RoundRobinInteractionScheme<>(othello), new SimpleAverageFitness(), wpcFactory,
			NUM_EVALUATIONS);

		rsel.addNextGenerationListener(this);

		// Ad infinium
		rsel.evolve(new GenerationsTarget(Integer.MAX_VALUE), context);
	}

	public static void main(String[] args) {
		new WPCBestMEUExperiment().run(args);
	}
}
