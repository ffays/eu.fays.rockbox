package eu.fays.rockbox.jaxb.collection.typed;

import java.util.Map.Entry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
public class XEntry implements Entry<String, String> {
	@XmlAttribute
	public String key;
	@XmlAttribute
	public String type;
	@XmlValue
	public String value;

	public XEntry() {

	}

	public XEntry(final String key, final Object value) {
		//
		assert key != null;
		assert !key.isEmpty();
		//
		this.key = key;
		this.value = value == null ? null : value.toString();
		this.type = value == null ? null : value.getClass().getSimpleName();
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	@Override
	public String setValue(final String value) {
		throw new UnsupportedOperationException();
	}
}
