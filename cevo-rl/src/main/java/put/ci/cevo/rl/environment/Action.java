package put.ci.cevo.rl.environment;

public interface Action {

	// TODO: PL: I would call it differently, description associates with text. Maybe getFeatures?
	// TODO: WJ: I doubt we need it (then we could resign from the Action class altogether (see the comment for State)
	double[] getDescription();
}
