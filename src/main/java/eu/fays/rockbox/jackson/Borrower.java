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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
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
	// JSON/Jackson Annotations
	@JsonIgnore
	// JPA annotations
	@Column(name = "uuid", columnDefinition = "UUID")
	@Convert(converter = UniversallyUniqueIdentifierAdapter.class)
	@Id
	final UUID uuid = makeNonColonizedNameCompliant(UUID.randomUUID());
	
	/** Username */
	// XML/JAXB Annotations
	@XmlElement
	// JSON/Jackson Annotations
	@JsonProperty
	// JPA annotations
	@Column(name = "username", length = 200)
	String username;
}
