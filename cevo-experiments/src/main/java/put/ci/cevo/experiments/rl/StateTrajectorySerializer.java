package put.ci.cevo.experiments.rl;

import java.io.IOException;
import java.util.List;

import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.environment.StateTrajectory;
import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

@AutoRegistered(defaultSerializer = true)
public class StateTrajectorySerializer<S extends State> implements ObjectSerializer<StateTrajectory<S>> {

	@Override
	public void save(SerializationManager manager, StateTrajectory<S> object, SerializationOutput output)
			throws IOException, SerializationException {
		manager.serialize(object.getStates(), output);
	}

	@Override
	public StateTrajectory<S> load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		List<S> states = manager.deserialize(input);
		return new StateTrajectory<>(states);
	}

	@Override
	public int getUniqueSerializerId() {
		return 20130722;
	}
}