package put.ci.cevo.experiments.ntuple;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.board.BoardPos;
import put.ci.cevo.games.board.BoardPosList;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;

/**
 * Generates all straight (horizontal, vertical and diagonal)
 */
public class NTuplesAllStraightFactory implements IndividualFactory<NTuples> {

	private final NTuplesGeneralSystematicFactory genericFactory;

	/** All 1-D horizontal tuples */
	public NTuplesAllStraightFactory(int tupleLength, RectSize boardSize, int numValues, double minWeight,
			double maxWeight, SymmetryExpander expander) {
		BoardPos[][] positions = new BoardPos[4][tupleLength];
		for (int i = 0; i < tupleLength; ++i) {
			positions[0][i] = new BoardPos(i, 0);
			positions[1][i] = new BoardPos(0, i);
			positions[2][i] = new BoardPos(i, i);
			positions[3][i] = new BoardPos(tupleLength - i - 1, i);
		}
		BoardPosList[] list = new BoardPosList[] { new BoardPosList(positions[0]), new BoardPosList(positions[1]),
				new BoardPosList(positions[2]), new BoardPosList(positions[3]) };
		genericFactory = new NTuplesGeneralSystematicFactory(list, boardSize, numValues, minWeight, maxWeight, expander);
	}

	@Override
	public NTuples createRandomIndividual(RandomDataGenerator random) {
		return genericFactory.createRandomIndividual(random);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
