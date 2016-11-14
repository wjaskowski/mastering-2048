package put.ci.cevo.games.game2048;

public class OptimisticHashMap {

	private float[] table;
	private int heuristicCount;

	public OptimisticHashMap(int capacity) {
		table = new float[capacity];
	}

	public float get(long key) {
		return table[address(key)];
	}

	public void put(long key, float value) {
		int address = address(key);
		//TODO: Hopefully this check does not influence performance
		if (table[address] == 0.0) {
			heuristicCount += 1;
		}
		table[address] = value;
	}

	private int address(long key) {
		return (int)(Math.floorMod(hash(key), table.length));
	}

	private long hash(long k) {
		k ^= k >>> 33;
		k *= -49064778989728563L;
		k ^= k >>> 33;
		k *= -4265267296055464877L;
		k ^= k >>> 33;
		return k;
	}

	public int getCapacity() {
		return table.length;
	}

	public void addTo(int key, float delta) {
		int address = address(key);
		//TODO: Hopefully this check does not influence performance
		if (delta != 0 && table[address] == 0.0) {
			heuristicCount += 1;
		}
		table[address] += delta;
	}

	public int getNonZero() {
		return heuristicCount;
	}
}

