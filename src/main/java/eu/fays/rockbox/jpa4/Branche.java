package eu.fays.rockbox.jpa4;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import eu.fays.rockbox.jpa.UUIDAdapter;

@Entity
@Table(schema = "foret")
public class Branche {

	@Column(name = "branche_uuid", columnDefinition = "UUID")
	@Convert(converter = UUIDAdapter.class)
	@Id
	UUID uuid = UUID.randomUUID();

	// @formatter:off
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "arbre_uuid", referencedColumnName = "arbre_uuid")
	})
	// @formatter:on
	Arbre arbre;

	@Enumerated(EnumType.STRING)
	Epaiseur epaiseur;

	int index;

	public Branche() {

	}

	public Branche(Arbre arbre, Epaiseur epaiseur) {
		this.arbre = arbre;
		this.epaiseur = epaiseur;
		this.index = (int) arbre.branches.stream().filter(b -> b.epaiseur == epaiseur).count();
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
		return epaiseur + ":" + index;
	}
}
