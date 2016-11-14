package put.ci.cevo.games.othello;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.IdentitySymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryUtils;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public final class OthelloLeague {
	private OthelloLeague() {
		// A static class
	}

	/*
	 * Simply weights separated by spaces
	 */
	public static String toOthelloLeagueFormat(WPC wpc) {
		Joiner jointer = Joiner.on(" ");
		return jointer.join(Arrays.asList(ArrayUtils.toObject(wpc.getWeights())));
	}

	@SuppressWarnings("UnusedDeclaration")
	public static void saveInOthelloLeagueFormat(WPC wpc, File file) {
		try {
			FileUtils.writeStringToFile(file, toOthelloLeagueFormat(wpc));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String toOthelloLeagueFormat(NTuples ntuples, RectSize boardSize) {
		SymmetryExpander expander = ntuples.getSymmetryExpander();
		Joiner joiner = Joiner.on(" ");

		String s = "{ " + ntuples.getMain().size() + "\n";
		for (NTuple tuple : ntuples.getMain()) {
			List<int[]> symmetrical = SymmetryUtils.createSymmetric(tuple.getLocations(), expander);
			s += "  { " + tuple.getSize() + " " + symmetrical.size() + "\n";
			for (int[] locations : symmetrical) {
				List<Integer> normalLocations = new ArrayList<>();
				for (int loc : locations) {
					normalLocations.add(BoardUtils.marginPosToPos(loc, boardSize.rows(), boardSize.columns()));
				}
				s += "    { " + joiner.join(normalLocations) + " }\n";
			}
			s += "\n";
			s += "    { " + joiner.join(Arrays.asList(ArrayUtils.toObject(tuple.getWeights()))) + " }\n";
			s += "  }\n";
		}
		s += "}\n";

		return s;
	}

	public static void saveInOthelloLeagueFormat(NTuples ntuples, File file) {
		saveInOthelloLeagueFormat(ntuples, file, OthelloBoard.SIZE);
	}

	public static void saveInOthelloLeagueFormat(NTuples ntuples, File file, RectSize boardSize) {
		try {
			FileUtils.writeStringToFile(file, toOthelloLeagueFormat(ntuples, boardSize));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static NTuples fromOthelloLeagueFormat(File file) {
		try {
			return fromOthelloLeagueFormat(Files.toString(file, Charset.defaultCharset()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static NTuples fromOthelloLeagueFormat(String str) {
		NTuples.Builder builder = new NTuples.Builder(new IdentitySymmetryExpander());

		str = str.replace('{', ' ').replace('}', ' ');

		try (Scanner scanner = new Scanner(str)) {
			scanner.useLocale(Locale.US);
			int tuples = scanner.nextInt();

			for (int i = 0; i < tuples; ++i) {
				ArrayList<int[]> locationsList = new ArrayList<>();

				int tupleLength = scanner.nextInt();
				int symmetries = scanner.nextInt();
				for (int j = 0; j < symmetries; ++j) {
					int[] locations = new int[tupleLength];
					for (int k = 0; k < tupleLength; ++k) {
						int zeroBasedPos = scanner.nextInt();
						locations[k] = BoardUtils.toMarginPos(OthelloBoard.SIZE, zeroBasedPos);
					}
					locationsList.add(locations);
				}
				int numWeights = NTuple.computeNumWeights(OthelloBoard.NUM_VALUES, tupleLength);
				float[] weights = new float[numWeights];
				for (int j = 0; j < numWeights; ++j) {
					weights[j] = (float)scanner.nextDouble();
				}

				for (int[] locations : locationsList) {
					builder.add(new NTuple(OthelloBoard.NUM_VALUES, locations, weights));
				}
			}
		}

		return builder.build();
	}

	@SuppressWarnings("UnusedDeclaration")
	public static NTuples loadFromOthelloLeagueFormat(File file) {
		try {
			return fromOthelloLeagueFormat(FileUtils.readFileToString(file));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
