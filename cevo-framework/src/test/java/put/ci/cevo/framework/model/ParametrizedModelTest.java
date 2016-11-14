package put.ci.cevo.framework.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ParametrizedModelTest {

	private static class FinalFields {

		@SuppressWarnings("unused")
		private final String a;
		@SuppressWarnings("unused")
		private final int b;

		public FinalFields(String a, int b) {
			this.a = a;
			this.b = b;
		}

	}

	@Test
	public void testParametersExtraction() {
		FinalFields finalFields = new FinalFields("test", 10);
		ParametrizedModel parametrizedModel = new ParametrizedModel(finalFields);

		assertEquals("test", parametrizedModel.getCachedParameter("a"));
		assertEquals(10, parametrizedModel.getCachedParameter("b"));

	}

	@Test
	public void testParametersChange() {
		FinalFields finalFields = new FinalFields("test", 10);

		ParametrizedModel parametrizedModel = new ParametrizedModel(finalFields);

		parametrizedModel.setParameter("a", "changed!");
		parametrizedModel.setParameter("b", 1024);

		assertEquals("changed!", parametrizedModel.getParameter("a"));
		assertEquals(1024, parametrizedModel.getParameter("b"));
	}
}
