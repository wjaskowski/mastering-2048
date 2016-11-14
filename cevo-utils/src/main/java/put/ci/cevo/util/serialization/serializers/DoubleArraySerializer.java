package put.ci.cevo.util.serialization.serializers;

import java.io.IOException;

import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;

//TODO: This class looks spurious
@AutoRegistered(defaultSerializer = true)
public class DoubleArraySerializer implements ObjectSerializer<double[]> {

	@Override
	public void save(SerializationManager manager, double[] arr, SerializationOutput output) throws IOException {
		output.writeDoubleArray(arr);
	}

	@Override
	public double[] load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		return input.readDoubleArray();
	}

	@Override
	public int getUniqueSerializerId() {
		return 121243534;
	}
}