package put.ci.cevo.framework.algorithms.factories;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.coevolution.random.RandomSamplingEvolutionaryLearning;
import put.ci.cevo.framework.factories.StaticPopulationFactory;
import put.ci.cevo.framework.fitness.SimpleSumFitness;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.model.MuCommaLambdaEvolutionModel;

public class AlgorithmsFactoryTest {

	@Test
	public void testFromTemplate() throws Exception {
		AlgorithmsFactory factory = AlgorithmsFactory.create();
		RandomSamplingEvolutionaryLearningBuilder<Double> builder1 = factory
			.getBuilder(RandomSamplingEvolutionaryLearning.class);
		builder1.setRandomSampleSize(15).setPopulationSize(200)
			.setPopulationFactory(new StaticPopulationFactory<>(Arrays.asList(0.1, 0.2)));

		RandomSamplingEvolutionaryLearningBuilder<Double> builder2 = factory
			.getBuilder(RandomSamplingEvolutionaryLearning.class);

		builder2.fromTemplate(builder1);
		assertEquals(builder1, builder2);
	}

	// @Test
	public void testFromAlgorithm() throws Exception {
		AlgorithmsFactory factory = AlgorithmsFactory.create();
		RandomSamplingEvolutionaryLearningBuilder<Double> builder1 = factory
			.getBuilder(RandomSamplingEvolutionaryLearning.class);
		builder1.setRandomSampleSize(15).setPopulationSize(200).setFitnessAggregate(new SimpleSumFitness())
			.setEvolutionModel(new MuCommaLambdaEvolutionModel<Double>(10, 10, null))
			.setPopulationFactory(new StaticPopulationFactory<>(Arrays.asList(0.1, 0.2)))
			.setInteractionScheme(new RoundRobinInteractionScheme<Double, Double>(null));
		GenerationalOptimizationAlgorithm algorithm = builder1.build();

		assertEquals(builder1, factory.fromAlgorithm(algorithm));
	}
}
