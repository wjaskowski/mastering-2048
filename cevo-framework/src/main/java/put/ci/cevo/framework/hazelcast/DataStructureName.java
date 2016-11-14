package put.ci.cevo.framework.hazelcast;

public enum DataStructureName {

	RESULTS("res"),
	RUN_ID("id"),
	MESSAGES("messages");

	DataStructureName(String name) {
		this.name = name;
	}

	private final String name;

	public String getName() {
		return name;
	}

}
