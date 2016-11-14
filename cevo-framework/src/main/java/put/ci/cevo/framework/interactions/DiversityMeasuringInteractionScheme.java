package put.ci.cevo.framework.interactions;

import put.ci.cevo.framework.measures.diversity.CoevolutionaryDiversityMeasure;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.TableUtil;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedRandom;
import put.ci.cevo.util.stats.EventHandler;
import put.ci.cevo.util.stats.EventsLogger;
import put.ci.cevo.util.stats.Handler;
import put.ci.cevo.util.stats.TableEventHandler;
import uk.ac.starlink.table.StarTable;

import java.util.ArrayList;
import java.util.List;

import static put.ci.cevo.util.Pair.create;
import static put.ci.cevo.util.TextUtils.format;

/**
 * Wrapper for a generic {@link InteractionScheme} which measures diversity in evolving population according to an
 * instance of {@link CoevolutionaryDiversityMeasure} in addition to performing interactions between coevolving entities.
 */
public class DiversityMeasuringInteractionScheme<S, T> implements InteractionScheme<S, T> {

	/**
	 * Make sure that {@link EventsLogger} is properly instantiated in {@link ThreadedContext} by calling either
	 * {@link ThreadedContext#withEventsLogger(ThreadedRandom, int, EventsLogger)}.

	 * By default the whole logging facility is turned off due to early stage of development.
	 */
	@Handler(targetEvent = DiversityMeasuringInteractionScheme.class)
	public static class DiversityMeasurementHandler implements TableEventHandler<Pair<Measurement, Measurement>> {

		private final List<Double> solutionsDiversity = new ArrayList<>();
		private final List<Double> testsDiversity = new ArrayList<>();

		/**
		 * This is not mandatory. For instance, you could write the data directly to a file in
		 * {@link DiversityMeasurementHandler#log(Pair)} or do virtually anything else there.
		 *
		 * If this is the case, simply implement {@link EventHandler} instead.
		 */
		@Override public StarTable getTable() {
			if (solutionsDiversity.isEmpty() || testsDiversity.isEmpty()) {
				return null;
			}

			TableUtil.TableBuilder builder = new TableUtil.TableBuilder("gen", "solutions-diversity", "tests-diversity");
			for (int i = 0; i < solutionsDiversity.size(); i++) {
				builder.addRow(i, format(solutionsDiversity.get(i)), format(testsDiversity.get(i)));
			}

			return builder.build();
		}

		/** Perform necessary operations here */
		@Override public void log(Pair<Measurement, Measurement> data) {
			solutionsDiversity.add(data.first().stats().getMean());
			testsDiversity.add(data.second().stats().getMean());
		}
	}

	private final InteractionScheme<S, T> scheme;
	private final CoevolutionaryDiversityMeasure<S, T> measure;

	@AccessedViaReflection
	public DiversityMeasuringInteractionScheme(InteractionScheme<S, T> scheme,
			CoevolutionaryDiversityMeasure<S, T> measure) {
		this.scheme = scheme;
		this.measure = measure;
	}

	@Override
	public InteractionTable<S, T> interact(List<S> solutions, List<T> tests, ThreadedContext context) {
		InteractionTable<S, T> table = scheme.interact(solutions, tests, context);

		Measurement solutionsDiversity = measure.measureSolutionsDiversity(table.getSolutionsPayoffs(), context);
		Measurement testsDiversity = measure.measureTestsDiversity(table.getTestsPayoffs(), context);
		context.getEventsLogger().log(this, create(solutionsDiversity, testsDiversity));

		return table;
	}
}
