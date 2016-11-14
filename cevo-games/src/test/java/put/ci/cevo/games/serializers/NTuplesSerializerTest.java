package put.ci.cevo.games.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTupleRandomFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.util.serialization.BinarySerializationInput;
import put.ci.cevo.util.serialization.BinarySerializationOutput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;
import put.ci.cevo.util.serialization.SerializationOutput;

public class NTuplesSerializerTest {

	@Test
	public void testSave() throws Exception {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		NTupleRandomFactory nTupleRandomFactory = new NTupleRandomFactory(3, 5, new RectSize(8), -1.0, 1.0);
		List<NTuple> tuples = new ArrayList<>();
		for (int i = 0; i < 10; ++i) {
			tuples.add(nTupleRandomFactory.create(random));
		}
		NTuples ntuples = new NTuples(tuples, new RotationMirrorSymmetryExpander(new RectSize(8)));

		SerializationManager manager = SerializationManagerFactory.create();

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try (SerializationOutput output = new BinarySerializationOutput(byteStream)) {
			manager.serialize(ntuples, output);
		}

		try (BinarySerializationInput input = new BinarySerializationInput(new ByteArrayInputStream(
			byteStream.toByteArray()))) {

			NTuples deserialized = manager.deserialize(input);
			Assert.assertEquals(ntuples, deserialized);
		}
	}
}
