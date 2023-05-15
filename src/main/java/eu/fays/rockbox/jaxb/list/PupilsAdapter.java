package eu.fays.rockbox.jaxb.list;

import java.util.List;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class PupilsAdapter extends XmlAdapter<Pupils, List<String>> {

	@Override
	public List<String> unmarshal(Pupils v) throws Exception {
		return v.pupil;
	}

	@Override
	public Pupils marshal(List<String> v) throws Exception {
		if (v == null || v.isEmpty()) {
			return null;
		}

		return new Pupils(v);
	}

}
