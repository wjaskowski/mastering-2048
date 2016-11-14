package put.ci.cevo.util.lister;

public class ClassesListerOptions {

	public static final ClassesListerOptions DEFAULT = new ClassesListerOptions(null, false);

	private final Class<?> superClass;
	private final boolean skipDeprecated;

	public ClassesListerOptions(Class<?> superClass) {
		this(superClass, false);
	}

	public ClassesListerOptions(Class<?> superClass, boolean skipDeprecated) {
		this.superClass = superClass;
		this.skipDeprecated = skipDeprecated;
	}

	public Class<?> getSuperClass() {
		return superClass;
	}

	public boolean skipDeprecated() {
		return skipDeprecated;
	}
}
