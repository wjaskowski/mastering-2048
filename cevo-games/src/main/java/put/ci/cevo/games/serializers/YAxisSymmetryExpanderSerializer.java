package put.ci.cevo.games.serializers;

import java.io.IOException;

import put.ci.cevo.games.encodings.ntuple.expanders.YAxisSymmetryExpander;
import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

@AutoRegistered(defaultSerializer = true)
public final class YAxisSymmetryExpanderSerializer implements ObjectSerializer<YAxisSymmetryExpander> {

	@Override
	public void save(SerializationManager manager, YAxisSymmetryExpander object, SerializationOutput output)
			throws IOException, SerializationException {
		output.writeInt(object.boardWidth());
	}

	@Override
	public YAxisSymmetryExpander load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		int boardWidth = input.readInt();
		return new YAxisSymmetryExpander(boardWidth);
	}

	@Override
	public int getUniqueSerializerId() {
		return 121345705;
	}
}
