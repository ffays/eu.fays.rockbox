package eu.fays.rockbox.jackson;

import static jakarta.persistence.CascadeType.ALL;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

/**
 * A book
 */

// XML/JAXB Annotations
@XmlType
@XmlAccessorType(XmlAccessType.NONE)
// JSON/Jackson Annotations
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//JPA annotations
@Entity
@Table(schema = "librarian", name = "book")
public class Book {

	/** Universally Unique Identifier compatible with XML IDs (cf. type="xs:ID") */
	// JPA annotations
	@Column(name = "uuid", columnDefinition = "UUID")
	@Convert(converter = UniversallyUniqueIdentifierAdapter.class)
	@Id
	private UUID uuid;

	/** Library indexing this book */
	// XML/JAXB Annotations
	@XmlTransient
	// JPA annotations
	@ManyToOne(cascade = ALL)
	@JoinColumns({
		@JoinColumn(name = "library_uuid", referencedColumnName = "uuid")
	})
	private Library library;

	/** The identifier of the book, derived from its Universally Unique Identifier */
	// XML/JAXB Annotations
	@XmlID
	@XmlAttribute
	// JPA annotations
	@Transient
	private String id;

	/** Title of the book */
	// XML/JAXB Annotations
	@XmlElement
	// JPA annotations
	@Column(name = "title", length = 200)
	String title;

	/** Author of the book */
	// XML/JAXB Annotations
	@XmlElement
	// JPA annotations
	@Column(name = "author", length = 200)
	String author;

	/** First publication date of the book */
	// XML/JAXB Annotations
	@XmlElement
	// JPA annotations
	@Column(name = "publication_date", columnDefinition = "DATE")
	LocalDate publicationDate;

	// XML/JAXB Annotations
	@XmlElement
	// JPA annotations
	@JoinColumn(name = "previous_edition")
	@OneToOne
	Book previousEdition;
	
	// XML/JAXB Annotations
	@XmlElement
	// JPA annotations
	@JoinColumn(name = "borrower_uuid")
	@ManyToOne
	Borrower borrower;
	
	/**
	 * Constructor
	 */
	public Book() {

	}

	/**
	 * Constructor
	 * 
	 * @param i the uuid
	 * @param t title of the book
	 * @param a author of the book
	 * @param d first publication date of the book
	 */
	public Book(final UUID i, final String t, final String a, final LocalDate d) {
		uuid = i;
		id = i.toString();
		title = t;
		author = a;
		publicationDate = d;
	}

	// JSON/Jackson Annotations
	@JsonIgnore
	public UUID getUuid() {
		return uuid;
	}

	// JSON/Jackson Annotations
	@JsonIgnore
	public Library getLibrary() {
		return library;
	}

	// JSON/Jackson Annotations
	@JsonProperty
	public String getId() {
		return id;
	}

	// JSON/Jackson Annotations
	@JsonProperty
	public String getTitle() {
		return title;
	}

	// JSON/Jackson Annotations
	@JsonProperty
	public String getAuthor() {
		return author;
	}

	// JSON/Jackson Annotations
	@JsonProperty
	public LocalDate getPublicationDate() {
		return publicationDate;
	}

	// JSON/Jackson Annotations
	@JsonProperty
	@JsonManagedReference
	public Book getPreviousEdition() {
		return previousEdition;
	}

	// JSON/Jackson Annotations
	@JsonProperty
	@JsonManagedReference
	public Borrower getBorrower() {
		return borrower;
	}
}
