package put.ci.cevo.util.configuration;

public class ConfKey implements ConfigurationKey {

	private final String key;

	public ConfKey(String key) {
		this.key = key;
	}

	@Override
	public final String toString() {
		return key;
	}

	@Override
	public ConfigurationKey dot(Object subKey) {
		return dot(this, subKey);
	}

	public static ConfigurationKey dot(ConfigurationKey parent, Object subKey) {
		if (subKey == null) {
			return parent;
		}
		if (parent == null || parent.toString().isEmpty()) {
			return new ConfKey(subKey.toString());
		}
		return new ConfKey(parent + "." + subKey);
	}

}
