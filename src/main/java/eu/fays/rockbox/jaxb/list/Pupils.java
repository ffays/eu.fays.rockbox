package eu.fays.rockbox.jaxb.list;

import java.util.ArrayList;
import java.util.List;

public class Pupils  {

	public List<String> pupil;
	
	public Pupils() {
		pupil = new ArrayList<>();
	}
	
	public Pupils(List<String> pupil) {
		this.pupil = pupil;
	}
}
