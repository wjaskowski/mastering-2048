package put.ci.cevo.experiments.ipd;

import static com.google.common.base.Objects.toStringHelper;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.MorePointsGameResultEvaluator;
import put.ci.cevo.games.ipd.IPDPayoffProvider;
import put.ci.cevo.games.ipd.IPDPlayer;
import put.ci.cevo.games.ipd.IteratedPrisonersDilemma;
import put.ci.cevo.games.ipd.LinearPayoffInterpolator;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class IPDInteraction implements InteractionDomain<IPDVector, IPDVector> {

	private final GenotypePhenotypeMapper<IPDVector, IPDPlayer> mapper;

	private final IPDPayoffProvider payoffProvider;
	private final GameResultEvaluator evaluator;

	private final int rounds;

	@AccessedViaReflection
	public IPDInteraction(int choices, int rounds) {
		this(new LinearPayoffInterpolator(choices), new IPDLookupTableMapper(), rounds);
	}

	@AccessedViaReflection
	public IPDInteraction(IPDPayoffProvider provider, GenotypePhenotypeMapper<IPDVector, IPDPlayer> mapper, int rounds) {
		this(mapper, provider, rounds, new MorePointsGameResultEvaluator(1.0, 0.0, 0.5));
	}

	@AccessedViaReflection
	public IPDInteraction(GenotypePhenotypeMapper<IPDVector, IPDPlayer> mapper, IPDPayoffProvider payoffProvider,
			int rounds, GameResultEvaluator evaluator) {
		this.mapper = mapper;
		this.payoffProvider = payoffProvider;
		this.rounds = rounds;
		this.evaluator = evaluator;
	}

	@Override
	public InteractionResult interact(IPDVector candidate, IPDVector opponent, RandomDataGenerator random) {
		IteratedPrisonersDilemma ipdGame = new IteratedPrisonersDilemma(payoffProvider, rounds, evaluator);
		GameOutcome outcome = ipdGame.play(mapper.getPhenotype(candidate, random), mapper.getPhenotype(opponent, random), random);
		return new InteractionResult(outcome.playerPoints(), outcome.opponentPoints(), 1);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("payoff", payoffProvider).add("evaluator", evaluator).add("mapper", mapper)
			.add("rounds", rounds).toString();
	}

}
