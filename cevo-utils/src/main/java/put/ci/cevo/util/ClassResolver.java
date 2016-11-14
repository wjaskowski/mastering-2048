package put.ci.cevo.util;

import static put.ci.cevo.util.TypeUtils.genericCast;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import put.ci.cevo.util.lister.ClassesLister;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class ClassResolver {

	private static final List<String> DEFAULT_PARSE_PACKAGES = ImmutableList.of("put.ci.cevo", "java");

	private final Future<ClassesLister> classesLister;

	public ClassResolver() {
		this(DEFAULT_PARSE_PACKAGES);
	}

	private static ClassResolver DEFAULT_CLASS_RESOLVER = null;
	public static ClassResolver DefaultSingleton() {
		if (DEFAULT_CLASS_RESOLVER == null)
			DEFAULT_CLASS_RESOLVER = new ClassResolver();
		return DEFAULT_CLASS_RESOLVER;
	}

	public ClassResolver(Iterable<String> packages) {
		this.classesLister = new ClassesLister.Builder(ImmutableList.copyOf(packages)).build();
	}

	public <T> Class<T> resolveSafe(String className) {
		Class<T> type = resolve(className);
		if (type != null) {
			return type;
		} else {
			throw new RuntimeException("Could not resolve class name: " + className);
		}
	}

	public <T> Class<T> resolve(String name) {
		String simpleName = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : name;
		if (simpleName.isEmpty()) {
			return null;
		}
		final Multimap<String, String> classesBySimpleName = getClassesLister().getClassesBySimpleName();
		for (String className : classesBySimpleName.get(simpleName)) {
			if (className.contains(name)) {
				try {
					return genericCast(Class.forName(className));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("A fatal error occured while loading a resolved class!", e);
				}
			}
		}
		return null;
	}

	public ClassesLister getClassesLister() {
		try {
			return classesLister.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException("Fatal error while accessing classes lister", e);
		}
	}

	public static List<String> defaultPackages() {
		return DEFAULT_PARSE_PACKAGES;
	}

}
