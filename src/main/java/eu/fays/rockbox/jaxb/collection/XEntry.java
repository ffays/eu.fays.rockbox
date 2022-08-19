package eu.fays.rockbox.jaxb.collection;

import java.util.Map.Entry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.NONE)
public class XEntry {
	@XmlAttribute
	private String key;
	@XmlAttribute
	private String value;
	
	public XEntry() {
		
	}
	
	public XEntry(final Entry<String, Integer> entry) {
		key = entry.getKey();
		value = entry.getValue().toString();
	}
}
