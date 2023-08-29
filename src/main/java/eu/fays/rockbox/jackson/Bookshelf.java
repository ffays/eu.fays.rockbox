package eu.fays.rockbox.jackson;

import static jakarta.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Bookshelf : a bookshelf containing books in a library
 */

// XML/JAXB Annotations
@XmlType
@XmlRootElement(name = "Bookshelf")
@XmlAccessorType(XmlAccessType.NONE)
// JSON/Jackson Annotations
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
// JPA annotations
@Entity
@Table(schema = "librarian", name = "bookshelf")
public class Bookshelf {

	/** Universally Unique Identifier compatible with XML IDs (cf. type="xs:ID") */
	// JSON/Jackson Annotations
	@JsonIgnore
	// JPA annotations
	@Column(name = "uuid", columnDefinition = "UUID")
	@Convert(converter = UniversallyUniqueIdentifierAdapter.class)
	@Id
	private UUID uuid;

	/** Library having this bookshelf */
	// XML/JAXB Annotations
	@XmlTransient
	// JSON/Jackson Annotations
	@JsonIgnore
	// JPA annotations
	@ManyToOne(cascade = ALL)
	@JoinColumns({
		@JoinColumn(name = "library_uuid", referencedColumnName = "uuid")
	})
	private Library library;

	/** Name of the bookshelf */
	// XML/JAXB Annotations
	@XmlElement
	// JSON/Jackson Annotations
	@JsonProperty
	// JPA annotations
	@Column(name = "name", length = 200)
	String name;

	/** Books contained in the bookshelf */
	// XML/JAXB Annotations
	// cf. https://stackoverflow.com/questions/10150263/jaxb-annotations-how-do-i-make-a-list-of-xmlidref-elements-have-the-id-value-a
	@XmlIDREF
	@XmlElement
	@XmlPath("books/book/text()")
	// JSON/Jackson Annotations
	@JsonProperty
	@JsonManagedReference
	// JPA annotations
	@JoinTable(schema = "librarian", name = "bookshelf_book", joinColumns = @JoinColumn(name = "bookshelf_uuid"), inverseJoinColumns = @JoinColumn(name = "book_uuid"))
	List<Book> books = new ArrayList<>();

	/**
	 * Constructor
	 */
	public Bookshelf() {

	}

	/**
	 * Constructor
	 * 
	 * @param i the uuid
	 * @param n name of the bookshelf
	 */
	public Bookshelf(final UUID i, final String n) {
		uuid = i;
		name = n;
	}
}
