package eu.fays.rockbox.jaxb.collection;

import static java.util.Arrays.stream;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBException;

import eu.fays.rockbox.jaxb.Fruit;

@SuppressWarnings("nls")
public class JAXBCollectionEssay {
	public static void main(String[] args) throws JAXBException {
		final Map<String, Integer> map = new LinkedHashMap<>();
		stream(Fruit.values()).forEach(e -> map.put(e.name(), e.ordinal()));
		final XMap xm = new XMap(map);
		final File file = new File("output/collection.xml");
		xm.marshal(file);
	}
}
