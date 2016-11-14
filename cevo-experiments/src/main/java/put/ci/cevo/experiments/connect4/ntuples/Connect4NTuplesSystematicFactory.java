package put.ci.cevo.experiments.connect4.ntuples;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.experiments.ntuple.NTuplesGeneralSystematicFactory;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.board.BoardPosList;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.YAxisSymmetryExpander;

/**
 * Generates systematic n-tuples for Connect4
 */
public class Connect4NTuplesSystematicFactory implements IndividualFactory<NTuples> {

	private final IndividualFactory<NTuples> factory;

	/**
	 * A shortcut. Required for the configuration
	 *
	 * @param patterns For example, "01|01; 1|1|1|1; 11|01"
	 */
	public Connect4NTuplesSystematicFactory(double minWeight, double maxWeight, String patterns) {
		this(minWeight, maxWeight, patterns.replaceAll(" ", "").split(";"));
	}

	/**
	 * @param shapes Shapes in {@link put.ci.cevo.games.board.BoardPosList} format. Eg. new String[] {"01|10",
	 *               "1|1|1|1|", "1111"}
	 */
	public Connect4NTuplesSystematicFactory(double minWeight, double maxWeight, String[] shapes) {
		List<BoardPosList> positions = new ArrayList<>(shapes.length);
		for (String shape : shapes) {
			positions.add(new BoardPosList(shape));
		}
		System.out.println(positions);

		this.factory = new NTuplesGeneralSystematicFactory(positions.toArray(new BoardPosList[positions.size()]),
				Connect4Board.BOARD_SIZE, Connect4Board.NUM_VALUES, minWeight, maxWeight,
				new YAxisSymmetryExpander(Connect4Board.BOARD_WIDTH));
	}

	@Override
	public NTuples createRandomIndividual(RandomDataGenerator random) {
		return factory.createRandomIndividual(random);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
