package put.ci.cevo.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;

public class TestCallback implements EvolutionStateListener {

	private final List<Integer> generations = new ArrayList<>();
	private final List<Integer> effort = new ArrayList<>();

	@Override
	public void onNextGeneration(EvolutionState state) {
		generations.add(state.getGeneration());
		effort.add((int) state.getTotalEffort());
	}

	public void assertGenerations(List<Integer> expected) {
		Assert.assertEquals(expected, generations);
	}

	public void assertEffort(List<Integer> expected) {
		Assert.assertEquals(expected, effort);
	}
}