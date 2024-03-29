package eu.fays.rockbox.dx;

import static java.lang.Boolean.TRUE;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.File;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.xml.sax.SAXException;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "articles" })
public class WishList {

	public WishList() {

	}

	public WishList(final List<Article> articles) {
		this.articles = articles;
	}

	/**
	 * Marshal to XML this instance into the given output file
	 * 
	 * @param file the output file
	 * @throws JAXBException in case of unexpected error
	 */
	public void marshal(final File file) throws JAXBException {
		//
		assert file != null;
		//
		
		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] {WishList.class}, null);
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders","<?xml-stylesheet type=\"text/xsl\" href=\"style/wishlist.xslt\"?>");
		// marshaller.setProperty(JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "../" + XML_SCHEMA_FILE.getPath().replace(System.getProperty("file.separator"), "/"));
		marshaller.marshal(this, file);
	}

	/**
	 * Unmarshal from XML the given input file
	 * 
	 * @param file the input file
	 * @return a new instance
	 * @throws JAXBException in case of unexpected error
	 * @throws SAXException in case of unexpected error
	 */
	public static WishList unmarshal(final File file) throws JAXBException, SAXException {
		//
		assert file != null;
		assert file.isFile();
		assert file.canRead();
		//
		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] {WishList.class}, null);
		final SchemaFactory factory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
		final Schema schema = factory.newSchema(XML_SCHEMA_FILE);

		final Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);

		final ValidationEventCollector handler = new ValidationEventCollector();
		unmarshaller.setEventHandler(handler);
		final WishList result = (WishList) unmarshaller.unmarshal(file);

		//
		assert result != null;
		//
		return result;

	}

//	@XmlElementWrapper
//	@XmlElement(name = "articles")
	@XmlElement(name = "article")
	private List<Article> articles = null;

	/** The XML schema file to validate this class */
	private static final File XML_SCHEMA_FILE = new File("todo.xsd");

}
