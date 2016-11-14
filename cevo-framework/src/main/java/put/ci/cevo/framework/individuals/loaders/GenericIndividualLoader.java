package put.ci.cevo.framework.individuals.loaders;

import put.ci.cevo.framework.state.EvaluatedIndividual;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static put.ci.cevo.util.sequence.Sequences.seq;

/**
 * This loaded could be used in conjunction with any {@link EvaluatedIndividual}s loader to extract a generic individual
 * object.
 */
public class GenericIndividualLoader<T> implements FileIndividualLoader<T> {

	private final FileIndividualLoader<EvaluatedIndividual<T>> loader;

	public GenericIndividualLoader(FileIndividualLoader<EvaluatedIndividual<T>> loader) {
		this.loader = loader;
	}

	@Override
	public List<T> load(File file) throws IOException {
		return seq(loader.load(file)).map(EvaluatedIndividual.<T> toIndividual()).toImmutableList();
	}

}
