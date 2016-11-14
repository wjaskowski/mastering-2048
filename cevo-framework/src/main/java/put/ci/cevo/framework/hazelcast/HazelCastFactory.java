package put.ci.cevo.framework.hazelcast;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import static put.ci.cevo.framework.hazelcast.DataStructureName.MESSAGES;

public class HazelCastFactory {

	private static final Supplier<HazelcastInstance> INSTANCE = Suppliers.memoize(new Supplier<HazelcastInstance>() {
		@Override
		public HazelcastInstance get() {
			HazelcastInstance instance = Hazelcast.newHazelcastInstance(new XmlConfigBuilder().build().setProperty(
				"hazelcast.logging.type", "log4j"));
			ITopic<MessageType> topic = instance.getTopic(MESSAGES.getName());
			topic.addMessageListener(new ClusterMessagesListener(instance));
			return instance;
		}
	});

	public static HazelcastInstance getInstance() {
		return INSTANCE.get();
	}

}
