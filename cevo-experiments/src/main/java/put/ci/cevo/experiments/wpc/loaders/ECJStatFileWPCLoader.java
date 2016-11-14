package put.ci.cevo.experiments.wpc.loaders;

import put.ci.cevo.framework.individuals.loaders.FileIndividualLoader;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class ECJStatFileWPCLoader implements FileIndividualLoader<WPC> {

	@Override
	public List<WPC> load(File file) throws IOException {
		LineNumberReader reader = new LineNumberReader(new FileReader(file));
		ArrayList<WPC> arr = new ArrayList<WPC>();
		reader.readLine(); // Ignore generation number
		while (true) {
			WPC wpc = readOneWPC(reader);
			if (wpc == null) {
				break;
			}
			arr.add(wpc);
		}
		return arr;
	}

	private WPC readOneWPC(LineNumberReader reader) throws IOException {

		reader.readLine(); // Ignore evaluated flag
		reader.readLine(); // Ignore fitness
		String line = reader.readLine();
		if (line == null) {
			return null;
		}
		return readWPCfromLine(line);
	}

	private WPC readWPCfromLine(String line) {
		String[] fields = line.split("\\|");
		int length = Integer.parseInt(fields[0].substring(1));
		double[] weights = new double[length];
		for (int i = 0; i < length; ++i) {
			String val = fields[1 + 2 * i];
			weights[i] = decode(val);
		}
		return new WPC(weights);
	}

	private double decode(String val) {
		val = val.substring(1);
		return Double.longBitsToDouble(Long.parseLong(val));
	}
}
