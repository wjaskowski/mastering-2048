package put.ci.cevo.util.serialization.serializers;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import put.ci.cevo.util.serialization.BinarySerializationInput;
import put.ci.cevo.util.serialization.BinarySerializationOutput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.mock.MyMockClass;

public class MapSerializerTest {

	private final SerializationManager serializationManager = SerializationManagerFactory.create();

	@Test
	public void testSaveLoad() throws Exception {
		MapSerializer<Integer, MyMockClass> serializer = new MapSerializer<Integer, MyMockClass>();
		HashMap<Integer, MyMockClass> map = new HashMap<Integer, MyMockClass>();

		map.put(14, new MyMockClass(-12.3, "asdfasd"));
		map.put(15, new MyMockClass(12.343, "asasdfdfasd"));
		map.put(19, new MyMockClass(132.3, "asdfasasdfadfd"));
		map.put(-14, new MyMockClass(212.3, "asdfasgasdgasd"));

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try (SerializationOutput output = new BinarySerializationOutput(byteStream)) {
			serializer.save(serializationManager, map, output);
		}

		try (BinarySerializationInput input = new BinarySerializationInput(new ByteArrayInputStream(
			byteStream.toByteArray()))) {

			Map<Integer, MyMockClass> deserialized = serializer.load(serializationManager, input);
			assertTrue(map.equals(deserialized));
		}
	}
}
