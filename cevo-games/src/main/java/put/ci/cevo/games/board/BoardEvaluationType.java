package put.ci.cevo.games.board;

public enum BoardEvaluationType {
	/** The first player prefers maximum utility and the second one minimum utility (like in OthelloLeague) */
	OUTPUT_NEGATION,
	/** The board is always seen from the first player's perspective */
	BOARD_INVERSION,
	/** It does change the evaluation score (this is good for a function which work differently
	 * for different player colors */
	STRAIGHT;
}
