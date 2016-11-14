package put.ci.cevo.framework.selection;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

import static put.ci.cevo.util.RandomUtils.sample;

/**
 * Returns the fittest individual of some <code>t</code> individuals picked at random, with replacement, from the
 * population.
 */
public class TournamentSelection<T> implements SelectionStrategy<T, T> {

	private final int selectionSize;
	private final int tournamentSize;

	@AccessedViaReflection
	public TournamentSelection(int selectionSize, int tournamentSize) {
		this.selectionSize = selectionSize;
		this.tournamentSize = tournamentSize;
	}

	@Override
	public List<T> select(List<EvaluatedIndividual<T>> individuals, RandomDataGenerator random) {
		final List<T> selection = new ArrayList<>(selectionSize);
		for (int i = 0; i < selectionSize; i++) {
			selection.add(tournament(individuals, random));
		}
		return selection;
	}

	private T tournament(List<EvaluatedIndividual<T>> individuals, RandomDataGenerator random) {
		List<EvaluatedIndividual<T>> tournamentPlayers = sample(individuals, tournamentSize, random);

		EvaluatedIndividual<T> best = tournamentPlayers.remove(0);
		for (EvaluatedIndividual<T> player : tournamentPlayers) {
			if (player.fitness().betterThan(best.fitness())) {
				best = player;
			}
		}
		return best.getIndividual();
	}

	@Override
	public String toString() {
		return "Tour(" + selectionSize + ", " + tournamentSize + ")";
	}
}
