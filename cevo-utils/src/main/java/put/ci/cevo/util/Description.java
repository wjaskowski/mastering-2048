package put.ci.cevo.util;

import static org.apache.commons.lang.StringUtils.lowerCase;
import static put.ci.cevo.util.sequence.Sequences.seq;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;

import put.ci.cevo.util.sequence.Sequence;

import com.google.common.collect.Lists;

/**
 * A simple generic description model class. This can be rendered into a variaty of formats e.g. INI, PDF, HTML, TXT,
 * XML, HTML etc.
 */
public class Description {

	public static class Element {

		public enum Type {
			SECTION,
			COMMENT,
			PROPERTY
		};

		private final Type type;
		private final String text;

		private Element(Type type, String text) {
			this.type = type;
			this.text = text;
		}

		public Type getType() {
			return type;
		}

		public String getText() {
			return text;
		}

		@Override
		public String toString() {
			return type + ": " + text;
		}
	}

	private final List<Element> elements = Lists.newArrayList();
	private final String name;

	public Description() {
		this.name = "";
	}

	public Description(String name) {
		this.name = name;
	}

	public Description(Object value) {
		this();
		this.addProperty(value);
	}

	public Description(Object bean, String... propertyNames) {
		this();
		this.addProperties(bean, propertyNames);
	}

	public String getName() {
		return name;
	}

	public void addSection(String sectionName) {
		this.elements.add(new Element(Element.Type.SECTION, sectionName));
	}

	public void addComment(String text) {
		this.elements.add(new Element(Element.Type.COMMENT, text));
	}

	public void addProperty(String name, Object value) {
		this.elements.add(new Element(Element.Type.PROPERTY, name + " = " + value));
	}

	public void addProperty(Object value) {
		addProperty(lowerCase(value.getClass().getSimpleName()), value);
	}

	public void addExistingProperty(String name, Object value) {
		if (value != null) {
			addProperty(name, value);
		}
	}

	public void addExistingProperty(Object value) {
		if (value != null) {
			addProperty(value);
		}
	}

	public void addProperties(Map<String, ? extends Object> properties) {
		for (Map.Entry<String, ? extends Object> property : properties.entrySet()) {
			addProperty(property.getKey(), property.getValue());
		}
	}

	public void addProperties(Object bean, String... propertyNames) {
		BeanUtilsBean beanUtils = BeanUtils.getBeanUtils();
		for (String propertyName : propertyNames) {
			try {
				if (propertyName.equals("class")) {
					addProperty("class", bean.getClass().getCanonicalName());
				} else {
					addProperty(propertyName, beanUtils.getProperty(bean, propertyName));
				}
			} catch (Exception e) {
				throw new RuntimeException("Invalid property: '" + propertyName + "' for '" + bean + "'", e);
			}
		}
	}

	public void addDescription(Description description) {
		this.elements.addAll(description.elements);
	}

	public void addDescription(String prefix, Description description) {
		for (Element element : description.elements) {
			this.elements
				.add((element.type == Element.Type.PROPERTY || element.type == Element.Type.SECTION) ? new Element(
					element.type, prefix + "." + element.text) : element);
		}
	}

	public Sequence<Element> listElements() {
		return seq(elements);
	}

	@Override
	public String toString() {
		return listElements().toList().toString();
	}

	public static Description forClass(Object object) {
		Description description = new Description();
		description.addProperty("name", object.getClass().getSimpleName());
		for (PropertyDescriptor property : BeanUtils.describe(object.getClass(), Object.class)) {
			description.addProperty(property.getName(), BeanUtils.getProperty(object, property.getName()));
		}
		return description;
	}

}
