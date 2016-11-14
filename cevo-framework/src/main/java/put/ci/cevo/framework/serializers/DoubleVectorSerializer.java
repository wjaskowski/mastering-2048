package put.ci.cevo.framework.serializers;

import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;
import put.ci.cevo.util.vectors.DoubleVector;

import java.io.IOException;

@AutoRegistered(defaultSerializer = true)
public class DoubleVectorSerializer implements ObjectSerializer<DoubleVector> {

	@Override
	public void save(SerializationManager manager, DoubleVector object, SerializationOutput output) throws IOException,
			SerializationException {
		manager.serialize(object.toArray(), output);
	}

	@Override
	public DoubleVector load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		double[] vector = manager.deserialize(input);
		return new DoubleVector(vector);
	}

	@Override
	public int getUniqueSerializerId() {
		return 2013110201;
	}

}