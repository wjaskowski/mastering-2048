package put.ci.cevo.experiments.wpc.othello.mappers;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.evaluators.OthelloRealFunctionMoveEvaluator;
import put.ci.cevo.games.othello.players.OthelloMoveEvaluatorPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.player.MoveEvaluatorPlayer;
import put.ci.cevo.rl.agent.functions.mlp.MLP;

public class MLPOthelloPlayerMapper implements GenotypePhenotypeMapper<MLP, OthelloPlayer> {

	private BoardEvaluationType boardEvaluationType;

	public MLPOthelloPlayerMapper() {
		this(BoardEvaluationType.OUTPUT_NEGATION);
	}

	public MLPOthelloPlayerMapper(BoardEvaluationType boardEvaluationType) {
		this.boardEvaluationType = boardEvaluationType;
	}

	@Override
	public OthelloPlayer getPhenotype(MLP genotype, RandomDataGenerator random) {
		return new OthelloMoveEvaluatorPlayer(new MoveEvaluatorPlayer<>(
			new OthelloRealFunctionMoveEvaluator(genotype), boardEvaluationType));
	}
}
