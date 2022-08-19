package eu.fays.rockbox.jpa3;

import static jakarta.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(schema = "forest")
public class Forest {

	@Column(name = "forest_name")
	@Id
	String name;

	@OneToMany(mappedBy = "forest", cascade = ALL, orphanRemoval = true)
	List<Tree> trees = new ArrayList<>();

	public Forest() {

	}

	public Forest(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Forest)) {
			return false;
		}
		if (!name.equals(((Forest) o).name)) {
			return false;

		}
		return true;
	}
	
	@Override
	public String toString() {
		return name + ":" + trees;
	}
}
