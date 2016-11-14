package put.ci.cevo.games.encodings.ntuple.expanders;

import put.ci.cevo.games.board.BoardUtils;

/**
 * Performs symmetry expansion only along Y axis.
 */
public class YAxisSymmetryExpander implements SymmetryExpander {

	private final int boardWidth;

	public YAxisSymmetryExpander(int boardWidth) {
		this.boardWidth = boardWidth;
	}

	@Override
	public int[] getSymmetries(int location) {
		int M = boardWidth - 1;

		int c = BoardUtils.colFromPos(location, boardWidth);
		int r = BoardUtils.rowFromPos(location, boardWidth);

		// @formatter:off
		int[] a = new int[] {
			flat(c, r),			// This must be first according to the contract
			flat(M - c, r),
		};
		// @formatter:on

		assert a.length == numSymmetries();

		return a;
	}

	public int flat(int c, int r) {
		return BoardUtils.toMarginPos(boardWidth, r, c);
	}

	@Override
	public int numSymmetries() {
		return 2;
	}

	public int boardWidth() {
		return boardWidth;
	}

}
