package put.ci.cevo.games.dct.rules;

import org.apache.commons.collections15.Factory;
import put.ci.cevo.games.dct.CARule;

public class JuillePollackCoevolution2Rule implements Factory<CARule> {

	private static final String RULE = "" +
			"00010100 01010001 00110000 01011100 00000000 01010000 11001110 01011111" +
			"00010111 00010001 11111111 01011111 00001111 01010011 11001111 01011111";

	@Override public CARule create() {
		return CARule.fromString(RULE);
	}

}
