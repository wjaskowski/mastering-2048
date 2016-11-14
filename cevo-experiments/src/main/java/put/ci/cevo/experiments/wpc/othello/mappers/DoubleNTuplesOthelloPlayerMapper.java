package put.ci.cevo.experiments.wpc.othello.mappers;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
import put.ci.cevo.games.othello.evaluators.OthelloDoubleNTuplesMoveDeltaEvaluator;
import put.ci.cevo.games.othello.players.OthelloMoveEvaluatorPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.player.MoveEvaluatorPlayer;

public final class DoubleNTuplesOthelloPlayerMapper implements GenotypePhenotypeMapper<DoubleNTuples, OthelloPlayer> {

	@Override
	public OthelloPlayer getPhenotype(DoubleNTuples genotype, RandomDataGenerator random) {
		return new OthelloMoveEvaluatorPlayer(new MoveEvaluatorPlayer<>(
				new OthelloDoubleNTuplesMoveDeltaEvaluator(genotype), BoardEvaluationType.STRAIGHT));
	}
}
