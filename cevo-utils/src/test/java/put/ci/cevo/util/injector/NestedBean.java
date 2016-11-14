package put.ci.cevo.util.injector;

public class NestedBean {

	private final ImmutableTestBean bean1;
	private final TestBean3 bean3;

	public NestedBean(ImmutableTestBean bean1, TestBean3 bean3) {
		this.bean1 = bean1;
		this.bean3 = bean3;
	}

	public ImmutableTestBean getBean1() {
		return bean1;
	}

	public TestBean3 getBean3() {
		return bean3;
	}

}
