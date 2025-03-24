package eu.fays.rockbox.jackson;

import static jakarta.persistence.CascadeType.ALL;
import static org.eclipse.persistence.oxm.MediaType.APPLICATION_XML;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

/**
 * A Library
 */

// XML/JAXB Annotations
@XmlType
@XmlRootElement(name = "library")
@XmlAccessorType(XmlAccessType.NONE)
// JSON/Jackson Annotations
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
// JPA annotations
@Entity
@Table(schema = "librarian", name = "library")
@SuppressWarnings("nls")
public class Library {

	/** File name of the XML Schema */
	private static final String XML_SCHEMA_FILE_NAME = "Library.xsd";
	/** File name of the JSON Schema */
	private static final String JSON_SCHEMA_FILE_NAME = "Library-eclipselink-moxy.schema.json";
	/** Masks: 0xA000000000000000, 0xB000000000000000, 0xC000000000000000, 0xD000000000000000, 0xE000000000000000 and 0xF000000000000000 */
	private static final long[] MASKS = { -0x6000000000000000L, -0x5000000000000000L, -0x4000000000000000L, -0x3000000000000000L, -0x2000000000000000L, -0x1000000000000000L };
	/** Random number generator */
	private static final Random RANDOM = new Random();

	/** Universally Unique Identifier compatible with XML IDs (cf. type="xs:ID") */
	// JPA annotations
	@Column(name = "uuid", columnDefinition = "UUID")
	@Convert(converter = UniversallyUniqueIdentifierAdapter.class)
	@Id
	private UUID uuid;

	/** Book index */
	// XML/JAXB Annotations
	@XmlElementWrapper(name = "books")
	@XmlElement(name = "book")
	// JPA annotations
	@OneToMany(mappedBy = "library", cascade = ALL, orphanRemoval = true)
	List<Book> books = new ArrayList<>();

	/** List of bookshelfs inside the library */
	// XML/JAXB Annotations
	@XmlElementWrapper(name = "bookshelfs")
	@XmlElement(name = "bookshelf")
	// JPA annotations
	@OneToMany(mappedBy = "library", cascade = ALL, orphanRemoval = true)
	List<Bookshelf> bookshelfs = new ArrayList<>();

	/** Physical location of the library */
	// XML/JAXB Annotations
	@XmlElement(name = "location")
	// JPA annotations
	@Column(name = "location", length = 200)
	String location;

	// XML/JAXB Annotations
	@XmlTransient
	// JPA annotations
	@JoinColumn(name = "best_book_uuid")
	@ManyToOne
	Book bestBook; 
	
	/**
	 * Constructor
	 */
	public Library() {

	}

	/**
	 * Constructor
	 * 
	 * @param i the uuid
	 * @param l physical location of the library
	 */
	public Library(final UUID i, final String l) {
		uuid = i;
		location = l;
	}
	
	/**
	 * Unmarshal the given input stream
	 * 
	 * @param resource the resource location of the input stream (e.g. the file URL)
	 * @param inputStream either a XML or a JSON input stream
	 * @param useSchemaValidation flag to activate the XML schema validation
	 * @param mediaType either application/xml or application/json
	 * @param encoding the encoding, e.g. {@link StandardCharsets#UTF_8 UTF-8}
	 * @return the library
	 * @throws JAXBException in case of unexpected error
	 * @throws SAXException in case of unexpected error
	 * @throws CustomXmlException in case of unexpected error
	 * @throws IOException  in case of unexpected error
	 */
	public static Library unmarshal(final URL resource, final MediaType mediaType, final Charset encoding, final boolean useSchemaValidation) throws JAXBException, SAXException, CustomXmlException, IOException {
		//
		assert resource != null;
		assert mediaType != null;
		assert encoding != null;
		//

		Library result = null;

		final Map<String, Object> properties;
		
		if(mediaType == MediaType.APPLICATION_JSON) {
			// https://eclipse.dev/eclipselink/documentation/4.0/moxy/moxy.html#BABFDBJG
//			properties = new HashMap<String, Object>();
//			properties.put("eclipselink.media-type", "application/json");
			properties = null;
		} else {
			properties = null;
		}
		
		final JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class<?>[] { Library.class }, properties);
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setProperty(JAXBContextProperties.MEDIA_TYPE /* "eclipselink.media-type" */, mediaType);
//		unmarshaller.setProperty(Marshaller.JAXB_ENCODING, encoding.name());

