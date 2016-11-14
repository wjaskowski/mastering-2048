package put.ci.cevo.games.connect4.players.adapters;

import static put.ci.cevo.games.board.Board.BLACK;
import static put.ci.cevo.games.board.Board.WHITE;
import static put.ci.cevo.games.board.BoardUtils.countPieces;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.connect4.thill.c4.Agent;
import put.ci.cevo.games.connect4.thill.c4.ConnectFour;
import put.ci.cevo.games.connect4.thill.c4.NotImplementedFakeAgent;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.player.BoardGamePlayer;

/**
 * Adapts {@link Connect4Player} interface to the one of {@link Agent} used in Thill's framework.
 * In this way {@link NTuples} players can be evaluated in Thill's {@link ConnectFour} implementation of the C4 game.
 */
public class Connect4PlayerAgentAdapter extends NotImplementedFakeAgent {

	private final BoardGamePlayer<Connect4Board> player;

	public Connect4PlayerAgentAdapter(BoardGamePlayer<Connect4Board> player) {
		this.player = player;
	}

	@Override
	public int getBestMove(int[][] table, RandomDataGenerator random) {
		Connect4Board board = Connect4Board.fromThillBoard(table);
		return player.getMove(board, countPieces(board) % 2 == 0 ? BLACK : WHITE, board.getValidMoves().toArray(), random);
	}

}
