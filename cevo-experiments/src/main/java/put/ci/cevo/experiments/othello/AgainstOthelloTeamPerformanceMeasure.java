package put.ci.cevo.experiments.othello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedContext.Worker;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.OthelloPlayer;

/**
 * Measures performance of a player against a number of opponents and number of boards. Can use multiple threads.
 */
public class AgainstOthelloTeamPerformanceMeasure implements PerformanceMeasure<OthelloPlayer> {

	private static class Job {
		public final OthelloPlayer opponent;
		public final OthelloState state;
		public final int repeat;

		public Job(OthelloPlayer opponent, OthelloState state, int repeat) {
			this.opponent = opponent;
			this.state = state;
			this.repeat = repeat;
		}
	}

	private final List<OthelloPlayer> opponents;

	private final int numRepeats;
	private final InteractionDomain<OthelloPlayer, OthelloStateOpponent> domain;
	private final ArrayList<OthelloState> states;

	public AgainstOthelloTeamPerformanceMeasure(InteractionDomain<OthelloPlayer, OthelloStateOpponent> domain,
			Collection<OthelloPlayer> opponents) {
		this(domain, opponents, 1);
	}

	public AgainstOthelloTeamPerformanceMeasure(InteractionDomain<OthelloPlayer, OthelloStateOpponent> domain,
			OthelloPlayer opponent, int repeats) {
		this(domain, Arrays.asList(opponent), repeats);
	}

	public AgainstOthelloTeamPerformanceMeasure(InteractionDomain<OthelloPlayer, OthelloStateOpponent> domain,
			Collection<OthelloPlayer> opponents, int repeats) {
		this(domain, opponents, Arrays.asList(new OthelloState()), repeats);
	}

	public AgainstOthelloTeamPerformanceMeasure(InteractionDomain<OthelloPlayer, OthelloStateOpponent> domain,
			Collection<OthelloPlayer> opponents, Collection<OthelloState> states) {
		this(domain, opponents, states, 1);
	}

	public AgainstOthelloTeamPerformanceMeasure(InteractionDomain<OthelloPlayer, OthelloStateOpponent> domain,
			Collection<OthelloPlayer> opponents, Collection<OthelloState> states, int repeats) {
		this.domain = domain;
		this.opponents = new ArrayList<>(opponents);
		this.numRepeats = repeats;
		this.states = new ArrayList<>(states);
	}

	@Override
	public Measurement measure(RandomFactory<OthelloPlayer> subjectFactory, ThreadedContext context) {
		List<Job> jobs = new ArrayList<>();
		for (OthelloPlayer opponent : opponents) {
			for (OthelloState state : states) {
				for (int i = 0; i < numRepeats; ++i) {
					jobs.add(new Job(opponent, state, i));
				}
			}
		}

		List<InteractionResult> results = context.invoke(new Worker<Job, InteractionResult>() {
			@Override
			public InteractionResult process(Job job, ThreadedContext context) {
				OthelloStateOpponent opponent = new OthelloStateOpponent(job.state, job.opponent);
				OthelloPlayer player = subjectFactory.create(context);
				return domain.interact(player, opponent, context.getRandomForThread());
			}
		}, jobs).toList();

		return new Measurement.Builder().add(results).build();
	}
}
