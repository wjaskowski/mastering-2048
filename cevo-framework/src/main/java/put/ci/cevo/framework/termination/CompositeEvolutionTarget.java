package put.ci.cevo.framework.termination;

import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.util.Description;
import put.ci.cevo.util.filter.AbstractFilter;
import put.ci.cevo.util.info.ProgressInfo;

import java.util.List;

import static put.ci.cevo.framework.termination.CompositeEvolutionTarget.TargetEvaluationStrategy.ANY;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class CompositeEvolutionTarget implements EvolutionTarget {

	public static enum TargetEvaluationStrategy {
		ALL {
			@Override
			public boolean evaluate(List<EvolutionTarget> targets, final EvolutionState state) {
				return seq(targets).all(new AbstractFilter<EvolutionTarget>() {
					@Override
					public boolean qualifies(EvolutionTarget target) {
						return target.isAchieved(state);
					}
				});
			}
		},
		ANY {
			@Override
			public boolean evaluate(List<EvolutionTarget> targets, final EvolutionState state) {
				return seq(targets).any(new AbstractFilter<EvolutionTarget>() {
					@Override
					public boolean qualifies(EvolutionTarget target) {
						return target.isAchieved(state);
					}
				});
			}
		};

		public abstract boolean evaluate(List<EvolutionTarget> targets, EvolutionState state);
	}

	private final List<EvolutionTarget> targets;
	private final TargetEvaluationStrategy strategy;

	public CompositeEvolutionTarget(List<EvolutionTarget> targets) {
		this(targets, ANY);
	}

	public CompositeEvolutionTarget(List<EvolutionTarget> targets, TargetEvaluationStrategy strategy) {
		this.targets = targets;
		this.strategy = strategy;
	}

	@Override
	public boolean isAchieved(EvolutionState state) {
		return strategy.evaluate(getTargets(), state);
	}

	@Override
	public ProgressInfo getProgressInfo() {
		return getTargets().iterator().next().getProgressInfo();
	}

	@Override
	public Description describe() {
		Description description = new Description();
		for (EvolutionTarget target : getTargets()) {
			description.addDescription(target.describe());
		}
		return description;
	}

	public List<EvolutionTarget> getTargets() {
		return targets;
	}

}
