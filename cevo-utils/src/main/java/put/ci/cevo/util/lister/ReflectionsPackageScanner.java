package put.ci.cevo.util.lister;

import com.google.common.collect.Lists;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.Store;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import put.ci.cevo.util.filter.AbstractFilter;
import put.ci.cevo.util.sequence.Sequence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isPublic;
import static put.ci.cevo.util.filter.Filters.in;
import static put.ci.cevo.util.filter.Filters.notNull;
import static put.ci.cevo.util.sequence.Sequences.filter;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class ReflectionsPackageScanner implements PackageScanner {

	@Override
	public Sequence<String> getPackageClasses(String pakage, ClassesListerOptions options) {
		final Reflections refl = createReflections(pakage, options);
		final List<String> classes = Lists.newArrayList();

		Class<?> superClass = options.getSuperClass();
		if (superClass == null || superClass.equals(Object.class)) {
			classes.addAll(filterClasses(refl.getAllTypes()).toList());
		} else {
			classes.addAll(filterClasses(refl.getStore().getAll(SubTypesScanner.class.getSimpleName(), superClass.getSimpleName())).toList());
		}

		return options.skipDeprecated() ? rejectDeprecated(refl.getStore(), classes) : seq(classes);
	}

	private Reflections createReflections(String pakage, ClassesListerOptions options) {
		Logger.getLogger(Reflections.class).setLevel(Level.WARN);
		List<Scanner> scanners = newArrayList(new SubTypesScanner(false),
			options.skipDeprecated() ? new TypeAnnotationsScanner() : null);
		Scanner[] scannersArr = filter(scanners, notNull()).toArray(Scanner.class);
		return new Reflections(ClasspathHelper.forClass(Object.class), pakage, scannersArr);
	}

	private Sequence<String> rejectDeprecated(Store store, List<String> classes) {
		Set<String> deprecated = new HashSet<>(store.get(TypeAnnotationsScanner.class.getSimpleName()).get(
			Deprecated.class.getName()));
		return seq(classes).filter(in(deprecated).not());
	}

	private Sequence<String> filterClasses(Iterable<String> classNames) {
		return seq(classNames).filter(new AbstractFilter<String>() {
			@Override
			public boolean qualifies(String className) {
				Class<?> clazz = ReflectionUtils.forName(className);
				int modifiers = clazz.getModifiers();
				if (isPublic(modifiers) && !clazz.isAnonymousClass() && !isAbstract(modifiers) && !clazz.isInterface()) {
					return true;
				}
				return false;
			}
		});
	}
}
