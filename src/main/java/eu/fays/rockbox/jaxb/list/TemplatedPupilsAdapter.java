package eu.fays.rockbox.jaxb.list;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
public class TemplatedPupilsAdapter extends WrappedListAdapter<String> {

	@Override
	@XmlElement(name = "pupil")
	public List<String> getList() {
		// TODO Auto-generated method stub
		return super.getList();
	}

	@Override
	@XmlElement
	public void setList(List<String> list) {
		// TODO Auto-generated method stub
		super.setList(list);
	}

}
