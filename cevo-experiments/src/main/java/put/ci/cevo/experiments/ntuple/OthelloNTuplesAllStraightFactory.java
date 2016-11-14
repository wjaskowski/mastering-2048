package put.ci.cevo.experiments.ntuple;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.othello.OthelloBoard;

/**
 * Generates all straight n-tuples of given sizes
 */
public class OthelloNTuplesAllStraightFactory implements IndividualFactory<NTuples> {

	private final NTuplesAllStraightFactory ntuplesFactory;

	public OthelloNTuplesAllStraightFactory(int tupleLength, double minWeight, double maxWeight) {
		this.ntuplesFactory = new NTuplesAllStraightFactory(
			tupleLength, OthelloBoard.SIZE, OthelloBoard.NUM_VALUES, minWeight, maxWeight,
			new RotationMirrorSymmetryExpander(OthelloBoard.SIZE));
	}

	@Override
	public NTuples createRandomIndividual(RandomDataGenerator random) {
		return ntuplesFactory.createRandomIndividual(random);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
