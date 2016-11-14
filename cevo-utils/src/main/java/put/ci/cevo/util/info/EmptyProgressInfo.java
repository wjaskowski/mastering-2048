package put.ci.cevo.util.info;

public class EmptyProgressInfo implements ProgressInfo {

	private static final EmptyProgressInfo INSTANCE = new EmptyProgressInfo();

	private EmptyProgressInfo() {
		// forbid instantiation
	}

	@Override
	public void processed() {
		// nothing to do
	}

	@Override
	public void processed(Object object) {
		// nothing to do
	}

	@Override
	public void multiProcessed(long numElements) {
		// nothing to do
	}

	@Override
	public void finished() {
		// nothing to do
	}

	public static ProgressInfo emptyProgressInfo() {
		return INSTANCE;
	}

}
