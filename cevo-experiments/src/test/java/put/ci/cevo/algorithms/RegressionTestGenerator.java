package put.ci.cevo.algorithms;

import com.google.common.io.Files;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.othello.OthelloWPCPopulationFactory;
import put.ci.cevo.experiments.wpc.WPCGaussianMutation;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.RetrospectiveAlgorithm;
import put.ci.cevo.framework.algorithms.coevolution.OnePopulationCompetitiveCoevolution;
import put.ci.cevo.framework.algorithms.coevolution.TwoPopulationCompetitiveCoevolution;
import put.ci.cevo.framework.algorithms.coevolution.random.RandomSamplingEvolutionaryLearning;
import put.ci.cevo.framework.algorithms.coevolution.random.RandomSamplingHybridCoevolution;
import put.ci.cevo.framework.algorithms.coevolution.random.RandomSamplingTwoPopHybrid;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.fitness.SimpleSumFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.GenericEvolutionModel;
import put.ci.cevo.framework.model.MuCommaLambdaEvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.framework.retrospection.queries.BestOfGenerationSolutionQuery;
import put.ci.cevo.framework.selection.StochasticUniversalSampling;
import put.ci.cevo.framework.selection.TournamentSelection;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.primitives.Doubles.toArray;
import static put.ci.cevo.util.ArrayUtils.toJavaArray;

public class RegressionTestGenerator {

	private static final OthelloWPCInteraction DOMAIN = new OthelloWPCInteraction();
	private static final int POP_SIZE = 50;
	private static final int GENERATIONS = 5;
	private static final int NUM_TESTS = 5;
	private static final int SAMPLE_SIZE = 128;
	private static final int THREADS = 4;


