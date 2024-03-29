package eu.fays.rockbox.jpa4;

import static jakarta.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;

import eu.fays.rockbox.jpa.UUIDAdapter;

@Entity
@Table(schema = "foret", name="foret")
public class Foret {

	@Column(name = "foret_uuid", columnDefinition = "UUID")
	@Convert(converter = UUIDAdapter.class)
	@Id
	UUID uuid = Generators.timeBasedGenerator(EthernetAddress.fromInterface()).generate();

	@Column(name = "foret_name")
	String name;

	@OneToMany(mappedBy = "foret", cascade = ALL, orphanRemoval = true)
	List<Arbre> arbres = new ArrayList<>();

	public Foret() {

	}

	public Foret(String name) {
		this.name = name;
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
		if (!(o instanceof Foret)) {
			return false;
		}
		if (!uuid.equals(((Foret) o).uuid)) {
			return false;

		}
		return true;
	}

	@Override
	public String toString() {
		return name + ":" + arbres;
	}
}
