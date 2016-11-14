package put.ci.cevo.games.game2048;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import put.ci.cevo.games.board.BoardUtils;

public final class SpecialBinarySerializer {

	public static void serialize(TilingsSet2048 tiles, DataOutput output, int format) throws IOException {
		output.writeInt(format);
		int floatsWritten = 0;
		int stages = tiles.getTilings().get(0).get(0).getNumSegments();
		int numValues = tiles.getTilings().get(0).get(0).getNumTileValues();
		output.writeInt(numValues);
		output.writeInt(stages);                                        // # of stages
		for (int stage = 0; stage < (1 << stages); ++stage) {
			output.writeInt(tiles.getNumMainTilings());                    // # of different tilings
			for (List<Tiling2048> symmetricTilings : tiles.getTilings()) {
				Tiling2048 main = symmetricTilings.get(0);
				output.writeInt(main.getNumLocations());                // # locations in a single NTuple
				output.writeInt(symmetricTilings.size());                // # Symmetric (sharing-weights) NTuples
				for (Tiling2048 tiling : symmetricTilings) {
					for (int location : tiling.getLocations()) {
						output.writeInt(BoardUtils.marginPosToPos(location, State2048.SIZE));
					}
				}
				// write data
				if (format == 1) {
					floatsWritten += writeCompressedLUT(output, stage, main, numValues);
				} else if (format == 2) {
					floatsWritten += writePlainLUT(output, stage, main);
				} else {
					throw new NotImplementedException();
				}
			}
		}

		System.out.println("Floats outputted: " + floatsWritten);
	}

	private static int writePlainLUT(DataOutput output, int stage, Tiling2048 main) throws IOException {
		int floatsWritten = 0;
		float[] lut = main.getLUT();
		for (int address = main.stageBegins(stage); address < main.stageBegins(stage + 1); address += 1) {
			output.writeFloat(lut[address]);
			floatsWritten += 1;
		}
		return floatsWritten;
	}

	public static int writeCompressedLUT(DataOutput output, int stage, Tiling2048 main, int numValues)
			throws IOException {
		int floatsWritten = 0;
		float[] lut = main.getLUT();
		for (int address = main.stageBegins(stage); address < main.stageBegins(
				stage + 1); address += numValues) {
			boolean allZero = true;
			for (int a = 0; a < numValues; ++a) {
				if (lut[address + a] != 0.0) {
					allZero = false;
					break;
				}
			}
			if (!allZero) {
				for (int a = address; a < address + numValues; ++a) {
					output.writeFloat(lut[a]);
					floatsWritten += 1;
				}
			} else {
				output.writeFloat(Float.NEGATIVE_INFINITY);
				floatsWritten += 1;
			}
		}
		return floatsWritten;
	}

	public static TilingsSet2048 deserialize(DataInput input) throws IOException {
		int format = input.readInt();
		if (!(format == 1 || format == 2))
			throw new NotImplementedException();

		List<List<Tiling2048>> tilings = new ArrayList<>();
		List<float[]> luts = new ArrayList<>();

		int numValues = input.readInt(); // # of values
		int stages = input.readInt();    // # of stages
		for (int stage = 0; stage < (1 << stages); ++stage) {
			int numMainTilings = input.readInt();
			for (int i = 0; i < numMainTilings; ++i) {
				if (stage == 0) {
					tilings.add(new ArrayList<>());
				}
				int numLocations = input.readInt();               // # locations in a single NTuple
				int numSymmetric = input.readInt();               // # Symmetric (sharing-weights) NTuples

				for (int j = 0; j < numSymmetric; ++j) {
					int[] locations = new int[numLocations];
					for (int k = 0; k < numLocations; ++k) {
						locations[k] = BoardUtils.toMarginPos(State2048.BOARD_SIZE, input.readInt());
					}
					if (stage == 0) {
						if (j == 0) {
							luts.add(new float[Tiling2048.computeNumWeights(numValues, numLocations, stages)]);
						}
						tilings.get(i).add(new Tiling2048(locations, luts.get(i), numValues, stages, numValues - 1));
					}
				}

				// read data into luts[i]
				if (format == 1) {
					Tiling2048 main = tilings.get(i).get(0);
					for (int address = main.stageBegins(stage); address < main.stageBegins(
							stage + 1); address += numValues) {
						float first = input.readFloat();
						if (first != Float.NEGATIVE_INFINITY) {
							luts.get(i)[address] = first;
							for (int a = address + 1; a < address + numValues; ++a) {
								luts.get(i)[a] = input.readFloat();
							}
						}
					}
				} else if (format == 2) {
					Tiling2048 main = tilings.get(i).get(0);
					for (int address = main.stageBegins(stage); address < main.stageBegins(
							stage + 1); address += 1) {
						luts.get(i)[address] = input.readFloat();
					}
				}
			}
		}
		return new TilingsSet2048(tilings);
	}
}
