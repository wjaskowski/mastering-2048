package put.ci.cevo.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.MultiStarTableWriter;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableOutput;
import uk.ac.starlink.table.TableSequence;
import uk.ac.starlink.table.ValueInfo;
import uk.ac.starlink.table.formats.AbstractTextTableWriter;

public class UTF8TextTableWriter extends AbstractTextTableWriter implements MultiStarTableWriter {

	public UTF8TextTableWriter() {
		super(true);
	}

	@Override
	public String getFormatName() {
		return "text";
	}

	@Override
	public String getMimeType() {
		return "text/plain";
	}

	@Override
	public boolean looksLikeFile(String location) {
		return location.equals("-");
	}

	@Override
	public void writeStarTables(TableSequence tableSeq, OutputStream out) throws IOException {
		int ix = 0;
		for (StarTable table; (table = tableSeq.nextTable()) != null; ix++) {
			if (ix > 0) {
				out.write('\n');
			}
			writeStarTable(table, out);
		}
	}

	@Override
	public void writeStarTables(TableSequence tableSeq, String location, StarTableOutput sto) throws IOException {
		try (OutputStream out = new BufferedOutputStream(sto.getOutputStream(location))) {
			writeStarTables(tableSeq, out);
			out.flush();
		}
	}

	@Override
	public int getMaxWidth() {
		return 40;
	}

	@Override
	protected String formatValue(Object val, ValueInfo vinfo, int width) {
		return vinfo.formatValue(val, width);
	}

	@Override
	protected void printSeparator(OutputStream strm, int[] colwidths) throws IOException {
		Writer outWriter = new OutputStreamWriter(strm);
		for (int i = 0; i < colwidths.length; i++) {
			outWriter.write('+');
			outWriter.write('-');
			for (int j = 0; j < colwidths[i]; j++) {
				outWriter.write('-');
			}
			outWriter.write('-');
		}
		outWriter.write('+');
		outWriter.write('\n');
		outWriter.flush();
	}

	@Override
	protected void printColumnHeads(OutputStream strm, int[] colwidths, ColumnInfo[] cinfos) throws IOException {
		int ncol = cinfos.length;
		String[] heads = new String[ncol];
		for (int i = 0; i < ncol; i++) {
			heads[i] = cinfos[i].getName();
		}
		printSeparator(strm, colwidths);
		printLine(strm, colwidths, heads);
		printSeparator(strm, colwidths);
	}

	@Override
	protected void printLine(OutputStream strm, int[] colwidths, String[] data) throws IOException {
		Writer outWriter = new OutputStreamWriter(strm);
		for (int i = 0; i < colwidths.length; i++) {
			outWriter.write('|');
			outWriter.write(' ');
			String datum = (data[i] == null) ? "" : data[i];
			int padding = colwidths[i] - datum.length();
			outWriter.write(datum, 0, Math.min(colwidths[i], datum.length()));
			if (padding > 0) {
				for (int j = 0; j < padding; j++) {
					outWriter.write(' ');
				}
			}
			outWriter.write(' ');
		}
		outWriter.write('|');
		outWriter.write('\n');
		outWriter.flush();
	}

	@Override
	protected void printParam(OutputStream strm, String name, String value) throws IOException {
		Writer outWriter = new OutputStreamWriter(strm);

		outWriter.write(name);
		outWriter.write(':');
		outWriter.write(' ');
		outWriter.write(value);
		outWriter.write('\n');
		outWriter.flush();
	}
}
