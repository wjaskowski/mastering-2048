package put.ci.cevo.rl.agent.functions.mlp;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import put.ci.cevo.rl.agent.functions.mlp.Neuron.TransferFunction;

public class MLPTest {

	private MLP mlp2;
	private Neuron[] hiddenNeurons;
	private Neuron[] outputNeurons;

	@Before
	public void setUp() {
		hiddenNeurons = new Neuron[] { createLinearNeuron(3), createLinearNeuron(3) };
		outputNeurons = new Neuron[] { createLinearNeuron(3) };
		Layer[] layers = new Layer[] { new Layer(hiddenNeurons), new Layer(outputNeurons) };
		mlp2 = new MLP(layers);
	}

	private Neuron createLinearNeuron(int numWeights) {
		List<Double> weights = Collections.nCopies(numWeights, 1.0);
		return new Neuron(ArrayUtils.toPrimitive(weights.toArray(new Double[0])), new TransferFunction() {
			@Override
			public double transfer(double activation) {
				return activation;
			}

			@Override
			public double derivative(double output) {
				return 1;
			}
		});
	}

	@Test
	public void testPropagateZeros() throws Exception {
		MLP mlp = new MLP(2, 2, 1);
		double[][] output = mlp.propagate(new double[] { 0, 0 });
		Assert.assertArrayEquals(new double[] { 0, 0 }, output[0], 0.001);
		Assert.assertArrayEquals(new double[] { 0 }, output[1], 0.001);

		double[][] output2 = mlp2.propagate(new double[] { 0, 0 });
		Assert.assertArrayEquals(new double[] { 1, 1 }, output2[0], 0.001);
		Assert.assertArrayEquals(new double[] { 3 }, output2[1], 0.001);
	}

	@Test
	public void testPropagateOnes() throws Exception {
		MLP mlp = new MLP(2, 2, 1);
		double[][] output = mlp.propagate(new double[] { 1, 1 });
		Assert.assertArrayEquals(new double[] { 0, 0 }, output[0], 0.001);
		Assert.assertArrayEquals(new double[] { 0 }, output[1], 0.001);

		double[][] output2 = mlp2.propagate(new double[] { 1, 1 });
		Assert.assertArrayEquals(new double[] { 3, 3 }, output2[0], 0.001);
		Assert.assertArrayEquals(new double[] { 7 }, output2[1], 0.001);
	}

	@Test
	public void testBackPropagate() throws Exception {
		MLP mlp = new MLP(2, 2, 1);
		double[][] output = mlp.propagate(new double[] { 1, 1 });
		double[][] errors = mlp.backPropagate(output, 1);
		Assert.assertArrayEquals(new double[] { 0, 0 }, errors[0], 0.001);
		Assert.assertArrayEquals(new double[] { 1 }, errors[1], 0.001);

		double[][] output2 = mlp2.propagate(new double[] { 0, 0 });
		double[][] errors2 = mlp2.backPropagate(output2, 1);
		Assert.assertArrayEquals(new double[] { -2 }, errors2[1], 0.001);
		Assert.assertArrayEquals(new double[] { -2, -2 }, errors2[0], 0.001);
	}

	@Test
	public void testUpdate() throws Exception {
		// mlp2.update(new double[] {1, 1}, 1, 0.1);
		mlp2.updateWeights(mlp2.getWeightUpdates(new double[] { 0, 0 }, 1, 0.1));

		for (int i = 0; i < 3; i++) {
			Assert.assertEquals(0.8, outputNeurons[0].getWeight(i), 0.001);
		}

		Assert.assertEquals(0.8, hiddenNeurons[0].getWeight(0), 0.001);
		Assert.assertEquals(0.8, hiddenNeurons[1].getWeight(0), 0.001);

		Assert.assertEquals(1, hiddenNeurons[0].getWeight(1), 0.001);
		Assert.assertEquals(1, hiddenNeurons[1].getWeight(1), 0.001);
		Assert.assertEquals(1, hiddenNeurons[0].getWeight(2), 0.001);
		Assert.assertEquals(1, hiddenNeurons[1].getWeight(2), 0.001);
	}

	@Test
	public void testGetValueromWeights() throws Exception {
		MLP randomizedMLP = MLP.createRandomizedMLP(mlp2, -1, 1, new RandomDataGenerator());
		double[] weights = randomizedMLP.getWeights();
		MLP copiedMLP = MLP.fromWeights(weights, mlp2);
		Assert.assertArrayEquals(weights, copiedMLP.getWeights(), 0.001);
	}
}
