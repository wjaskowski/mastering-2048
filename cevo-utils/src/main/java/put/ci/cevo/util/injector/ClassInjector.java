package put.ci.cevo.util.injector;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import put.ci.cevo.util.ClassResolver;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.ReflectionUtils;
import put.ci.cevo.util.configuration.ConfigurationKey;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.Sequences;
import put.ci.cevo.util.sequence.aggregates.Aggregates;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static put.ci.cevo.util.TypeUtils.explicitCast;
import static put.ci.cevo.util.configuration.Configuration.getConfiguration;
import static put.ci.cevo.util.filter.Filters.notNull;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class ClassInjector {

	private final Map<String, Object> instances = new HashMap<>();

	private final ClassResolver resolver;

	public ClassInjector() {
		this(ClassResolver.DefaultSingleton());
	}

	public ClassInjector(ClassResolver resolver) {
		this.resolver = resolver;
	}

	public <T> Sequence<T> injectMultipleConstructors(ConfigurationKey key) {
		return Sequences.transform(createConfiguredProperties(key), new Transform<ConfigurationProperties, T>() {
			@Override
			public T transform(ConfigurationProperties properties) {
				return injectConstructor(properties);
			}
		});
	}

	public <T> T injectConstructor(ConfigurationKey key) {
		return injectConstructor(new ConfigurationProperties(key));
	}

	public <T> T injectConstructor(ConfigurationProperties properties) {
		final ConfigurationProperties resolvedProperties = new ConfigurationProperties();
		for (Map.Entry<String, Object> property : properties.entrySet()) {
			if (property.getValue() instanceof ConfigurationProperties) {
				ConfigurationProperties nestedProperties = (ConfigurationProperties) property.getValue();
				if (nestedProperties.isClass() || nestedProperties.isInstance()) {
					resolvedProperties.put(property.getKey(), injectConstructor(nestedProperties));
					continue;
				}
			}
			resolvedProperties.put(property.getKey(), property.getValue());
		}
		String className = resolvedProperties.getClassName();
		if (instances.containsKey(className) && resolvedProperties.isInstance()) {
			return explicitCast(instances.get(className));
		}
		Class<T> type = resolver.resolveSafe(className);
		T object = createConfiguredObject(type, resolvedProperties);
		if (resolvedProperties.isInstance()) {
			instances.put(className, object);
		}
		return object;
	}

	private List<ConfigurationProperties> createConfiguredProperties(ConfigurationKey key) {
		final int prefixLength = key.toString().length() + 1;
		final List<Pair<Integer, ConfigurationKey>> keys = getConfiguration().getKeys(key)
			.transform(new Transform<ConfigurationKey, Pair<Integer, ConfigurationKey>>() {
				@Override
				public Pair<Integer, ConfigurationKey> transform(ConfigurationKey key) {
					String num = key.toString().substring(prefixLength);
					try {
						return Pair.create(Integer.valueOf(num), key);
					} catch (NumberFormatException e) {
						return null;
					}
				}
			}).filter(notNull()).toList();

		Collections.sort(keys, (o1, o2) -> Integer.valueOf(o1.first()).compareTo(o2.first()));

		return seq(keys).transform(new Transform<Pair<Integer, ConfigurationKey>, ConfigurationProperties>() {
			@Override
			public ConfigurationProperties transform(Pair<Integer, ConfigurationKey> key) {
				return new ConfigurationProperties(key.second());
			}
		}).toList();
	}

	private <T> T createConfiguredObject(Class<T> type, final ConfigurationProperties properties) {
		Constructor<?> constructor = findMatchingConstructor(type, properties);
		List<String> parameterNames = getParameterNames(constructor);
		final Map<String, Class<?>> typesByNames = seq(parameterNames).zip(asList(constructor.getParameterTypes()))
			.aggregate(Maps.<String, Class<?>> newHashMap(), Aggregates.<String, Class<?>> pairToMapAggregate());

		return ReflectionUtils.invokeConstructor(type, seq(parameterNames).transform(new Transform<String, Object>() {
			@Override
			public Object transform(String parameterName) {
				return properties.getProperty(parameterName, typesByNames.get(parameterName));
			}
		}).toArray());
	}

	private <T> Constructor<?> findMatchingConstructor(Class<T> type, ConfigurationProperties properties) {
		final List<Constructor<?>> matchingConstructors = Lists.newArrayList();
		final int propertiesSize = properties.propertiesSize();
		for (Constructor<?> constructor : type.getConstructors()) {
			List<String> parameterNames = getParameterNames(constructor);
			if (properties.containsProperties(parameterNames) && (propertiesSize == parameterNames.size())) {
				matchingConstructors.add(constructor);
			}
		}
		switch (matchingConstructors.size()) {
		case 0:
			throw new RuntimeException("No matching constructors found for type: " + type + " and parameterNames: "
				+ properties.keySet()
				+ ", check if parameter names in configuration are consistent with the constructor!");

		case 1:
			return matchingConstructors.iterator().next();

		default:
			throw new RuntimeException("Multiple matching constructors: " + matchingConstructors + " for type: " + type
				+ " and parameterNames: " + properties.keySet());
		}
	}

	private List<String> getParameterNames(Constructor<?> constructor) {
		List<String> parameterNames = stream(constructor.getParameters()).map(Parameter::getName).collect(toList());
		if (parameterNames.isEmpty() && !isEmpty(constructor.getParameterTypes())) {
			throw new RuntimeException("Could not retrieve constructor parameter names for: " + constructor);
		}
		return parameterNames;
	}

}
