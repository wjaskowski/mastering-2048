package put.ci.cevo.games.encodings.ntuple;

import static put.ci.cevo.util.CollectionUtils.concat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.encodings.ntuple.expanders.IdentitySymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;
import put.ci.cevo.rl.agent.functions.RealFunction;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import static put.ci.cevo.games.encodings.ntuple.NTupleUtils.createSymmetric;

public class RelativeNTuples implements Iterable<RelativeNTuple>, Serializable, RealFunction {

	/** A class helping in building RelativeNTuples object */
	public static class Builder {
		private final List<RelativeNTuple> tuples = new ArrayList<>();

		private final SymmetryExpander expander;

		public Builder(SymmetryExpander expander) {
			this.expander = expander;
		}

		public void add(RelativeNTuple tuple) {
			tuples.add(tuple);
		}

		public RelativeNTuples build() {
			return new RelativeNTuples(tuples, expander);
		}
	}

	private static final long serialVersionUID = -3843856387088040436L;

	// AllRelativeNTuples contain tuples equal to mainRelativeNTuples (those are different objects, but are exactly equal).
	// Moreover, the weights (LUT) are shared between allRelativeNTuples and mainRelativeNTuples.
	private final List<RelativeNTuple> allRelativeNTuples;
	private final List<RelativeNTuple> mainRelativeNTuples;

	private final SymmetryExpander symmetryExpander;

	/** No symmetry */
	public RelativeNTuples(List<RelativeNTuple> tuples) {
		this(tuples, new IdentitySymmetryExpander());
	}

	public RelativeNTuples(RelativeNTuples ntuples) {
		this(ntuples.mainRelativeNTuples, ntuples.symmetryExpander);
	}

	public RelativeNTuples(List<RelativeNTuple> main, List<RelativeNTuple> all) {
		this.mainRelativeNTuples = main;
		this.allRelativeNTuples = all;
		this.symmetryExpander = null;
	}

	static public RelativeNTuples createWithRandomWeights(List<List<int[]>> locations, int numValues, double minWeight,
			double maxWeight, RandomDataGenerator random) {
		List<RelativeNTuple> mainTuples = new ArrayList<>();
		List<RelativeNTuple> allTuples = new ArrayList<>();
		for (List<int[]> single : locations) {
			RelativeNTuple nTuple = RelativeNTuple.newWithRandomWeights(numValues, single.get(0), minWeight, maxWeight, random);
			mainTuples.add(nTuple);
			allTuples.add(nTuple);

			for (int i = 1; i < single.size(); ++i) {
				allTuples.add(RelativeNTuple.newWithSharedWeights(nTuple, single.get(i)));
			}
		}
		return new RelativeNTuples(mainTuples, allTuples);
	}

	/** Creates a n-tuples system where each tuple from tuples is expanded by expander */
	public RelativeNTuples(List<RelativeNTuple> tuples, SymmetryExpander expander) {
		this.mainRelativeNTuples = new ArrayList<>(tuples);
		this.allRelativeNTuples = new ArrayList<>();
		for (RelativeNTuple mainTuple : mainRelativeNTuples) {
			List<RelativeNTuple> symmetric = createSymmetric(mainTuple, expander);
			assert symmetric.get(0).equals(mainTuple);
			allRelativeNTuples.addAll(symmetric);
		}
		this.symmetryExpander = expander;
	}

	/** The returned RelativeNTuples does have an identity symmetry expander */
	public RelativeNTuples add(RelativeNTuples other) {
		return new RelativeNTuples(concat(getAll(), other.getAll()));
	}

	public List<RelativeNTuple> getMain() {
		return mainRelativeNTuples;
	}

	public List<RelativeNTuple> getAll() {
		return allRelativeNTuples;
	}

	public RelativeNTuple getTuple(int idx) {
		return allRelativeNTuples.get(idx);
	}

	public int totalWeights() {
		int len = 0;
		for (RelativeNTuple tuple : mainRelativeNTuples) {
			len += tuple.getNumWeights();
		}
		return len;
	}

	public float[] weights() {
		float[] weights = new float[totalWeights()];
		int pos = 0;
		for (RelativeNTuple tuple : mainRelativeNTuples) {
			System.arraycopy(tuple.getWeights(), 0, weights, pos, tuple.getNumWeights());
			pos += tuple.getNumWeights();
		}
		return weights;
	}

	public SymmetryExpander getSymmetryExpander() {
		return symmetryExpander;
	}

	@Override
	public Iterator<RelativeNTuple> iterator() {
		return allRelativeNTuples.iterator();
	}

	public int size() {
		return allRelativeNTuples.size();
	}

	@Override
	public int hashCode() {
		return Objects.hash(allRelativeNTuples);
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
		RelativeNTuples other = (RelativeNTuples) obj;
		return allRelativeNTuples.equals(other.allRelativeNTuples);
	}

	@Override
	public String toString() {
		return "RelativeNTuples [mainRelativeNTuples=" + mainRelativeNTuples + ", symmetryExpander=" + symmetryExpander + "]";
	}

	// TODO: This a 2048 HACK
	@Override
	@Deprecated
	public double getValue(double[] input) {
		throw new NotImplementedException();
	}

	// TODO: This a 2048 HACK and should be moved out of here
	@Override
	@Deprecated
	public void update(double[] input, double expectedValue, double learningRate) {
		throw new NotImplementedException();
	}
}
