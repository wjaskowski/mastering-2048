package put.ci.cevo.games.encodings.ntuple.expanders;

import java.util.Arrays;
import java.util.HashSet;

import com.google.common.collect.Sets;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

public class YAxisSymmetryExpanderTest {

	@Test
	public void testGetSymmetries1() throws Exception {
		YAxisSymmetryExpander yAxisSymmetryExpander = new YAxisSymmetryExpander(8);

		HashSet<Integer> actual = Sets.newHashSet(ArrayUtils.toObject(yAxisSymmetryExpander.getSymmetries(11)));
		HashSet<Integer> expected = Sets.newHashSet(Arrays.asList(11, 18));

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetSymmetries2() throws Exception {
		YAxisSymmetryExpander yAxisSymmetryExpander = new YAxisSymmetryExpander(8);

		HashSet<Integer> actual = Sets.newHashSet(ArrayUtils.toObject(yAxisSymmetryExpander.getSymmetries(33)));
		HashSet<Integer> expected = Sets.newHashSet(Arrays.asList(33, 36));

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetSymmetries3() throws Exception {
		YAxisSymmetryExpander yAxisSymmetryExpander = new YAxisSymmetryExpander(8);

		HashSet<Integer> actual = Sets.newHashSet(ArrayUtils.toObject(yAxisSymmetryExpander.getSymmetries(27)));
		HashSet<Integer> expected = Sets.newHashSet(Arrays.asList(22, 27));

		Assert.assertEquals(expected, actual);
	}
}