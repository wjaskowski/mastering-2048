package put.ci.cevo.util.vectors;

import org.junit.Assert;
import org.junit.Test;

public class DoubleVectorTest {

	@Test
	public void testNormalize() throws Exception {
		Assert.assertArrayEquals(DoubleVector.of(0.8, 0.6).toArray(), DoubleVector.of(3.2, 2.4).normalize().toArray(),
				1e-6);
	}
}