package eu.fays.rockbox.jpa4;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;

import eu.fays.rockbox.jpa.UUIDAdapter;

@Entity
@Table(schema = "foret", name="branche")
public class Branche {

	@Column(name = "branche_uuid", columnDefinition = "UUID")
	@Convert(converter = UUIDAdapter.class)
	@Id
	UUID uuid = Generators.timeBasedGenerator(EthernetAddress.fromInterface()).generate();

	// @formatter:off
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "arbre_uuid", referencedColumnName = "arbre_uuid")
	})
	// @formatter:on
	Arbre arbre;

	@Column(name = "epaiseur")
	@Enumerated(EnumType.STRING)
	Epaiseur epaiseur;

	@Column(name = "ix")
	int ix;

	public Branche() {

	}

	public Branche(Arbre arbre, Epaiseur epaiseur) {
		this.arbre = arbre;
		this.epaiseur = epaiseur;
		this.ix = (int) arbre.branches.stream().filter(b -> b.epaiseur == epaiseur).count();
		arbre.branches.add(this);
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Branche)) {
			return false;
		}
		if (!uuid.equals(((Branche) o).uuid)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return epaiseur + ":" + ix;
	}
}
