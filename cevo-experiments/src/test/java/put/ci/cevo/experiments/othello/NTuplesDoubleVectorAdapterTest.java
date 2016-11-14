package put.ci.cevo.experiments.othello;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.experiments.ntuple.NTuplesDoubleVectorAdapter;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.util.vectors.DoubleVector;
import put.ci.cevo.games.encodings.ntuple.NTupleRandomFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.NTuples.Builder;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;

public class NTuplesDoubleVectorAdapterTest {

	@Test
	public final void testFromNTuples() throws Exception {
		RectSize boardSize = new RectSize(3);
		NTupleRandomFactory factory = new NTupleRandomFactory(3, 5, boardSize, -1.0, 1.0);
		Builder builder = new NTuples.Builder(new RotationMirrorSymmetryExpander(boardSize));
		RandomDataGenerator random = new RandomDataGenerator();
		for (int i = 0; i < 2; ++i) {
			builder.add(factory.create(random));
		}
		NTuples original = builder.build();

		NTuplesDoubleVectorAdapter adapter = new NTuplesDoubleVectorAdapter();

		DoubleVector vector = adapter.from(original);
		NTuples converted = adapter.from(vector, original);

		Assert.assertEquals(converted, original);

		DoubleVector onceagain = adapter.from(original);
		Assert.assertEquals(vector, onceagain);
	}
}
