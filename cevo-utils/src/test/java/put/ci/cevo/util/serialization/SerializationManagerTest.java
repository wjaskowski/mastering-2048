package put.ci.cevo.util.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static put.ci.cevo.util.sequence.Sequences.range;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import put.ci.cevo.util.serialization.mock.MyMockClass;
import put.ci.cevo.util.serialization.mock.MyMockClass.MyMockClassSerializer;

import com.google.common.io.FileBackedOutputStream;

public class SerializationManagerTest {

	private static class MyMockClassSerializerDuplicatedId implements ObjectSerializer<MyMockClass> {
		@Override
		public void save(SerializationManager manager, MyMockClass object, SerializationOutput output)
				throws IOException {
			output.writeDouble(object.getD());
			output.writeString(object.getS());
		}

		@Override
		public MyMockClass load(SerializationManager manager, SerializationInput input) throws IOException,
				SerializationException {
			return new MyMockClass(input.readDouble(), input.readString());
		}

		@Override
		public int getUniqueSerializerId() {
			return new MyMockClassSerializer().getUniqueSerializerId();
		}
	}

	private static class MyMockClassB {
		private final double[] arr;
		private final int i;

		MyMockClassB(int i, double[] arr) {
			this.i = i;
			this.arr = arr;
		}

		public double[] getArr() {
			return arr;
		}

		public int getI() {
			return i;
		}
	}

	private static class MyDataBSerializer implements ObjectSerializer<MyMockClassB> {
		@Override
		public void save(SerializationManager manager, MyMockClassB object, SerializationOutput output)
				throws IOException, SerializationException {
			output.writeInt(object.getI());
			manager.serialize(object.getArr(), output);
		}

		@Override
		public MyMockClassB load(SerializationManager manager, SerializationInput input) throws IOException,
				SerializationException {
			int i = input.readInt();
			double[] arr = manager.deserialize(input);
			return new MyMockClassB(i, arr);
		}

		@Override
		public int getUniqueSerializerId() {
			return 1233121;
		}
	}

	private static class MyGeneric<T> {
		private final T t;
		private final int i;

		public MyGeneric(int i, T t) {
			this.i = i;
			this.t = t;
		}

		public int getI() {
			return i;
		}

		public T getT() {
			return t;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			MyGeneric<?> o = (MyGeneric<?>) obj;
			return (this.i == o.getI() && this.t.equals(o.getT()));
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(t).append(i).build();
		}
	}

	private static class MyGenericSerializer<T> implements ObjectSerializer<MyGeneric<T>> {
		@Override
		public void save(SerializationManager manager, MyGeneric<T> object, SerializationOutput output)
				throws IOException, SerializationException {
			output.writeInt(object.getI());
			manager.serialize(object.getT(), output);
		}

		@Override
		public MyGeneric<T> load(SerializationManager manager, SerializationInput input) throws IOException,
				SerializationException {
			int i = input.readInt();
			T t = manager.deserialize(input);
			return new MyGeneric<T>(i, t);
		}

		@Override
		public int getUniqueSerializerId() {
			return 1234565666;
		}
	}

	private static class MyGenericSerializerOld<T> implements ObjectSerializer<MyGeneric<T>> {
		@Override
		public void save(SerializationManager manager, MyGeneric<T> object, SerializationOutput output)
				throws IOException, SerializationException {
			manager.serialize(object.getT(), output);
		}

		@Override
		public MyGeneric<T> load(SerializationManager manager, SerializationInput input) throws IOException,
				SerializationException {
			T t = manager.deserialize(input);
			return new MyGeneric<T>(0, t);
		}

		@Override
		public int getUniqueSerializerId() {
			return 1234565669;
		}
	}

	private static class NothingReally {
		// Nothing
	}

	private static class NothingReallySerializer implements ObjectSerializer<NothingReally> {

		@Override
		public void save(SerializationManager manager, NothingReally object, SerializationOutput output)
				throws IOException, SerializationException {
			// Nothing
		}

		@Override
		public NothingReally load(SerializationManager manager, SerializationInput input) throws IOException,
				SerializationException {
			return new NothingReally();
		}

		@Override
		public int getUniqueSerializerId() {
			return -1906723890;
		}

	}

	private static final SerializationManager serializationManager = SerializationManagerFactory.create();

	@BeforeClass
	public static void setUp() throws SerializationException {
		serializationManager.register(new MyDataBSerializer());
		serializationManager.register(new MyGenericSerializer<>());
	}

	@Test
	public void testSimple() throws Exception {
		MyMockClass a = new MyMockClass(2.3, "mama");

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		try (SerializationOutput output = new BinarySerializationOutput(b)) {
			serializationManager.serialize(a, output);
		}

		byte[] bytes = b.toByteArray();

		try (BinarySerializationInput input = new BinarySerializationInput(new ByteArrayInputStream(bytes))) {
			MyMockClass deserialized = serializationManager.deserialize(input);
			assertEquals(a, deserialized);
		}
	}

	@Test
	public void testGenericA() throws Exception {
		MyMockClass a = new MyMockClass(2.3, "mama");
		MyGeneric<MyMockClass> gen = new MyGeneric<MyMockClass>(12, a);

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		try (SerializationOutput output = new BinarySerializationOutput(b)) {
			serializationManager.serialize(gen, output);
		}

		byte[] bytes = b.toByteArray();

		try (BinarySerializationInput input = new BinarySerializationInput(new ByteArrayInputStream(bytes))) {
			MyGeneric<MyMockClass> deserialized = serializationManager.deserialize(input);
			assertEquals(gen, deserialized);
		}
	}

