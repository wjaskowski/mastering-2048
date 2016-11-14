package put.ci.cevo.games.encodings.ntuple;

import java.util.ArrayList;
import java.util.List;

import put.ci.cevo.games.encodings.bigntuple.BigNTuple;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;

import com.carrotsearch.hppc.IntArrayList;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryUtils;

public class NTupleUtils {

	/**
	 * Returns a list of tuples expanded by symmetry expander. All the returned tuples share weights with the given
	 * template. The first returned tuple is equal to template (but is a new object)
	 */
	public static List<NTuple> createSymmetric(NTuple template, SymmetryExpander expander) {
		List<int[]> symmetric = SymmetryUtils.createSymmetric(template.getLocations(), expander);

		List<NTuple> tuples = new ArrayList<>(symmetric.size());
		for (int[] locations : symmetric) {
			// Watch out: weights are shared among symmetric tuples
			tuples.add(NTuple.newWithSharedWeights(template, locations));
		}
		assert tuples.get(0).equals(template);
		return tuples;
	}

	public static List<RelativeNTuple> createSymmetric(RelativeNTuple template, SymmetryExpander expander) {
		List<int[]> symmetric = SymmetryUtils.createSymmetric(template.getLocations(), expander);

		List<RelativeNTuple> tuples = new ArrayList<>(symmetric.size());
		for (int[] locations : symmetric) {
			// Watch out: weights are shared among symmetric tuples
			tuples.add(RelativeNTuple.newWithSharedWeights(template, locations));
		}
		assert tuples.get(0).equals(template);
		return tuples;
	}

	public static List<BigNTuple> createSymmetric(BigNTuple template, SymmetryExpander expander) {
		List<int[]> symmetric = SymmetryUtils.createSymmetric(template.getLocations(), expander);

		List<BigNTuple> tuples = new ArrayList<>(symmetric.size());
		for (int[] locations : symmetric) {
			// Watch out: weights are shared among symmetric tuples
			tuples.add(new BigNTuple(template.getNumValues(), locations, template.getWeights()));
		}
		assert tuples.get(0).equals(template);
		return tuples;
	}

	/**
	 * List of tuples that contain given board position for every board position.
	 * 
	 * @param numBoardPositions
	 *            the number of margin-based board positions (all positions (also with margin position))
	 */
	public static IntArrayList[] getTuplesForPositions(NTuples tuples, int numBoardPositions) {
		IntArrayList[] tuplesForPosition = new IntArrayList[numBoardPositions];
		for (int i = 0; i < numBoardPositions; ++i) {
			tuplesForPosition[i] = new IntArrayList();
		}
		for (int idx = 0; idx < tuples.size(); ++idx) {
			NTuple tuple = tuples.getTuple(idx);
			for (int pos : tuple.getLocations()) {
				tuplesForPosition[pos].add(idx);
			}
		}
		return tuplesForPosition;
	}
}
