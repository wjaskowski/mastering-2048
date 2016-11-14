package put.ci.cevo.framework.termination;

import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.util.Description;
import put.ci.cevo.util.info.ProgressInfo;
import put.ci.cevo.util.info.TextProgressInfo;

public class EffortTarget implements EvolutionTarget {

	private final long effort;
	private final ProgressInfo info;

	private long lastEffort;

	public EffortTarget(long effort) {
		this(effort, "EvolutionaryLearning");
	}

	public EffortTarget(long effort, String info) {
		this.effort = effort;
		this.info = new TextProgressInfo(effort, info);
	}

	@Override
	public boolean isAchieved(EvolutionState state) {
		long currentEffort = state.getTotalEffort();
		long deltaEffort = currentEffort - lastEffort;
		updateLastEffort(currentEffort);
		return currentEffort + deltaEffort > effort;
	}

	private void updateLastEffort(long currentTotalEffort) {
		lastEffort = currentTotalEffort;
	}

	@Override
	public ProgressInfo getProgressInfo() {
		return info;
	}

	public long getEffort() {
		return effort;
	}

	@Override
	public Description describe() {
		Description description = new Description();
		description.addProperties(this, "class", "effort");
		return description;
	}

}
