package eu.fays.rockbox.jaxb.ms;

import java.text.MessageFormat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class A {
	@XmlElement
	@XmlJavaTypeAdapter(PartAdapter.class)
	public Part part;

	public A() {
	}

	public A(final Part part) {
		this.part = part;
	}

	@Override
	public int hashCode() {
		return part == null ? 0 : part.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		if (o != null && o instanceof A) {
			if (part == null && ((A) o).part == null) {
				return true;
			}
			if (part.equals(((A) o).part)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0}[{1}]", this.getClass().getSimpleName(), part.toString());
	}
}
