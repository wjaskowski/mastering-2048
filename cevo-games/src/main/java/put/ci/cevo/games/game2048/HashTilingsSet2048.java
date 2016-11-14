package put.ci.cevo.games.game2048;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import put.ci.cevo.util.CollectionUtils;

public class HashTilingsSet2048 implements Iterable<OptimisticHashTiling2048>, Serializable {
	private final List<List<OptimisticHashTiling2048>> tilings;
	private final List<OptimisticHashTiling2048> flattened;

	public HashTilingsSet2048(List<List<OptimisticHashTiling2048>> tilings) {
		this.tilings = tilings;
		this.flattened = CollectionUtils.flatten(tilings);
	}

	public List<List<OptimisticHashTiling2048>> getTilings() {
		return tilings;
	}

	private List<OptimisticHashTiling2048> getMainTilings() {
		return tilings.stream().map(x -> x.get(0)).collect(Collectors.toList());
	}

	static public HashTilingsSet2048 create(int numSegments, int maxSegment, List<List<int[]>> locations,
			int numValues, long totalCapacity) {
		List<List<OptimisticHashTiling2048>> tilings = new ArrayList<>();
		long capacityPerLocationSet = totalCapacity / locations.size();
		for (List<int[]> oneLocationsSet : locations) {
			List<OptimisticHashTiling2048> oneTilingSet = new ArrayList<>();
			OptimisticHashTiling2048 tiling = OptimisticHashTiling2048.create(numSegments, maxSegment, oneLocationsSet.get(0), numValues,
					capacityPerLocationSet);
			oneTilingSet.add(tiling);

			for (int i = 1; i < oneLocationsSet.size(); ++i) {
				oneTilingSet.add(OptimisticHashTiling2048.createWithSharedWeights(tiling, oneLocationsSet.get(i)));
			}

			tilings.add(oneTilingSet);
		}
		return new HashTilingsSet2048(tilings);
	}

	public long getTotalWeights() {
		long len = 0;
		for (OptimisticHashTiling2048 tiling : getMainTilings()) {
			len += tiling.getNumWeights();
		}
		return len;
	}

	@Override
	public Iterator<OptimisticHashTiling2048> iterator() {
		return flattened.iterator();
	}

	public int getNumAllTilings() {
		return flattened.size();
	}

	public int getNumMainTilings() {
		return getMainTilings().size();
	}

	public OptimisticHashTiling2048 getTiling(int idx) {
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
		return new EqualsBuilder().append(tilings, obj).build();
	}
}
