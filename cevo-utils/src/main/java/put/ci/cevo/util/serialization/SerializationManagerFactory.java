package put.ci.cevo.util.serialization;

import org.apache.log4j.Logger;
import put.ci.cevo.util.ReflectionUtils;
import put.ci.cevo.util.lister.ClassesLister;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

public class SerializationManagerFactory {

	private static final Logger logger = Logger.getLogger(SerializationManagerFactory.class);

	public static SerializationManager create() {
		return create("put.ci.cevo");
	}

	@SuppressWarnings("rawtypes")
	public static SerializationManager create(String packageName) {
		final ClassesLister classesLister = new ClassesLister.Builder(packageName).buildImmediately();
		final SerializationManager manager = new SerializationManager();
		for (Class<? extends ObjectSerializer> clazz : classesLister.getSubtypes(ObjectSerializer.class)) {
			try {
				if (clazz.isAnnotationPresent(AutoRegistered.class)) {
					ObjectSerializer<?> serializer = ReflectionUtils.invokeConstructor(clazz);
					AutoRegistered autoRegistered = clazz.getAnnotation(AutoRegistered.class);
					if (autoRegistered.defaultSerializer()) {
						manager.register(serializer);
					} else {
						manager.registerAdditional(serializer);
					}
				}
			} catch (SerializationException e) {
				logger.error("Unable to register serializer: " + clazz.getSimpleName(), e);
			}
		}
		return manager;
	}
}
