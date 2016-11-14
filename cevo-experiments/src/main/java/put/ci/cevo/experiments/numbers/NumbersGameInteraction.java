package put.ci.cevo.experiments.numbers;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.Game;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.number.NumbersGamePlayer;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.vectors.DoubleVector;

public class NumbersGameInteraction implements InteractionDomain<DoubleVector, DoubleVector> {

	private final Game<NumbersGamePlayer, NumbersGamePlayer> game;
	private final GenotypePhenotypeMapper<DoubleVector, NumbersGamePlayer> mapper;

	@AccessedViaReflection
	public NumbersGameInteraction(Game<NumbersGamePlayer, NumbersGamePlayer> game, double discretization) {
		this(game, new NumbersGameMapper(discretization));
	}

	@AccessedViaReflection
	public NumbersGameInteraction(Game<NumbersGamePlayer, NumbersGamePlayer> game,
			GenotypePhenotypeMapper<DoubleVector, NumbersGamePlayer> mapper) {
		this.game = game;
		this.mapper = mapper;
	}

	@Override
	public InteractionResult interact(DoubleVector candidate, DoubleVector opponent, RandomDataGenerator random) {
		return interact(mapper.getPhenotype(candidate, random), mapper.getPhenotype(opponent, random), random);
	}

	private InteractionResult interact(NumbersGamePlayer player, NumbersGamePlayer opponent, RandomDataGenerator random) {
		GameOutcome firstResult = game.play(player, opponent, random);
		return new InteractionResult(firstResult.playerPoints(), firstResult.opponentPoints(), 1);
	}


}
