package put.ci.cevo.games.encodings.ntuple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import put.ci.cevo.games.board.BoardPosList;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;

public class NTuplesLocationsGeneralSystematicSupplier implements Supplier<List<List<int[]>>> {

	private final RectSize boardSize;
	private final SymmetryExpander expander;
	private final BoardPosList[] positionsList;
	private final boolean removeSubtuples;

	// TODO: Better name for BoardPosList and positionsList

	/**
	 * A shortcut. Required for the configuration
	 *
	 * @param patterns For example, "01|01; 1|1|1|1; 11|01"
	 */
	public NTuplesLocationsGeneralSystematicSupplier(String patterns, RectSize boardSize,
			SymmetryExpander symmetryExpander, boolean removeSubtuples) {
		this(patterns.replaceAll(" ", "").split(";"), boardSize, symmetryExpander, removeSubtuples);
	}

	NTuplesLocationsGeneralSystematicSupplier(BoardPosList[] positionsList, RectSize boardSize,
			SymmetryExpander expander, boolean removeSubtuples) {
		Preconditions.checkArgument(positionsList.length > 0);
		this.positionsList = positionsList;
		this.expander = expander;
		this.boardSize = boardSize;
		this.removeSubtuples = removeSubtuples;
	}

	NTuplesLocationsGeneralSystematicSupplier(BoardPosList positions, RectSize boardSize, SymmetryExpander expander,
			boolean removeSubtuples) {
		this(new BoardPosList[] { positions }, boardSize, expander, removeSubtuples);
	}

	/**
	 * @param shapes Shapes in {@link BoardPosList} format. Eg. new String[] {"01|10", "1|1|1|1|", "1111"}
	 */
	private NTuplesLocationsGeneralSystematicSupplier(String[] shapes, RectSize boardSize,
			SymmetryExpander symmetryExpander, boolean removeSubtuples) {
		this(positionsFromShapes(shapes), boardSize, symmetryExpander, removeSubtuples);
	}

	private static BoardPosList[] positionsFromShapes(String[] shapes) {
		List<BoardPosList> positions = new ArrayList<>(shapes.length);
		for (String shape : shapes) {
			positions.add(new BoardPosList(shape));
		}
		return positions.toArray(new BoardPosList[positions.size()]);
	}


	@Override
	public List<List<int[]>> get() {
		NTuplesLocationsBuilder builder = new NTuplesLocationsBuilder(expander, removeSubtuples);

		// Most of them are redundant, but it is general. Its performance could be improved, but this does not seem as a
		// bottleneck
		for (BoardPosList positions : positionsList)
			for (int r = 0; r < boardSize.rows(); ++r)
				for (int c = 0; c < boardSize.columns(); ++c) {
					BoardPosList nextPositions = positions.getAligned().getShifted(r, c);
					if (nextPositions.fitOnBoard(boardSize)) {
						builder.addLocations(nextPositions.toLocations(boardSize));
					}
				}
		return builder.build();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}