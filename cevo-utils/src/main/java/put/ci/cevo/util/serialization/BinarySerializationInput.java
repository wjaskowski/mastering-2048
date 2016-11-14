package put.ci.cevo.util.serialization;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class BinarySerializationInput implements SerializationInput {

	private final ObjectInputStream input;

	public BinarySerializationInput(InputStream input) throws IOException {
		this.input = new ObjectInputStream(input);
	}

	static BinarySerializationInput fromGzipped(InputStream input) throws IOException {
		return new BinarySerializationInput(new GZIPInputStream(input));
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
		try {
			return (int[]) input.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}

	@Override
	public double[] readDoubleArray() throws IOException {
		try {
			return (double[]) input.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}

	@Override
	public float[] readFloatArray() throws IOException {
		try {
			return (float[]) input.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}
}
