package put.ci.cevo.games.tetris.agents;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntDoubleLinkedSet;
import com.carrotsearch.hppc.cursors.IntCursor;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTupleUtils;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.tetris.Tetris;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisBoard;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.rl.evaluation.ActionValueFunction;

public class DeltaNTuplesTetrisActionValueFunction implements ActionValueFunction<TetrisState, TetrisAction> {

	private final NTuples tuples;
	private final boolean avoidTerminalStates;
	private final IntArrayList[] tuplesForPosition;

	public DeltaNTuplesTetrisActionValueFunction(NTuples ntuples) {
		this(ntuples, false, TetrisBoard.DEFAULT_BOARD_SIZE);
	}

	public DeltaNTuplesTetrisActionValueFunction(NTuples ntuples, boolean avoidTerminalStates) {
		this(ntuples, avoidTerminalStates, TetrisBoard.DEFAULT_BOARD_SIZE);
	}

	public DeltaNTuplesTetrisActionValueFunction(NTuples ntuples, boolean avoidTerminalStates, RectSize boardSize) {
		this.tuples = ntuples;
		this.avoidTerminalStates = avoidTerminalStates;
		this.tuplesForPosition = NTupleUtils.getTuplesForPositions(ntuples,
				(boardSize.columns() + BoardUtils.TOTAL_MARGIN) * (boardSize.rows() + BoardUtils.TOTAL_MARGIN));
	}

	@Override
	public double getValue(TetrisState state, TetrisAction action) {
		TetrisBoard.SimulationResult simulationResult = Tetris.simulateAction(state, action);
		IntArrayList changedPositions = simulationResult.changedPositions;

		if (avoidTerminalStates && simulationResult.isTerminal)
			return -Double.MAX_VALUE;

		double eval = 0.0;

		IntDoubleLinkedSet deltaTuples = new IntDoubleLinkedSet(TetrisBoard.DEFAULT_BOARD_SIZE.area() / 4, tuples.size());

		// DeltaTuples contains all tuples which value changes when making move
		for (int i = 0; i < changedPositions.size(); ++i) {
			int pos = changedPositions.buffer[i];
			for (IntCursor cur : tuplesForPosition[pos]) {
				deltaTuples.add(cur.value);
			}
		}

		// From eval remove values for tuples that will change
		for (int i = 0; i < deltaTuples.elementsCount; ++i) {
			NTuple tuple = tuples.getTuple(deltaTuples.dense[i]);
			eval -= tuple.getValue(state.getBoard());
		}

		// Make the action...
		Tetris.swapValues(state, changedPositions);

		// Add to eval new values for tuples that changed
		for (int i = 0; i < deltaTuples.elementsCount; ++i) {
			NTuple tuple = tuples.getTuple(deltaTuples.dense[i]);
			eval += tuple.getValue(state.getBoard());
		}

		// ... and revert it
		Tetris.swapValues(state, changedPositions);

		return eval + simulationResult.reward;
	}
}
