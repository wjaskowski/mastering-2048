package put.ci.cevo.framework.hazelcast;

import com.hazelcast.core.HazelcastInstance;

import static com.google.common.base.Objects.toStringHelper;

public enum MessageType {

	SHUTDOWN("HazelCast instance shutdown") {
		@Override
		public void performAction(HazelcastInstance instance) {
			instance.getLifecycleService().shutdown();
		}
	};

	MessageType(String message) {
		this.message = message;
	}

	private String message;

	public MessageType withCustomMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("message", message).toString();
	}

	public abstract void performAction(HazelcastInstance instance);
}
