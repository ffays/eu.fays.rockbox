package eu.fays.rockbox.jaxb.mapofmap3;

import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;
import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Document {

	@XmlElement
	private String name;

	@XmlElementWrapper(name = "dictionaries")
	@XmlElement(name = "dictionary")
	private List<Dictionary> dictionaries;

	@XmlTransient
	private Map<String, Object> dictionary;

	/**
	 * Constructor
	 */
	public Document() {

	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name of the document
	 * @param dictionary
	 *            the dictionaries
	 */
	public Document(final String name, final Map<String, Object> dictionary) {
		this.name = name;
		this.dictionary = dictionary;
	}

	/**
	 * Returns the name of the document
	 * 
	 * @return the name of the document
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the document
	 * 
	 * @param name
	 *            the name of the document
	 */
	public void setName(final String name) {
		this.name = name;
	}

	public Map<String, Object> getDictionary() {
		return dictionary;
	}

	public void setDictionary(Map<String, Object> dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * Marshal to XML this instance into the given output file
	 * 
	 * @param out
	 *            the output stream
	 * @throws JAXBException
	 *             in case of unexpected error
	 */
	public void marshal(final OutputStream out) throws JAXBException {
		//
		assert out != null;
		//

		if (dictionary != null && !dictionary.isEmpty()) {
			final Map<String, Map<String, Object>> maps = new LinkedHashMap<>();
			final Map<String, Object> map0 = new LinkedHashMap<>();
			maps.put(null, map0);
			for (Entry<String, Object> entry : dictionary.entrySet()) {
				if (entry.getValue() instanceof Map) {
					@SuppressWarnings("unchecked")
					final Map<String, Object> map1 = (Map<String, Object>) entry.getValue();
					map0.put(entry.getKey(), map1);
				} else {
					map0.put(entry.getKey(), entry.getValue());
				}
			}

			// @formatter:off
			final List<Dictionary> dictionaries = maps.entrySet().stream()
				.map(Dictionary::new)
				.flatMap(Dictionary::dictionaryStream)
				.collect(toList());
			// @formatter::on

			this.dictionaries = dictionaries;
		}
		
		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] { getClass() }, null);
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		marshaller.marshal(this, out);
	}

}
