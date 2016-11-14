package put.ci.cevo.util.serialization.serializers;

import java.io.IOException;

import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;

//TODO: This class seems spurious
@AutoRegistered(defaultSerializer = true)
public class IntArraySerializer implements ObjectSerializer<int[]> {

	@Override
	public void save(SerializationManager manager, int[] arr, SerializationOutput output) throws IOException {
		output.writeIntArray(arr);
	}

	@Override
	public int[] load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		return input.readIntArray();
	}

	@Override
	public int getUniqueSerializerId() {
		return 14232534;
	}
}