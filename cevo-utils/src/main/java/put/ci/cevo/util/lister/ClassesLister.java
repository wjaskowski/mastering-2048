package put.ci.cevo.util.lister;

import static com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static put.ci.cevo.util.TypeUtils.genericCast;
import static put.ci.cevo.util.concurrent.ThreadUtils.createSingleThreadedPoolExecutor;
import static put.ci.cevo.util.filter.Filters.notNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import put.ci.cevo.util.sequence.AsyncTransformSequence;
import put.ci.cevo.util.sequence.Sequences;
import put.ci.cevo.util.sequence.transforms.Transform;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class ClassesLister {

	public static class Builder {

		private final ExecutorService executor = getExitingExecutorService(createSingleThreadedPoolExecutor());

		private final List<String> includePackages;
		private final List<String> excludePackages;

		private ClassesListerOptions options;

		public Builder(String... include) {
			this(asList(include));
		}

		public Builder(Iterable<String> include) {
			this.includePackages = new ArrayList<String>();
			this.excludePackages = new ArrayList<String>();
			this.options = ClassesListerOptions.DEFAULT;
			include(include);
		}

		public Builder include(String... include) {
			return include(asList(include));
		}

		public Builder include(Iterable<String> include) {
			for (String incl : include) {
				includePackages.add(incl);
			}
			return this;
		}

		public Builder exclude(String... exclude) {
			return exclude(asList(exclude));
		}

		public Builder exclude(Iterable<String> exclude) {
			for (String excl : exclude) {
				excludePackages.add(excl);
			}
			return this;
		}

		public Builder options(ClassesListerOptions options) {
			this.options = options;
			return this;
		}

		public ClassesLister buildImmediately() {
			try {
				return build().get();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException("Fatal error occured while trying to build ClassesLister", e);
			}
		}

		public Future<ClassesLister> build() {
			Future<ClassesLister> future = executor.submit(new Callable<ClassesLister>() {
				@Override
				public ClassesLister call() throws Exception {
					PackageScanner packageScanner = PackageScannerFactory.create();
					Set<String> classNames = scanForClasses(packageScanner, options);

					final Multimap<String, String> bySimpleName = ArrayListMultimap.create();
					for (String className : classNames) {
						bySimpleName.put(className.contains("$") ? substringAfterLast(className, "$")
								: substringAfterLast(className, "."), className);
					}

					final List<String> unambiguousNames = Sequences
							.transform(bySimpleName.keySet(), new Transform<String, String>() {
								@Override
								public String transform(String key) {
									Collection<String> collection = bySimpleName.get(key);
									return collection.size() == 1 ? collection.iterator().next() : null;
								}
							}).filter(notNull()).toImmutableList();

					return new ClassesLister(ImmutableList.copyOf(classNames), bySimpleName, unambiguousNames);
				}
			});
			executor.shutdown();
			return future;
		}

		private Set<String> scanForClasses(PackageScanner packageScanner, ClassesListerOptions options) {
			Set<String> classNames = new LinkedHashSet<String>();
			for (String packagez : includePackages) {
				String pakageDot = packagez + ".";
				List<String> excludesDot = new ArrayList<String>(excludePackages.size());
				for (String exclude : excludePackages) {
					String excludeDot = exclude + ".";
					if (pakageDot.startsWith(excludeDot)) {
						continue;
					}
					if (exclude.startsWith(pakageDot)) {
						excludesDot.add(excludeDot);
					}
				}
				for (String className : packageScanner.getPackageClasses(packagez, options)) {
					for (String excludeDot : excludesDot) {
						if (className.startsWith(excludeDot)) {
							continue;
						}
					}
					classNames.add(className);
				}
			}
			return classNames;
		}

	}

	private static final Logger logger = Logger.getLogger(ClassesLister.class);

	private final List<String> classNames;
	private final Multimap<String, String> bySimpleName;
	private final List<String> unambiguousNames;

	public ClassesLister(List<String> classNames, Multimap<String, String> bySimpleName, List<String> unambiguousNames) {
		this.classNames = classNames;
		this.bySimpleName = bySimpleName;
		this.unambiguousNames = unambiguousNames;
	}

	public List<String> getClassNames() {
		return classNames;
	}

	public Multimap<String, String> getClassesBySimpleName() {
		return bySimpleName;
	}

	public List<String> getUnambiguousNames() {
		return unambiguousNames;
	}

	public <T> Set<Class<? extends T>> getSubtypes(final Class<T> type) {
		return new AsyncTransformSequence<String, Class<? extends T>>(getClassNames()) {
			@Override
			protected void getNext(String className) {
				try {
					Class<? extends T> clazz = genericCast(Class.forName(className, false, getClass().getClassLoader()));
					if (type.isAssignableFrom(clazz) && !clazz.equals(type)) {
						next(clazz);
					}
				} catch (ClassNotFoundException e) {
					logger.error("Unable to resolve class: " + className, e);
				}
			}
		}.toSet();
	}

}
