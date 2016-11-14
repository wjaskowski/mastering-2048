package put.ci.cevo.util.injector;

import static put.ci.cevo.util.TypeUtils.explicitCast;
import static put.ci.cevo.util.TypeUtils.genericCast;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

import put.ci.cevo.util.NestedPropertiesParser;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;
import put.ci.cevo.util.configuration.LocallyConfiguredCallable;

/**
 * A class representing object properties according to the java beans convention. It extends the {@link TreeMap} to
 * enforce a particular order when setting properties (simple properties set before nested with the same root property
 * object).
 */
public class ConfigurationProperties extends TreeMap<String, Object> {

	private static final long serialVersionUID = 20120619114824L;

	private static final Configuration configuration = Configuration.getConfiguration();
	private static final BeanUtilsBean beanUtils = createBeanUtils();

	private static final Pattern RESOLUTION_PATTERN = Pattern.compile("^\\$\\{(.*)\\}$", Pattern.MULTILINE);
	private static final Pattern CLASS_PATTERN = Pattern.compile("^[@&]\\{(.*)\\}$", Pattern.MULTILINE);
	private static final Pattern PROPERTY_PATTERN = Pattern.compile("^[A-Za-z0-9]+=.+$", Pattern.MULTILINE);
	private static final Pattern SEPARATOR_PATTERN = Pattern.compile("[ ]*(?<=[^\\\\]),[ ]*");

	public ConfigurationProperties() {
		super();
	}

	public ConfigurationProperties(ConfigurationKey key) {
		this(parseProperties(key));
	}

	public ConfigurationProperties(Map<String, ? extends Object> properties) {
		super(properties);
	}

	public boolean isClass() {
		return containsProperty("class") || containsProperty("@");
	}

	public boolean isInstance() {
		return containsProperty("instance") || containsProperty("&");
	}

	public boolean containsProperties(List<String> properties) {
		for (String property : properties) {
			if (!containsProperty(property)) {
				return false;
			}
		}
		return true;
	}

	public boolean containsProperty(String name) {
		return containsKey(name);
	}

	public int propertiesSize() {
		return (isClass() || isInstance()) ? size() - 1 : size();
	}

	public <P> P getProperty(String name) {
		try {
			return explicitCast(get(name));
		} catch (ClassCastException e) {
			throw new RuntimeException("The requested property: '" + name + "' is of incompatible type!", e);
		}
	}

	public <P> P getProperty(String name, Class<P> type) {
		return convertValue(name, type, get(name));
	}

	private <P> P convertValue(String name, Class<P> type, Object value) {
		if (type.isInstance(value)) {
			return explicitCast(value);
		} else {
			Object convertedValue = beanUtils.getConvertUtils().convert(value, type);
			if (type.isInstance(convertedValue) || type.isPrimitive()) {
				return explicitCast(convertedValue);
			} else {
				String message = "The requested property: '" + name + "' could not be converted to: " + type + " in: "
					+ this;
				throw new RuntimeException(message);
			}
		}
	}

	public String getClassName() {
		return isClass() ? getProperty("class", "@") : getProperty("instance", "&");
	}

	public String getProperty(String... keys) {
		for (String key : keys) {
			if (containsKey(key)) {
				return getProperty(key);
			}
		}
		return null;
	}

	public static boolean isProperties(final String string) {
		for (final String token : SEPARATOR_PATTERN.split(string)) {
			if (!PROPERTY_PATTERN.matcher(token).matches()) {
				return false;
			}
		}
		return true;
	}

	private static BeanUtilsBean createBeanUtils() {
		return new BeanUtilsBean(new ParsingConverters(), new PropertyUtilsBean());
	}

	private static ConfigurationProperties parseProperties(final ConfigurationKey key) {
		Map<String, ? extends Object> properties = genericCast(configuration.getProperties(key));
		ConfigurationProperties expandedProperties = new ConfigurationProperties();
		for (Map.Entry<String, ? extends Object> property : properties.entrySet()) {
			Object propValue = property.getValue();
			String stringValue = propValue.toString().trim();
			Matcher matcher = RESOLUTION_PATTERN.matcher(stringValue);
			Object value;
			if (matcher.matches()) {
				String group = matcher.group(1);
				ConfKey subKey = new ConfKey(group);
				if (configuration.containsKey(subKey)) {
					if (isProperties(configuration.getString(subKey))) {
						value = parseProperties(subKey);
					} else {
						value = configuration.getProperty(subKey);
					}
				} else {
					value = new LocallyConfiguredCallable<ConfigurationProperties>() {
						@Override
						protected ConfigurationProperties callInternal() throws Exception {
							return parseProperties(key.dot("__sub"));
						}
					}.withOverriddenConfiguration(key.dot("__sub"), NestedPropertiesParser.parse(group))
						.callWrapExceptions();
				}
			} else {
				Matcher classMatcher = CLASS_PATTERN.matcher(stringValue);
				if (classMatcher.matches()) {
					value = matcher.group();
				}
				value = propValue;
			}
			expandedProperties.put(property.getKey(), value);
		}
		return expandedProperties;
	}

}
