package put.ci.cevo.framework.serializers;

import put.ci.cevo.framework.fitness.ScalarFitness;
import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

import java.io.IOException;

@AutoRegistered(defaultSerializer = true)
public class ScalarFitnessSerializer implements ObjectSerializer<ScalarFitness> {

	@Override
	public void save(SerializationManager manager, ScalarFitness object, SerializationOutput output)
			throws IOException, SerializationException {
		output.writeDouble(object.fitness());
	}

	@Override
	public ScalarFitness load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		double fitness = input.readDouble();
		return new ScalarFitness(fitness);
	}

	@Override
	public int getUniqueSerializerId() {
		return 45154124;
	}

}
