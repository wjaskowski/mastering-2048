package put.ci.cevo.framework.serializers;

import put.ci.cevo.framework.fitness.MultiobjectiveFitness;
import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

import java.io.IOException;
import java.util.List;

@AutoRegistered(defaultSerializer = true)
public class MultiobjectiveFitnessSerializer implements ObjectSerializer<MultiobjectiveFitness> {

	@Override
	public void save(SerializationManager manager, MultiobjectiveFitness object, SerializationOutput output)
			throws IOException, SerializationException {
		manager.serialize(object.getObjectives(), output);
	}

	@Override
	public MultiobjectiveFitness load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		List<Double> fitness = manager.deserialize(input);
		return new MultiobjectiveFitness(fitness);
	}

	@Override
	public int getUniqueSerializerId() {
		return 98653;
	}

}
