package put.ci.cevo.experiments.othello;

import put.ci.cevo.experiments.wpc.othello.mappers.NTuplesOthelloPlayerMapper;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.MorePointsGameResultEvaluator;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * @deprecated Works on genotypes. Use {@link put.ci.cevo.experiments.othello.OthelloInteractionDomain}
 */
@Deprecated
public class OthelloNTuplesInteraction extends OthelloInteraction<NTuples, NTuples> {

	@AccessedViaReflection
	public OthelloNTuplesInteraction(boolean playBoth) {
		this(new MorePointsGameResultEvaluator(1, 0, 0.5), playBoth);
	}

	@AccessedViaReflection
	public OthelloNTuplesInteraction(GameResultEvaluator board, boolean playBoth) {
		super(new NTuplesOthelloPlayerMapper(), new NTuplesOthelloPlayerMapper(), playBoth, board);
	}
}
