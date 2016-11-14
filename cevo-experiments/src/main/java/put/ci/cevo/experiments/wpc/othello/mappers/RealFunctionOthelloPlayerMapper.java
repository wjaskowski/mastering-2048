package put.ci.cevo.experiments.wpc.othello.mappers;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.evaluators.OthelloRealFunctionMoveEvaluator;
import put.ci.cevo.games.othello.players.OthelloMoveEvaluatorPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.player.MoveEvaluatorPlayer;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class RealFunctionOthelloPlayerMapper implements GenotypePhenotypeMapper<RealFunction, OthelloPlayer> {
	private BoardEvaluationType boardEvaluatorType;

	@AccessedViaReflection
	public RealFunctionOthelloPlayerMapper() {
		this(BoardEvaluationType.OUTPUT_NEGATION);
	}

	@AccessedViaReflection
	public RealFunctionOthelloPlayerMapper(BoardEvaluationType boardEvaluationType) {
		this.boardEvaluatorType = boardEvaluationType;
	}

	@Override
	public OthelloPlayer getPhenotype(RealFunction genotype, RandomDataGenerator random) {
		return new OthelloMoveEvaluatorPlayer(new MoveEvaluatorPlayer<>(
			new OthelloRealFunctionMoveEvaluator(genotype), boardEvaluatorType));
	}

}
