package put.ci.cevo.games.dct.rules;

import org.apache.commons.collections15.Factory;
import put.ci.cevo.games.dct.CARule;

public class AndreBennettKozaRule implements Factory<CARule> {

	private static final String RULE = "" +
			"00000101 00000000 01010101 00000101 00000101 00000000 01010101 00000101" +
			"01010101 11111111 01010101 11111111 01010101 11111111 01010101 11111111";

	@Override public CARule create() {
		return CARule.fromString(RULE);
	}

}
