package put.ci.cevo.experiments.wpc.loaders;

import static org.apache.commons.io.FileUtils.readLines;
import static put.ci.cevo.util.sequence.Sequences.seq;

import java.io.File;
import java.io.IOException;
import java.util.List;

import put.ci.cevo.framework.individuals.loaders.DefaultIndividualsLoader;
import put.ci.cevo.framework.individuals.loaders.FileIndividualLoader;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.sequence.transforms.Transform;

public class LegacyWPCShapingPoolLoader extends DefaultIndividualsLoader<WPC> {

	public static class LegacyTextFileWPCLoader implements FileIndividualLoader<WPC> {

		@Override
		public List<WPC> load(File file) throws IOException {
			return seq(readLines(file)).transform(new Transform<String, WPC>() {
				@Override
				public WPC transform(String wpcLine) {
					return fromString(wpcLine.split("=")[0], " ");
				}
			}).toList();
		}

		private static WPC fromString(String line, String separator) {
			String[] ss = line.split(separator);
			if (ss.length != 64) {
				throw new RuntimeException("Invalid size of WPC: " + ss.length);
			}
			double[] wpc = new double[ss.length];
			for (int i = 0; i < ss.length; ++i) {
				wpc[i] = Double.parseDouble(ss[i]);
			}
			return new WPC(wpc);
		}
	}

	public LegacyWPCShapingPoolLoader() {
		super(new LegacyTextFileWPCLoader());
	}

}
