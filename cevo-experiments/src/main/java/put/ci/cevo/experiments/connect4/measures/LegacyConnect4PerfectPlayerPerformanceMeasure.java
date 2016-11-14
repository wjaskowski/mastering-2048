package put.ci.cevo.experiments.connect4.measures;

import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.connect4.players.adapters.Connect4PlayerAgentAdapter;
import put.ci.cevo.games.connect4.thill.c4.AlphaBetaAgent;
import put.ci.cevo.games.connect4.thill.c4.TDParams;
import put.ci.cevo.games.connect4.thill.c4.competition.Evaluate;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

/**
 * Uses Thill's perfromance measure {@link Evaluate} and his {@link AlphaBetaAgent} to evaluate {@link Connect4Player}.
 */
public class LegacyConnect4PerfectPlayerPerformanceMeasure implements PerformanceMeasure<Connect4Player> {

	private static final TDParams DEFAULT_PARAMS = new TDParams();
	private static final AlphaBetaAgent AB = AlphaBetaAgent.createAgent(true, true);
	
	private final Evaluate measure;
	private final TDParams params;

	@AccessedViaReflection
	public LegacyConnect4PerfectPlayerPerformanceMeasure() {
		this(DEFAULT_PARAMS);
	}

	public LegacyConnect4PerfectPlayerPerformanceMeasure(TDParams params) {
		this.params = params;
		this.measure = new Evaluate(AB);
	}

	@Override
	/** Measure performance of the subject, not thread-safe! */
	public Measurement measure(RandomFactory<Connect4Player> subjectFactory, ThreadedContext context) {
		Connect4PlayerAgentAdapter player = new Connect4PlayerAgentAdapter(subjectFactory.create(context));
		double[] score = measure.getScore(player, AB, params, context.getRandomForThread());
		return new Measurement.Builder().add(score[1], params.numEvaluationMatches).build();
	}
}
