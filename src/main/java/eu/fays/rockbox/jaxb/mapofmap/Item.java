package eu.fays.rockbox.jaxb.mapofmap;

import java.util.Map.Entry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
public class Item implements Entry<String, String> {
	@XmlAttribute
	public String key;
	@XmlAttribute
	public String type;
	@XmlValue
	public String value;

	public Item() {
	}

	public Item(Entry<String, Object> entry) {
		key = entry.getKey();
		value = entry.getValue().toString();
		type = entry.getValue().getClass().getSimpleName();
	}

	@Override
	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String setValue(String value) {
		throw new UnsupportedOperationException();
	}
}
