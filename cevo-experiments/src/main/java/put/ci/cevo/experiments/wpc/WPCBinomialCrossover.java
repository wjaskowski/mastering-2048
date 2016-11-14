package put.ci.cevo.experiments.wpc;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.crossover.BinomialCrossover;
import put.ci.cevo.framework.operators.crossover.CrossoverAdapter;
import put.ci.cevo.framework.operators.crossover.CrossoverOperator;
import put.ci.cevo.util.vectors.DoubleVector;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class WPCBinomialCrossover implements CrossoverOperator<WPC> {

	private final CrossoverAdapter<WPC, DoubleVector> crossover;

	@AccessedViaReflection
	public WPCBinomialCrossover(double rate) {
		this.crossover = new CrossoverAdapter<>(new BinomialCrossover(rate), new WPCDoubleVectorAdapter());
	}

	@Override
	public WPC produce(Pair<WPC, WPC> individuals, RandomDataGenerator random) {
		return crossover.produce(individuals, random);
	}

}
