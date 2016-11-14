package put.ci.cevo.rl.agent.policies;

import java.util.Arrays;

import org.junit.Test;

public class MySoftMaxAfterstatePolicyTest {

	@Test
	public void testMysoftmax() throws Exception {
		double[] probabilities = MySoftMaxAfterstatePolicy.mysoftmax(new double[] { 10000, 9900, 10000 }, 100);
		System.out.print(Arrays.toString(probabilities));
	}
}