package put.ci.cevo.experiments.othello;

import put.ci.cevo.experiments.wpc.othello.mappers.RealFunctionOthelloPlayerMapper;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.MorePointsGameResultEvaluator;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class OthelloRealFunctionInteraction extends OthelloInteraction<RealFunction, RealFunction> {

	@AccessedViaReflection
	public OthelloRealFunctionInteraction() {
		this(new MorePointsGameResultEvaluator(1, 0, 0.5), true);
	}

	@AccessedViaReflection
	public OthelloRealFunctionInteraction(boolean playBoth) {
		this(new MorePointsGameResultEvaluator(1, 0, 0.5), playBoth);
	}

	@AccessedViaReflection
	public OthelloRealFunctionInteraction(GameResultEvaluator board, boolean playBoth) {
		super(new RealFunctionOthelloPlayerMapper(), new RealFunctionOthelloPlayerMapper(), playBoth, board);
	}
}
