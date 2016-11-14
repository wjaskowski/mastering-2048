package put.ci.cevo.framework.evaluators.sampling;

import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.PhenotypeMappingMachine;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.fitness.SimpleAverageFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

import static com.google.common.base.Objects.toStringHelper;
import static put.ci.cevo.framework.evaluators.EvaluatorUtils.assignFitness;

public final class SamplingEvaluator<S, X> implements PopulationEvaluator<S> {

	private final InteractionScheme<X, X> scheme;
	private final GenotypePhenotypeMapper<S, X> mapper;

	private final FitnessAggregate aggregate;

	private final PopulationFactory<X> factory;
	private final int sampleSize;

	@AccessedViaReflection
	public SamplingEvaluator(InteractionDomain<X, X> domain, GenotypePhenotypeMapper<S, X> mapper,
			PopulationFactory<X> factory, int sampleSize) {
		this(new RoundRobinInteractionScheme<>(domain), mapper, new SimpleAverageFitness(), factory, sampleSize);
	}

	public SamplingEvaluator(InteractionScheme<X, X> scheme, GenotypePhenotypeMapper<S, X> mapper,
			FitnessAggregate aggregate, PopulationFactory<X> factory, int sampleSize) {
		this.scheme = scheme;
		this.factory = factory;
		this.mapper = mapper;
		this.aggregate = aggregate;
		this.sampleSize = sampleSize;
	}

	@Override
	public EvaluatedPopulation<S> evaluate(List<S> population, int generation, ThreadedContext context) {
		PhenotypeMappingMachine<S, X> machine = new PhenotypeMappingMachine<>(population, mapper, context);

		List<X> tests = factory.createPopulation(sampleSize, context.getRandomForThread());
		InteractionTable<X, X> table = scheme.interact(machine.phenotypes(), tests, context);

		List<EvaluatedIndividual<X>> evaluatedPhenotypes = assignFitness(machine.phenotypes(), generation,
				table.getEfforts(), aggregate.aggregateFitness(table.getSolutionsPayoffs(), context));

		return machine.getEvaluatedGenotypes(evaluatedPhenotypes);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("scheme", scheme).add("aggregate", aggregate).add("factory", factory)
				.add("sample", sampleSize).toString();
	}

}
