package put.ci.cevo.util.configuration;

/**
 * The class for a dynamic runtime configuration flag, automatically synchronized with {@link Configuration}.
 */
public class RuntimeFlag extends RuntimeConfigurable<Boolean> {

	public RuntimeFlag(String name, ConfigurationKey key) {
		this(name, key, false);
	}

	public RuntimeFlag(String name, ConfigurationKey key, Boolean defaultValue) {
		super(name, key, defaultValue);
	}

	public boolean isSet() {
		return getValue();
	}

}
