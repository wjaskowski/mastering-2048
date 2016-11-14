package put.ci.cevo.framework.evaluators.coev;

import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.PhenotypeMappingMachine;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.fitness.SimpleAverageFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.framework.interactions.OnePopulationRoundRobinAlternativeInteractionScheme;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

import static put.ci.cevo.framework.evaluators.EvaluatorUtils.assignFitness;
import static put.ci.cevo.framework.interactions.OnePopulationRoundRobinAlternativeInteractionScheme.SelfPlay.WITHOUT_SELF_PLAY;

/**
 * Evaluates a population in one-population coevolution setting
 */
public class OnePopulationCoevolutionEvaluator<S, X> implements PopulationEvaluator<S> {

	private final GenotypePhenotypeMapper<S, X> mapper;

	private final FitnessAggregate aggregate;
	private final InteractionScheme<X, X> scheme;

	/**
	 * With default {@link SimpleAverageFitness} and {@link OnePopulationRoundRobinAlternativeInteractionScheme}
	 */
	@AccessedViaReflection
	public OnePopulationCoevolutionEvaluator(InteractionDomain<X, X> domain, GenotypePhenotypeMapper<S, X> mapper) {
		this(new OnePopulationRoundRobinAlternativeInteractionScheme<>(domain, WITHOUT_SELF_PLAY),
				new SimpleAverageFitness(), mapper);
	}

	@AccessedViaReflection
	public OnePopulationCoevolutionEvaluator(InteractionScheme<X, X> scheme, FitnessAggregate aggregate,
			GenotypePhenotypeMapper<S, X> mapper) {
		this.mapper = mapper;
		this.aggregate = aggregate;
		this.scheme = scheme;
	}

	@Override
	public EvaluatedPopulation<S> evaluate(final List<S> population, int generation, ThreadedContext context) {
		PhenotypeMappingMachine<S, X> machine = new PhenotypeMappingMachine<>(population, mapper, context);

		InteractionTable<X, X> table = scheme.interact(machine.phenotypes(), machine.phenotypes(), context);

		// TODO: This is quite unusuall to have EvaluatedIndividual<Phenotype>, but I did not want to make any revolution
		// and to use as much existing code as possible
		List<EvaluatedIndividual<X>> evaluatedPhenotypes = assignFitness(machine.phenotypes(), generation,
				table.getEfforts(), aggregate.aggregateFitness(table.getSolutionsPayoffs(), context));

		return machine.getEvaluatedGenotypes(evaluatedPhenotypes);
	}
}