	@Test
	public void testGenericB() throws Exception {
		MyMockClassB data = new MyMockClassB(123, new double[] { 123.0, 10.0, -0.123 });
		MyGeneric<MyMockClassB> gen = new MyGeneric<MyMockClassB>(12, data);

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		try (SerializationOutput output = new BinarySerializationOutput(b)) {
			serializationManager.serialize(gen, output);
		}

		byte[] bytes = b.toByteArray();

		try (BinarySerializationInput input = new BinarySerializationInput(new ByteArrayInputStream(bytes))) {
			MyGeneric<MyMockClassB> deserialized = serializationManager.deserialize(input);
			assertEquals(gen.getI(), deserialized.getI());
			assertEquals(gen.getT().getI(), deserialized.getT().getI());
			Assert.assertArrayEquals(gen.getT().getArr(), deserialized.getT().getArr(), 0.0);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testWithNonDefaultSerializer() throws Exception {
		MyGenericSerializerOld oldSerializer = new MyGenericSerializerOld();
		serializationManager.registerAdditional(oldSerializer);

		try {
			MyMockClass a = new MyMockClass(2.3, "mama");
			MyGeneric<MyMockClass> gen = new MyGeneric<MyMockClass>(12, a);

			ByteArrayOutputStream b = new ByteArrayOutputStream();
			try (SerializationOutput output = new BinarySerializationOutput(b)) {
				serializationManager.serialize(gen, oldSerializer, output);
			}

			byte[] bytes = b.toByteArray();

			try (BinarySerializationInput input = new BinarySerializationInput(new ByteArrayInputStream(bytes))) {
				MyGeneric<MyMockClass> deserialized = serializationManager.deserialize(input);
				assertEquals(gen.getT(), deserialized.getT());
				assertEquals(0, deserialized.getI());
			}
		} finally {
			serializationManager.unregister(oldSerializer);
		}
	}

	/**
	 * Registration of the second serializer with the same id should throw exception
	 */
	@Test(expected = SerializationException.class)
	public void testRegisterAdditional() throws Exception {
		MyMockClassSerializerDuplicatedId duplicatedSerializer = new MyMockClassSerializerDuplicatedId();
		try {
			serializationManager.registerAdditional(duplicatedSerializer);

			MyMockClass a = new MyMockClass(2.3, "mama");
			MyGeneric<MyMockClass> gen = new MyGeneric<MyMockClass>(12, a);

			ByteArrayOutputStream b = new ByteArrayOutputStream();
			try (SerializationOutput output = new BinarySerializationOutput(b)) {
				serializationManager.serialize(gen, output);
			}

			byte[] bytes = b.toByteArray();

			try (BinarySerializationInput input = new BinarySerializationInput(new ByteArrayInputStream(bytes))) {
				MyGeneric<MyMockClass> deserialized = serializationManager.deserialize(input);
				assertEquals(gen, deserialized);
			}
		} finally {
			serializationManager.unregister(duplicatedSerializer);
		}
	}

	@Test
	public void testSerializeStreamDeserializeFileOutput() throws IOException, SerializationException {
		ArrayList<MyMockClass> arr = new ArrayList<>();
		for (int i = 0; i < 10000; ++i) {
			arr.add(new MyMockClass(i / 100.0, "a" + i));
		}

		File tmp = null;
		try {
			tmp = File.createTempFile("test", null);

			serializationManager.serialize(arr, tmp);

			ArrayList<MyMockClass> deserialized = serializationManager.deserialize(tmp);
			assertEquals(arr, deserialized);
		} finally {
			if (tmp != null) {
				tmp.delete();
			}
		}
	}

	@Test
	public void testRegisterSerializer() throws Exception {
		try {
			serializationManager.register(new NothingReallySerializer());
			serializationManager.serialize(new NothingReally(), new BinarySerializationOutput(new NullOutputStream()));
		} finally {
			serializationManager.unregister(new NothingReallySerializer());
		}
		assertTrue(true);
	}

	@Test(expected = SerializationException.class)
	public void testUnregisterSerializer() throws Exception {
		serializationManager.register(new NothingReallySerializer());
		serializationManager.unregister(new NothingReallySerializer());
		serializationManager.serialize(new NothingReally(), new BinarySerializationOutput(new NullOutputStream()));
	}

	@Test
	public void testDeserializeStream() throws Exception {
		List<Integer> numbers = range(100000).toList();
		FileBackedOutputStream outputStream = new FileBackedOutputStream(10);
		// serializationManager.serializeStream(numbers, new BinarySerializationOutput(outputStream));
		try (BinarySerializationOutput output = new BinarySerializationOutput(outputStream)) {
			for (Integer integer : numbers) {
				serializationManager.serialize(integer, output);
			}
		}
		
		BinarySerializationInput input = new BinarySerializationInput(outputStream.asByteSource().openBufferedStream());
		List<Integer> result = serializationManager.<Integer> deserializeStream(input).toList();

			assertEquals(numbers, result);
	}
}
