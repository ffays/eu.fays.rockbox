package eu.fays.rockbox.jpa3;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(schema = "forest")
@IdClass(BranchId.class)
public class Branch {

	// @formatter:off
	@ManyToOne
	@JoinColumns({ 
		@JoinColumn(name = "forest_name", referencedColumnName = "forest_name"),
		@JoinColumn(name = "tree_name", referencedColumnName = "tree_name")
	})
	@Id
	// @formatter:on
	Tree tree;
	@Id
	@Enumerated(EnumType.STRING)
	Tickness tickness;
	@Id
	int index;

	public Branch() {

	}

	public Branch(Tree tree, Tickness tickness) {
		this.tree = tree;
		this.tickness = tickness;
		this.index = (int) tree.branches.stream().filter(b -> b.tickness == tickness).count();
		tree.branches.add(this);
	}

	@Override
	public int hashCode() {
		return index + tickness.hashCode() * 3 + tree.hashCode() * 7;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Branch)) {
			return false;
		}
		if (index != ((Branch) o).index) {
			return false;
		}
		if (tickness != ((Branch) o).tickness) {
			return false;
		}
		if (!tree.equals(((Branch) o).tree)) {
			return false;
		}

		return true;
	}
	
	@Override
	public String toString() {
		return tickness + ":" + index;
	}
}
