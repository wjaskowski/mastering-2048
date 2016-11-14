package put.ci.cevo.framework.termination;

import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.util.Describable;
import put.ci.cevo.util.info.ProgressInfo;

public interface EvolutionTarget extends Describable {

	public boolean isAchieved(EvolutionState state);

	public ProgressInfo getProgressInfo();

}