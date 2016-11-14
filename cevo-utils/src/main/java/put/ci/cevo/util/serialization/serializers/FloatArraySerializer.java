package put.ci.cevo.util.serialization.serializers;

import java.io.IOException;

import put.ci.cevo.util.serialization.*;

@AutoRegistered(defaultSerializer = true)
public class FloatArraySerializer implements ObjectSerializer<float[]> {

	@Override
	public void save(SerializationManager manager, float[] arr, SerializationOutput output) throws IOException {
		output.writeFloatArray(arr);
	}

	@Override
	public float[] load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		return input.readFloatArray();
	}

	@Override
	public int getUniqueSerializerId() { return 983462925; };
}