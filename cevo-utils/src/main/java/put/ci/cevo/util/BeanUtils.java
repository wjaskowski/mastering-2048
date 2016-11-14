package put.ci.cevo.util;

import static put.ci.cevo.util.TypeUtils.genericCast;
import static put.ci.cevo.util.sequence.Sequences.seq;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.beans.ExtendedBeanInfoFactory;

import put.ci.cevo.util.filter.AbstractFilter;
import put.ci.cevo.util.injector.ParsingConverters;

public class BeanUtils {

	private static final BeanUtilsBean beanUtils = new BeanUtilsBean(new ParsingConverters(), new PropertyUtilsBean());
	private static final PropertyUtilsBean propertyUtils = new PropertyUtilsBean();

	public static Map<String, Object> describe(Object bean) {
		try {
			Map<String, Object> properties = genericCast(propertyUtils.describe(bean));
			return properties;
		} catch (Exception e) {
			throw new RuntimeException("A fatal error occured while describing bean: " + bean + "!", e);
		}
	}

	public static PropertyDescriptor[] describe(Class<?> type) {
		try {
			return Introspector.getBeanInfo(type).getPropertyDescriptors();
		} catch (Exception e) {
			throw new RuntimeException("A fatal error occured while describing type: " + type + "!", e);
		}
	}

	public static PropertyDescriptor[] describe(Class<?> type, Class<?> superType) {
		try {
			return Introspector.getBeanInfo(type, superType).getPropertyDescriptors();
		} catch (Exception e) {
			throw new RuntimeException("A fatal error occured while describing type: " + type + "!", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <P> P getProperty(Object bean, String name) {
		try {
			return (P) propertyUtils.getProperty(bean, name);
		} catch (Exception e) {
			throw new RuntimeException("Error while getting property: '" + name + "' from bean of type: "
				+ bean.getClass(), e);
		}
	}

	public static <P> P getProperty(Object bean, String name, Class<P> type) {
		try {
			return convertValue(name, type, propertyUtils.getProperty(bean, name));
		} catch (Exception e) {
			throw new RuntimeException("Error while getting property: '" + name + "' from bean: " + bean, e);
		}
	}

	public static void setProperty(Object bean, String name, Object value) {
		try {
			beanUtils.copyProperty(bean, name, value);
		} catch (Exception e) {
			String message = "Error while setting property: '" + name + " = " + value + "' in bean: " + bean;
			throw new RuntimeException(message, e);
		}
	}

	public static void copyProperties(Object from, Object to) {
		ExtendedBeanInfoFactory extendedBeanInfoFactory = new ExtendedBeanInfoFactory();
		try {
			BeanInfo fromBean = extendedBeanInfoFactory.getBeanInfo(from.getClass());
			BeanInfo toBean = extendedBeanInfoFactory.getBeanInfo(to.getClass());

			List<Pair<PropertyDescriptor, PropertyDescriptor>> propertyPairs = seq(toBean.getPropertyDescriptors())
				.zip(seq(fromBean.getPropertyDescriptors()))
				.filter(new AbstractFilter<Pair<PropertyDescriptor, PropertyDescriptor>>() {
					@Override
					public boolean qualifies(Pair<PropertyDescriptor, PropertyDescriptor> properties) {
						PropertyDescriptor fromProperty = properties.second();
						PropertyDescriptor toProperty = properties.first();
						return !fromProperty.getDisplayName().equals("class")
							&& fromProperty.getDisplayName().equals(toProperty.getDisplayName());
					}
				}).toList();

			for (Pair<PropertyDescriptor, PropertyDescriptor> properties : propertyPairs) {
				PropertyDescriptor fromProperty = properties.second();
				PropertyDescriptor toProperty = properties.first();
				if (toProperty.getWriteMethod() != null) {
					Object propertyToSet = ReflectionUtils.invoke(from, fromProperty.getReadMethod());
					ReflectionUtils.invoke(to, toProperty.getWriteMethod(), propertyToSet);
				}

			}
		} catch (IntrospectionException e) {
			throw new RuntimeException("A fatal error occured while copying beans!", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <P> P convertValue(String name, Class<P> type, Object value) {
		if (type.isInstance(value)) {
			return (P) value;
		} else {
			Object convertedValue = beanUtils.getConvertUtils().convert(value, type);
			if (type.isInstance(convertedValue) || type.isPrimitive()) {
				return (P) convertedValue;
			} else {
				String message = "The requested property: '" + name + "' could not be converted to: " + type;
				throw new RuntimeException(message);
			}
		}
	}

	public static BeanUtilsBean getBeanUtils() {
		return beanUtils;
	}

	public static PropertyUtilsBean getPropertyUtils() {
		return propertyUtils;
	}

}
