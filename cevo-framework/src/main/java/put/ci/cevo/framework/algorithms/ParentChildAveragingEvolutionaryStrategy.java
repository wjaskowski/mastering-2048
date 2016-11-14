package put.ci.cevo.framework.algorithms;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import put.ci.cevo.framework.PopulationUtils;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.OnePopulationEvolutionState;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.vectors.DoubleVector;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * As in "Temporal Difference Learning Versus Co-Evolution for Acquiring Othello Position Evaluation" (2006), S. Lucas &
 * T. Runarsson
 */
public class ParentChildAveragingEvolutionaryStrategy<S> extends AbstractGenerationalOptimizationAlgorithm {

	private final PopulationEvaluator<S> evaluator;
	private final PopulationFactory<S> factory;

	private final MutationOperator<S> mutation;
	private final IndividualAdapter<S, DoubleVector> doubleAdapter;

	private final double beta;
	private final int mu;
	private final int lambda;

	private final Stopwatch timer;

	/**
	 * We have mu*lambda evaluations each generation (each mu parent generates lambda children that are evaluated
	 *
	 * @param mu     here it is actually the population size
	 * @param lambda how many children
	 * @param beta   averaging factor. 0 means no evolution (makes no sense). 1 means take the best child. Lucas used
	 *               0.05.
	 */
	public ParentChildAveragingEvolutionaryStrategy(int mu, int lambda, double beta,
			PopulationFactory<S> initialPopulationFactory,
			PopulationEvaluator<S> evaluator, MutationOperator<S> mutation,
			IndividualAdapter<S, DoubleVector> doubleAdapter) {
		Preconditions.checkArgument(0 < beta && beta <= 1.0);
		Preconditions.checkArgument((lambda + mu) % mu == 0);
		this.mu = mu;
		this.lambda = lambda;
		this.beta = beta;
		this.factory = initialPopulationFactory;
		this.evaluator = evaluator;
		this.mutation = mutation;
		this.doubleAdapter = doubleAdapter;
		this.timer = Stopwatch.createUnstarted();
	}

	@Override
	public void evolve(EvolutionTarget target, final ThreadedContext context) {
		timer.start();

		EvolutionState state = OnePopulationEvolutionState.<S>initialEvolutionState();

		List<S> parents = factory.createPopulation(mu, context.getRandomForThread());

		List<EvaluatedIndividual<S>> evaluatedChildrenList;
		Map<S, Double> evaluatedChildrenMap;
		while (!state.targetAchieved(target)) {
			List<S> children = new ArrayList<>();
			Map<S, List<S>> childrenOfParent = new IdentityHashMap<>(parents.size());

			// Breed children from parents + useful data structures
			for (S parent : parents) {
				List<S> breededChildren = breedChildren(parent, context);
				children.addAll(breededChildren);
				childrenOfParent.put(parent, breededChildren);
			}

			// Evaluate all children, create a map Child => Fitness
			evaluatedChildrenList = evaluator.evaluate(children, state.getGeneration() + 1, context).getPopulation();
			evaluatedChildrenMap = new IdentityHashMap<>(evaluatedChildrenList.size());
			for (EvaluatedIndividual<S> child : evaluatedChildrenList) {
				evaluatedChildrenMap.put(child.getIndividual(), child.getFitness());
			}

			// Find best children among siblings. Move parents towards these best children
			List<S> newParents = new ArrayList<>(parents.size());
			for (S parent : parents) {
				S bestChild = findBestChild(childrenOfParent.get(parent), evaluatedChildrenMap);
				newParents.add(createAveragedParent(parent, bestChild));
			}
			parents = newParents;

			state = nextGeneration(state, evaluatedChildrenList, target);
			fireNextGenerationEvent(state);
		}

		timer.stop();
	}

	private S findBestChild(List<S> children, Map<S, Double> evaluatedChildrenMap) {
		S bestChild;
		bestChild = children.get(0);
		double bestChildFitness = evaluatedChildrenMap.get(bestChild);
		for (S child : children) {
			if (evaluatedChildrenMap.get(child) > bestChildFitness) {
				bestChild = child;
			}
		}
		return bestChild;
	}

	private List<S> breedChildren(S parent, ThreadedContext context) {
		List<S> children = new ArrayList<>();

		int numChildrenPerParent = (lambda + mu) / mu;
		for (int j = 0; j < numChildrenPerParent; ++j) {
			children.add(mutation.produce(parent, context.getRandomForThread()));
		}
		return children;
	}

	private S createAveragedParent(S parent, S bestChild) {
		DoubleVector parentVector = doubleAdapter.from(parent);
		DoubleVector childVector = doubleAdapter.from(bestChild);
		parentVector = parentVector.add(childVector.subtract(parentVector).multiply(beta));
		return doubleAdapter.from(parentVector, parent);
	}

	// Parents are never evaluated so I add children instead of parents to the state
	private EvolutionState nextGeneration(EvolutionState state, List<EvaluatedIndividual<S>> children,
			EvolutionTarget target) {
		int generation = state.getGeneration() + 1;

		long effort = PopulationUtils.sumarizedEffort(children);
		long elapsed = timer.elapsed(MILLISECONDS);

		return new OnePopulationEvolutionState<>(
				elapsed, generation, state.getTotalEffort() + effort, children, target);
	}
}
