package put.ci.cevo.experiments.reports;

import static com.google.common.collect.Ordering.natural;
import static put.ci.cevo.util.sequence.Sequences.range;
import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.transforms.Transforms.formatDouble;

import java.io.File;

import org.apache.log4j.Logger;

import put.ci.cevo.experiments.reports.tables.savers.CsvTableSaver;
import put.ci.cevo.experiments.reports.tables.savers.TableSaver;
import put.ci.cevo.framework.retrospection.RetrospectionResult;
import put.ci.cevo.framework.retrospection.RetrospectionTable;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.ObjectArrayColumn;
import uk.ac.starlink.table.StarTable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class TestsPopulationReport extends ConfiguredExperimentReport {

	private static final Logger logger = Logger.getLogger(TestsPopulationReport.class);

	private final TableSaver saver;

	@AccessedViaReflection
	public TestsPopulationReport() {
		this(new CsvTableSaver());
	}

	@AccessedViaReflection
	public TestsPopulationReport(TableSaver saver) {
		this.saver = saver;
	}

	@Override
	public void generate() {
		logger.info("Generating report for population of tests");

		final File resultsDir = new File(getOutputDir(), "results");
		final File csvDir = new File(resultsDir, "csv");

		RetrospectionTable retrospectionTable = experiment.getRetrospectionTable();

		for (String task : retrospectionTable.tasks()) {
			for (String model : retrospectionTable.models()) {
				Multimap<Integer, Double> fitnessByGeneration = ArrayListMultimap.create();
				RetrospectionResult result = retrospectionTable.get(task, model);
				fitnessByGeneration.putAll(result.getFitness());

				File taskCsvDir = new File(csvDir, task);
				StarTable table = createTable(fitnessByGeneration);
				saver.saveTable(table, new File(taskCsvDir, model + "-pop.csv"));
			}
		}

	}

	private StarTable createTable(Multimap<Integer, Double> fitness) {
		final int rows = fitness.get(0).size();
		final ColumnStarTable table = ColumnStarTable.makeTableWithRows(rows);

		table.addColumn(new ObjectArrayColumn(new ColumnInfo("num"), range(rows).toArray()));
		for (Integer key : natural().sortedCopy(fitness.keySet())) {
			table.addColumn(new ObjectArrayColumn(new ColumnInfo("generation-" + key), seq(
				natural().reverse().sortedCopy(fitness.get(key))).map(formatDouble()).toArray()));
		}
		return table;
	}

}
