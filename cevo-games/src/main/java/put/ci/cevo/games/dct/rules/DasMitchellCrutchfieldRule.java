package put.ci.cevo.games.dct.rules;

import org.apache.commons.collections15.Factory;
import put.ci.cevo.games.dct.CARule;

public class DasMitchellCrutchfieldRule implements Factory<CARule> {

	private static final String RULE = "" +
			"00000111 00000000 00000111 11111111 00001111 00000000 00001111 11111111" +
			"00001111 00000000 00000111 11111111 00001111 00110001 00001111 11111111";

	@Override public CARule create() {
		return CARule.fromString(RULE);
	}

}
