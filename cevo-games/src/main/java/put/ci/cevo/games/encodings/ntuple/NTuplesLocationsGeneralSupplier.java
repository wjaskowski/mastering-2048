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

public class NTuplesLocationsGeneralSupplier implements Supplier<List<List<int[]>>> {

	private final RectSize boardSize;
	private final SymmetryExpander expander;
	private final BoardPosList[] positionsList;

	// TODO: Better name for BoardPosList and positionsList

	/**
	 * A shortcut. Required for the configuration
	 *
	 * @param patterns For example, "01|01; 1|1|1|1; 11|01"
	 */
	public NTuplesLocationsGeneralSupplier(String patterns, RectSize boardSize,
			SymmetryExpander symmetryExpander) {
		this(patterns.replaceAll(" ", "").split(";"), boardSize, symmetryExpander);
	}

	NTuplesLocationsGeneralSupplier(BoardPosList[] positionsList, RectSize boardSize,
			SymmetryExpander expander) {
		Preconditions.checkArgument(positionsList.length > 0);
		this.positionsList = positionsList;
		this.expander = expander;
		this.boardSize = boardSize;
	}

	/**
	 * @param shapes Shapes in {@link BoardPosList} format. Eg. new String[] {"01|10", "1|1|1|1|", "1111"}
	 */
	private NTuplesLocationsGeneralSupplier(String[] shapes, RectSize boardSize, SymmetryExpander symmetryExpander) {
		this(positionsFromShapes(shapes), boardSize, symmetryExpander);
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
		NTuplesLocationsBuilder builder = new NTuplesLocationsBuilder(expander, true);

		for (BoardPosList positions : positionsList) {
			builder.addLocations(positions.toLocations(boardSize));
		}
		return builder.build();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}