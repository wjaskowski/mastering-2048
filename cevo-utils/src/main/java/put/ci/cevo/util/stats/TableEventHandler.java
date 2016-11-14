package put.ci.cevo.util.stats;

import uk.ac.starlink.table.StarTable;

public interface TableEventHandler<T> extends EventHandler<T> {
	
	public StarTable getTable();
	
}