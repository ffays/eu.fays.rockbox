package eu.fays.rockbox.jaxb.collection.typed;

import java.util.Map.Entry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
public final class XStringEntry implements Entry<String, String> {
	@XmlAttribute
	public String key;
	@XmlValue
	public String value;

	public XStringEntry() {

	}

	public XStringEntry(final String key, final String value) {
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
	public String getValue() {
		return value;
	}

	@Override
	public String setValue(final String value) {
		throw new UnsupportedOperationException();
	}
}
