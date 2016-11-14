package put.ci.cevo.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import static put.ci.cevo.util.sequence.Sequences.seq;

public class FileUtils {

	private FileUtils() {
	}

	public static List<String> getSubdirNames(File file) {
		return seq(file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		})).toList();

	}
}
