package put.ci.cevo.util.configuration;

/**
 * Object corresponding to a configuration key.
 */
public interface ConfigurationKey {

	/** Get the configuration key. */
	@Override
	public String toString();

	/** Return the key extended by adding another part after a dot. */
	public ConfigurationKey dot(Object subKey);

}

/*
 * To create a new configuration keys enum, start with this code: public static enum <ENUM_NAME> implements
 * ConfigurationKey { CONSTANT("<key.suffix>"); private final String key; private <ENUM_NAME>(String key) { this.key =
 * "<key.prefix>." + key; }
 * @Override public String toString() { return key; } public ConfigurationKey dot(Object subKey) { return
 * ConfKey.dot(this, subKey); } }
 */
