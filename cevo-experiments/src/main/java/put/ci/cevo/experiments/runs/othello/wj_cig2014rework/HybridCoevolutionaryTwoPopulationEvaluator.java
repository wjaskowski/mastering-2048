package put.ci.cevo.experiments.runs.othello.wj_cig2014rework;

import org.apache.commons.collections15.ListUtils;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.PhenotypeMappingMachine;
import put.ci.cevo.framework.algorithms.common.EffortTable;
import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.TwoPopulationEvaluator;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.fitness.SimpleAverageFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static put.ci.cevo.framework.evaluators.EvaluatorUtils.assignFitness;

/**
 * Evaluates a population by playing a round-robin tournament (without symmetric games) + each individual plays against
 * all tests
 */
public class HybridCoevolutionaryTwoPopulationEvaluator<S, X> implements TwoPopulationEvaluator<S, S> {

	private final InteractionDomain<X, X> domain;
	private final GenotypePhenotypeMapper<S, X> genotypeToPhenotypeMapper;
	private final FitnessAggregate fitnessAggregate;

	public HybridCoevolutionaryTwoPopulationEvaluator(InteractionDomain<X, X> domain,
			GenotypePhenotypeMapper<S, X> genotypeToPhenotypeMapper) {
		this(domain, genotypeToPhenotypeMapper, new SimpleAverageFitness());
	}

	public HybridCoevolutionaryTwoPopulationEvaluator(InteractionDomain<X, X> domain,
			GenotypePhenotypeMapper<S, X> genotypeToPhenotypeMapper, FitnessAggregate fitnessAggregate) {
		this.domain = domain;
		this.genotypeToPhenotypeMapper = genotypeToPhenotypeMapper;
		this.fitnessAggregate = fitnessAggregate;
	}

	private static class Job<S> {
		public final S solution;
		public final S test;

		public Job(S solution, S test) {
			this.solution = solution;
			this.test = test;
		}
	}

	@Override
	public Pair<EvaluatedPopulation<S>, EvaluatedPopulation<S>> evaluate(List<S> solutions, List<S> tests,
			int generation, ThreadedContext context) {
		System.out.println(solutions.size() + " " + tests.size());
		// TODO: This method seems to me a bit over complicated

		PhenotypeMappingMachine<S, X> solutionsMachine = new PhenotypeMappingMachine<>(solutions,
				genotypeToPhenotypeMapper, context);
		PhenotypeMappingMachine<S, X> testsMachine = new PhenotypeMappingMachine<>(solutions,
				genotypeToPhenotypeMapper, context);

		List<Job<X>> jobs = new ArrayList<>();
		for (int i = 0; i < solutions.size(); ++i) {
			// Only half of the matrix (symmetry), without the main axis
			for (int j = 0; j < i; ++j) {
				jobs.add(new Job<>(solutionsMachine.phenotype(i), solutionsMachine.phenotype(j)));
			}
			// Against all tests
			for (X testPhenotype : testsMachine.phenotypes()) {
				jobs.add(new Job<>(solutionsMachine.phenotype(i), testPhenotype));
			}
			// Notice: here we do not have "test vs. test" jobs
		}

		// The table is (all x all) just for convenience. We actually use only the upper part (solutions x all).
		final List<X> all = ListUtils.union(solutionsMachine.phenotypes(), testsMachine.phenotypes());
		final PayoffTable.PayoffTableBuilder<X, X> payoffTable = PayoffTable.create(all, all);
		final EffortTable.EffortTableBuilder<X, X> effortTable = EffortTable.create(all, all);
		context.submit(new ThreadedContext.Worker<Job<X>, Void>() {
			@Override
			public Void process(Job<X> job, ThreadedContext context) throws Exception {
				InteractionResult result = domain.interact(job.solution, job.test, context.getRandomForThread());

				payoffTable.put(job.solution, job.test, result.firstResult());
				payoffTable.put(job.test, job.solution, result.secondResult());
				effortTable.put(job.solution, job.test, result.getEffort());
				return null;
			}
		}, jobs);

		Map<X, Fitness> fitness = fitnessAggregate.aggregateFitness(payoffTable.build(), context);

		List<EvaluatedIndividual<X>> evaluatedSolutions = assignFitness(solutionsMachine.phenotypes(), generation,
				effortTable.build(), fitness);

		//TODO: We do not care about tests. Here we use TwoPopulationEvaluator, because it uses two populations, but
		//what we actually mean is a OnePopulationEvaluator with some extra tests (like archive)
		return Pair.create(solutionsMachine.getEvaluatedGenotypes(evaluatedSolutions), new EvaluatedPopulation<S>());
	}
}

