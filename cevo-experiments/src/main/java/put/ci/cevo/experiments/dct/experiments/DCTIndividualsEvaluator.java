package put.ci.cevo.experiments.dct.experiments;

import org.apache.commons.collections15.Factory;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.dct.CADensityFactory;
import put.ci.cevo.experiments.dct.interaction.RuleDensityDCTInteraction;
import put.ci.cevo.framework.factories.StaticPopulationFactory;
import put.ci.cevo.framework.individuals.loaders.DefaultIndividualsLoader;
import put.ci.cevo.framework.individuals.loaders.FilesIndividualLoader;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.games.dct.CADensity;
import put.ci.cevo.games.dct.CARule;
import put.ci.cevo.games.dct.DCTParams;
import put.ci.cevo.games.dct.rules.*;
import put.ci.cevo.util.random.ThreadedContext;

import java.io.File;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.primitives.Doubles.toArray;
import static put.ci.cevo.experiments.dct.CADensityFactory.difficultTestsFactory;
import static put.ci.cevo.util.FileUtils.getSubdirNames;
import static put.ci.cevo.util.TableUtil.TableBuilder;
import static put.ci.cevo.util.TableUtil.tableToString;
import static put.ci.cevo.util.TextUtils.format;

public class DCTIndividualsEvaluator implements Runnable {

	private static final Logger logger = Logger.getLogger(DCTIndividualsEvaluator.class);

	private final ThreadedContext context = new ThreadedContext(123, 8);

	private final ExpectedUtility<CARule, CADensity> measure;

	private final FilesIndividualLoader<CARule> playersLoader;
	private final File dir;

	public DCTIndividualsEvaluator(RuleDensityDCTInteraction domain, FilesIndividualLoader<CARule> playersLoader,
			StaticPopulationFactory<CADensity> testsFactory, File dir) {
		this.playersLoader = playersLoader;
		this.measure = new ExpectedUtility<>(domain, testsFactory);
		this.dir = dir;
	}

	@Override
	public void run() {
		TableBuilder builder = new TableBuilder("Algorithm", "Performance");
		for (String experiment : getSubdirNames(dir)) {
			List<CARule> players = playersLoader.loadIndividuals(new File(dir, experiment), "run-*");
			logger.info("Loaded " + players.size() + " players from " + experiment);

			List<Double> results = context.invoke(new ThreadedContext.Worker<CARule, Double>() {
				@Override public Double process(CARule piece, ThreadedContext context) throws Exception {
					return measure.measure(piece, context).stats().getMean();
				}
			}, players).toList();

			DescriptiveStatistics statistics = new DescriptiveStatistics(toArray(results));
			builder.addRow(experiment,
					format(statistics.getMean(), 4) + " ± " + format(statistics.getStandardDeviation(), 2));
		}
		System.out.println(tableToString(builder.build()));
	}

	public void evaluatePerformancePublished() {
		List<Factory<CARule>> players = of(
				new FiciciParetoRule(),
				new AndreBennettKozaRule(),
				new DasMitchellCrutchfieldRule(),
				new GacsKurdyumovLevinRule(),
				new JuillePollackCoevolution1Rule(),
				new JuillePollackCoevolution2Rule()
		);
		final TableBuilder builder = new TableBuilder("Rule", "Performance");
		context.submit(new ThreadedContext.Worker<Factory<CARule>, Void>() {
			@Override public Void process(Factory<CARule> player, ThreadedContext context) throws Exception {
				double performance = measure.measure(player.create(), context).stats().getMean();
				builder.addRow(player.getClass().getSimpleName(), format(performance));
				return null;
			}
		}, players);
		System.out.println(tableToString(builder.build()));
	}

	public static void evaluateOnDifficultTests(int sampleSize, DCTParams params, String individualsDir) {
		DCTIndividualsEvaluator evaluator = new DCTIndividualsEvaluator(
				new RuleDensityDCTInteraction(params.getTimeSteps(), params.getRadius(), 1), DefaultIndividualsLoader.<CARule> create(),
				new StaticPopulationFactory<>(difficultTestsFactory(params.getTestLength()), sampleSize, 123),
				new File(individualsDir));
		evaluator.run();

	}
	public static void evaluateOnUniformTests(int sampleSize, DCTParams param, String individualsDir) {
		DCTIndividualsEvaluator evaluator = new DCTIndividualsEvaluator(
				new RuleDensityDCTInteraction(param.getTimeSteps(), param.getRadius(), 1), DefaultIndividualsLoader.<CARule> create(),
				new StaticPopulationFactory<>(new CADensityFactory(param.getTestLength()), sampleSize, 123),
				new File(individualsDir));
		evaluator.run();

	}
	public static void evaluatePublishedOnUniformTests(int sampleSize) {
		DCTIndividualsEvaluator evaluator = new DCTIndividualsEvaluator(
				new RuleDensityDCTInteraction(320, 3, 1), DefaultIndividualsLoader.<CARule> create(),
				new StaticPopulationFactory<>(new CADensityFactory(149), sampleSize, 123),
				null);
		evaluator.evaluatePerformancePublished();

	}
	public static void main(String[] args) {
		DCTIndividualsEvaluator.evaluatePublishedOnUniformTests(50000);
	}

}
