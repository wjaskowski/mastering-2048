package put.ci.cevo.util.injector;

public class ImmutableTestBean {

	private final TestBean1 bean1;

	private final int a;
	private final int b;

	public ImmutableTestBean(int b, int a, TestBean1 bean1, @SuppressWarnings("unused") TestBean2 bean2) {
		this.bean1 = bean1;
		this.a = a;
		this.b = b;
	}

	public TestBean1 getBean1() {
		return bean1;
	}

	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

}
