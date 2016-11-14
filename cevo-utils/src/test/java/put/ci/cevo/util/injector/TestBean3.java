package put.ci.cevo.util.injector;

public class TestBean3 {

	private TestBean1 bean1;

	private int a;
	private int b;

	public TestBean3(TestBean1 bean1, int b, int a) {
		this.bean1 = bean1;
		this.a = a;
		this.b = b;
	}

	public TestBean1 getBean1() {
		return bean1;
	}

	public void setBean1(TestBean1 bean1) {
		this.bean1 = bean1;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

}
