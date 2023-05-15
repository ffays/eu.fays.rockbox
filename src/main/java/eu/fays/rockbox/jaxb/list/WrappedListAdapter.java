package eu.fays.rockbox.jaxb.list;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class WrappedListAdapter<T> extends XmlAdapter<WrappedListAdapter<T>, List<T>> {


	private List<T> list;

	@XmlElement
	public List<T> getList() {
		return list;
	}
	
	public void setList(List<T> list) {
		this.list = list;
	}
	
	@Override
	public List<T> unmarshal(WrappedListAdapter<T> v) throws Exception {
		return v.list;
	}

	@Override
	public WrappedListAdapter<T> marshal(List<T> v) throws Exception {
		if (v == null || v.isEmpty()) {
			return null;
		}
		@SuppressWarnings("unchecked")
		final WrappedListAdapter<T> result = getClass().getDeclaredConstructor().newInstance();
		result.list = v;
		return result;
	}

}
