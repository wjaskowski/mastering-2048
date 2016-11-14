package put.ci.cevo.experiments.runs.cartpole;

import static java.lang.Runtime.getRuntime;

import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import put.ci.cevo.experiments.mlp.FeedForwardNetworkDoubleVectorAdapter;
import put.ci.cevo.experiments.mlp.FeedForwardNetworkIndividualFactory;
import put.ci.cevo.framework.algorithms.coevolution.random.RandomSamplingEvolutionaryLearning;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.fitness.SimpleAverageFitness;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.framework.operators.mutation.AdaptedUniformMutation;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.util.vectors.DoubleVector;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.agent.functions.mlp.FeedForwardNetwork;
import put.ci.cevo.rl.environment.cartpole.CartPoleEnvironment;

public class TwoPolesExperiment<T extends RealFunction> implements Experiment, EvolutionStateListener {

	private static final int THREADS = getRuntime().availableProcessors();
	private static final int POPULATION_SIZE = 200;
	private static final int MU = 50;
	private static final int LAMBDA = POPULATION_SIZE - MU;
	private static final int NUM_EVALUATIONS = 1;

	private EvaluatedIndividual<T> bestOfRun = new EvaluatedIndividual<T>(null, 0);

	ThreadedContext context = new ThreadedContext(111, THREADS);
	Logger logger = Logger.getLogger(TwoPolesExperiment.class);

	public TwoPolesExperiment(IndividualAdapter<T, DoubleVector> adapter, IndividualFactory<T> factory) {
		this.adapter = adapter;
		this.factory = factory;
	}

	@Override
	public void onNextGeneration(EvolutionState state) {
		EvaluatedIndividual<T> bestOfGeneration = state.getBestSolution();
		if (bestOfGeneration.getFitness() > bestOfRun.getFitness()) {
			bestOfRun = new EvaluatedIndividual<T>(bestOfGeneration.getIndividual(), bestOfGeneration.getFitness());
		}

		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (EvaluatedIndividual<T> ind : state.<T> getEvaluatedSolutions()) {
			stats.addValue(ind.getFitness());
		}

		logger.info(String.format("gen = %3d, eff = %10d, pop = (%.3f,%.3f,%.3f), obj_best = %.5f",
			state.getGeneration(), state.getTotalEffort(), stats.getMin(), stats.getMean(), stats.getMax(),
			bestOfRun.getFitness()));
	}

	private final IndividualAdapter<T, DoubleVector> adapter;
	private final IndividualFactory<T> factory;

	@Override
	public void run(String[] args) {
		Logger.getRootLogger().setLevel(Level.INFO);

		MutationOperator<T> gaussianMutation = new AdaptedUniformMutation<T>(-0.01, 0.01, 1.0, adapter);
		EvolutionModel<T> muPlusLambda = new MuPlusLambdaEvolutionModel<T>(MU, LAMBDA, gaussianMutation);
		PopulationFactory<T> initialPopulationFactory = new UniformRandomPopulationFactory<T>(factory);
		Species<T> species = new Species<T>(muPlusLambda, initialPopulationFactory, POPULATION_SIZE);

		RealFunctionCartPoleEnvironmentInteraction<T> interaction = new RealFunctionCartPoleEnvironmentInteraction<T>(
				1000, true, false);

		PopulationFactory<CartPoleEnvironment> environmentFactory = new PopulationFactory<CartPoleEnvironment>() {
			private final CartPoleEnvironment env = new CartPoleEnvironment(2);

			@Override
			public List<CartPoleEnvironment> createPopulation(int populationSize, RandomDataGenerator random) {
				return Collections.singletonList(env);
			}
		};

		RandomSamplingEvolutionaryLearning<T, CartPoleEnvironment> rsel = new RandomSamplingEvolutionaryLearning<>(
			species, new RoundRobinInteractionScheme<>(interaction), new SimpleAverageFitness(), environmentFactory,
			NUM_EVALUATIONS);

		rsel.addNextGenerationListener(this);

		// Ad infinium
		rsel.evolve(new GenerationsTarget(Integer.MAX_VALUE), context);
	}

	public static void main(String[] args) {
		FeedForwardNetworkIndividualFactory factory = new FeedForwardNetworkIndividualFactory(
			CartPoleEnvironment.NUM_INPUTS_TWO_POLES, 10, 1, -6.0, 6.0);
		FeedForwardNetworkDoubleVectorAdapter adapter = new FeedForwardNetworkDoubleVectorAdapter();

		new TwoPolesExperiment<FeedForwardNetwork>(adapter, factory).run(args);
	}
}
