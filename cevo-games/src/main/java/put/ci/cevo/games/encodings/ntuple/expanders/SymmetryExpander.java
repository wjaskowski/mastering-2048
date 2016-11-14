package put.ci.cevo.games.encodings.ntuple.expanders;

//TODO: Move expanders outside ntuples
public interface SymmetryExpander {

	/**
	 * Returns an array of locations expanded by the inherent symmetry of the board. The expanded locations can (and
	 * should sometimes, e.g. for the middle locations in Othello, repeat. Have to always return the same number of
	 * symmetries.
	 * @param location 0-based position
	 * 
	 * @return the first element has to be the original location
	 **/
	// TODO: why not add boardSize to this function?
	public int[] getSymmetries(int location);

	/**
	 * Number of symmetric expansions for the given symmetry expander. >0
	 */
	public int numSymmetries();
}
