package put.ci.cevo.experiments.ntuple;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.board.BoardPosList;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;

public class NTuples7RandomIndividualFactory implements IndividualFactory<NTuples> {

	private final NTuplesGeneralSystematicFactory generalFactory;

	public NTuples7RandomIndividualFactory(RectSize boardSize, int numValues, double minWeight, double maxWeight,
			SymmetryExpander expander) {
		//@formatter:off
		this.generalFactory = new NTuplesGeneralSystematicFactory(
			new BoardPosList(
				new String[] {
					"1111",
					"111"
				}
			), 
			boardSize, numValues, minWeight, maxWeight, expander
		);
		//@formatter:on
	}

	@Override
	public NTuples createRandomIndividual(RandomDataGenerator random) {
		return generalFactory.createRandomIndividual(random);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
