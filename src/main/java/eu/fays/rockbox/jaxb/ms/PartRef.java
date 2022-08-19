package eu.fays.rockbox.jaxb.ms;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "part" })
public class PartRef {
	@XmlIDREF
	@XmlAttribute(name = "ref-number")
	public Part part;

	public PartRef() {
	}

	public PartRef(final Part part) {
		this.part = part;
	}
}
