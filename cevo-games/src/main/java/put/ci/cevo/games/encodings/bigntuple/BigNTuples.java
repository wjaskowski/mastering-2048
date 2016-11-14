package put.ci.cevo.games.encodings.bigntuple;

import static put.ci.cevo.util.CollectionUtils.concat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import put.ci.cevo.games.encodings.ntuple.NTupleUtils;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;

public class BigNTuples implements Iterable<BigNTuple>, Serializable {

	private static final long serialVersionUID = -3843856387088040436L;

	// AllLargeNTuples contain tuples equal to mainLargeNTuples (those are different objects, but are exactly equal).
	// Moreover, the weights (LUT) are shared between allLargeNTuples and mainLargeNTuples.
	private final List<BigNTuple> allNTuples;
	private final List<BigNTuple> mainNTuples;

	/** No symmetry */
	public BigNTuples(List<BigNTuple> tuples) {
		this(tuples, tuples);
	}

	private BigNTuples(List<BigNTuple> main, List<BigNTuple> all) {
		this.mainNTuples = main;
		this.allNTuples = all;
	}

	/** Creates a n-tuples system where each tuple from tuples is expanded by expander */
	public static BigNTuples createSymmetric(List<BigNTuple> mainTuples, SymmetryExpander expander) {
		ArrayList<BigNTuple> allTuples = new ArrayList<>();
		for (BigNTuple mainTuple : mainTuples) {
			List<BigNTuple> symmetric = NTupleUtils.createSymmetric(mainTuple, expander);
			assert symmetric.get(0).equals(mainTuple);
			allTuples.addAll(symmetric);
		}
		return new BigNTuples(mainTuples, allTuples);
	}

	/** The returned LargeNTuples does have an identity symmetry expander */
	public BigNTuples add(BigNTuples other) {
		return new BigNTuples(concat(getAll(), other.getAll()));
	}

	public List<BigNTuple> getMain() {
		return mainNTuples;
	}

	public List<BigNTuple> getAll() {
		return allNTuples;
	}

	public BigNTuple getTuple(int idx) {
		return allNTuples.get(idx);
	}

	public long totalWeights() {
		long len = 0;
		for (BigNTuple tuple : mainNTuples) {
			len += tuple.getNumWeights();
		}
		return len;
	}

	@Override
	public Iterator<BigNTuple> iterator() {
		return allNTuples.iterator();
	}

	public int size() {
		return allNTuples.size();
	}

	@Override
	public int hashCode() {
		return Objects.hash(allNTuples);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BigNTuples other = (BigNTuples) obj;
		return allNTuples.equals(other.allNTuples);
	}

	@Override
	public String toString() {
		return "LargeNTuples [mainTuples=" + mainNTuples + "]";
	}
}
