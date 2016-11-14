package put.ci.cevo.experiments.wpc.othello.mappers;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class WPCOthelloPlayerMapper implements GenotypePhenotypeMapper<WPC, OthelloPlayer> {

	private final BoardEvaluationType boardEvaluationType;

	@AccessedViaReflection
	public WPCOthelloPlayerMapper() {
		this(BoardEvaluationType.OUTPUT_NEGATION);
	}

	@AccessedViaReflection
	public WPCOthelloPlayerMapper(BoardEvaluationType boardEvaluationType) {
		this.boardEvaluationType = boardEvaluationType;
	}

	@Override
	public OthelloWPCPlayer getPhenotype(WPC genotype, RandomDataGenerator random) {
		return new OthelloWPCPlayer(genotype, boardEvaluationType);
	}
}
