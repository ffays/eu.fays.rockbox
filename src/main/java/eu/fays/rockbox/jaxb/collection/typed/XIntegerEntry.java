package eu.fays.rockbox.jaxb.collection.typed;

import java.util.Map.Entry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
public final class XIntegerEntry implements Entry<String, Integer> {
	@XmlAttribute
	public String key;
	@XmlValue
	public Integer value;

	public XIntegerEntry() {

	}

	public XIntegerEntry(final String key, final Integer value) {
		//
		assert key != null;
		assert !key.isEmpty();
		//
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public Integer setValue(final Integer value) {
		throw new UnsupportedOperationException();
	}
}
