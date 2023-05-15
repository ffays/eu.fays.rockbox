package eu.fays.rockbox.jaxb.collection;

import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;
import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.File;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Map")
public class XMap {

	@XmlElement(name = "Entry")
	public XEntry[] _entries;

	public XMap() {
	}

	public XMap(final Map<String, Integer> map) {
		_entries = map.entrySet().stream().map(e -> new XEntry(e)).collect(toList()).toArray(new XEntry[0]);
	}

	/**
	 * Marshal to XML this instance into the given output file
	 * @param file the output file
	 * @throws JAXBException in case of unexpected error
	 */
	public void marshal(final File file) throws JAXBException {
		//
		assert file != null;
		//

		
		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] { getClass() }, null);
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		marshaller.marshal(this, file);
	}
}
