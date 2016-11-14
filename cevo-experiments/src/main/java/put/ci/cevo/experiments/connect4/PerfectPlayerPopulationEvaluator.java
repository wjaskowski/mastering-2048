package put.ci.cevo.experiments.connect4;

import put.ci.cevo.experiments.connect4.measures.Connect4PerfectPlayerPerformanceMeasure;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.OneByOnePopulationEvaluator;
import put.ci.cevo.framework.evaluators.PerformanceMeasureIndividualEvaluator;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.connect4.thill.c4.AlphaBetaAgent;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

/**
 * Evaluates the population on the basis of games with perfectly playing {@link AlphaBetaAgent}.
 * Uses {@link put.ci.cevo.games.connect4.Connect4} implementation to play the game.
 */
public class PerfectPlayerPopulationEvaluator<S> implements PopulationEvaluator<S> {

	private final PopulationEvaluator<S> evaluator;

	public PerfectPlayerPopulationEvaluator(GenotypePhenotypeMapper<S, Connect4Player> mapper, int numRepeats) {
		this.evaluator = new OneByOnePopulationEvaluator<>(new PerformanceMeasureIndividualEvaluator<>(mapper,
				new Connect4PerfectPlayerPerformanceMeasure(new Connect4Interaction(), numRepeats)));
	}

	@Override
	public EvaluatedPopulation<S> evaluate(List<S> population, int generation, ThreadedContext context) {
		return evaluator.evaluate(population, generation, context);
	}
}
