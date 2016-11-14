package put.ci.cevo.framework.individuals.loaders;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.serialization.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Loads a list of {@link EvaluatedIndividual}s from a file. This class should be used whenever there are multiple
 * individuals serialized into a single file.
 */
public class EvaluatedIndividualsLoader<T> extends SerializedFileIndividualLoader<EvaluatedIndividual<T>> {

	@Override
	public List<EvaluatedIndividual<T>> load(File file) throws IOException {
		try {
			List<EvaluatedIndividual<T>> individuals = manager.deserialize(file);
			return individuals;
		} catch (SerializationException e) {
			throw new IOException(e);
		}
	}

	public static <T> FileIndividualLoader<T> individualLoader() {
		return new GenericIndividualLoader<>(new EvaluatedIndividualsLoader<T>());
	}
}
