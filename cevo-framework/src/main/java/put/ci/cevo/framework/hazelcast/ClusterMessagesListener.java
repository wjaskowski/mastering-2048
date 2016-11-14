package put.ci.cevo.framework.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.apache.log4j.Logger;

import static put.ci.cevo.util.HostnameUtil.getHostName;

public class ClusterMessagesListener implements MessageListener<MessageType> {

	private static final Logger logger = Logger.getLogger(ClusterMessagesListener.class);

	private final HazelcastInstance instance;

	public ClusterMessagesListener(HazelcastInstance instance) {
		this.instance = instance;
	}

	@Override
	public void onMessage(Message<MessageType> message) {
		logger.info("Host: " + getHostName() + " performing action: " + message.getMessageObject());
		message.getMessageObject().performAction(instance);
	}

}