	public static void main(String[] args) throws IOException {
		RegressionTestGenerator testGenerator = new RegressionTestGenerator();
		testGenerator.generateRegressionTest(createOnePopulationCompetitiveCoevolution(DOMAIN, POP_SIZE),
				POP_SIZE, NUM_TESTS, SAMPLE_SIZE, GENERATIONS, THREADS);
		testGenerator.generateRegressionTest(createTwoPopulationCompetitiveCoevolution(DOMAIN, POP_SIZE),
				POP_SIZE, NUM_TESTS, SAMPLE_SIZE, GENERATIONS, THREADS);
		testGenerator.generateRegressionTest(createRandomSamplingEvolutionaryLearning(DOMAIN, POP_SIZE),
				POP_SIZE, NUM_TESTS, SAMPLE_SIZE, GENERATIONS, THREADS);
		testGenerator.generateRegressionTest(createRandomSamplingHybridCoevolution(DOMAIN, POP_SIZE),
				POP_SIZE, NUM_TESTS, SAMPLE_SIZE, GENERATIONS, THREADS);
		testGenerator.generateRegressionTest(createRandomSamplingTwoPopHybrid(DOMAIN, POP_SIZE),
				POP_SIZE, NUM_TESTS, SAMPLE_SIZE, GENERATIONS, THREADS);

	}
	private void generateRegressionTest(final GenerationalOptimizationAlgorithm algorithm, int popSize, int numTests, int meuSample, int gens, int threads)
			throws IOException {
		StringBuilder s = new StringBuilder();
		final String className = algorithm.getClass().getSimpleName();
		for (int i = 0; i < numTests; i++) {
			ThreadedContext context = new ThreadedContext(i, threads);
			OthelloWPCInteraction domain = new OthelloWPCInteraction();

			TestCallback stateCallback = new TestCallback();
			algorithm.addNextGenerationListener(stateCallback);

			RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
			Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(gens), context);

			ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), meuSample, context);
			List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();


			s.append(String.format("@Test\npublic void test%s%02d() {\n", className, i + 1));
			s.append(String.format("	ThreadedContext context = new ThreadedContext(%s, %s, %s);\n", i, threads, threads));
			s.append(String.format("	OthelloWPCInteraction domain = new OthelloWPCInteraction();\n\n"));
			s.append(String.format("	GenerationalOptimizationAlgorithm algorithm = create%s(domain, %s);\n", className, popSize));
			s.append( "	TestCallback stateCallback = new TestCallback();\n"
					+ "	algorithm.addNextGenerationListener(stateCallback);\n\n"
					+ "	RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);\n");
			s.append(String.format("	Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(%s), context);\n\n", gens));
			s.append(String.format("	ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), %s, context);\n", meuSample));
			s.append(String.format("	List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();\n\n"));

			s.append(String.format("	stateCallback.assertGenerations(Sequences.range(%s).toList());\n", gens));
			s.append(String.format("	stateCallback.assertEffort(Sequences.range(%s * %s * 2, %s * %s * 2 * %s, %s * %s * 2).toList());\n\n", popSize, popSize, popSize, popSize, gens + 1, popSize, popSize));
			s.append(String.format("	double[] expected = %s;\n", toJavaArray(toArray(fitness))));
			s.append("	Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);\n");
			s.append("}\n\n");
		}

		File to = new File(className + "Test.txt");
		System.out.println("Saving tests to: " + to.getAbsolutePath());
		Files.write(s.toString(), to, UTF_8);
	}



	public static OnePopulationCompetitiveCoevolution<WPC> createOnePopulationCompetitiveCoevolution(InteractionDomain<WPC, WPC> domain, int popSize) {
		RoundRobinInteractionScheme<WPC, WPC> scheme = new RoundRobinInteractionScheme<>(domain);
		EvolutionModel<WPC> evolutionModel = new MuPlusLambdaEvolutionModel<>(popSize / 2, popSize / 2, new WPCGaussianMutation(0.2, 0.2));
		Species<WPC> species = new Species<>(evolutionModel, new OthelloWPCPopulationFactory(), popSize);
		return new OnePopulationCompetitiveCoevolution<>(species, scheme, new SimpleSumFitness());
	}

	public static TwoPopulationCompetitiveCoevolution<WPC, WPC> createTwoPopulationCompetitiveCoevolution(InteractionDomain<WPC, WPC> domain, int popSize) {
		RoundRobinInteractionScheme<WPC, WPC> scheme = new RoundRobinInteractionScheme<>(domain);
		EvolutionModel<WPC> solutionsModel = new MuPlusLambdaEvolutionModel<>(popSize / 2, popSize / 2, new WPCGaussianMutation(0.2, 0.2));
		EvolutionModel<WPC> testsModel = new MuCommaLambdaEvolutionModel<>(popSize / 2, popSize, new WPCGaussianMutation(0.2, 0.2));
		Species<WPC> solutionsSpecies = new Species<>(solutionsModel, new OthelloWPCPopulationFactory(), popSize);
		Species<WPC> testsSpecies = new Species<>(testsModel, new OthelloWPCPopulationFactory(), popSize);
		return new TwoPopulationCompetitiveCoevolution<>(solutionsSpecies, testsSpecies, new SimpleSumFitness(), scheme);
	}

	public static RandomSamplingHybridCoevolution<WPC> createRandomSamplingHybridCoevolution(InteractionDomain<WPC, WPC> domain, int popSize) {
		RoundRobinInteractionScheme<WPC, WPC> scheme = new RoundRobinInteractionScheme<>(domain);
		EvolutionModel<WPC> evolutionModel = new MuPlusLambdaEvolutionModel<>(popSize / 2, popSize / 2, new WPCGaussianMutation(0.2, 0.2));
		Species<WPC> species = new Species<>(evolutionModel, new OthelloWPCPopulationFactory(), popSize);
		return new RandomSamplingHybridCoevolution<>(species, scheme, new SimpleSumFitness(),
				new OthelloWPCPopulationFactory(), popSize / 2, popSize / 2);
	}

	public static RandomSamplingTwoPopHybrid<WPC, WPC> createRandomSamplingTwoPopHybrid(InteractionDomain<WPC, WPC> domain, int popSize) {
		RoundRobinInteractionScheme<WPC, WPC> scheme = new RoundRobinInteractionScheme<>(domain);
		final WPCGaussianMutation mut = new WPCGaussianMutation(0.2, 0.2);

		EvolutionModel<WPC> solutionsModel = new GenericEvolutionModel<>(new TournamentSelection<WPC>(popSize, 5), mut);
		EvolutionModel<WPC> testsModel = new GenericEvolutionModel<>(new StochasticUniversalSampling<WPC>(popSize / 2), mut);
		Species<WPC> solutionsSpecies = new Species<>(solutionsModel, new OthelloWPCPopulationFactory(), popSize);
		Species<WPC> testSpecies = new Species<>(testsModel, new OthelloWPCPopulationFactory(), popSize / 2);
		return new RandomSamplingTwoPopHybrid<>(solutionsSpecies, testSpecies, new SimpleSumFitness(), scheme,
				new OthelloWPCPopulationFactory(), popSize / 2);
	}

	public static RandomSamplingEvolutionaryLearning<WPC, WPC> createRandomSamplingEvolutionaryLearning(InteractionDomain<WPC, WPC> domain, int popSize) {
		RoundRobinInteractionScheme<WPC, WPC> scheme = new RoundRobinInteractionScheme<>(domain);

		EvolutionModel<WPC> solutionsModel = new MuPlusLambdaEvolutionModel<>(popSize / 2, popSize / 2, new WPCGaussianMutation(0.2, 0.2));
		Species<WPC> species = new Species<>(solutionsModel, new OthelloWPCPopulationFactory(), popSize);

		return new RandomSamplingEvolutionaryLearning<>(species, scheme, new SimpleSumFitness(),
				new OthelloWPCPopulationFactory(), 50);
	}
}
