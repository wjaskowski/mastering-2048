package put.ci.cevo.util.stats;

import put.ci.cevo.util.ReflectionUtils;
import put.ci.cevo.util.lister.ClassesLister;

public class EventsLoggerFactory {

	private static final ClassesLister classesLister = new ClassesLister.Builder("put.ci.cevo").buildImmediately();

	@SuppressWarnings("rawtypes")
	public static EventsLogger createDefault() {
		final EventsLogger manager = new EventsLogger();
		for (Class<? extends TableEventHandler> clazz : classesLister.getSubtypes(TableEventHandler.class)) {
			TableEventHandler<?> handler = ReflectionUtils.invokeConstructor(clazz);
			if (clazz.isAnnotationPresent(Handler.class)) {
				Handler autoRegistered = clazz.getAnnotation(Handler.class);
				Class<?> target = autoRegistered.targetEvent();
				manager.register(target, handler);
			} else {
				manager.register(handler);
			}
		}
		return manager;
	}
	
	public static EventsLogger createEmpty() {
		return new EventsLogger();
	}

}
