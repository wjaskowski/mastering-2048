package put.ci.cevo.games.tetris.agents;

import com.google.common.base.Preconditions;
import org.apache.commons.collections15.Factory;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.games.tetris.Tetromino;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.Decision;

import static put.ci.cevo.games.tetris.Tetromino.S;
import static put.ci.cevo.games.tetris.Tetromino.Z;

import java.util.List;

/**
 * Implements hand-coded SZ-Tetris strategy from "SZ-Tetris as a Benchmark for Studying Key Problems of
 * Reinforcement Learning" by Szita and Szepesvari.
 * <p>
 * Watch out: the agent is stateful thus *not* threadsafe!
 */
//TODO: Make it stateless by checking what is the combination in the middle column
public class SzitaSzepesvariSZTetrisAgent implements Factory<Agent<TetrisState, TetrisAction>> {

	@Override
	public Agent<TetrisState, TetrisAction> create() {
		return new ActualAgent();
	}

	private static class ActualAgent implements Agent<TetrisState, TetrisAction> {

		private static final int MID_CHANGE_HEIGHT = 16;

		private Tetromino midColumnType = null;

		@Override
		public Decision<TetrisAction> chooseAction(TetrisState state, List<TetrisAction> availableActions,
				RandomDataGenerator random) {
			Preconditions.checkArgument(state.getTetromino() == S || state.getTetromino() == Z);

			final Tetromino tetromino = state.getTetromino();

			// middle column is initialized to the type of the first tetromino
			if (midColumnType == null) {
				midColumnType = tetromino;
			}

			// board is divided into 5 two-blocks-wide columns, here we compute their heights
			final int[] columnHeights = getColumnHeights(state);

			// determines column to place the next tetromino
			int col = 0;

			// S tetromino is placed in column 0 or 1
			if (tetromino == S) {
				col = columnHeights[0] <= columnHeights[1] ? 0 : 1;
				if (midColumnType == S) {
					col = columnHeights[col] <= columnHeights[2] ? col : 2;
				} else {
					if (columnHeights[col] > MID_CHANGE_HEIGHT) {
						midColumnType = S;
						col = 2;
					}
				}
			}

			// S tetromino is placed in column 3 or 4
			if (tetromino == Z) {
				col = columnHeights[3] <= columnHeights[4] ? 3 : 4;
				if (midColumnType == Z) {
					col = columnHeights[col] <= columnHeights[2] ? col : 2;
				} else {
					if (columnHeights[col] > MID_CHANGE_HEIGHT) {
						midColumnType = Z;
						col = 2;
					}
				}
			}

			// pieces are are placed vertically on the board hence rot = 1 and a column is rescaled
			return Decision.of(new TetrisAction(col * 2, 1));
		}

		private int[] getColumnHeights(TetrisState state) {
			final int[] heights = new int[5];
			final int[] skyline = state.getBoard().getSkyline();
			for (int i = 0; i < heights.length; i++) {
				int ptr = i * 2;
				heights[i] = Math.max(skyline[ptr], skyline[ptr + 1]);
			}
			return heights;
		}
	}
}
