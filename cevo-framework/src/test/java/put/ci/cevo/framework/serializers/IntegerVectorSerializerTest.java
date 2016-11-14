package put.ci.cevo.framework.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.util.vectors.IntegerVector;
import put.ci.cevo.util.serialization.BinarySerializationInput;
import put.ci.cevo.util.serialization.BinarySerializationOutput;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;
import put.ci.cevo.util.serialization.SerializationOutput;

public class IntegerVectorSerializerTest {

	@Test
	public void testIntegerVectorSerialize() throws IOException, SerializationException {
		SerializationManager manager = SerializationManagerFactory.create();
		IntegerVectorSerializer serializer = new IntegerVectorSerializer();

		IntegerVector vector = new IntegerVector(new int[] { 1, 1, 3, 3, 5, 5, 0, 2, 3, -30, 0, -999 });

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		SerializationOutput output = new BinarySerializationOutput(byteStream);

		serializer.save(manager, vector, output);

		BinarySerializationInput input = new BinarySerializationInput(
			new ByteArrayInputStream(byteStream.toByteArray()));

		IntegerVector deserialized = serializer.load(manager, input);
		Assert.assertEquals(vector, deserialized);
	}
}