		final CustomValidationEventCollector handler;
		if (useSchemaValidation) {
			final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// Trying to load schema from bundle
			final String schemaFileName = XML_SCHEMA_FILE_NAME;
			final URL schemaURL = Library.class.getResource("/" + schemaFileName); //$NON-NLS-1$
			final Schema schema = (schemaURL != null) ? factory.newSchema(schemaURL) : factory.newSchema(new File(schemaFileName));

			unmarshaller.setSchema(schema);
			handler = new CustomValidationEventCollector();
			unmarshaller.setEventHandler(handler);
		} else {
			handler = null;
		}

		try(final InputStream is = resource.openStream(); final InputStreamReader isr = new InputStreamReader(is, encoding); ) {
			result = (Library) unmarshaller.unmarshal(isr);
			if (useSchemaValidation && handler != null && !handler.isValid()) {
				throw new CustomXmlException(resource, handler);
			}
		}


		//
		assert result != null;
		//
		return result;
	}

	/**
	 * Marshal the library information as either XML or JSON via the given writer
	 * 
	 * @param writer the writer
	 * @param mediaType one of
	 * <ul>
	 * <li>application/xml
	 * <li>application/json
	 * </ul>
	 * @param encoding the encoding, e.g. {@link StandardCharsets#UTF_8 UTF-8}
	 * @throws JAXBException in case of unexpected error
	 */
	public void marshal(final Writer writer, final MediaType mediaType, final Charset encoding) throws JAXBException {
		//
		assert writer != null;
		assert mediaType != null;
		//
		final JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class<?>[] { getClass() }, null);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding.name());
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, mediaType == APPLICATION_XML ? XML_SCHEMA_FILE_NAME : JSON_SCHEMA_FILE_NAME);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.setProperty(JAXBContextProperties.MEDIA_TYPE /* "eclipselink.media-type" */, mediaType);
		marshaller.marshal(this, writer);
	}

	/**
	 * Adjust the given Universally Unique Identifier to be compatible with XML IDs (cf. type="xs:ID")<br>
	 * <br>
	 * To be valid, an XML Identifier must start with an alphabetic character,<br>
	 * therefore the two most significant bits of the UUID are set<br>
	 * in order to have the UUID starting with hexadecimal digit from 0xA to 0xF<br>
	 * 
	 * @param uuid the Universally Unique Identifier
	 * @return the ajusted Universally Unique Identifier
	 */
	static UUID makeNonColonizedNameCompliant(final UUID uuid) {
		//
		assert uuid != null;
		//

		final UUID result;
		long msb = uuid.getMostSignificantBits();
		if((msb  & -0x6000000000000000L) == -0x6000000000000000L /* 0xA000000000000000 1010 */ || (msb  & -0x4000000000000000L) == -0x4000000000000000L /* 0xC000000000000000 1100 */) {
			result = uuid;
		} else {
			msb = msb & 0x0FFFFFFFFFFFFFFFL | MASKS[RANDOM.nextInt(6 /* MASKS.length */)];
			result = new UUID(msb, uuid.getLeastSignificantBits());
		}

		return result;
	}

	// JSON/Jackson Annotations
	@JsonIgnore
	public UUID getUuid() {
		return uuid;
	}

	// JSON/Jackson Annotations
	@JsonProperty
	public List<Book> getBooks() {
		return books;
	}

	// JSON/Jackson Annotations
	@JsonProperty
	public List<Bookshelf> getBookshelfs() {
		return bookshelfs;
	}

	// JSON/Jackson Annotations
	@JsonProperty
	public String getLocation() {
		return location;
	}

	// JSON/Jackson Annotations
	@JsonIgnore
	public Book getBestBook() {
		return bestBook;
	}
}
