package put.ci.cevo.util.lister;

import put.ci.cevo.util.sequence.Sequence;

/**
 * Low-level utility used by {@link ClassesLister}.
 */
public interface PackageScanner {

	public Sequence<String> getPackageClasses(String pakage, ClassesListerOptions options);

}
