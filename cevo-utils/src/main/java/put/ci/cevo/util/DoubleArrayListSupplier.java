package put.ci.cevo.util;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Supplier;

public class DoubleArrayListSupplier implements Supplier<List<Double>>, Serializable {

	private static final long serialVersionUID = 7785766419338527502L;

	@Override
	public List<Double> get() {
		return new DoubleArrayList();
	}

}