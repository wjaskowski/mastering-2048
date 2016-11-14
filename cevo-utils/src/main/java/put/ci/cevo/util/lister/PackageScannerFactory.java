package put.ci.cevo.util.lister;

import static org.apache.commons.lang.StringUtils.join;
import static put.ci.cevo.util.sequence.Sequences.transform;

import java.util.List;

import org.apache.log4j.Logger;

import put.ci.cevo.util.ReflectionUtils;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transforms;

import com.google.common.collect.ImmutableList;

public class PackageScannerFactory {

	private static final Logger logger = Logger.getLogger(PackageScannerFactory.class);

	private static final List<Class<? extends PackageScanner>> PACKAGE_SCANNERS = ImmutableList.of(
		ReflectionsPackageScanner.class, SpringPackageScanner.class);

	public static PackageScanner create() {
		for (int i = 0; i < PACKAGE_SCANNERS.size(); i++) {
			Class<? extends PackageScanner> scannerClass = PACKAGE_SCANNERS.get(i);
			PackageScanner scanner = ReflectionUtils.invokeConstructor(scannerClass);
			String packageName = PackageScannerFactory.class.getPackage().getName();
			Sequence<String> test = scanner.getPackageClasses(packageName, ClassesListerOptions.DEFAULT);
			if (!test.isEmpty()) {
				if (i > 0) {
					logger.debug("Using " + scannerClass.getSimpleName() + "(previously did not work: "
						+ classList(PACKAGE_SCANNERS.subList(0, i)) + ")");
				}
				return scanner;
			}
		}

		logger.error("No working scanner " + PackageScanner.class.getSimpleName() + " found, "
			+ "returning dummy (did not work: " + classList(PACKAGE_SCANNERS) + ")");
		return new NullPackageScanner();
	}

	private static String classList(List<? extends Class<?>> classes) {
		return join(transform(classes, Transforms.<String> get("simpleName")).iterator(), ", ");
	}

}
