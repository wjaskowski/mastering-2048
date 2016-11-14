package put.ci.cevo.framework.evaluators.sampling;

import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Objects.toStringHelper;
import static org.apache.commons.collections15.ListUtils.union;
import static put.ci.cevo.framework.evaluators.EvaluatorUtils.assignFitness;
import static put.ci.cevo.util.RandomUtils.sample;

public class SamplingHybridEvaluator<S> implements PopulationEvaluator<S> {

	private final InteractionScheme<S, S> scheme;
	private final PopulationFactory<S> factory;
	private final FitnessAggregate aggregate;

	private final int sampleSize;
	private final int populationSampleSize;

	@AccessedViaReflection
	public SamplingHybridEvaluator(InteractionScheme<S, S> scheme, FitnessAggregate aggregate,
			PopulationFactory<S> factory, int sampleSize, int populationSampleSize) {
		this.scheme = scheme;
		this.aggregate = aggregate;
		this.factory = factory;
		this.sampleSize = sampleSize;
		this.populationSampleSize = populationSampleSize;
	}

	@Override
	public EvaluatedPopulation<S> evaluate(List<S> pop, int generation, ThreadedContext context) {
		List<S> distributionSample = factory.createPopulation(sampleSize, context.getRandomForThread());
		List<S> populationSample = sample(pop, populationSampleSize, context.getRandomForThread());
		InteractionTable<S, S> table = scheme.interact(pop, union(distributionSample, populationSample), context);

		Map<S, Fitness> fitness = aggregate.aggregateFitness(table.getSolutionsPayoffs(), context);
		return new EvaluatedPopulation<>(assignFitness(pop, generation, table.getEfforts(), fitness));
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("scheme", scheme).add("aggregate", aggregate).add("factory", factory)
			.add("sample", sampleSize).add("popSample", populationSampleSize).toString();
	}

}
