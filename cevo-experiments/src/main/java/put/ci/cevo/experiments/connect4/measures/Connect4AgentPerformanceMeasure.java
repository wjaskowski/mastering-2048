package put.ci.cevo.experiments.connect4.measures;

import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.AgainstTeamPerformanceMeasure;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.games.connect4.thill.c4.Agent;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.random.ThreadedContext;

public class Connect4AgentPerformanceMeasure implements PerformanceMeasure<Agent> {

	private final AgainstTeamPerformanceMeasure<Agent, Agent> measure;

	/**
	 * @param numRepeats number of interactions to make against the random opponent
	 */
	public Connect4AgentPerformanceMeasure(InteractionDomain<Agent, Agent> c4, Agent agent, int numRepeats) {
		this.measure = new AgainstTeamPerformanceMeasure<>(c4, agent, numRepeats);
	}

	@Override
	public Measurement measure(RandomFactory<Agent> subjectFactory, ThreadedContext context) {
		return measure.measure(subjectFactory, context);
	}

}
