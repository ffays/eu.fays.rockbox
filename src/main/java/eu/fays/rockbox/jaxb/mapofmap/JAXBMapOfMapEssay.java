package eu.fays.rockbox.jaxb.mapofmap;

import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import eu.fays.rockbox.jaxb.Fruit;

public class JAXBMapOfMapEssay {

	public static void main(String[] args) throws Exception {
		final File file = File.createTempFile(JAXBMapOfMapEssay.class.getSimpleName() + "-", ".xml");
		final String now = LocalDateTime.now().toString();
		System.out.println(now);

		final Map<String, Object> dict1 = stream(Fruit.values()).map(e -> new SimpleImmutableEntry<>(e.name(), e.ordinal())).collect(toMap(Entry::getKey, Entry::getValue));
		final LinkedHashMap<String, Object> dict0 = new LinkedHashMap<>();
		dict0.put("PI", PI);
		dict0.put("E", E);
		dict0.put(Fruit.class.getSimpleName(), dict1);
		final Document document = new Document(now, dict0);
		document.marshal(file);
		System.out.println(file);
		try (final FileInputStream in = new FileInputStream(file)) {
			int c = in.read();
			while (c != -1) {
				System.out.write(c);
				c = in.read();
			}
		}
	}
}
