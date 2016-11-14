package put.ci.cevo.experiments.wpc;

import static com.google.common.base.Objects.toStringHelper;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.rl.agent.functions.wpc.Symmetric8x8WPC;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import com.google.common.base.Preconditions;

public final class SymmetricWPCIndividualFactory implements IndividualFactory<Symmetric8x8WPC> {

	private final double minWeight;
	private final double maxWeight;

	@AccessedViaReflection
	public SymmetricWPCIndividualFactory(double minWeight, double maxWeight) {
		Preconditions.checkArgument(minWeight < maxWeight, "maxWeight must be greater than minWeight!");

		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
	}

	@Override
	public Symmetric8x8WPC createRandomIndividual(RandomDataGenerator random) {
		return new Symmetric8x8WPC(RandomUtils.randomDoubleVector(Symmetric8x8WPC.NUM_WEIGHTS, minWeight, maxWeight,
			random));
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("minWeight", minWeight).add("maxWeight", maxWeight).toString();
	}
}
