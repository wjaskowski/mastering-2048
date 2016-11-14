package put.ci.cevo.experiments.tetris;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.experiments.ntuple.NTuplesGeneralSystematicFactory;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.board.BoardPosList;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.YAxisSymmetryExpander;
import put.ci.cevo.games.tetris.TetrisBoard;

public class TetrisNTuplesSystematicFactory implements IndividualFactory<NTuples> {

	private final NTuplesGeneralSystematicFactory ntuplesFactory;

	/**
	 * A shortcut. Required for the configuration
	 *
	 * @param patterns For example, "01|01; 1|1|1|1; 11|01"
	 */
	public TetrisNTuplesSystematicFactory(double minWeight, double maxWeight, String patterns) {
		this.ntuplesFactory = new NTuplesGeneralSystematicFactory(
				patterns, new TetrisBoard().getSize(), TetrisBoard.NUM_VALUES, minWeight, maxWeight,
				new YAxisSymmetryExpander(new TetrisBoard().getSize().width()));
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
