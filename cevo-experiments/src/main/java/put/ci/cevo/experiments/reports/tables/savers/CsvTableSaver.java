package put.ci.cevo.experiments.reports.tables.savers;

import static org.apache.commons.io.FileUtils.openOutputStream;

import java.io.File;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.formats.CsvTableWriter;

public class CsvTableSaver implements TableSaver {

	private static final Logger logger = Logger.getLogger(CsvTableSaver.class);

	@Override
	public void saveTable(StarTable table, File file) {
		logger.info("Saving csv table to: " + file);
		CsvTableWriter csvTableWriter = new CsvTableWriter(true);
		try (PrintStream fileOutputStream = new PrintStream(openOutputStream(file), true, "UTF-8")) {
			csvTableWriter.writeStarTable(table, fileOutputStream);
		} catch (Exception e) {
			logger.error("A fatal error occured while saving table!", e);
		}
	}
}
