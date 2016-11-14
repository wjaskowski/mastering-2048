package put.ci.cevo.games.encodings.ntuple.expanders;
//TODO: This package should be moved to put.ci.cevo.games.board.symmetry (I do not do this currently just because
//I anticipate huge merge conflicts

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class IdentitySymmetryExpander implements SymmetryExpander {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4457686707063785758L;

	@Override
	public int[] getSymmetries(int location) {
		return new int[] { location };
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int numSymmetries() {
		return 1;
	}
}
