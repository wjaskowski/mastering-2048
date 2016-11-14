package put.ci.cevo.rl.agent.functions.wpc;

/**
 * WPC with Margin. Knows the dimensions of the board
 */
public class MarginWPC {

	private final int rows;
	private final int cols;
	private final int totalMargin;

	public final double buffer[];

	public MarginWPC(WPC wpc, int rows, int cols, int margin) {
		assert (wpc.getSize() == rows * cols);
		this.rows = rows;
		this.cols = cols;
		this.totalMargin = 2 * margin;
		this.buffer = new double[(rows + totalMargin) * (cols + totalMargin)];
		assign(wpc);
	}

	private void assign(WPC wpc) {
		for (int r = 0; r < rows; ++r) {
			for (int c = 0; c < cols; ++c) {
				double value = wpc.get(r * cols + c);
				buffer[(r + 1) * (cols + totalMargin) + (c + 1)] = value;
			}
		}
	}

	public double getValue(int row, int col) {
		assert (0 <= row && row < rows);
		assert (0 <= col && col < cols);
		return buffer[(row + 1) * (cols + totalMargin) + col + 1];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("  ");
		for (int c = 0; c < cols; c++) {
			builder.append(" " + (char) ('A' + c) + "     ");
		}
		builder.append("\n");

		for (int r = 0; r < rows; r++) {
			builder.append(r + " ");
			for (int c = 0; c < cols; c++) {
				builder.append(String.format("%6.3f ", getValue(r, c)));
			}
			builder.append("\n");
		}
		return builder.toString();
	}
}