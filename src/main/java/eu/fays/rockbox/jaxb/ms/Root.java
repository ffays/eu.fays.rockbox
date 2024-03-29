package eu.fays.rockbox.jaxb.ms;

import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static java.lang.Boolean.TRUE;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.util.ValidationEventCollector;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "a", "parts" })
public class Root {

	@XmlElement(name = "A")
	public A[] a;
	@XmlElementWrapper(name = "B")
	@XmlElement(name = "part")
	public List<Part> parts = new ArrayList<>();

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final List<Object> list = new ArrayList<>();
		if (a != null) {
			list.addAll(Arrays.asList(a));
		}
		list.addAll(parts);
		return list.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof Root)) {
			return false;
		}
		final Root o = (Root) obj;

		boolean result = true;
		result &= ((a == null) && (o.a == null)) || ((a != null) && (o.a != null));
		result &= parts.equals(o.parts);
		if (result) {
			result = Arrays.asList(a).equals(Arrays.asList(o.a));
		}
		return result;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0}[{1},{2}]", this.getClass().getSimpleName(), Arrays.asList(a).toString(), parts.toString());
	}

	/**
	 * Marshal to XML this instance into the given output file
	 * @param outputFile the output file
	 * @throws JAXBException in case of unexpected error
	 */
	public void marshal(final File outputFile) throws JAXBException {
		//
		assert outputFile != null;
		//

		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] { getClass() }, null);
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		// marshaller.setProperty(JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "../" + XML_SCHEMA_FILE.getPath().replace(System.getProperty("file.separator"), "/"));
		marshaller.marshal(this, outputFile);
	}

	/**
	 * Unmarshal from XML the given input file
	 * @param file the input file
	 * @return a new instance
	 * @throws JAXBException in case of unexpected error
	 * @throws SAXException in case of unexpected error
	 */
	public static Root unmarshal(final File file) throws JAXBException, SAXException {
		//
		assert file != null;
		assert file.isFile();
		assert file.canRead();
		//

		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] { Root.class }, null);
		final SchemaFactory factory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
		final Schema schema = factory.newSchema(XML_SCHEMA_FILE);

		final Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);

		final ValidationEventCollector handler = new ValidationEventCollector();
		unmarshaller.setEventHandler(handler);
		final Root result = (Root) unmarshaller.unmarshal(file);

		//
		assert result != null;
		//
		return result;
	}

	/** The XML schema file to validate this class */
	private static final File XML_SCHEMA_FILE = new File("xml/key.xsd");

}
