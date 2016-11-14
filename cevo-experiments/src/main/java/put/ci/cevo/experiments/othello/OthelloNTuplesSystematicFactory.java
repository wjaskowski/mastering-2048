package put.ci.cevo.experiments.othello;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.experiments.ntuple.NTuplesGeneralSystematicFactory;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.othello.OthelloBoard;

/**
 * Generates systematic n-tuples for Othello. Subtuples are removed
 */
public class OthelloNTuplesSystematicFactory implements IndividualFactory<NTuples> {

	private final IndividualFactory<NTuples> ntuplesFactory;

	/** A shortcut. Required for the configuration
	 * @param patterns For example, "01|01; 1|1|1|1; 11|01" */
	public OthelloNTuplesSystematicFactory(double minWeight, double maxWeight, String patterns) {
		this.ntuplesFactory = new NTuplesGeneralSystematicFactory(patterns, OthelloBoard.SIZE, OthelloBoard.NUM_VALUES,
				minWeight, maxWeight, new RotationMirrorSymmetryExpander(OthelloBoard.SIZE));
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
