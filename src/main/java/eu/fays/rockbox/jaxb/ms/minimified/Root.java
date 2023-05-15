package eu.fays.rockbox.jaxb.ms.minimified;

import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static java.lang.Boolean.TRUE;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.util.List;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.util.ValidationEventCollector;

@XmlRootElement(name = "root")
@XmlType
public class Root {
	@XmlElement(name = "A")
	public A[] _a;
	
	@XmlElementWrapper(name = "B")
	@XmlElement(name = "part")
	public List<Part> _parts;

	public void marshal(final File outputFile) throws JAXBException {
		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] { getClass() }, null);
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		marshaller.marshal(this, outputFile);
	}

	public static Root unmarshal(final File file) throws JAXBException, SAXException {
		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] { Root.class }, null);
		final SchemaFactory factory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final Schema schema = factory.newSchema(new File("key.xsd"));
		unmarshaller.setSchema(schema);
		final ValidationEventCollector handler = new ValidationEventCollector();
		unmarshaller.setEventHandler(handler);
		final Root result = (Root) unmarshaller.unmarshal(file);
		return result;
	}
}
