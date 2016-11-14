package put.ci.cevo.newexperiments;

/** Can run() and describe itself() */
public interface Experiment {
	/** Run experiment with command line arguments */
	void run(String[] args);

	default void run() {
		run(new String[] {});
	}
}
