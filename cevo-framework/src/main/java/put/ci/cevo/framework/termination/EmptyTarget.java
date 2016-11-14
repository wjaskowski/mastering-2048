package put.ci.cevo.framework.termination;

import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.util.Description;
import put.ci.cevo.util.info.ProgressInfo;

import static put.ci.cevo.util.info.EmptyProgressInfo.emptyProgressInfo;

public class EmptyTarget implements EvolutionTarget {

	@Override
	public Description describe() {
		return new Description();
	}

	@Override
	public boolean isAchieved(EvolutionState state) {
		return false;
	}

	@Override
	public ProgressInfo getProgressInfo() {
		return emptyProgressInfo();
	}

}
