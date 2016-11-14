package put.ci.cevo.framework.evaluators.coev;

import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.PhenotypeMappingMachine;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.interactions.Tournament;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

/**
 * General population evaluator working with different (one population) schemes. What this thing does is actually
 * only genotype<->phenotyp mapping
 */
public class TournamentCoevolutionEvaluator<S, X> implements PopulationEvaluator<S> {

	private final GenotypePhenotypeMapper<S, X> genotypeToPhenotypeMapper;
	private final Tournament<X> tournament;

	@AccessedViaReflection
	public TournamentCoevolutionEvaluator(Tournament<X> tournament,
			GenotypePhenotypeMapper<S, X> genotypeToPhenotypeMapper) {
		this.genotypeToPhenotypeMapper = genotypeToPhenotypeMapper;
		this.tournament = tournament;
	}

	@Override
	public EvaluatedPopulation<S> evaluate(final List<S> population, int generation, ThreadedContext context) {
		PhenotypeMappingMachine<S, X> machine = new PhenotypeMappingMachine<>(population, genotypeToPhenotypeMapper,
				context);

		EvaluatedPopulation<X> evaluatedPhenotypicPopulation = tournament.execute(machine.phenotypes(), context);

		return machine.getEvaluatedGenotypes(evaluatedPhenotypicPopulation);
	}
}
