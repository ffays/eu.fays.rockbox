package eu.fays.rockbox.jaxb.ms.minimified;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType
public class A {
	@XmlElement(name = "part")
	@XmlJavaTypeAdapter(PartAdapter.class)
	public Part _part;

	public A() {}
	public A(Part part) { _part = part; }
}
