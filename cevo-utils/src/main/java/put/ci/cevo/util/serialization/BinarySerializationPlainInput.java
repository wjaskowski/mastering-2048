package put.ci.cevo.util.serialization;

import java.io.*;

/**
 * This class exists only for backward compatibility (old-serialized files)
 */
public class BinarySerializationPlainInput implements SerializationInput {

	private final DataInputStream input;

	public BinarySerializationPlainInput(InputStream input) throws IOException {
		this.input = new DataInputStream(input);
	}

	public BinarySerializationPlainInput(FileInputStream input) throws IOException {
		this(new BufferedInputStream(input));
	}

	@Override
	public int readInt() throws IOException {
		return input.readInt();
	}

	@Override
	public long readLong() throws IOException {
		return input.readLong();
	}

	@Override
	public String readString() throws IOException {
		return input.readUTF();
	}

	@Override
	public double readDouble() throws IOException {
		return input.readDouble();
	}

	@Override
	public void close() throws IOException {
		input.close();
	}

	@Override
	public int available() throws IOException {
		return input.available();
	}

	@Override
	public int[] readIntArray() throws IOException {
		throw new IllegalStateException("I cannot do that");
	}

	@Override
	public double[] readDoubleArray() throws IOException {
		throw new IllegalStateException("I cannot do that");
	}

	@Override
	public float[] readFloatArray() throws IOException {
		throw new IllegalStateException("I cannot do that");
	}
}
