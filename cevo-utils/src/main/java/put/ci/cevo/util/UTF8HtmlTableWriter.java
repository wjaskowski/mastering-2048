package put.ci.cevo.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.MultiStarTableWriter;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableOutput;
import uk.ac.starlink.table.StarTableWriter;
import uk.ac.starlink.table.StreamStarTableWriter;
import uk.ac.starlink.table.TableSequence;
import uk.ac.starlink.table.Tables;

public class UTF8HtmlTableWriter extends StreamStarTableWriter implements MultiStarTableWriter {

	private boolean standalone_;
	private final boolean useRowGroups_;

	public UTF8HtmlTableWriter() {
		this(true, true);
	}

	public UTF8HtmlTableWriter(boolean standalone, boolean useRowGroups) {
		setStandalone(standalone);
		useRowGroups_ = useRowGroups;
	}

	public void setStandalone(boolean standalone) {
		standalone_ = standalone;
	}

	public boolean isStandalone() {
		return standalone_;
	}

	@Override
	public String getFormatName() {
		return standalone_ ? "HTML" : "HTML-element";
	}

	@Override
	public String getMimeType() {
		return "text/html";
	}

	@Override
	public boolean looksLikeFile(String location) {
		return location.endsWith(".html") || location.endsWith(".htm");
	}

	@Override
	public void writeStarTable(StarTable table, OutputStream out) throws IOException {
		Writer outWriter = new OutputStreamWriter(out);
		if (standalone_) {
			printHeader(outWriter, table);
		}
		writeTableElement(table, outWriter);
		if (standalone_) {
			printFooter(outWriter);
		}
		outWriter.flush();
	}

	@Override
	public void writeStarTables(TableSequence tableSeq, OutputStream out) throws IOException {
		Writer outWriter = new OutputStreamWriter(out);
		if (standalone_) {
			printHeader(outWriter, null);
		}
		for (StarTable table; (table = tableSeq.nextTable()) != null;) {
			printLine(outWriter, "<P>");
			writeTableElement(table, outWriter);
			printLine(outWriter, "</P>");
		}
		if (standalone_) {
			printFooter(outWriter);
		}
	}

	@Override
	public void writeStarTables(TableSequence tableSeq, String location, StarTableOutput sto) throws IOException {
		try (OutputStream out = new BufferedOutputStream(sto.getOutputStream(location))) {
			writeStarTables(tableSeq, out);
			out.flush();
		}
	}

	private void writeTableElement(StarTable table, Writer ostrm) throws IOException {

		/* Get an iterator over the table data. */
		RowSequence rseq = table.getRowSequence();

		/* Output table header. */
		try {
			printLine(ostrm, "<TABLE BORDER='1'>");
			String tname = table.getName();
			if (tname != null) {
				printLine(ostrm, "<CAPTION><STRONG>" + tname + "</STRONG></CAPTION>");
			}

			/* Output column headings. */
			int ncol = table.getColumnCount();
			ColumnInfo[] colinfos = Tables.getColumnInfos(table);
			String[] names = new String[ncol];
			String[] units = new String[ncol];
			boolean hasUnits = false;
			for (int icol = 0; icol < ncol; icol++) {
				ColumnInfo colinfo = colinfos[icol];
				String name = colinfo.getName();
				String unit = colinfo.getUnitString();
				if (unit != null) {
					hasUnits = true;
					unit = "(" + unit + ")";
				}
				names[icol] = name;
				units[icol] = unit;
			}
			String[] headings = new String[ncol];
			for (int icol = 0; icol < ncol; icol++) {
				String heading = names[icol];
				String unit = units[icol];
				if (hasUnits) {
					heading += "<BR>";
					if (unit != null) {
						heading += "(" + unit + ")";
					}
				}
				headings[icol] = heading;
			}
			if (useRowGroups_) {
				printLine(ostrm, "<THEAD>");
			}
			outputRow(ostrm, "TH", null, names);
			if (hasUnits) {
				outputRow(ostrm, "TH", null, units);
			}

			/* Separator. */
			printLine(ostrm, "<TR><TD colspan='" + ncol + "'></TD></TR>");
			if (useRowGroups_) {
				printLine(ostrm, "</THEAD>");
			}

			/* Output the table data. */
			if (useRowGroups_) {
				printLine(ostrm, "<TBODY>");
			}
			while (rseq.next()) {
				Object[] row = rseq.getRow();
				String[] cells = new String[ncol];
				for (int icol = 0; icol < ncol; icol++) {
					cells[icol] = colinfos[icol].formatValue(row[icol], 600);
				}
				outputRow(ostrm, "TD", null, cells);
			}
			if (useRowGroups_) {
				printLine(ostrm, "</TBODY>");
			}

			/* Finish up. */
			printLine(ostrm, "</TABLE>");
		} finally {
			rseq.close();
		}
	}

