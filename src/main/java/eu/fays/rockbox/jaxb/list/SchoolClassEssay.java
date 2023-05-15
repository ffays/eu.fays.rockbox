package eu.fays.rockbox.jaxb.list;

import java.util.ArrayList;

public class SchoolClassEssay {

	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception {
		SchoolClass schoolClass = new SchoolClass();
		schoolClass.teacher = "Maggie";
		schoolClass.pupils0 = new ArrayList<>();
		schoolClass.pupils1 = new ArrayList<>();
		schoolClass.pupils2 = new ArrayList<>();
		schoolClass.pupils3 = new ArrayList<>();

		
		schoolClass.pupils0.add("Bart");
		schoolClass.pupils0.add("Lisa");
		schoolClass.pupils1.add("Bart");
		schoolClass.pupils1.add("Lisa");
		schoolClass.pupils2.add("Bart");
		schoolClass.pupils2.add("Lisa");
		schoolClass.pupils3.add("Bart");
		schoolClass.pupils3.add("Lisa");
		
//		File targetFile = new File(new File("output"), "classroom.xml");
//		schoolClass.marshal(targetFile);
		schoolClass.marshal(System.out);
	}
}
