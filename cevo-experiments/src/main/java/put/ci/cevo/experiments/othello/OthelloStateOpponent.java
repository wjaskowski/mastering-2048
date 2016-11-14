package put.ci.cevo.experiments.othello;

import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.OthelloPlayer;

public final class OthelloStateOpponent {
	private final OthelloState state;
	private final OthelloPlayer opponent;

	public OthelloStateOpponent(OthelloState state, OthelloPlayer opponent) {
		this.state = state;
		this.opponent = opponent;
	}

	public OthelloState state() {
		return state;
	}

	public OthelloPlayer opponent() {
		return opponent;
	}
}
