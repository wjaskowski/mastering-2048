package put.ci.cevo.experiments.wpc;

import static com.google.common.base.Objects.toStringHelper;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import com.google.common.base.Preconditions;

public final class WPCIndividualFactory implements IndividualFactory<WPC> {

	private final int wpcLength;

	private final double minWeight;
	private final double maxWeight;

	@AccessedViaReflection
	public WPCIndividualFactory(int wpcLength, double minWeight, double maxWeight) {
		Preconditions.checkArgument(minWeight < maxWeight, "maxWeight must be greater than minWeight!");

		this.wpcLength = wpcLength;
		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
	}

	@Override
	public WPC createRandomIndividual(RandomDataGenerator random) {
		return new WPC(RandomUtils.randomDoubleVector(wpcLength, minWeight, maxWeight, random));
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("wpcLength", wpcLength).add("minWeight", minWeight).add("maxWeight", maxWeight)
			.toString();
	}
}
