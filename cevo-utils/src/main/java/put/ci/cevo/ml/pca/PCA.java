package put.ci.cevo.ml.pca;

import static org.apache.commons.math3.linear.MatrixUtils.createColumnRealMatrix;
import static org.apache.commons.math3.linear.MatrixUtils.createRealMatrix;
import static org.apache.commons.math3.stat.StatUtils.sum;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.stat.correlation.Covariance;

public class PCA {

	/** stores principle component subspace is stored in the rows */
	private RealMatrix Ureduce;

	/** Column-wise means */
	private double[] mean;

	/**
	 * Computes the principle components from the most dominant eigenvectors.
	 * 
	 * @param varianceToRetain
	 *            the number of components will be chosen according to variance specified by this variable
	 * @return input data in the eigen space
	 */
	public double[][] reduce(double[][] data, double varianceToRetain) {
		int rows = data.length;
		int cols = data[0].length;

		// mean normalize the data
		RealMatrix input = createRealMatrix(meanNormalize(data, rows, cols));

		// perform SVD
		SingularValueDecomposition svd = new SingularValueDecomposition(new Covariance(input).getCovarianceMatrix());

		double[] singularValues = svd.getSingularValues();
		double singularValuesSum = sum(singularValues);

		// compute number of components to retain
		int components = 0;
		double variance;
		do {
			components++;
			variance = sum(singularValues, 0, components) / singularValuesSum;
		} while (variance < varianceToRetain && components < singularValues.length);

		// strip off unneeded components to find the basis
		RealMatrix U = svd.getU();
		Ureduce = U.getSubMatrix(0, U.getRowDimension() - 1, 0, components - 1);
		return input.multiply(Ureduce).getData();
	}

	/**
	 * Computes the principle components from the most dominant eigenvectors.
	 */
	/**
	 * @param components
	 *            the number of vectors that will used to describe the data. Typically much smaller than the number of
	 *            elements in the input vector.
	 * @return input data in the eigen space
	 */
	public double[][] reduce(double[][] data, int components) {
		int rows = data.length;
		int cols = data[0].length;

		RealMatrix input = createRealMatrix(meanNormalize(data, rows, cols));
		SingularValueDecomposition svd = new SingularValueDecomposition(new Covariance(input).getCovarianceMatrix());

		RealMatrix U = svd.getU();
		Ureduce = U.getSubMatrix(0, U.getRowDimension() - 1, 0, components - 1);
		return input.multiply(Ureduce).getData();
	}

	public double[][] eigenToSampleSpace(double[][] eigenData) {
		RealMatrix means = MatrixUtils.createRealMatrix(eigenData.length, mean.length);
		for (int row = 0; row < eigenData.length; row++) {
			means.setRow(row, mean);
		}
		return createRealMatrix(eigenData).multiply(Ureduce.transpose()).add(means).getData();

	}

	/** Converts a vector from sample space into eigen space. */
	public double[] sampleToEigenSpace(double[] sampleData) {
		RealMatrix r = createColumnRealMatrix(sampleData).subtract(createColumnRealMatrix(mean));
		return Ureduce.transpose().multiply(r).getColumn(0);
	}

	/** Converts a vector from eigen space into sample space. */
	public double[] eigenToSampleSpace(double[] eigenData) {
		return Ureduce.multiply(createColumnRealMatrix(eigenData)).add(createColumnRealMatrix(mean)).getColumn(0);
	}

	private double[][] meanNormalize(double[][] data, int rows, int cols) {
		double[][] normalized = new double[rows][cols];

		// compute the mean of all the samples in each column
		mean = new double[cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				mean[j] += data[i][j];
			}
		}

		for (int j = 0; j < mean.length; j++) {
			mean[j] /= rows;
		}

		// subtract the means from elements in each column
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				normalized[i][j] = data[i][j] - mean[j];
			}
		}
		return normalized;
	}
}
