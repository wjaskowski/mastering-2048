package put.ci.cevo.framework.model;

import org.apache.log4j.Logger;
import org.reflections.ReflectionUtils;
import put.ci.cevo.util.Describable;
import put.ci.cevo.util.Description;
import put.ci.cevo.util.filter.Filters;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;

import static org.apache.commons.lang.StringUtils.join;
import static put.ci.cevo.util.sequence.Sequences.transform;

public class ParametrizedModel implements Describable {

	private static final Logger logger = Logger.getLogger(ParametrizedModel.class);

	/**
	 * States that bean property is a model parameter. Should be used with getters that do not return primitive types,
	 * enums, number or booleans.
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Parameter {
		// Empty
	}

	private final Map<String, Object> parameters = new HashMap<>();
	private final Map<String, Field> fields = new HashMap<>();

	private final Object parametrizedObject;

	public ParametrizedModel(Object object) {
		this.parametrizedObject = object;
		addParameters(object);
	}

	// FIXME(Pawel): object is not used. Is it a bug?
	private void addParameters(Object object) {
		Set<Field> allFields = ReflectionUtils.getAllFields(parametrizedObject.getClass(), Filters.<Field> all());
		for (Field field : allFields) {
			field.setAccessible(true);
			try {
				parameters.put(field.getName(), field.get(parametrizedObject));
				fields.put(field.getName(), field);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error("Unable to access field: " + field);
			}
		}
	}

	public Set<String> getParametersNames() {
		return parameters.keySet();
	}

	public Object getCachedParameter(String name) {
		return parameters.get(name);
	}

	public Object getParameter(String name) {
		try {
			return fields.get(name).get(parametrizedObject);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Unable to retrieve field value", e);
		}
	}

	public void setParameter(String name, Object value) {
		Field field = fields.get(name);
		try {
			field.set(parametrizedObject, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.error("Unable to set field: " + field + " to value: " + value);
		}
	}

	@Override
	public Description describe() {
		Description description = new Description();
		description.addProperties(getParameters());
		return description;
	}

	public Map<String, Object> getParameters() {
		Set<String> keys = getParametersNames();
		Map<String, Object> map = new LinkedHashMap<String, Object>(keys.size());
		for (String key : keys) {
			map.put(key, getCachedParameter(key));
		}
		return map;
	}

	@Override
	public String toString() {
		return "{"
			+ join(transform(getParameters().entrySet(), new Transform<Map.Entry<String, ?>, String>() {
				@Override
				public String transform(Map.Entry<String, ?> entry) {
					return entry.getKey() + "=" + simplifyValue(entry.getValue());
				}
			}).toList(), ", ") + "}";
	}

	private static Object simplifyValue(Object value) {
		if (value instanceof Class<?>) {
			Class<?> clazz = (Class<?>) value;
			return clazz.getSimpleName();
		}
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ParametrizedModel)) {
			return false;
		}
		return Objects.equals(parametrizedObject, ((ParametrizedModel) obj).parametrizedObject);
	}

	@Override
	public int hashCode() {
		return parametrizedObject.hashCode();
	}
}
