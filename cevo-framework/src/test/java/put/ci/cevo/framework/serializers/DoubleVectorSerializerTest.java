package put.ci.cevo.framework.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.util.vectors.DoubleVector;
import put.ci.cevo.util.serialization.BinarySerializationInput;
import put.ci.cevo.util.serialization.BinarySerializationOutput;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;
import put.ci.cevo.util.serialization.SerializationOutput;

public class DoubleVectorSerializerTest {

	@Test
	public void testDoubleVectorSerialize() throws IOException, SerializationException {
		SerializationManager manager = SerializationManagerFactory.create();
		DoubleVectorSerializer serializer = new DoubleVectorSerializer();

		DoubleVector vector = new DoubleVector(new double[] { 1.0, 3.0, 5.6, 0.8431431, 0.0000001 });

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		SerializationOutput output = new BinarySerializationOutput(byteStream);

		serializer.save(manager, vector, output);

		BinarySerializationInput input = new BinarySerializationInput(
			new ByteArrayInputStream(byteStream.toByteArray()));

		DoubleVector deserialized = serializer.load(manager, input);
		Assert.assertEquals(vector, deserialized);
	}
}
