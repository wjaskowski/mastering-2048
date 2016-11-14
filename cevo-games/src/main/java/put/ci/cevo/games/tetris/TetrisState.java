package put.ci.cevo.games.tetris;

import com.carrotsearch.hppc.IntArrayList;
import org.apache.commons.lang.NotImplementedException;
import put.ci.cevo.games.board.BoardState;

public final class TetrisState implements BoardState {

	private final TetrisBoard board;
	private final Tetromino tetromino;

	public TetrisState(TetrisBoard board, Tetromino tetromino) {
		this.board = board;
		this.tetromino = tetromino;
	}

	public TetrisState clone() {
		return new TetrisState(board.clone(), tetromino);
	}

	public Tetromino getTetromino() {
		return tetromino;
	}

	public TetrisBoard getBoard() {
		return board;
	}

	@Override
	public double[] getFeatures() {
		//TODO board + tetromino (WJ: I think we don't need this method at all)
		throw new NotImplementedException();
	}

	public int placeTetromino(int position, int rotation) {
		int landingHeight = board.calculateLandingHeight(tetromino, position, rotation);
		IntArrayList changedPositions = board.placeTetromino(tetromino, position, rotation, landingHeight);
		
		if (!isTerminal()) {
			return board.eraseCompleteLines(changedPositions).size();
		}

		return -1;
	}

	public boolean isTerminal() {
		return board.isHeightExceeded();
	}

	@Override
	public String toString() {
		return "Tetromino: " + tetromino.toString() + "\n" + board.toString();
	}

}
