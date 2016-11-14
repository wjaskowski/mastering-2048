package put.ci.cevo.games.serializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

@AutoRegistered(defaultSerializer = true)
public final class NTuplesSerializer implements ObjectSerializer<NTuples> {

	// TODO: I should get rid of saving SymmetryExpander. SymmetryExpander is not important. What is important are
	// getMain() and getAll()
	@Override
	public void save(SerializationManager manager, NTuples object, SerializationOutput output) throws IOException,
			SerializationException {

		manager.serialize(object.getMain(), output);

		output.writeInt(object.getAll().size());
		for (NTuple symmetric : object.getAll()) {
			output.writeInt(getMainIdxForNTuple(symmetric, object.getMain()));
			output.writeIntArray(symmetric.getLocations());
		}
	}

	private int getMainIdxForNTuple(NTuple ntuple, List<NTuple> main) {
		for (int i = 0; i < main.size(); ++i) {
			if (ntuple.getWeights() == main.get(i).getWeights()) // the same reference
				return i;
		}
		throw new IllegalStateException();
	}

	@Override
	public NTuples load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		List<NTuple> main = manager.deserialize(input);
		int allCount = input.readInt();
		List<NTuple> all = new ArrayList<>(allCount);
		for (int i = 0; i < allCount; ++i) {
			int idx = input.readInt();
			int[] locations = input.readIntArray();
			all.add(NTuple.newWithSharedWeights(main.get(idx), locations));
		}
		return new NTuples(main, all);
	}

	@Override
	public int getUniqueSerializerId() {
		return 890349344;
	}
}
