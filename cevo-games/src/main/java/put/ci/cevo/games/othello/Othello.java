package put.ci.cevo.games.othello;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntDoubleLinkedSet;
import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.MorePointsGameResultEvaluator;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardGame;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.util.RandomUtils;

public final class Othello implements BoardGame<OthelloPlayer, OthelloPlayer, OthelloState> {

	static final GameResultEvaluator DEFAULT_GAME_RESULT_EVALUATOR = new MorePointsGameResultEvaluator(1, 0, 0.5);
	static final double DEFAULT_RANDOM_MOVE_PROBABILITY = 0.0;
	static final int DEFAULT_RANDOM_MOVE_MAX_TURNS = Integer.MAX_VALUE;
	static final int NULL_MOVE = Integer.MIN_VALUE;

	private final GameResultEvaluator boardEvaluator;
	private final double[] randomMoveProbability;
	private final int randomMoveMaxTurns;

	public Othello() {
		this(DEFAULT_GAME_RESULT_EVALUATOR);
	}

	public Othello(GameResultEvaluator boardEvaluator) {
		this(boardEvaluator, DEFAULT_RANDOM_MOVE_PROBABILITY, DEFAULT_RANDOM_MOVE_PROBABILITY,
				DEFAULT_RANDOM_MOVE_MAX_TURNS);
	}

	/**
	 * @param randomMoveProbability random move can happen to both players
	 */
	public Othello(double randomMoveProbability) {
		this(randomMoveProbability, DEFAULT_RANDOM_MOVE_MAX_TURNS);
	}

	public Othello(double randomMoveProbability, int randomMoveMaxTurns) {
		this(DEFAULT_GAME_RESULT_EVALUATOR, randomMoveProbability, randomMoveProbability, randomMoveMaxTurns);
	}

	public Othello(double randomMoveProbabilityForBlack, double randomMoveProbabilityForWhite) {
		this(randomMoveProbabilityForBlack, randomMoveProbabilityForWhite, DEFAULT_RANDOM_MOVE_MAX_TURNS);
	}

	public Othello(double randomMoveProbabilityForBlack, double randomMoveProbabilityForWhite, int randomMoveMaxTurns) {
		this(DEFAULT_GAME_RESULT_EVALUATOR, randomMoveProbabilityForBlack, randomMoveProbabilityForWhite,
				randomMoveMaxTurns);
	}

	public Othello(GameResultEvaluator boardEvaluator, double randomMoveProbabilityForBlack,
			double randomMoveProbabilityForWhite) {
		this(boardEvaluator, randomMoveProbabilityForBlack, randomMoveProbabilityForWhite,
				DEFAULT_RANDOM_MOVE_MAX_TURNS);
	}

	/**
	 * @param randomMoveMaxTurns Random moves are forced only if the ply <= randomMoveMaxPlies
	 */
	public Othello(GameResultEvaluator boardEvaluator, double randomMoveProbabilityForBlack,
			double randomMoveProbabilityForWhite, int randomMoveMaxTurns) {
		Preconditions.checkArgument(0 <= randomMoveProbabilityForBlack && randomMoveProbabilityForBlack <= 1);
		Preconditions.checkArgument(0 <= randomMoveProbabilityForWhite && randomMoveProbabilityForWhite <= 1);
		this.boardEvaluator = boardEvaluator;
		this.randomMoveProbability = new double[] { randomMoveProbabilityForBlack, randomMoveProbabilityForWhite };
		this.randomMoveMaxTurns = randomMoveMaxTurns;
	}

	public GameResultEvaluator getBoardEvaluator() {
		return boardEvaluator;
	}

	@Override
	public GameOutcome play(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, RandomDataGenerator random) {
		return play(blackPlayer, whitePlayer, new OthelloState(new OthelloBoard(), Board.BLACK), random);
	}

