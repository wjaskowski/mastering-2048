package put.ci.cevo.experiments.ntuple;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.board.BoardPosList;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.bigntuple.BigNTuples;
import put.ci.cevo.games.encodings.bigntuple.BigNTuplesBuilder;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;

/**
 * Generates all n-tuples of given shapes shifted in every possible direction
 */
//TODO: It is doable to decouple generating tuple locations and LUT weights, but such refactoring may involve a bunch of changes 
public class BigNTuplesGeneralSystematicFactory implements IndividualFactory<BigNTuples> {

	private final double maxWeight;
	private final double minWeight;
	private final RectSize boardSize;
	private final int numValues;
	private final SymmetryExpander expander;
	private final BoardPosList[] positionsList;
	private final boolean shiftToGenerateAll;

	public BigNTuplesGeneralSystematicFactory(String patterns, RectSize boardSize, int numValues, double minWeight,
			double maxWeight, SymmetryExpander symmetryExpander) {
		this(patterns, boardSize, numValues, minWeight, maxWeight, symmetryExpander, true);
	}

	/**
	 * A shortcut. Required for the configuration
	 *
	 * @param patterns For example, "01|01; 1|1|1|1; 11|01"
	 * @param shiftToGenerateAll if false than not systematic generation is executed. They are take as is.
	 */
	public BigNTuplesGeneralSystematicFactory(String patterns, RectSize boardSize, int numValues, double minWeight,
			double maxWeight, SymmetryExpander symmetryExpander, boolean shiftToGenerateAll) {
		this(minWeight, maxWeight, patterns.replaceAll(" ", "").split(";"), numValues, boardSize, symmetryExpander,
				shiftToGenerateAll);
	}

	/**
	 * @param shapes Shapes in {@link BoardPosList} format. Eg. new String[] {"01|10",
	 *               "1|1|1|1|", "1111"}
	 */
	private BigNTuplesGeneralSystematicFactory(double minWeight, double maxWeight, String[] shapes, int numValues,
			RectSize boardSize, SymmetryExpander symmetryExpander, boolean shiftToGenerateAll) {
		this(positionsFromShapes(shapes), boardSize, numValues, minWeight, maxWeight, symmetryExpander,
				shiftToGenerateAll);
	}

	private static BoardPosList[] positionsFromShapes(String[] shapes) {
		List<BoardPosList> positions = new ArrayList<>(shapes.length);
		for (String shape : shapes) {
			positions.add(new BoardPosList(shape));
		}
		return positions.toArray(new BoardPosList[positions.size()]);
	}

	private BigNTuplesGeneralSystematicFactory(BoardPosList[] positionsList, RectSize boardSize, int numValues,
			double minWeight, double maxWeight, SymmetryExpander expander, boolean shiftToGenerateAll) {
		Preconditions.checkArgument(positionsList.length > 0);
		this.positionsList = positionsList;
		this.expander = expander;
		this.boardSize = boardSize;
		this.numValues = numValues;
		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
		this.shiftToGenerateAll = shiftToGenerateAll;
	}

	@Override
	public BigNTuples createRandomIndividual(RandomDataGenerator random) {
		BigNTuplesBuilder builder = new BigNTuplesBuilder(numValues, minWeight, maxWeight, expander, random, true);

		if (shiftToGenerateAll) {
			// Most of them are redundant, but it is general. Its performance could be improved, but this does not seem
			// as a bottleneck
			for (BoardPosList positions : positionsList)
				for (int r = 0; r < boardSize.rows(); ++r)
					for (int c = 0; c < boardSize.columns(); ++c) {
						BoardPosList nextPositions = positions.getAligned().getShifted(r, c);
						if (nextPositions.fitOnBoard(boardSize)) {
							builder.addTuple(nextPositions.toLocations(boardSize));
						}
					}
		} else {
			for (BoardPosList positions : positionsList) {
				Preconditions.checkState(positions.fitOnBoard(boardSize));
				builder.addTuple(positions.toLocations(boardSize));
			}
		}
		return builder.buildNTuples();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}