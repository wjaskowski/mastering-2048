package put.ci.cevo.games.game2048;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

public class Tiling2048Test {

	@Test
	public void testAddress() throws Exception {
		Tiling2048 tiling2048 = Tiling2048.createWithRandomWeights(1, 15, new int[] { 7, 8, 9, 10, 15, 16 }, 16,
				0, 0, new RandomDataGenerator(new MersenneTwister(123)));

		Assert.assertEquals(0, tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 })));

		Assert.assertEquals(0, tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 })));

		Assert.assertEquals(0, tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14 })));

		Assert.assertEquals(0, tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 14, 12, 14 })));

		Assert.assertEquals(1<<24, tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15 })));

		Assert.assertEquals(1, tiling2048.address(new State2048(
				new double[] { 0,0,0,0, 0,0,0,1, 0,0,0,0, 0,0,0,0 })));

		Assert.assertEquals(16+1, tiling2048.address(new State2048(
				new double[] { 0,0,0,0, 0,0,1,1, 0,0,0,0, 0,0,0,0 })));

		Assert.assertEquals((1<<20)+16+1, tiling2048.address(new State2048(
				new double[] { 1,0,0,0, 0,0,1,1, 0,0,0,0, 0,0,0,0 })));

		Assert.assertEquals(3*(1<<20)+2*16+1, tiling2048.address(new State2048(
				new double[] { 3,0,0,0, 0,0,2,1, 0,0,0,0, 0,0,0,0 })));

		Assert.assertEquals((1<<24) + 3*(1<<20)+2*16+1, tiling2048.address(new State2048(
				new double[] { 3,0,0,0, 0,0,2,1, 0,0,0,0, 0,0,15,0 })));

		Assert.assertEquals((1<<24) + 3*(1<<20)+15*16+1, tiling2048.address(new State2048(
				new double[] { 3,0,0,0, 0,0,15,1, 0,0,0,0, 0,0,15,0 })));

		Assert.assertEquals((1<<24) + 3*(1<<20)+15*16+1, tiling2048.address(new State2048(
				new double[] { 3,0,0,0, 0,0,15,1, 0,0,0,0, 0,0,0,0 })));
	}

	@Test
	public void testAddress2() throws Exception {
		Tiling2048 tiling2048 = Tiling2048.createWithRandomWeights(2, 15, new int[] { 7, 8, 9, 10, 15, 16 }, 16,
				0, 0, new RandomDataGenerator(new MersenneTwister(123)));

		Assert.assertEquals(0, tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 })));

		Assert.assertEquals(0, tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 })));

		Assert.assertEquals((1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14 })));

		Assert.assertEquals((1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 14 })));

		Assert.assertEquals(2*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0})));

		Assert.assertEquals(3*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 14 })));

		Assert.assertEquals(3*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 15, 14 })));

		Assert.assertEquals(3*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 15, 0 })));
	}

	@Test
	public void testAddress3() throws Exception {
		Tiling2048 tiling2048 = Tiling2048.createWithRandomWeights(3, 15, new int[] { 7, 8, 9, 10, 15, 16 }, 16,
				0, 0, new RandomDataGenerator(new MersenneTwister(123)));

		Assert.assertEquals(0, tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 })));

		Assert.assertEquals(0, tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 })));

		Assert.assertEquals((1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13 })));

		Assert.assertEquals((1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 13 })));

		Assert.assertEquals(2*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0, 0})));

		Assert.assertEquals(3*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 13 })));

		Assert.assertEquals(4*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 0 })));

		Assert.assertEquals(5*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 13, 0 })));

		Assert.assertEquals(6*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 15, 0, 0 })));

		Assert.assertEquals(7*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 0, 14, 15, 0, 0 })));

		Assert.assertEquals(3*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0, 14, 14, 0, 0 })));

		Assert.assertEquals(7*(1<<24), tiling2048.address(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 15, 15, 0, 0 })));
	}

	@Test
	public void testAddressSpecial() throws Exception {
		Tiling2048 tiling2048 = Tiling2048.createWithRandomWeights(3, 15, new int[] { 7, 8, 9, 10, 15, 16 }, 16,
				0, 0, new RandomDataGenerator(new MersenneTwister(123)));

		Assert.assertEquals(0, tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 })));

		Assert.assertEquals(0, tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 })));

		Assert.assertEquals(0, tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13 })));

		Assert.assertEquals(0, tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 13 })));

		Assert.assertEquals((1<<24), tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0, 0})));

		Assert.assertEquals(2*(1<<24), tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 13 })));

		Assert.assertEquals(3*(1<<24), tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 0 })));

		Assert.assertEquals(4*(1<<24), tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 13, 0 })));

		Assert.assertEquals(5*(1<<24), tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 15, 0, 0 })));

		Assert.assertEquals(6*(1<<24), tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 0, 14, 15, 0, 0 })));

		Assert.assertEquals(2*(1<<24), tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0, 14, 14, 0, 0 })));

		Assert.assertEquals(6*(1<<24), tiling2048.addressSpecial(new State2048(
				new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 15, 15, 0, 0 })));
	}
}