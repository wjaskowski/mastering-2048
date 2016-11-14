package put.ci.cevo.framework.serializers;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

import java.io.IOException;

@Deprecated
@AutoRegistered
public class EvaluatedIndividualSerializerOld<T> implements ObjectSerializer<EvaluatedIndividual<T>> {

	@Override
	public void save(SerializationManager manager, EvaluatedIndividual<T> evaluated, SerializationOutput output)
			throws IOException, SerializationException {
		output.writeDouble(evaluated.getFitness());
		output.writeInt(evaluated.getGeneration());
		output.writeLong(evaluated.getEffort());
		manager.serialize(evaluated.getIndividual(), output);
	}

	@Override
	public EvaluatedIndividual<T> load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		double fitness = input.readDouble();
		int generation = input.readInt();
		int effort = input.readInt();
		T individual = manager.deserialize(input);
		return new EvaluatedIndividual<T>(individual, fitness, generation, effort);
	}

	@Override
	public int getUniqueSerializerId() {
		return 12341134;
	}
}
