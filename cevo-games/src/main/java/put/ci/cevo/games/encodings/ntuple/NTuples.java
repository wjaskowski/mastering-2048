package put.ci.cevo.games.encodings.ntuple;

import static put.ci.cevo.games.encodings.ntuple.NTupleUtils.createSymmetric;
import static put.ci.cevo.util.CollectionUtils.concat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.encodings.ntuple.eval.BoardNTupleEvaluator;
import put.ci.cevo.games.encodings.ntuple.expanders.IdentitySymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;
import put.ci.cevo.games.game2048.Game2048Board;
import put.ci.cevo.rl.agent.functions.RealFunction;

public class NTuples implements Iterable<NTuple>, Serializable, RealFunction {

	/** A class helping in building NTuples object */
	public static class Builder {
		private final List<NTuple> tuples = new ArrayList<>();

		private final SymmetryExpander expander;

		public Builder(SymmetryExpander expander) {
			this.expander = expander;
		}

		public void add(NTuple tuple) {
			tuples.add(tuple);
		}

		public NTuples build() {
			return new NTuples(tuples, expander);
		}
	}

	private static final long serialVersionUID = -3843856387088040436L;

	// AllNTuples contain tuples equal to mainNTuples (those are different objects, but are exactly equal).
	// Moreover, the weights (LUT) are shared between allNTuples and mainNTuples.
	private final List<NTuple> allNTuples;
	private final List<NTuple> mainNTuples;

	private final SymmetryExpander symmetryExpander;

	/** No symmetry */
	public NTuples(List<NTuple> tuples) {
		this(tuples, new IdentitySymmetryExpander());
	}

	public NTuples(NTuples ntuples) {
		this(ntuples.mainNTuples, ntuples.symmetryExpander);
	}

	public NTuples(List<NTuple> main, List<NTuple> all) {
		this.mainNTuples = main;
		this.allNTuples = all;
		this.symmetryExpander = null;
	}

	static public NTuples createWithRandomWeights(List<List<int[]>> locations, int numValues, double minWeight,
			double maxWeight, RandomDataGenerator random) {
		List<NTuple> mainTuples = new ArrayList<>();
		List<NTuple> allTuples = new ArrayList<>();
		for (List<int[]> single : locations) {
			NTuple nTuple = NTuple.newWithRandomWeights(numValues, single.get(0), minWeight, maxWeight, random);
			mainTuples.add(nTuple);
			allTuples.add(nTuple);

			for (int i = 1; i < single.size(); ++i) {
				allTuples.add(NTuple.newWithSharedWeights(nTuple, single.get(i)));
			}
		}
		return new NTuples(mainTuples, allTuples);
	}

	/** Creates a n-tuples system where each tuple from tuples is expanded by expander */
	public NTuples(List<NTuple> tuples, SymmetryExpander expander) {
		this.mainNTuples = new ArrayList<>(tuples);
		this.allNTuples = new ArrayList<>();
		for (NTuple mainTuple : mainNTuples) {
			List<NTuple> symmetric = createSymmetric(mainTuple, expander);
			assert symmetric.get(0).equals(mainTuple);
			allNTuples.addAll(symmetric);
		}
		this.symmetryExpander = expander;
	}

	/** The returned NTuples does have an identity symmetry expander */
	public NTuples add(NTuples other) {
		return new NTuples(concat(getAll(), other.getAll()));
	}

	public List<NTuple> getMain() {
		return mainNTuples;
	}

	public List<NTuple> getAll() {
		return allNTuples;
	}

	public NTuple getTuple(int idx) {
		return allNTuples.get(idx);
	}

	public int totalWeights() {
		int len = 0;
		for (NTuple tuple : mainNTuples) {
			len += tuple.getNumWeights();
		}
		return len;
	}

	public float[] weights() {
		float[] weights = new float[totalWeights()];
		int pos = 0;
		for (NTuple tuple : mainNTuples) {
			System.arraycopy(tuple.getWeights(), 0, weights, pos, tuple.getNumWeights());
			pos += tuple.getNumWeights();
		}
		return weights;
	}

	public SymmetryExpander getSymmetryExpander() {
		return symmetryExpander;
	}

	@Override
	public Iterator<NTuple> iterator() {
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
		NTuples other = (NTuples) obj;
		return allNTuples.equals(other.allNTuples);
	}

	@Override
	public String toString() {
		return "NTuples [mainNTuples=" + mainNTuples + ", symmetryExpander=" + symmetryExpander + "]";
	}

	// TODO: This a 2048 HACK
	@Override
	@Deprecated
	public double getValue(double[] input) {
		BoardNTupleEvaluator evaluator = new BoardNTupleEvaluator();

		return evaluator.evaluate(this, new Game2048Board(input));
	}

	// TODO: This a 2048 HACK and should be moved out of here
	@Override
	@Deprecated
	public void update(double[] input, double expectedValue, double learningRate) {
		BoardNTupleEvaluator evaluator = new BoardNTupleEvaluator();

		double val = evaluator.evaluate(this, new Game2048Board(input));

		double error = expectedValue - val;

		double delta = error * learningRate;

		Game2048Board board = new Game2048Board(input);
		for (NTuple tuple : getAll()) {
			tuple.increaseValue(board, (float)delta);
		}
	}
}
