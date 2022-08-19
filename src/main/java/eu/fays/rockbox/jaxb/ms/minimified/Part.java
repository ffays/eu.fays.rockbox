package eu.fays.rockbox.jaxb.ms.minimified;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
public class Part {
	@XmlID
	@XmlAttribute(name = "key-number")
	public String _keyNumber;

	public Part() {}
	public Part(int keyNumber) {_keyNumber = Integer.toString(keyNumber);}
}
