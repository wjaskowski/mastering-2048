package put.ci.cevo.util.lister;

import static java.util.Collections.singleton;
import static put.ci.cevo.util.sequence.Sequences.seq;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import put.ci.cevo.util.sequence.Sequence;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class SpringPackageScanner implements PackageScanner {

	@Override
	public Sequence<String> getPackageClasses(String pakage, ClassesListerOptions options) {
		Class<?> superClass = options.getSuperClass();
		if (superClass == null || superClass.equals(Object.class)) {
			return getSimpleClasses(pakage);
		}
		return getSubClasses(pakage, superClass);
	}

	private Sequence<String> getSubClasses(String pakage, Class<?> superClass) {
		final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
			false);
		final Multimap<String, String> subClasses = ArrayListMultimap.create();
		provider.addIncludeFilter(new TypeFilter() {
			@Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
				ClassMetadata metadata = metadataReader.getClassMetadata();
				String cl = metadata.getClassName();
				subClasses.put(cl, cl);
				if (metadata.isInterface()) {
					subClasses.put(Object.class.getName(), cl);
				} else {
					subClasses.put(metadata.getSuperClassName(), cl);
				}
				for (String iface : metadata.getInterfaceNames()) {
					subClasses.put(iface, cl);
				}
				return false;
			}
		});
		provider.findCandidateComponents(StringUtils.replace(pakage, ".", "/"));
		Set<String> result = new HashSet<String>();
		Set<String> current = singleton(superClass.getName());
		while (!current.isEmpty()) {
			Set<String> next = new HashSet<String>();
			for (String curr : current) {
				next.addAll(subClasses.get(curr));
			}
			next.removeAll(result);
			result.addAll(next);
			current = next;
		}
		return seq(result);
	}

	private Sequence<String> getSimpleClasses(String pakage) {
		final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
			false);
		final List<String> classes = Lists.newArrayList();
		provider.addIncludeFilter(new TypeFilter() {
			@Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
				ClassMetadata metadata = metadataReader.getClassMetadata();
				classes.add(metadata.getClassName());
				return false;
			}
		});
		provider.findCandidateComponents(StringUtils.replace(pakage, ".", "/"));
		return seq(classes);
	}

}
