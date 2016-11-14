package put.ci.cevo.util.injector;

public class TestBean1 {

	private int x;

	private String name;

	public TestBean1(int x, String name) {
		this.x = x;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

}