	public static StarTableWriter[] getStarTableWriters() {
		return new StarTableWriter[] { new UTF8HtmlTableWriter(true, true), new UTF8HtmlTableWriter(false, true), };
	}

	/**
	 * Outputs a row of header or data cells.
	 * 
	 * @param ostrm
	 *            the stream for output
	 * @param tagname
	 *            the name of the element in which to wrap each cell ("TH" or "TD")
	 * @param attlist
	 *            any attributes to put on the elements
	 * @param values
	 *            the array of values providing cell contents
	 */
	private void outputRow(Writer ostrm, String tagname, String attlist, String[] values) throws IOException {
		int ncol = values.length;
		printLine(ostrm, "<TR>");
		StringBuffer sbuf = new StringBuffer();
		for (int icol = 0; icol < ncol; icol++) {
			sbuf.append(' ').append('<').append(tagname);
			if (attlist != null) {
				sbuf.append(" " + attlist);
			}
			sbuf.append('>');
			String value = values[icol] == null ? null : escape(values[icol]);
			if (value == null || value.length() == 0) {
				sbuf.append("&nbsp;");
			} else if (isUrl(value)) {
				sbuf.append("<A href='").append(value).append("'>").append(value).append("</A>");
			} else {
				sbuf.append(value);
			}
			sbuf.append("</").append(tagname).append(">");
		}
		printLine(ostrm, sbuf.toString());
		printLine(ostrm, "</TR>");
	}

	private void printLine(Writer ostrm, String str) throws IOException {
		// ostrm.write( str.getBytes() );
		ostrm.write(str);
		ostrm.write('\n');
	}

	protected void printHeader(Writer ostrm, StarTable table) throws IOException {
		String publicId = useRowGroups_ ? "-//W3C//DTD HTML 4.01 Transitional//EN" : "-//W3C//DTD HTML 3.2 Final//EN";
		String declaration = "<!DOCTYPE HTML PUBLIC \"" + publicId + "\">";
		printLine(ostrm, declaration);
		printLine(ostrm, "<HTML>");
		String tname = table == null ? null : table.getName();
		if (tname != null && tname.trim().length() > 0) {
			printLine(ostrm, "<HEAD><TITLE>Table " + escape(tname) + "</TITLE></HEAD>");
		}
		printLine(ostrm, "<BODY>");
	}

	protected void printFooter(Writer ostrm) throws IOException {
		printLine(ostrm, "</BODY>");
		printLine(ostrm, "</HTML>");
	}

	private String escape(String line) {
		StringBuffer sbuf = new StringBuffer();
		for (int i = 0; i < line.length(); i++) {
			char chr = line.charAt(i);
			switch (chr) {
			case '&':
				sbuf.append("&amp;");
				break;
			case '<':
				sbuf.append("&lt;");
				break;
			case '>':
				sbuf.append("&gt;");
				break;
			case '"':
				sbuf.append("&quot;");
				break;
			case '\'':
				sbuf.append("&apos;");
				break;
			default:
				sbuf.append(chr);
			}
		}
		return sbuf.toString();
	}

	protected boolean isUrl(String txt) {
		if (txt.startsWith("http:") || txt.startsWith("ftp:") || txt.startsWith("mailto:")) {
			try {
				new URL(txt);
				return true;
			} catch (MalformedURLException e) {
				return false;
			}
		} else {
			return false;
		}
	}
}
