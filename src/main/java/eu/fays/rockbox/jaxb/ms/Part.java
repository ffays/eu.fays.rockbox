package eu.fays.rockbox.jaxb.ms;

import java.text.MessageFormat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "keyNumber" })
public class Part {

	@XmlID
	@XmlAttribute(name = "key-number")
	public String keyNumber;

	public Part() {
	}

	public Part(final int keyNumber) {
		this.keyNumber = Integer.toString(keyNumber);
	}

	@Override
	public int hashCode() {
		return keyNumber == null ? 0 : keyNumber.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		if (o != null && o instanceof Part) {
			if (keyNumber == null && ((Part) o).keyNumber == null) {
				return true;
			}
			if (keyNumber.equals(((Part) o).keyNumber)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0}[{1}]", this.getClass().getSimpleName(), keyNumber);
	}
}
