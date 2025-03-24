package eu.fays.rockbox.jackson;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static eu.fays.rockbox.jackson.Library.makeNonColonizedNameCompliant;
import static java.lang.System.lineSeparator;
import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.randomUUID;
import static org.eclipse.persistence.config.PersistenceUnitProperties.APP_LOCATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CLASSLOADER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CREATE_JDBC_DDL_FILE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DROP_JDBC_DDL_FILE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_DRIVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_PASSWORD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_URL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_USER;
import static org.eclipse.persistence.oxm.MediaType.APPLICATION_JSON;
import static org.eclipse.persistence.oxm.MediaType.APPLICATION_XML;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Proof of concept for both XML and JSON, both marshaling and unmarshaling
 */
@SuppressWarnings("nls")
public class Librarian {

	private static final UUID libraryId = makeNonColonizedNameCompliant(randomUUID());
	private static final UUID bookId1 = makeNonColonizedNameCompliant(randomUUID());
	private static final UUID bookId2 = makeNonColonizedNameCompliant(randomUUID());
	private static final UUID bookId3 = makeNonColonizedNameCompliant(randomUUID());
	private static final UUID bookId4 = makeNonColonizedNameCompliant(randomUUID());
	private static final UUID bookId5 = makeNonColonizedNameCompliant(randomUUID());
	private static final UUID bookId6 = makeNonColonizedNameCompliant(randomUUID());
	private static final UUID bookshelfId1 = makeNonColonizedNameCompliant(randomUUID());
	private static final UUID bookshelfId2 = makeNonColonizedNameCompliant(randomUUID());
	private static final UUID bookshelfId3 = makeNonColonizedNameCompliant(randomUUID());
	
	private static final Book book1 = new Book(bookId1, "A la recherche du temps perdu", "Proust", LocalDate.parse("1913-01-01"));
	private static final Book book2 = new Book(bookId2, "Effective Java 2nd edition", "Joshua Bloch", LocalDate.parse("2008-05-28"));
	private static final Book book3 = new Book(bookId3, "Effective Java 3rd edition", "Joshua Bloch", LocalDate.parse("2017-12-27"));
	private static final Book book4 = new Book(bookId4, "Java Concurrency in Practice", "Brian Goetz", LocalDate.parse("2006-05-09"));
	private static final Book book5 = new Book(bookId5, "UML Distilled 3rd edition", "Martin Fowler", LocalDate.parse("2003-09-15"));
	private static final Book book6 = new Book(bookId6, "Le rouge et le noir", "Stendhal", LocalDate.parse("1830-11-13"));

