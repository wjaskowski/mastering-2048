package put.ci.cevo.util.stats;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.jodah.typetools.TypeResolver;
import put.ci.cevo.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EventsLogger {

	private static final Logger logger = Logger.getLogger(EventsLogger.class);

	private final Map<Type, EventHandler<? extends Object>> handlers = new HashMap<>();

	protected EventsLogger() {
		// forbid direct construction
	}

	public <T> void register(EventHandler<T> eventHandler) {
		register(getEventType(eventHandler), eventHandler);
	}
	
	private <T> Class<?> getEventType(EventHandler<T> eventHandler) {
		return TypeResolver.resolveRawArgument(EventHandler.class, eventHandler.getClass());
	}
	
	public <T> void register(Class<?> eventClass, EventHandler<T> eventHandler) {
		if (handlers.containsKey(eventClass)) {
			logger.warn("Registering logger " + eventHandler.getClass().getName()
				+ " which overrides previously registered logger for class " + eventClass);
		}
		handlers.put(eventClass, eventHandler);
	}

	public <T> EventHandler<? extends Object> unregister(Class<?> target) {
		return handlers.remove(target);
	}

	public <T> void log(T object) {
		log(object.getClass(), object);
	}
	
	public <T> void log(Object event, T object) {
		log(event.getClass(), object);
	}
	
	public <T> void log(Class<?> eventClass, T object) {
		Preconditions.checkNotNull(object);
		EventHandler<T> handler = resolveHandler(eventClass);
		log(object, handler);
	}

	<T> void log(T object, EventHandler<T> handler) {
		handler.log(object);
	}

	public <T> T getHandler(Class<?> eventClass, Class<T> typeClass) {
		return TypeUtils.explicitCast(resolveHandler(eventClass));
	}
	
	@SuppressWarnings("unchecked")
	private <T> EventHandler<T> resolveHandler(Class<?> eventClass) {
		EventHandler<T> handler = (EventHandler<T>) handlers.get(eventClass);
		if (handler != null) {
			return handler;
		}
		throw new RuntimeException("No handler found for event: " + eventClass);
	}
	
}
