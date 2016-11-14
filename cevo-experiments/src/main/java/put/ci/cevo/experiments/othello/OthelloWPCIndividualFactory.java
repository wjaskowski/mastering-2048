package put.ci.cevo.experiments.othello;

import static com.google.common.base.Objects.toStringHelper;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class OthelloWPCIndividualFactory implements IndividualFactory<WPC> {

	private final WPCIndividualFactory factory;
	private final double minWeight;
	private final double maxWeight;

	@AccessedViaReflection
	public OthelloWPCIndividualFactory() {
		this(-1.0, 1.0);
	}

	@AccessedViaReflection
	public OthelloWPCIndividualFactory(double minWeight, double maxWeight) {
		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
		this.factory = new WPCIndividualFactory(OthelloBoard.NUM_FIELDS, minWeight, maxWeight);
	}

	@Override
	public WPC createRandomIndividual(RandomDataGenerator random) {
		return factory.createRandomIndividual(random);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("minWeight", minWeight).add("maxWeight", maxWeight).toString();
	}
}
