package put.ci.cevo.games.dct.rules;

import org.apache.commons.collections15.Factory;
import put.ci.cevo.games.dct.CARule;

public class GacsKurdyumovLevinRule implements Factory<CARule> {

	private static final String RULE = "" +
			"00000000 01011111 00000000 01011111 00000000 01011111 00000000 01011111" +
			"00000000 01011111 11111111 01011111 00000000 01011111 11111111 01011111";

	@Override public CARule create() {
		return CARule.fromString(RULE);
	}

}
