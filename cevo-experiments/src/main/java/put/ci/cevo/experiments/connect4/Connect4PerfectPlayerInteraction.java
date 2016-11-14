package put.ci.cevo.experiments.connect4;

import static com.google.common.base.Objects.toStringHelper;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.connect4.thill.c4.Agent;
import put.ci.cevo.games.connect4.thill.c4.AlphaBetaAgent;
import put.ci.cevo.games.connect4.thill.c4.ConnectFour;
import put.ci.cevo.games.connect4.thill.c4.competition.Competition;
import put.ci.cevo.games.connect4.thill.c4.competition.ResultCompSingle;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * Performs interaction with perfectly playing alpha-beta player.
 * TODO: should be implemented as performance measure
 */
@Deprecated
public class Connect4PerfectPlayerInteraction<S> implements InteractionDomain<S, AlphaBetaAgent> {

	private final GenotypePhenotypeMapper<S, Agent> playerMapper;

	@AccessedViaReflection
	public Connect4PerfectPlayerInteraction(GenotypePhenotypeMapper<S, Agent> playerMapper) {
		this.playerMapper = playerMapper;
	}

	@Override
	public InteractionResult interact(S candidate, AlphaBetaAgent ab, RandomDataGenerator random) {
		Agent player = playerMapper.getPhenotype(candidate, random);
		Competition compX = new Competition(player, ab, ab);
		ab.randomizeLosses(true);
		ResultCompSingle rcs = compX.compete(42, false, true, random);
		double candidateScore;
		double abScore;
		if (rcs.winner + 1 == ConnectFour.PLAYER1) {
			candidateScore = 1;
			abScore = 0;
		} else if (rcs.winner + 1 == 0) {
			candidateScore = 0.5;
			abScore = 0.5;
		} else {
			candidateScore = 0;
			abScore = 1;
		}
		ab.randomizeLosses(false);
		return new InteractionResult(candidateScore, abScore, 1);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("candidateMapper", playerMapper).toString();
	}

}
