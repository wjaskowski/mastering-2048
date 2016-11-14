package put.ci.cevo.experiments.runs.cartpole;

import static java.lang.Runtime.getRuntime;

import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.mlp.MLPIndividualFactory;
import put.ci.cevo.experiments.mlp.MLPUniformMutation;
import put.ci.cevo.experiments.rl.CartPoleNumStepsInteractionEvaluator;
import put.ci.cevo.experiments.rl.MDPEpisodeInteraction;
import put.ci.cevo.experiments.rl.MDPGenotypeMappingInteraction;
import put.ci.cevo.experiments.rl.RealFunctionActionValueAgentMapping;
import put.ci.cevo.framework.algorithms.coevolution.random.RandomSamplingEvolutionaryLearning;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.fitness.SimpleAverageFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.functions.mlp.MLP;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.rl.environment.ContinuousAction;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.cartpole.CartPoleEnvironment;
import put.ci.cevo.rl.environment.cartpole.CartPoleState;
import put.ci.cevo.util.random.ThreadedContext;

public class MLPSinglePoleExperiment implements Experiment, EvolutionStateListener {

	private static final int THREADS = getRuntime().availableProcessors();
	private static final int POPULATION_SIZE = 200;
	private static final int MU = 50;
	private static final int LAMBDA = POPULATION_SIZE - MU;
	private static final int NUM_EVALUATIONS = 2500;

	private EvaluatedIndividual<MLP> bestOfRun = new EvaluatedIndividual<MLP>(null, 0);

	ThreadedContext context = new ThreadedContext(123, THREADS);
	Logger logger = Logger.getLogger(MLPSinglePoleExperiment.class);

	@Override
	public void onNextGeneration(EvolutionState state) {
		EvaluatedIndividual<MLP> bestOfGeneration = state.getBestSolution();
		if (bestOfGeneration.getFitness() > bestOfRun.getFitness()) {
			bestOfRun = new EvaluatedIndividual<MLP>(bestOfGeneration.getIndividual(), bestOfGeneration.getFitness());
		}

		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (EvaluatedIndividual<WPC> ind : state.<WPC>getEvaluatedSolutions()) {
			stats.addValue(ind.getFitness());
		}

		logger.info(String.format("gen = %3d, eff = %10d, pop = (%.3f,%.3f,%.3f), obj_best = %.5f",
				state.getGeneration(), state.getTotalEffort(), stats.getMin(), stats.getMean(), stats.getMax(),
				bestOfRun.getFitness()));
	}

	@Override
	public void run(String[] args) {
		Logger.getRootLogger().setLevel(Level.INFO);

		MutationOperator<MLP> gaussianMutation = new MLPUniformMutation(-0.1, 0.1, 0.1);
		EvolutionModel<MLP> muPlusLambda = new MuPlusLambdaEvolutionModel<MLP>(MU, LAMBDA, gaussianMutation);
		PopulationFactory<MLP> initialPopulationFactory = new UniformRandomPopulationFactory<>(
				new MLPIndividualFactory(CartPoleEnvironment.NUM_INPUTS_ONE_POLE + 1, 10, 1, -0.2, 0.2));
		Species<MLP> species = new Species<>(muPlusLambda, initialPopulationFactory, POPULATION_SIZE);

		MDPEpisodeInteraction<CartPoleState, ContinuousAction> mdpInteraction = new MDPEpisodeInteraction<CartPoleState, ContinuousAction>(
				new CartPoleNumStepsInteractionEvaluator(), 1500);
		RealFunctionActionValueAgentMapping<MLP, CartPoleState, ContinuousAction> agentMapping = new RealFunctionActionValueAgentMapping<MLP, CartPoleState, ContinuousAction>(
				new CartPoleEnvironment(1));

		final InteractionDomain<MLP, Environment<CartPoleState, ContinuousAction>> interaction = new MDPGenotypeMappingInteraction<>(
				agentMapping, mdpInteraction);

		PopulationFactory<Environment<CartPoleState, ContinuousAction>> environmentFactory = new PopulationFactory<Environment<CartPoleState, ContinuousAction>>() {
			private final Environment<CartPoleState, ContinuousAction> env = new CartPoleEnvironment(1);

			@Override
			public List<Environment<CartPoleState, ContinuousAction>> createPopulation(int populationSize,
					RandomDataGenerator random) {
				return Collections.singletonList(env);
			}
		};

		RandomSamplingEvolutionaryLearning<MLP, Environment<CartPoleState, ContinuousAction>> rsel = new RandomSamplingEvolutionaryLearning<>(
				species, new RoundRobinInteractionScheme<MLP, Environment<CartPoleState, ContinuousAction>>(
				interaction),
				new SimpleAverageFitness(), environmentFactory, NUM_EVALUATIONS);

		rsel.addNextGenerationListener(this);

		RandomDataGenerator random = context.getRandomForThread();
		List<MLP> list = initialPopulationFactory.createPopulation(POPULATION_SIZE, random);
		double bestRandom = 0;
		for (MLP mlp : list) {
			InteractionResult result = interaction.interact(mlp, new CartPoleEnvironment(1), random);
			bestRandom = Math.max(bestRandom, result.firstResult());
		}
		System.out.println(bestRandom);

		// Ad infinium
		rsel.evolve(new GenerationsTarget(Integer.MAX_VALUE), context);
	}

	public static void main(String[] args) {
		new MLPSinglePoleExperiment().run(args);
	}
}
