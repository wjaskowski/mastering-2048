package put.ci.cevo.framework.algorithms.history;

import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;

import java.io.Serializable;
import java.util.List;

public interface EvolutionHistory extends EvolutionStateListener, Serializable {

	public List<EvolutionState> getEvolutionHistory();

}
