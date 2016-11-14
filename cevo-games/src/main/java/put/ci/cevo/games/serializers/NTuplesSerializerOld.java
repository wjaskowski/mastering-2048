package put.ci.cevo.games.serializers;

import java.io.IOException;
import java.util.List;

import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;
import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

@AutoRegistered(defaultSerializer = false)
public final class NTuplesSerializerOld implements ObjectSerializer<NTuples> {

	// TODO: I should get rid of saving SymmetryExpander. SymmetryExpander is not important. What is important are
	// getMain() and getAll()
	@Override
	public void save(SerializationManager manager, NTuples object, SerializationOutput output) throws IOException,
			SerializationException {
		manager.serialize(object.getMain(), output);
		manager.serialize(object.getSymmetryExpander(), output);
	}

	@Override
	public NTuples load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		List<NTuple> tuples = manager.deserialize(input);
		SymmetryExpander expander = manager.deserialize(input);
		return new NTuples(tuples, expander);
	}

	@Override
	public int getUniqueSerializerId() {
		return 796418617;
	}
}
