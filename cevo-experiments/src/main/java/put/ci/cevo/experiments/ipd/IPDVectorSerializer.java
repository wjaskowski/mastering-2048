package put.ci.cevo.experiments.ipd;

import java.io.IOException;

import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

@AutoRegistered(defaultSerializer = true)
public class IPDVectorSerializer implements ObjectSerializer<IPDVector> {

	@Override
	public void save(SerializationManager manager, IPDVector object, SerializationOutput output) throws IOException,
			SerializationException {
		manager.serialize(object.getVector(), output);
	}

	@Override
	public IPDVector load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		int[] vector = manager.deserialize(input);
		return new IPDVector(vector);
	}

	@Override
	public int getUniqueSerializerId() {
		return 2014010401;
	}

}
