package put.ci.cevo.experiments.reports.tables.savers;

import java.io.File;

import uk.ac.starlink.table.StarTable;

public interface TableSaver {

	public void saveTable(StarTable table, File file);

}
