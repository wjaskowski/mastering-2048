package put.ci.cevo.games.encodings.ntuple;

import static put.ci.cevo.util.ArrayUtils.sorted;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.primitives.Ints;
import org.apache.commons.lang3.ArrayUtils;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryUtils;
import put.ci.cevo.util.CollectionUtils;

/**
 * Builds NTuple with random weights. Excludes any tuple duplicates and any "subtuples"
 */
public class NTuplesLocationsBuilder {

	private final List<List<int[]>> all = new ArrayList<>();
	private final List<int[]> main = new ArrayList<>();

	private final SymmetryExpander expander;

	private final boolean removeSubtuples;

	public NTuplesLocationsBuilder(SymmetryExpander expander, boolean removeSubtuples) {
		this.expander = expander;
		this.removeSubtuples = removeSubtuples;
	}

	public void addLocations(int[] locations) {
		// All contains all tuples added to the builder along with all their symmetry expansions. Sorted for efficiency.

		// Remove duplicates right away (regardless of the order)
		int[] sortedLocations = sorted(locations);
		for (int[] tuple : CollectionUtils.flatten(all)) {
			if (Arrays.equals(sorted(tuple), sortedLocations)) {
				return;
			}
		}

		main.add(sortedLocations);
		all.add(SymmetryUtils.createSymmetric(sortedLocations, expander));
	}

	public List<List<int[]>> build() {
		List<int[]> newMain = new ArrayList<>(main);
		if (removeSubtuples) {
			newMain = getMainWithoutSubtuples();
		}
		Collections.sort(newMain, Ints.lexicographicalComparator()); // Unnecessary, but does not harm

		return newMain.stream().map(x -> SymmetryUtils.createSymmetric(x, expander)).collect(Collectors.toList());
	}

	private List<int[]> getMainWithoutSubtuples() {
		List<int[]> newMain = new ArrayList<>();
		int n = main.size();
		for (int a = 0; a < n; ++a) {
			boolean aIsSubTupleOfB = false;
			for (int b = 0; b < n && !aIsSubTupleOfB; ++b) {
				if (a == b || main.get(a).length > main.get(b).length) {
					continue;
				}
				aIsSubTupleOfB = containsAll(all.get(b), all.get(a));
			}
			if (!aIsSubTupleOfB) {
				newMain.add(main.get(a));
			}
		}
		return newMain;
	}

	private boolean containsAll(List<int[]> container, List<int[]> contained) {
		//TODO: Performance can be improved (without Integers)
		for (int[] containerElement : container) {
			HashSet<Integer> containerElementSet = new HashSet<>(Arrays.asList(ArrayUtils.toObject(containerElement)));
			boolean found = false;
			for (int[] containedElement : contained) {
				if (containerElementSet.containsAll(Arrays.asList(ArrayUtils.toObject(containedElement)))) {
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