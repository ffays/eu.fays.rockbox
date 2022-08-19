package eu.fays.rockbox.jaxb.ms.minimified;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;

public class PartRef {
	@XmlIDREF
	@XmlAttribute(name = "ref-number")
	public Part _part;

	public PartRef(Part part) {_part = part;}
	public PartRef() {}	
}
