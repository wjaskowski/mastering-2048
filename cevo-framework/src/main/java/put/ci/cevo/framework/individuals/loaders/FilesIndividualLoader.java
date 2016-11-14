package put.ci.cevo.framework.individuals.loaders;

import java.io.File;
import java.util.List;

/**
 * Loads individuals from a directory, or more generally, from a set of files. Loading logic is encapsulated in an
 * instance of {@link FileIndividualLoader}.
 */
public interface FilesIndividualLoader<T> {

	public List<T> loadIndividuals(File directory, String... wildcards);

	public List<T> loadIndividuals(Iterable<File> files);

	public FileIndividualLoader<T> getLoader();

}
