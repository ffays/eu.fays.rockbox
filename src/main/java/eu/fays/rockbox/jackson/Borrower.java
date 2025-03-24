package eu.fays.rockbox.jackson;

import static eu.fays.rockbox.jackson.Library.makeNonColonizedNameCompliant;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlType;

//XML/JAXB Annotations
@XmlType
@XmlAccessorType(XmlAccessType.NONE)
//JSON/Jackson Annotations
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//JPA annotations
@Entity
@Table(schema = "librarian", name = "borrower")
public class Borrower {
	/** Universally Unique Identifier compatible with XML IDs (cf. type="xs:ID") */
	// JPA annotations
	@Column(name = "uuid", columnDefinition = "UUID")
	@Convert(converter = UniversallyUniqueIdentifierAdapter.class)
	@Id
	UUID uuid = makeNonColonizedNameCompliant(UUID.randomUUID());
	
	/** The identifier of the book, derived from its Universally Unique Identifier */
	// XML/JAXB Annotations
	@XmlID
	@XmlAttribute
	// JPA annotations
	@Transient
	String id;

	/** Username */
	// XML/JAXB Annotations
	@XmlElement
	// JPA annotations
	@Column(name = "username", length = 200)
	String username;
	
	
	public Borrower() {
		
	}
	
	public Borrower(final UUID i, final String un) {
		uuid = i;
		id = i.toString();
		username = un;
	}

	// JSON/Jackson Annotations
	@JsonIgnore
	public UUID getUuid() {
		return uuid;
	}
	
	// JSON/Jackson Annotations
	@JsonProperty
	public String getId() {
		return id;
	}


	// JSON/Jackson Annotations
	@JsonProperty
	public String getUsername() {
		return username;
	}
}
