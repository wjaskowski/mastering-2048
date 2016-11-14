package put.ci.cevo.experiments.othello;

import put.ci.cevo.experiments.wpc.othello.mappers.WPCOthelloPlayerMapper;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.MorePointsGameResultEvaluator;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * @deprecated Use {@link put.ci.cevo.experiments.othello.OthelloInteractionDomain} instead
 */

@Deprecated
public class OthelloWPCInteraction extends OthelloInteraction<WPC, WPC> {

	@AccessedViaReflection
	public OthelloWPCInteraction() {
		this(new MorePointsGameResultEvaluator(1, 0, 0.5), true);
	}

	@AccessedViaReflection
	public OthelloWPCInteraction(boolean playBoth) {
		this(new MorePointsGameResultEvaluator(1, 0, 0.5), playBoth);
	}

	@AccessedViaReflection
	public OthelloWPCInteraction(GameResultEvaluator board, boolean playBoth) {
		super(new WPCOthelloPlayerMapper(), new WPCOthelloPlayerMapper(), playBoth, board);
	}
}
