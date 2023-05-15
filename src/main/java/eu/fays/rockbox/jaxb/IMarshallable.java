package eu.fays.rockbox.jaxb;

import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static jakarta.xml.bind.Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION;
import static java.lang.Boolean.TRUE;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.util.ValidationEventCollector;

public interface IMarshallable<T> {

	/**
	 * Marshal to XML this instance into the given output file
	 * @param outputFile the output file
	 * @param schemaFile the XML Schema file used to validate (optional, may be null)
	 * @throws JAXBException in case of unexpected error
	 */
	default void marshal(final File outputFile, final File schemaFile) throws JAXBException {
		//
		assert outputFile != null;
		//

		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] { getClass() }, null);
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		if (schemaFile != null) {
			marshaller.setProperty(JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "../" + schemaFile.getPath().replace(System.getProperty("file.separator"), "/"));
		}
		marshaller.marshal(this, outputFile);
	}

	/**
	 * Unmarshal from XML the given input file
	 * @param inputFile the input file
	 * @param schemaFile the XML Schema file used to validate (optional, may be null)
	 * @return a new instance
	 * @throws JAXBException in case of unexpected error
	 * @throws SAXException in case of unexpected error
	 */
	default T unmarshal(final File inputFile, final File schemaFile) throws JAXBException, SAXException {
		//
		assert inputFile != null;
		assert schemaFile != null;
		assert inputFile.isFile();
		assert inputFile.canRead();
		assert schemaFile.isFile();
		assert schemaFile.canRead();
		//

		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] { getClass() }, null);
		final SchemaFactory factory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		if (schemaFile != null) {
			final Schema schema = factory.newSchema(schemaFile);
			unmarshaller.setSchema(schema);
			final ValidationEventCollector handler = new ValidationEventCollector();
			unmarshaller.setEventHandler(handler);
		}
		@SuppressWarnings("unchecked")
		final T result = (T) unmarshaller.unmarshal(inputFile);

		//
		assert result != null;
		//
		return result;
	}
}
