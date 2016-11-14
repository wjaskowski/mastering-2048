package put.ci.cevo.games.dct.rules;

import org.apache.commons.collections15.Factory;
import put.ci.cevo.games.dct.CARule;

public class JuillePollackCoevolution1Rule implements Factory<CARule> {

	private static final String RULE = "" +
			"00000001 00010100 00110000 11010111 00010001 00001111 00111001 01010111" +
			"00000101 10110100 11111111 00010111 11110001 00111101 11111001 01010111";

	@Override public CARule create() {
		return CARule.fromString(RULE);
	}

}
