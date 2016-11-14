package put.ci.cevo.experiments.wpc.othello.mappers;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.othello.evaluators.OthelloNTuplesMoveDeltaEvaluator;
import put.ci.cevo.games.othello.players.OthelloMoveEvaluatorPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.player.MoveEvaluatorPlayer;

public final class NTuplesOthelloPlayerMapper implements GenotypePhenotypeMapper<NTuples, OthelloPlayer> {

	private final BoardEvaluationType boardEvaluationType;

	public NTuplesOthelloPlayerMapper() {
		this(BoardEvaluationType.OUTPUT_NEGATION);
	}

	public NTuplesOthelloPlayerMapper(BoardEvaluationType boardEvaluationType) {
		this.boardEvaluationType = boardEvaluationType;
	}

	@Override
	public OthelloPlayer getPhenotype(NTuples genotype, RandomDataGenerator random) {
		return new OthelloMoveEvaluatorPlayer(new MoveEvaluatorPlayer<>(
			new OthelloNTuplesMoveDeltaEvaluator(genotype), boardEvaluationType));
	}
}
