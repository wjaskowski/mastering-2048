package put.ci.cevo.util;

import static java.lang.reflect.Modifier.isFinal;
import static put.ci.cevo.util.TypeUtils.genericCast;
import static put.ci.cevo.util.sequence.Sequences.seq;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;

public class ReflectionUtils {

	public static <T> T invokeConstructor(Class<? extends T> clazz, Object... args) {

		Class<?>[] argsTypes = getTypes(args);
		Constructor<? extends T> constructor = getMatchingConstructor(clazz, argsTypes);
		if (constructor == null) {
			String errorMessage = "No matching constructor for: class=" + clazz + " arguments="
				+ seq(argsTypes).toList();
			throw new RuntimeException(errorMessage);
		}
		return invokeConstructor(constructor, args);
	}

	public static <T> T invokeConstructor(Constructor<? extends T> constructor, Object... args) {
		try {
			constructor.setAccessible(true);
			return constructor.newInstance(args);
		} catch (InstantiationException e) {
			throw new RuntimeException(invConstrErrorMsg(constructor, args), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(invConstrErrorMsg(constructor, args), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Failed to instantiate class. Check the constructor + fields initialization",
					e.getTargetException());
		}
	}

	private static String invConstrErrorMsg(Constructor<?> constructor, Object[] args) {
		return "Object construction exception occured: constructor=" + constructor + "; args=" + Arrays.asList(args);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<Constructor<T>> getMatchingConstructors(Class<T> clazz, Class<?>... argsTypes) {
		List<Constructor<T>> matchingConstructors = Lists.newArrayList();
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			Class<?>[] parameterTypes = constructor.getParameterTypes();
			if (parameterTypes.length != argsTypes.length) {
				continue;
			}
			boolean matching = true;
			for (int parameterIndex = 0; parameterIndex < parameterTypes.length; ++parameterIndex) {
				Class<?> argType = argsTypes[parameterIndex];
				if (argType == null) {
					continue; // matches any type
				}
				Class<?> parameterType = parameterTypes[parameterIndex];
				if (!MethodUtils.isAssignmentCompatible(parameterType, argType)) {
					matching = false;
					break;
				}
			}
			if (matching) {
				matchingConstructors.add((Constructor<T>) constructor);
			}
		}
		return matchingConstructors;
	}

	public static <T> Constructor<T> getMatchingConstructor(Class<T> clazz, Class<?>... argsTypes) {
		List<Constructor<T>> matchingConstructors = getMatchingConstructors(clazz, argsTypes);
		if (matchingConstructors.isEmpty()) {
			return null;
		}
		if (matchingConstructors.size() == 1) {
			Constructor<T> constructor = matchingConstructors.get(0);
			constructor.setAccessible(true);
			return constructor;
		}
		String errorMessage = "Multiple matching constructors for: class=" + clazz + " arguments=" + argsTypes
			+ " constructors=" + matchingConstructors;
		throw new RuntimeException(errorMessage);
	}

	public static <T> T invoke(Object object, String methodName, Object... arguments) {
		Class<?> objectClass = object.getClass();
		Class<?>[] argumentTypes = getTypes(arguments);
		Method method = getMatchingMethod(objectClass, methodName, argumentTypes);
		if (method == null) {
			String errorMessage = "No matching method for: class=" + objectClass + " name=" + methodName
				+ " arguments=" + argumentTypes;
			throw new RuntimeException(errorMessage);
		}
		return ReflectionUtils.invoke(object, method, arguments);
	}

	public static <T> T invoke(Object object, Method method, Object... arguments) {
		try {
			method.setAccessible(true);
			@SuppressWarnings("unchecked")
			T result = (T) method.invoke(object, arguments);
			return result;
		} catch (ClassCastException e) {
			throw new RuntimeException("The method " + method + " returns result of incompatible type!", e);
		} catch (IllegalAccessException e) {
			List<Object> args = Arrays.asList(arguments);
			throw new RuntimeException("A fatal error occured while invoking: object=" + object + "; method=" + method
				+ "; args=" + args, e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		}
	}

	public static <T> Class<T> forName(String className) {
		try {
			Class<T> name = genericCast(Class.forName(className));
			return name;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to resolve class: " + className, e);
		}
	}

	public static <T> T invokeByPathExpression(Object object, String path) {
		int dotIndex = path.indexOf(".");
		if (dotIndex == -1) {
			Method method = getMatchingMethod(object.getClass(), "get" + StringUtils.capitalize(path));
			if (method != null) {
				return invoke(object, method);
			} else {
				method = getMatchingMethod(object.getClass(), "is" + StringUtils.capitalize(path));
				if (method != null) {
					return invoke(object, method);
				} else {
					throw new RuntimeException("Could not find accessible field: " + path + " in object: "
						+ object.toString());
				}
			}
		} else {
			String field = path.substring(0, dotIndex);
			String rest = path.substring(dotIndex + 1);
			Object semiObject = invoke(object, "get" + StringUtils.capitalize(field));
			return invokeByPathExpression(semiObject, rest);
		}
	}

	public static Method getMatchingMethod(Class<?> objectClass, String methodName, Class<?>... argumentTypes) {
		List<Method> methods = getMatchingMethods(objectClass, methodName, argumentTypes);
		if (methods.isEmpty()) {
			return null;
		}
		if (methods.size() == 1) {
			return methods.get(0);
		}
		String errorMessage = "Multiple matching methods for: class=" + objectClass + " name=" + methodName
			+ " arguments=" + argumentTypes + " methods=" + methods;
		throw new RuntimeException(errorMessage);
	}

	public static List<Method> getMatchingMethods(Class<?> objectClass, String methodName, Class<?>... argumentTypes) {
		List<Method> matchingMethods = Lists.newArrayList();
		for (Method method : objectClass.getMethods()) {
			if (!method.getName().equals(methodName)) {
				continue;
			}
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length != argumentTypes.length) {
				continue;
			}
			boolean matching = true;
			for (int parameterIndex = 0; parameterIndex < parameterTypes.length; ++parameterIndex) {
				Class<?> argType = argumentTypes[parameterIndex];
				if (argType == null) {
					continue; // matches any type
				}
				Class<?> parameterType = parameterTypes[parameterIndex];
				if (!MethodUtils.isAssignmentCompatible(parameterType, argType)) {
					matching = false;
					break;
				}
			}
			if (matching) {
				matchingMethods.add(method);
			}
		}
		return matchingMethods;
	}

	public static List<Class<?>> getTypes(List<?> objects) {
		List<Class<?>> types = new ArrayList<Class<?>>(objects.size());
		for (Object object : objects) {
			types.add(object == null ? null : object.getClass());
		}
		return types;
	}

	public static Class<?>[] getTypes(Object[] objects) {
		Class<?>[] types = new Class<?>[objects.length];
		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			types[i] = object == null ? null : object.getClass();
		}
		return types;
	}

