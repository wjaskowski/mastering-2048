package put.ci.cevo.framework.serializers;

import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;
import put.ci.cevo.util.vectors.IntegerVector;

import java.io.IOException;

@AutoRegistered(defaultSerializer = true)
public class IntegerVectorSerializer implements ObjectSerializer<IntegerVector> {

	@Override
	public void save(SerializationManager manager, IntegerVector object, SerializationOutput output)
			throws IOException, SerializationException {
		manager.serialize(object.getVector(), output);
	}

	@Override
	public IntegerVector load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		int[] vector = manager.deserialize(input);
		return new IntegerVector(vector);
	}

	@Override
	public int getUniqueSerializerId() {
		return 2013071913;
	}

}