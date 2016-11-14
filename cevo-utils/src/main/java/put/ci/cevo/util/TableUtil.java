package put.ci.cevo.util;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.sequence.transforms.Transforms;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.RowListStarTable;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.formats.CsvTableWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.transforms.Transforms.convertToString;

public class TableUtil {

	public static class TableBuilder {

		private static final ColumnInfo INDENTATION = new ColumnInfo("");

		private List<ColumnInfo> headers = newArrayList();
		private final List<List<Object>> rows = newArrayList();

		public TableBuilder(Iterable<?> headers) {
			setHeaders(seq(copyOf(headers)).map(convertToString()).toArray());
		}

		public TableBuilder(Object... headersList) {
			setHeaders(headersList);
		}

		public TableBuilder setHeaders(Iterable<?> headers) {
			return setHeaders(copyOf(seq(headers).map(convertToString())).toArray());
		}

		public TableBuilder setHeaders(Object... headersList) {
			headers = new ArrayList<>(headersList.length);
			addHeaders(headersList);
			return this;
		}

		public int getColumnsCount() {
			return headers.size();
		}

		public int getRowsCount() {
			return rows.size();
		}
		
		public boolean isEmpty() {
			return rows.isEmpty();
		}

		public TableBuilder addHeaders(Iterable<?> headers) {
			return addHeaders(copyOf(seq(headers).map(convertToString())).toArray());
		}

		public TableBuilder addHeaders(Object... headersList) {
			for (Object header : headersList) {
				headers.add(new ColumnInfo(header.toString()));
			}
			return this;
		}

		public TableBuilder addIndentedHeaders(Iterable<?> headersIterable) {
			addIndentation(headers);
			return addHeaders(headersIterable);
		}

		public TableBuilder addIndentedHeaders(Object... headersList) {
			addIndentation(headers);
			return addHeaders(headersList);
		}

		public TableBuilder addRow(Iterable<?> cells) {
			return addRow(seq(cells).map(convertToString()).toArray());
		}

		public TableBuilder addRow(Object... cells) {
			rows.add(newArrayList(cells));
			return this;
		}
		
		public List<List<Object>> getRows() {
			return rows;
		}

		/** If rows have variable size, then they are aligned to the same size with the passed object */
		public StarTable buildAlignedWith(Object object) {
			Preconditions.checkArgument(!rows.isEmpty());
			Preconditions.checkArgument(!headers.isEmpty());
			RowListStarTable table = new RowListStarTable(headers.toArray(new ColumnInfo[headers.size()]));
			for (List<Object> row : alignRows(rows, object)) {
				table.addRow(row.toArray());
			}
			return table;
		}

		/** Assumes rows of equal length */
		public StarTable build() {
			Preconditions.checkArgument(!rows.isEmpty());
			return headers.isEmpty() ? buildWithoutHeader() : buildWithHeader();
		}

		/** First row is used as a header */
		private StarTable buildWithoutHeader() {
			addHeaders(rows.get(0));
			rows.remove(0);
			return buildWithHeader();
		}

		private StarTable buildWithHeader() {
			Preconditions.checkArgument(!headers.isEmpty());
			RowListStarTable table = new RowListStarTable(headers.toArray(new ColumnInfo[headers.size()]));
			for (List<Object> row : rows) {
				table.addRow(row.toArray());
			}
			return table;
		}

		private List<List<Object>> alignRows(List<List<Object>> rows, Object object) {
			int maxRowLength = seq(rows).map(new Transform<List<Object>, Integer>() {
				@Override
				public Integer transform(List<Object> object) {
					return object.size();
				}
			}).reduce(Transforms.<Integer> max());
			for (int i = 0; i < rows.size(); i++) {
				int size = rows.get(i).size();
				if (size < maxRowLength) {
					List<Object> objects = rows.get(i);
					for (int j = 0; j < maxRowLength - size; j++) {
						objects.add(object);
					}
				}
			}
			return rows;
		}

		/** Adds an empty string to create an indentation */
		private void addIndentation(List<ColumnInfo> list) {
			list.add(INDENTATION);
		}

		@Override
		public String toString() {
			return tableToString(build());
		}
	}

	private static final Logger logger = Logger.getLogger(TableUtil.class);

	public static String tableToString(StarTable table) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		UTF8TextTableWriter w = new UTF8TextTableWriter();
		try {
			w.writeStarTable(table, stream);
			return stream.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void saveTableAsCSV(StarTable table, File file) {
		CsvTableWriter csvTableWriter = new CsvTableWriter(true);
		try (PrintStream fileOutputStream = new PrintStream(openOutputStream(file), true, "UTF-8")) {
			csvTableWriter.writeStarTable(table, fileOutputStream);
		} catch (Exception e) {
			logger.error("A fatal error occurred while saving table!", e);
		}
	}
}
