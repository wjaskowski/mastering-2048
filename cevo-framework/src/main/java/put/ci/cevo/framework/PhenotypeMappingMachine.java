package put.ci.cevo.framework;

import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.state.EvaluatedIndividual;

/**
 * Stores phenotypes for a given set of genotypes and mappings between them.
 * Useful for mapping between populations / lists of genotypes and phenotypes
 */
public class PhenotypeMappingMachine<S, X> {
	
	private final List<X> phenotypes;

	private final IdentityHashMap<X, S> genotypesMap;

	public PhenotypeMappingMachine(List<S> genotypes, GenotypePhenotypeMapper<S, X> genotypeToPhenotypeMapper,
			ThreadedContext context) {

		this.genotypesMap = new IdentityHashMap<>(genotypes.size());
		this.phenotypes = new ArrayList<>(genotypes.size());
		for (S individual : genotypes) {
			X phenotype = genotypeToPhenotypeMapper.getPhenotype(individual, context.getRandomForThread());
			this.genotypesMap.put(phenotype, individual);
			this.phenotypes.add(phenotype);
		}
	}

	public List<X> phenotypes() {
		return phenotypes;
	}

	public X phenotype(int idx) {
		return phenotypes.get(idx);
	}

	public EvaluatedPopulation<S> getEvaluatedGenotypes(EvaluatedPopulation<X> phenotypicPopulation) {
		List<EvaluatedIndividual<S>> evaluated = new ArrayList<>(phenotypicPopulation.getPopulation().size());
		for (EvaluatedIndividual<X> evaluatedPhenotype : phenotypicPopulation.getPopulation()) {
			X phenotype = evaluatedPhenotype.getIndividual();
			S genotype = genotypesMap.get(phenotype);
			evaluated.add(new EvaluatedIndividual<>(genotype, evaluatedPhenotype.fitness(),
					evaluatedPhenotype.getGeneration(), evaluatedPhenotype.getEffort()));
		}

		return new EvaluatedPopulation<>(evaluated, phenotypicPopulation.getTotalEffort());
	}

	public EvaluatedPopulation<S> getEvaluatedGenotypes(List<EvaluatedIndividual<X>> evaluatedPhenotypes) {
		List<EvaluatedIndividual<S>> evaluated = new ArrayList<>(evaluatedPhenotypes.size());
		for (EvaluatedIndividual<X> evaluatedPhenotype : evaluatedPhenotypes) {
			X phenotype = evaluatedPhenotype.getIndividual();
			S genotype = genotypesMap.get(phenotype);
			evaluated.add(new EvaluatedIndividual<>(genotype, evaluatedPhenotype.fitness(),
					evaluatedPhenotype.getGeneration(), evaluatedPhenotype.getEffort()));
		}

		return new EvaluatedPopulation<>(evaluated);
	}
}
