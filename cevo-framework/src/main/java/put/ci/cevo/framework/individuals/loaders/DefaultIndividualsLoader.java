package put.ci.cevo.framework.individuals.loaders;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import put.ci.cevo.framework.individuals.loaders.filters.IdentityIndividualsFilter;
import put.ci.cevo.framework.individuals.loaders.filters.IndividualsFilter;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import static put.ci.cevo.util.filter.Filters.notNull;
import static put.ci.cevo.util.sequence.Sequences.flatten;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class DefaultIndividualsLoader<T> implements FilesIndividualLoader<T> {

	private static final Logger logger = Logger.getLogger(DefaultIndividualsLoader.class);

	private final FileIndividualLoader<T> loader;
	private final IndividualsFilter<T> filter;

	public DefaultIndividualsLoader(FileIndividualLoader<T> loader) {
		this(loader, new IdentityIndividualsFilter<T>());
	}

	public DefaultIndividualsLoader(FileIndividualLoader<T> loader, IndividualsFilter<T> filter) {
		this.loader = loader;
		this.filter = filter;
	}

	@Override
	public List<T> loadIndividuals(File directory, String... wildcards) {
		return loadIndividuals(seq(directory.listFiles(createFilter(wildcards))));
	}

	@Override
	public List<T> loadIndividuals(Iterable<File> files) {
		return flatten(seq(files).map(new Transform<File, List<T>>() {
			@Override
			public List<T> transform(File file) {
				try {
					return loader.load(file);
				} catch (IOException e) {
					logger.error("Fatal error while trying to load players from file: " + file, e);
					return null;
				}
			}
		}).map(getSelectionFilter())).filter(notNull()).toImmutableList();
	}

	@Override
	public FileIndividualLoader<T> getLoader() {
		return loader;
	}

	public IndividualsFilter<T> getSelectionFilter() {
		return filter;
	}

	protected FileFilter createFilter(String... wildcards) {
		if (wildcards.length != 0) {
			return new WildcardFileFilter(wildcards, IOCase.INSENSITIVE);
		}
		return TrueFileFilter.TRUE;
	}

	public static <T> FilesIndividualLoader<T> create() {
		return new DefaultIndividualsLoader<>(EvaluatedIndividualLoader.<T> individualLoader());
	}

}