	/**
	 * Proof of concept for both XML and JSON, both marshaling and unmarshaling<br>
	 * <br>
	 * JVM Args:<br>
	 * 
	 * <pre>
	 * -Djavax.xml.bind.context.factory=org.eclipse.persistence.jaxb.JAXBContextFactory
	 * </pre>
	 * 
	 * @param args unused
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		book3.previousEdition = book2;
		try (final OutputStreamWriter writer = new OutputStreamWriter(out)) {
			{
				// Building the library
				final Library library = buildLibrary();
				// JSON via EclipseLink MOXY
				{
					out.print(cartouche("JSON via EclipseLink MOXY"));
					library.marshal(writer, APPLICATION_JSON, UTF_8);
					out.println();
				}
				
				// JSON via Jackson
				{
					final ObjectMapper mapper = newObjectMapper();
					out.print(cartouche("JSON via Jackson"));
					final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(library);
					out.println(json);
				}

				// JSON Schema via EclipseLink MOXY
				{
					out.print(cartouche("JSON Schema via EclipseLink MOXY"));
					final JAXBContext jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class<?>[] { Library.class }, null);
					org.eclipse.persistence.internal.jaxb.json.schema.JsonSchemaGenerator jsonSchemaGenerator = new org.eclipse.persistence.internal.jaxb.json.schema.JsonSchemaGenerator(
							jaxbContext, new HashMap<>());
					final org.eclipse.persistence.internal.jaxb.json.schema.model.JsonSchema jsonSchema = jsonSchemaGenerator.generateSchema(Library.class);
					exportToJSON(writer, jsonSchema);
					out.println();
				}
			
				// JSON Schema via Jackson
				{
					final ObjectMapper mapper = newObjectMapper();
					out.print(cartouche("JSON Schema via Jackson"));
					final JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(mapper);
					final JsonSchema jsonSchema = schemaGenerator.generateSchema(Library.class);
					final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
					out.println(json);
				}

				// XML
				{
					out.print(cartouche("XML"));
					library.marshal(writer, APPLICATION_XML, UTF_8);
					System.out.println();
				}
			}

			// XML unmarshaling
			{
				out.print(cartouche("XML unmarshaling + marshaling cycle"));
				final URL resource = Librarian.class.getResource("Library.xml");
				final Library library = Library.unmarshal(resource, APPLICATION_XML, UTF_8, true);
				library.marshal(writer, APPLICATION_XML, UTF_8);
				System.out.println();
			}
			
			// JSON unmarshaling
			{
				out.print(cartouche("JSON unmarshaling + marshaling cycle"));
				final URL resource = Librarian.class.getResource("Library.json");
				final Library library = Library.unmarshal(resource, APPLICATION_JSON, UTF_8, false);
				library.marshal(writer, APPLICATION_JSON, UTF_8);
				System.out.println();
			}
			
			// JPA DDL
			{
				out.print(cartouche("META-INF/persistence.xml"));
				final ClassLoader classLoader = Librarian.class.getClassLoader();
				
				try(final InputStream is = classLoader.getResourceAsStream("META-INF/persistence.xml")) {
					int c;
					while((c = is.read()) != -1) {
						writer.write(c);
					}
				}
				writer.flush();
				out.print(cartouche("JPA Data Definition Language"));
				// Needed to find the persistence.xml file in this bundle
				final Map<String, Object> properties = new HashMap<String, Object>();

				properties.put(CLASSLOADER, Librarian.class.getClassLoader());
				properties.put(JDBC_URL, "jdbc:h2:mem:librarian;INIT=CREATE SCHEMA IF NOT EXISTS librarian\\;CREATE SCHEMA IF NOT EXISTS kanban\\;CREATE SCHEMA IF NOT EXISTS forest\\;CREATE SCHEMA IF NOT EXISTS foret");
				properties.put(JDBC_USER, "sa");
				properties.put(JDBC_PASSWORD, "sa");
				properties.put(JDBC_DRIVER, "org.h2.Driver");
				properties.put(DDL_GENERATION, "drop-and-create-tables");
				properties.put(DDL_GENERATION_MODE, "both");
				final String cwd = Paths.get("").toAbsolutePath().toString();
				properties.put(APP_LOCATION, cwd);
				properties.put(CREATE_JDBC_DDL_FILE, "librarian_create_tables.sql");
				properties.put(DROP_JDBC_DDL_FILE, "librarian_drop_tables.sql");
				properties.put("h2.returnOffsetDateTime", "true");
				
				final PersistenceProvider persistenceProvider = new PersistenceProvider();
				final EntityManagerFactory entityManagerFactory = persistenceProvider.createEntityManagerFactory("librarian-pesistence-unit" /* cf. META-INF/persistence.xml */, properties);
				final EntityManager entityManager = entityManagerFactory.createEntityManager(properties);

				//
				// Store the Case into the Database
				//
				{
					final Library library = buildLibrary();
					library.setBestBook(book3);	
					entityManager.getTransaction().begin();
					entityManager.persist(library);
					entityManager.getTransaction().commit();
					entityManager.getEntityManagerFactory().getCache().evictAll();
				}
				
				// JPA unmarshaling
				{
					final Library library = entityManager.find(Library.class, libraryId);
					if(library != null) {
//						entityManager.getTransaction().begin();
//						entityManager.remove(library);
//						entityManager.getTransaction().commit();
					}
				}
				
				entityManager.close();
			}

		}
	}

	private static Library buildLibrary() {
		final Library library = new Library(libraryId, "The great library of Alexandria");
		
		// Indexing the books
		library.books.add(book1);
		library.books.add(book2);
		library.books.add(book3);
		library.books.add(book4);
		library.books.add(book5);
		library.books.add(book6);

		// Moving the empty bookshelves inside the library
		final Bookshelf bookshelf1 = new Bookshelf(bookshelfId1, "100 French literature");
		final Bookshelf bookshelf2 = new Bookshelf(bookshelfId2, "200 Computer science");
		final Bookshelf bookshelf3 = new Bookshelf(bookshelfId3, "999 Duplicates");
		library.bookshelfs.add(bookshelf1);
		library.bookshelfs.add(bookshelf2);
		library.bookshelfs.add(bookshelf3);
		// Putting books in the bookshelfs
		bookshelf1.books.add(book1);
		bookshelf1.books.add(book6);
		bookshelf2.books.add(book2);
		bookshelf2.books.add(book3);
		bookshelf2.books.add(book4);
		bookshelf2.books.add(book5);
		bookshelf3.books.add(book1);
		bookshelf3.books.add(book5);
	
		return library;
	}

	/**
	 * Provides a new {@link ObjectMapper}, configured to handle ISO 8601 timestamps.
	 * 
	 * @return the object mapper
	 */
	public static ObjectMapper newObjectMapper() {
		final ObjectMapper result = new ObjectMapper();
		result.registerModule(new JavaTimeModule());
		result.configure(WRITE_DATES_AS_TIMESTAMPS, false);
		return result;
	}

	/**
	 * Converts to JavaScript Object Notation this instance
	 * 
	 * @param o the object to be converted
	 * @return the JSON
	 * @throws JsonProcessingException in case of unexpected error
	 */
	static String toJson(final Object o) throws JsonProcessingException {
		final ObjectMapper mapper = newObjectMapper();
		final String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
		return result;
	}

	/**
	 * Export the given object information as JSON via the given writer
	 * 
	 * @param writer the writer
	 * @param object the object to be marshaled to JSON
	 * @throws JAXBException in case of unexpected error
	 */
	static void exportToJSON(final Writer writer, final Object object) throws JAXBException {
		final JAXBContext jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class<?>[] { object.getClass() }, null);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, UTF_8.name());
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(JAXBContextProperties.MEDIA_TYPE /* "eclipselink.media-type" */, APPLICATION_JSON);
//		marshaller.setProperty(JAXBContextProperties.JSON_INCLUDE_ROOT /* "eclipselink.media-type" */, false);
		marshaller.marshal(object, writer);
	}

	/**
	 * Builds a header
	 * 
	 * @param title the title
	 * @return the header with the title
	 */
	static String cartouche(final String title) {
		final int l = 72;
		final String hr = IntStream.rangeClosed(0, l).mapToObj(i -> "-").collect(Collectors.joining());
		final StringBuilder builder = new StringBuilder();
		builder.append(hr);
		builder.append(lineSeparator());
		builder.append("-- ");
		builder.append(title);
		IntStream.range(0, l - 5 - title.length()).forEach(i -> builder.append(' '));
		builder.append(" --");
		builder.append(lineSeparator());
		builder.append(hr);
		builder.append(lineSeparator());
		return builder.toString();
	}
}
