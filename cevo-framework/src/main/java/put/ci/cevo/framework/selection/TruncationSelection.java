package put.ci.cevo.framework.selection;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.MIN_VALUE;
import static java.lang.Math.min;
import static java.lang.Math.round;

/**
 * Selects the fittest candidates. If the selectionRatio results in fewer selected candidates than required, then these
 * candidates are selected multiple times to make up for the shortfall.
 */
public class TruncationSelection<T> implements SelectionStrategy<T, T> {

	private static final int UNKNOWN = MIN_VALUE;

	private final double ratio;
	private final int selectionSize;

	@AccessedViaReflection
	public TruncationSelection(double ratio) {
		this(ratio, UNKNOWN);
	}

	@AccessedViaReflection
	public TruncationSelection(int selectionSize) {
		this(1, selectionSize);
	}

	@AccessedViaReflection
	public TruncationSelection(double ratio, int selectionSize) {
		this.ratio = ratio;
		this.selectionSize = selectionSize;
	}

	@Override
	public List<T> select(List<EvaluatedIndividual<T>> individuals, RandomDataGenerator random) {
		int ratioCount = (int) round(ratio * individuals.size());
		int numIndividuals = selectionSize != UNKNOWN ? selectionSize : individuals.size();

		final List<T> selection = new ArrayList<T>(numIndividuals);
		ratioCount = ratioCount > numIndividuals ? numIndividuals : ratioCount;
		do {
			int count = min(ratioCount, numIndividuals - selection.size());
			for (int i = 0; i < count; i++) {
				selection.add(individuals.get(i).getIndividual());
			}
		} while (selection.size() < numIndividuals);
		return selection;
	}

	@Override
	public String toString() {
		return "Trunc(" + (selectionSize == UNKNOWN ? ratio : selectionSize + ", " + ratio) + ")";
	}

}
