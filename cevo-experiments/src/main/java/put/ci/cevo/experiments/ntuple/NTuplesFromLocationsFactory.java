package put.ci.cevo.experiments.ntuple;

import java.util.List;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;

public class NTuplesFromLocationsFactory implements IndividualFactory<NTuples> {

	private final double maxWeight;
	private final double minWeight;
	private final int numValues;
	private final List<List<int[]>> locations;

	// TODO: Better name for BoardPosList and positionsList

	public NTuplesFromLocationsFactory(List<List<int[]>> locations, int numValues, double minWeight, double maxWeight) {
		Preconditions.checkArgument(locations.size() > 0);
		this.locations = locations;
		this.numValues = numValues;
		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
	}

	@Override
	public NTuples createRandomIndividual(RandomDataGenerator random) {
		return NTuples.createWithRandomWeights(locations, numValues, minWeight, maxWeight, random);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}