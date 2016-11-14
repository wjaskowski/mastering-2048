package put.ci.cevo.framework.termination;

import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.util.Description;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.info.ProgressInfo;
import put.ci.cevo.util.info.TextProgressInfo;

public class GenerationsTarget implements EvolutionTarget {

	private final int generations;
	private final ProgressInfo info;

	@AccessedViaReflection
	public GenerationsTarget(int generations) {
		this(generations, "EvolutionaryLearning");
	}

	@AccessedViaReflection
	public GenerationsTarget(int generations, String info) {
		this.generations = generations;
		this.info = new TextProgressInfo(generations, info);
	}

	@Override
	public boolean isAchieved(EvolutionState state) {
		return state.getGeneration() >= generations - 1;
	}

	@Override
	public ProgressInfo getProgressInfo() {
		return info;
	}

	public int getGenerations() {
		return generations;
	}

	@Override
	public Description describe() {
		Description description = new Description();
		description.addProperties(this, "class", "generations");
		return description;
	}

}
