package put.ci.cevo.rl.agent.functions.wpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.rl.agent.functions.wpc.WPC.WPCSerializer;
import put.ci.cevo.util.serialization.BinarySerializationInput;
import put.ci.cevo.util.serialization.BinarySerializationOutput;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;
import put.ci.cevo.util.serialization.SerializationOutput;

public class WPCSerializerTest {

	@Test
	public void test() throws IOException, SerializationException {
		SerializationManager manager = SerializationManagerFactory.create();
		WPCSerializer serializer = new WPCSerializer();

		WPC wpc = new WPC(new double[] { 1.2, 123.2, 31.3, -0.1 });

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		SerializationOutput output = new BinarySerializationOutput(byteStream);

		serializer.save(manager, wpc, output);

		BinarySerializationInput input = new BinarySerializationInput(
			new ByteArrayInputStream(byteStream.toByteArray()));

		WPC deserialized = serializer.load(manager, input);
		Assert.assertEquals(wpc, deserialized);
	}

	@Test
	public void testAutoRegisterSerializer() throws SerializationException, IOException {
		SerializationManager manager = SerializationManagerFactory.create();

		WPC wpc = new WPC(new double[] { 1.2, 123.2, 31.3, -0.1 });
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		SerializationOutput output = new BinarySerializationOutput(byteStream);
		manager.serialize(wpc, output);

		BinarySerializationInput input = new BinarySerializationInput(
			new ByteArrayInputStream(byteStream.toByteArray()));

		WPC deserialized = manager.deserialize(input);
		Assert.assertEquals(wpc, deserialized);
	}
}