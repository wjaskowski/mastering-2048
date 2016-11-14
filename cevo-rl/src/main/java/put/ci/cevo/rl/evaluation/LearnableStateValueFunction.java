package put.ci.cevo.rl.evaluation;

import put.ci.cevo.rl.environment.State;

public interface LearnableStateValueFunction<S extends State> extends StateValueFunction<S> {

	void increase(S state, double delta);

	int getActiveFeaturesCount();

	double getActiveWeight(S state, int idx);

	void setActiveWeight(S state, int idx, double value);

	void increaseActiveWeight(S state, int idx, double delta);
}