	public static <T> T getFieldValue(Object object, Field field, Class<T> clazz) {
		try {
			if (field.getType() == clazz) {
				@SuppressWarnings("unchecked")
				T result = (T) field.get(object);
				return result;
			} else {
				String msg = "Field type=" + field.getType() + " does not match declared type=" + clazz;
				throw new RuntimeException(msg);
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean methodMatches(String name, Class<?>[] params, Class<?> retType, Method candidate) {
		if (!candidate.getName().equals(name)) {
			return false;
		}
		if (!retType.isAssignableFrom(candidate.getReturnType())
			&& !candidate.getReturnType().isAssignableFrom(retType)) {
			return false;
		}
		Class<?>[] candidateParams = candidate.getParameterTypes();
		if (candidateParams.length != params.length) {
			return false;
		}
		for (int i = 0; i < params.length; i++) {
			Class<?> param = params[i];
			Class<?> candidateParam = candidateParams[i];
			if (!paramsMatch(param, candidateParam)) {
				return false;
			}
		}
		return true;
	}

	public static Class<?> getFieldType(String fieldName, Class<?> target) {
		try {
			return target.getDeclaredField(fieldName).getType();
		} catch (SecurityException e) {
			throw new RuntimeException("Unable to retrive type for fieldName=" + fieldName + " and class=" + target, e);
		} catch (NoSuchFieldException e) {
			final Class<?> superClass = target.getSuperclass();
			if (superClass == null || superClass == Object.class) {
				throw new RuntimeException("Field of type fieldName=" + fieldName + " doesn't exist in " + target, e);
			} else {
				return getFieldType(fieldName, superClass);
			}

		}
	}

	public static boolean isMutable(Class<?> type) {
		for (Field field : type.getDeclaredFields()) {
			if (!isFinal(field.getModifiers())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBean(Class<?> type) {
		try {
			for (PropertyDescriptor descriptor : Introspector.getBeanInfo(type).getPropertyDescriptors()) {
				if (descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null) {
					return false;
				}
			}
			return true;
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean paramsMatch(Class<?> param, Class<?> candidateParam) {
		if (candidateParam.isAssignableFrom(param) || param.isAssignableFrom(candidateParam)) {
			return true;
		}
		return false;
	}

}
