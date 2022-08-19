package eu.fays.rockbox.jaxb.ms.minimified;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class PartAdapter extends XmlAdapter<PartRef, Part> {
	@Override
	public PartRef marshal(Part part) throws Exception { return new PartRef(part); }

	@Override
	public Part unmarshal(PartRef partRef) throws Exception { return partRef._part; }
}
