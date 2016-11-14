package put.ci.cevo.games.dct;

import java.util.Arrays;

import com.google.common.base.Objects;

import static java.lang.Character.getNumericValue;

public class CARule {

	private final int[] rule;

	public CARule(int[] rule) {
		this.rule = rule;
	}

	public int get(int i) {
		return rule[i];
	}

	public int getFirst() {
		return rule[0];
	}

	public int getLast() {
		return rule[size() - 1];
	}

	public int size() {
		return rule.length;
	}

	public int[] toArray() {
		return rule.clone();
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(rule);
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
		CARule other = (CARule) obj;
		if (!Arrays.equals(rule, other.rule)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("rule", Arrays.toString(rule)).toString();
	}

	public static CARule fromString(String bitString) {
		char[] chars = bitString.replaceAll("\\s+","").toCharArray();
		int[] rule = new int[chars.length];
		for (int i = 0; i < chars.length; i++) {
			rule[i] = getNumericValue(chars[i]);
		}
		return new CARule(rule);
	}
}
