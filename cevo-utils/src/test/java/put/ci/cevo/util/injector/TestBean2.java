package put.ci.cevo.util.injector;

public class TestBean2 {

	private TestType testField;

	public TestBean2(TestType testField) {
		this.testField = testField;
	}

	public TestType getTestField() {
		return testField;
	}

	public void setTestField(TestType testField) {
		this.testField = testField;
	}

}
