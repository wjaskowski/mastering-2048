package put.ci.cevo.framework.serializers;

import put.ci.cevo.framework.algorithms.multiobjective.nsga2.NSGA2Fitness;
import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

import java.io.IOException;
import java.util.List;

@AutoRegistered(defaultSerializer = true)
public class NSGA2FitnessSerializer implements ObjectSerializer<NSGA2Fitness> {

	@Override
	public void save(SerializationManager manager, NSGA2Fitness object, SerializationOutput output) throws IOException,
			SerializationException {
		output.writeInt(object.rank);
		output.writeDouble(object.sparsity);
		manager.serialize(object.getObjectives(), output);
		
	}

	@Override
	public NSGA2Fitness load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		int rank = input.readInt();
		double sparsity = input.readDouble();
		List<Double> fitness = manager.deserialize(input);
		return new NSGA2Fitness(fitness, rank, sparsity);
	}

	@Override
	public int getUniqueSerializerId() {
		return 543202;
	}

}
