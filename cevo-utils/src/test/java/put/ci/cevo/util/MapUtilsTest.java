package put.ci.cevo.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class MapUtilsTest {

	@Test
	public void testSortByValueAscending() {
		Map<String, Integer> testMap = new LinkedHashMap<>(1000);
		for (int i = 999; i >= 0; i--) {
			testMap.put("test" + i, i);
		}

		testMap = MapUtils.sortByValueAscending(testMap);
		Assert.assertEquals(1000, testMap.size());

		Integer previous = null;
		for (Map.Entry<String, Integer> entry : testMap.entrySet()) {
			Assert.assertNotNull(entry.getValue());
			if (previous != null) {
				Assert.assertTrue(entry.getValue() >= previous);
			}
			previous = entry.getValue();
		}
	}

	@Test
	public void testSortByValueDescending() {
		Map<String, Integer> testMap = new LinkedHashMap<>(1000);
		for (int i = 0; i < 1000; ++i) {
			testMap.put("test" + i, i);
		}

		testMap = MapUtils.sortByValueDescending(testMap);
		Assert.assertEquals(1000, testMap.size());

		Integer previous = null;
		for (Map.Entry<String, Integer> entry : testMap.entrySet()) {
			Assert.assertNotNull(entry.getValue());
			if (previous != null) {
				Assert.assertTrue(entry.getValue() <= previous);
			}
			previous = entry.getValue();
		}
	}

}
