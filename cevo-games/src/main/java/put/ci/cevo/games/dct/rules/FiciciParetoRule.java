package put.ci.cevo.games.dct.rules;

import org.apache.commons.collections15.Factory;
import put.ci.cevo.games.dct.CARule;

public class FiciciParetoRule implements Factory<CARule> {

	private static final String RULE = "" +
			"00010000 01010011 00000000 11010010 00000000 01010001 00001111 01011011" +
			"00011111 01010011 11111111 11011111 00001111 01010101 11001111 01011111";

	@Override public CARule create() {
		return CARule.fromString(RULE);
	}

}
