package put.ci.cevo.framework.individuals.loaders;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.serialization.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Loads a single {@link EvaluatedIndividual} from a file. It assumes, that there is only one individual in a given
 * file. To fulfill the {@link FileIndividualLoader} contract, the loaded individual is wrapped into a list.
 */
public class EvaluatedIndividualLoader<T> extends SerializedFileIndividualLoader<EvaluatedIndividual<T>> {

	@Override
	public List<EvaluatedIndividual<T>> load(File file) throws IOException {
		try {
			return singletonList(manager.<EvaluatedIndividual<T>> deserialize(file));
		} catch (SerializationException e) {
			throw new IOException(e);
		}
	}

	public static <T> FileIndividualLoader<T> individualLoader() {
		return new GenericIndividualLoader<>(new EvaluatedIndividualLoader<T>());
	}

}
