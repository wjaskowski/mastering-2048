package put.ci.cevo.games.encodings.bigntuple;

import static put.ci.cevo.util.ArrayUtils.sorted;

import java.util.*;

import com.google.common.primitives.Ints;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryUtils;
import put.ci.cevo.util.CollectionUtils;

/**
 * Builds NTuple with random weights. Excludes any tuple duplicates and any "subtuples"
 */
public class BigNTuplesBuilder {

	private final RandomDataGenerator random;

	private final List<List<int[]>> all = new ArrayList<>();
	private final List<int[]> main = new ArrayList<>();

	private final SymmetryExpander expander;

	private final int numValues;

	private final double minWeight;
	private final double maxWeight;

	private final boolean removeSubtuples;

	public BigNTuplesBuilder(int numValues, double minWeight, double maxWeight, SymmetryExpander expander,
			RandomDataGenerator random, boolean removeSubtuples) {
		this.maxWeight = maxWeight;
		this.minWeight = minWeight;
		this.numValues = numValues;
		this.expander = expander;
		this.random = random;
		this.removeSubtuples = removeSubtuples;
	}

	public void addTuple(int[] locations) {
		// All contains all tuples added to the builder along with all their symmetry expansions. Sorted for efficiency.

		// Remove duplicates right away (regardless of the order)
		int[] sortedLocations = sorted(locations);
		for (int[] sortedTuple : CollectionUtils.flatten(all)) {
			if (Arrays.equals(sortedTuple, sortedLocations)) {
				return;
			}
		}

		List<int[]> tmp = new ArrayList<>();
		for (int[] tuple : SymmetryUtils.createSymmetric(locations, expander)) {
			tmp.add(sorted(tuple));
		}
		all.add(tmp);
		main.add(sorted(locations));
	}

	public BigNTuples buildNTuples() {
		List<int[]> newMain = new ArrayList<>(main);
		if (removeSubtuples) {
			newMain = getMainWithoutDuplicates();
		}

		ArrayList<BigNTuple> mainSorted = createNTuplesFromLocations(newMain);

		return BigNTuples.createSymmetric(mainSorted, expander);
	}

	private ArrayList<BigNTuple> createNTuplesFromLocations(List<int[]> newMain) {
		List<BigNTuple> mainNtuples = new ArrayList<>();
		for (int[] locations : newMain) {
			mainNtuples.add(BigNTuple.createWithRandomWeights(numValues, locations, minWeight, maxWeight, random));
		}
		// Sorting is not obligatory, but does not harm and eases some tests
		Collections.sort(mainNtuples, new Comparator<BigNTuple>() {
			@Override
			public int compare(BigNTuple ntuple1, BigNTuple ntuple2) {
				return Ints.lexicographicalComparator().compare(ntuple1.getLocations(), ntuple2.getLocations());
			}
		});
		return new ArrayList<>(mainNtuples);
	}

	private List<int[]> getMainWithoutDuplicates() {
		List<int[]> newMain = new ArrayList<>();
		int n = main.size();
		for (int a = 0; a < n; ++a) {
			boolean aIsSubtupleOfB = false;
			for (int b = 0; b < n && !aIsSubtupleOfB; ++b) {
				if (a == b || main.get(a).length > main.get(b).length) {
					continue;
				}
				aIsSubtupleOfB = containsAll(all.get(b), all.get(a));
			}
			if (!aIsSubtupleOfB) {
				newMain.add(main.get(a));
			}
		}
		return newMain;
	}

	private boolean containsAll(List<int[]> container, List<int[]> containee) {
		//TODO: Performance can be improved (without Integers)
		for (int[] containerElement : container) {
			HashSet<Integer> containerElementSet = new HashSet<>(Arrays.asList(ArrayUtils.toObject(containerElement)));
			boolean found = false;
			for (int[] containeeElement : containee) {
				if (containerElementSet.containsAll(Arrays.asList(ArrayUtils.toObject(containeeElement)))) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}
}