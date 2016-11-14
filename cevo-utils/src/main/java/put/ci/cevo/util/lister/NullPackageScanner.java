package put.ci.cevo.util.lister;

import static put.ci.cevo.util.sequence.Sequence.emptySequence;
import put.ci.cevo.util.sequence.Sequence;

public class NullPackageScanner implements PackageScanner {

	@Override
	public Sequence<String> getPackageClasses(String pakage, ClassesListerOptions options) {
		return emptySequence();
	}

}
