package eu.fays.rockbox.jpa4;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;

import eu.fays.rockbox.jpa.UUIDAdapter;

@Entity
@Table(schema = "foret", name="arbre")
public class Arbre {

	@Column(name = "arbre_uuid", columnDefinition = "UUID")
	@Convert(converter = UUIDAdapter.class)
	@Id
	UUID uuid = Generators.timeBasedGenerator(EthernetAddress.fromInterface()).generate();

	// @formatter:off
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "foret_uuid", referencedColumnName = "foret_uuid")
	})
	// @formatter:on
	Foret foret;

	@Column(name = "arbre_name")
	String name;

	@Column(name = "ix")
	int ix;

	@Column(name = "epaiseur")
	@Enumerated(EnumType.STRING)
	Epaiseur epaiseur;

	@OneToMany(mappedBy = "arbre", cascade = ALL, orphanRemoval = true)
	List<Branche> branches = new ArrayList<>();

	public Arbre() {
	}

	public Arbre(Foret foret, String name, Epaiseur epaiseur) {
		this.foret = foret;
		this.name = name;
		this.epaiseur = epaiseur;
		this.ix = foret.arbres.size();
		foret.arbres.add(this);
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
		if (!(o instanceof Arbre)) {
			return false;
		}
		if (!uuid.equals(((Arbre) o).uuid)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return name + ":" + branches;
	}
}
