package put.ci.cevo.games.game2048;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.CollectionUtils;

public class TilingsSet2048 implements Iterable<Tiling2048>, Serializable {
	private final List<List<Tiling2048>> tilings;
	private final List<Tiling2048> flattened;

	public TilingsSet2048(List<List<Tiling2048>> tilings) {
		this.tilings = tilings;
		this.flattened = CollectionUtils.flatten(tilings);
	}

	public List<List<Tiling2048>> getTilings() {
		return tilings;
	}

	private List<Tiling2048> getMainTilings() {
		return tilings.stream().map(x -> x.get(0)).collect(Collectors.toList());
	}

	static public TilingsSet2048 createWithRandomWeights(int numSegments, int maxSegment, List<List<int[]>> locations,
			int numValues, double minWeight, double maxWeight, RandomDataGenerator random) {
		List<List<Tiling2048>> tilings = new ArrayList<>();
		for (List<int[]> oneLocationsSet : locations) {
			List<Tiling2048> oneTilingSet = new ArrayList<>();
			Tiling2048 tiling = Tiling2048.createWithRandomWeights(numSegments, maxSegment, oneLocationsSet.get(0),
					numValues, minWeight, maxWeight, random);
			oneTilingSet.add(tiling);

			for (int i = 1; i < oneLocationsSet.size(); ++i) {
				oneTilingSet.add(Tiling2048.createWithSharedWeights(tiling, oneLocationsSet.get(i)));
			}

			tilings.add(oneTilingSet);
		}
		return new TilingsSet2048(tilings);
	}

	public long getTotalWeights() {
		int len = 0;
		for (Tiling2048 tiling : getMainTilings()) {
			len += tiling.getNumWeights();
		}
		return len;
	}

	@Override
	public Iterator<Tiling2048> iterator() {
		return flattened.iterator();
	}

	public int getNumAllTilings() {
		return flattened.size();
	}

	public int getNumMainTilings() {
		return getMainTilings().size();
	}

	public Tiling2048 getTiling(int idx) {
		return flattened.get(idx);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tilings);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TilingsSet2048 other = (TilingsSet2048) obj;
		return tilings.equals(other.tilings);
	}

	public long countNonZeroWeights() {
		int cnt = 0;
		for (List<Tiling2048> x : getTilings()) {
			float[] lut = x.get(0).getLUT();
			for (float w : lut) {
				if (w != 0) {
					cnt += 1;
				}
			}
		}
		return cnt;
	}

	public long[] consecutiveZeros() {
		long[] zeros = new long[100];
		for (List<Tiling2048> x : getTilings()) {
			float[] lut = x.get(0).getLUT();
			int consecutive = 0;
			for (float w : lut) {
				if (w != 0) {
					if (consecutive != 0) {
						zeros[Math.min(99, consecutive)] += 1;
					}
					consecutive = 0;
				} else {
					consecutive += 1;
				}
			}
		}
		return zeros;
	}

	@Override
	public String toString() {
		return "main: " + getNumMainTilings() + "; all: " + getNumAllTilings() + "; weights: " + getTotalWeights();
	}

	public long countStrangeWeights() {
		int cnt = 0;
		for (List<Tiling2048> x : getTilings()) {
			float[] lut = x.get(0).getLUT();
			for (float w : lut) {
				if (w < -100000 || w > 1000000) {
					cnt += 1;
				}
			}
		}
		return cnt;
	}
}
