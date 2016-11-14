package put.ci.cevo.games.encodings.ntuple;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

public class NTupleTest {

	@Test
	public void testEquals() {
		NTuple tuple1 = NTuple.newWithRandomWeights(3, new int[] { 9, 14, 13, 19 }, -1, +1, new RandomDataGenerator());
		Assert.assertTrue(tuple1.equals(new NTuple(tuple1)));
		NTuple tuple2 = NTuple.newWithSharedWeights(tuple1, new int[] { 9, 14, 13, 19 });
		Assert.assertTrue(tuple1.equals(new NTuple(tuple2)));
	}
}
