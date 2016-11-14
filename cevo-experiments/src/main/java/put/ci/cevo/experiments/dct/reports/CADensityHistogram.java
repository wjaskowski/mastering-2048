package put.ci.cevo.experiments.dct.reports;

import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.google.common.collect.Maps;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.games.dct.CATest;
import uk.ac.starlink.table.StarTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static put.ci.cevo.util.TableUtil.TableBuilder;
import static put.ci.cevo.util.sequence.Sequences.range;

public class CADensityHistogram implements EvolutionStateListener {

	private static final int BUCKETS = 100;

	private final Map<Integer, IntIntOpenHashMap> histograms = Maps.newHashMap();

	@Override public void onNextGeneration(EvolutionState state) {
		IntIntOpenHashMap histogram = new IntIntOpenHashMap();
		List<EvaluatedIndividual<CATest>> tests = state.getEvaluatedTests();
		for (EvaluatedIndividual<CATest> test : tests) {
			int bucket = (int) (test.getIndividual().getDensity() * BUCKETS);
			histogram.putOrAdd(bucket, 1, 1);
		}
		histograms.put(state.getGeneration(), histogram);
	}

	public StarTable createHistogramTable() {
		TableBuilder builder = new TableBuilder().addHeaders("gen").addHeaders(range(BUCKETS).toList());
		for (int i = 0; i < histograms.size(); i++) {
			IntIntOpenHashMap histogram = histograms.get(i);
			List<Integer> densities = new ArrayList<>(BUCKETS + 1);
			densities.add(i);
			for (int j = 0; j < BUCKETS; j++) {
				if (histogram.containsKey(j)) {
					densities.add(histogram.lget());
				} else {
					densities.add(0);
				}
			}
			builder.addRow(densities);
		}
		return builder.build();
	}
}