	/**
	 * Play a game from initialState
	 */
	@Override
	public GameOutcome play(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, OthelloState initialState,
			RandomDataGenerator random) {

		OthelloBoard finalBoard = playImpl(blackPlayer, whitePlayer, initialState.getBoard(),
			initialState.getPlayerToMove(), random);
		final int blackPieces = BoardUtils.countPieces(finalBoard, Board.BLACK);
		final int whitePieces = BoardUtils.countPieces(finalBoard, Board.WHITE);
		return getBoardEvaluator().evaluate(blackPieces, whitePieces);
	}

	OthelloBoard playImpl(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, RandomDataGenerator random) {
		return playImpl(blackPlayer, whitePlayer, new OthelloBoard(), OthelloBoard.BLACK, random);
	}

	/**
	 * Play a game and return OthelloBoard
	 */
	OthelloBoard playImpl(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, OthelloBoard initialBoard,
			int playerToMove, RandomDataGenerator random) {
		Preconditions.checkArgument(playerToMove == Board.BLACK || playerToMove == Board.WHITE);

		OthelloBoard board = initialBoard.clone();

		IntDoubleLinkedSet possibleMoves = getPossibleMoves(board);

		final OthelloPlayer[] players = playerToMove == Board.BLACK ? new OthelloPlayer[] { blackPlayer, whitePlayer }
			: new OthelloPlayer[] { whitePlayer, blackPlayer };
		final int[] playerColors = new int[] { playerToMove, OthelloBoard.opponent(playerToMove) };

		boolean aMoveWasPossible;
		int turn = 0;
		do {
			aMoveWasPossible = false;
			for (int p = 0; p < players.length; p++) {
				OthelloPlayer player = players[p];
				int playerColor = playerColors[p];

				int[] validMoves = getValidMoves(board, playerColor, possibleMoves);
				if (validMoves.length == 0) {
					continue;
				}
				// There is no need to check whether randomMoveProbability > 0.0, but I do it for regression tests
				boolean forceRandomMove = (turn < randomMoveMaxTurns && 0.0 < randomMoveProbability[p] &&
						RandomUtils.nextUniform(0.0, 1.0, random) < randomMoveProbability[p]);
				int move = forceRandomMove ? RandomUtils.pickRandom(validMoves, random)
					                  : player.getMove(board, playerColor, validMoves, random);
				board.makeMove(move, playerColor);
				aMoveWasPossible = true;
				updatePossibleMoves(possibleMoves, board, move);
			}
			turn += 1;
		} while (aMoveWasPossible);

		return board;
	}

	/**
	 * Get a superset of valid moves for a given board
	 */
	private static IntDoubleLinkedSet getPossibleMoves(OthelloBoard board) {
		IntDoubleLinkedSet moves = new IntDoubleLinkedSet(OthelloBoard.BUFFER_SIZE, OthelloBoard.BUFFER_SIZE);
		for (int pos = 0; pos < board.buffer.length; pos++) {
			if (!board.isEmpty(pos)) {
				continue;
			}
			for (int dir : OthelloBoard.DIRS) {
				int neighbour = pos + dir;
				if (!board.isEmpty(neighbour) && board.isInBoard(neighbour)) {
					moves.add(pos);
					break;
				}
			}
		}
		return moves;
	}

	/*
	 * Update the list of possible moves by taking into account the lastMove made
	 */
	private static void updatePossibleMoves(IntDoubleLinkedSet moves, OthelloBoard board, int lastMove) {
		if (lastMove == NULL_MOVE) {
			return;
		}
		moves.remove(lastMove);

		for (int dir : OthelloBoard.DIRS) {
			int neighbour = lastMove + dir;
			if (board.isEmpty(neighbour)) {
				moves.add(neighbour);
			}
		}
	}

	private static int[] getValidMoves(OthelloBoard board, int color, IntDoubleLinkedSet possibleMoves) {
		IntArrayList validMoves = new IntArrayList(possibleMoves.size());
		for (int i = 0; i < possibleMoves.size(); ++i) {
			if (board.isValidMove(possibleMoves.dense[i], color)) {
				validMoves.add(possibleMoves.dense[i]);
			}
		}
		return validMoves.toArray();
	}
}
