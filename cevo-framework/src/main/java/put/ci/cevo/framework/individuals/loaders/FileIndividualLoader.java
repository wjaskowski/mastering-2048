package put.ci.cevo.framework.individuals.loaders;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileIndividualLoader<T> {

	public List<T> load(File file) throws IOException;

}
