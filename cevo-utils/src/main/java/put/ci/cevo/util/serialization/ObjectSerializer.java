package put.ci.cevo.util.serialization;

import java.io.IOException;

public interface ObjectSerializer<T> {

	void save(SerializationManager manager, T object, SerializationOutput output) throws IOException,
			SerializationException;

	T load(SerializationManager manager, SerializationInput input) throws IOException, SerializationException;

	int getUniqueSerializerId();
}
