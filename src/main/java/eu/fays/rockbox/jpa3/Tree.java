package eu.fays.rockbox.jpa3;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(schema = "forest")
@IdClass(TreeId.class)
public class Tree {

	// @formatter:off
	@ManyToOne
	@JoinColumns({ 
		@JoinColumn(name = "forest_name", referencedColumnName = "forest_name")
	})
	@Id
	// @formatter:on
	Forest forest;

	@Id
	@Column(name = "tree_name")
	String name;

	@Column
	int index;

	@Column
	@Enumerated(EnumType.STRING)
	Tickness tickness;

	@OneToMany(mappedBy = "tree", cascade = ALL, orphanRemoval = true)
	List<Branch> branches = new ArrayList<>();

	public Tree() {
	}

	public Tree(Forest forest, String name, Tickness tickness) {
		this.forest = forest;
		this.name = name;
		this.index = forest.trees.size();
		forest.trees.add(this);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + forest.hashCode() * 3;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Tree)) {
			return false;
		}
		if (!name.equals(((Tree) o).name)) {
			return false;
		}
		if (!forest.equals(((Tree) o).forest)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return name + ":" + branches;
	}
}
