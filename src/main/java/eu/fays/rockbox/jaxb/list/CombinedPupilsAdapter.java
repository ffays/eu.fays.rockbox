package eu.fays.rockbox.jaxb.list;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class CombinedPupilsAdapter extends XmlAdapter<CombinedPupilsAdapter, List<String>> {

	@XmlElement(name = "pupil")
	public List<String> list;

	@Override
	public List<String> unmarshal(CombinedPupilsAdapter v) throws Exception {
		return v.list;
	}

	@Override
	public CombinedPupilsAdapter marshal(List<String> v) throws Exception {
		if(v == null || v.isEmpty()) {
			return null;
		}
		final CombinedPupilsAdapter result = new CombinedPupilsAdapter();
		result.list = v;
		return result;
	}

}

