package eu.fays.rockbox.jpa3;

public class BranchId {

	TreeId tree;
	Tickness tickness;
	int index;

	public BranchId() {

	}

	public BranchId(String forest, TreeId tree, Tickness tickness, int index) {
//		this.forest = forest;
		this.tree = tree;
		this.tickness = tickness;
		this.index = index;
	}

	@Override
	public int hashCode() {
		return index + tickness.hashCode() * 3 + tree.hashCode() * 7 ; //+ forest.hashCode() * 11;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof BranchId)) {
			return false;
		}
		if (index != ((BranchId) o).index) {
			return false;
		}
		if (!tickness.equals(((BranchId) o).tickness)) {
			return false;
		}
		if (!tree.equals(((BranchId) o).tree)) {
			return false;
		}
//		if (!forest.equals(((BranchId) o).forest)) {
//			return false;
//		}

		return true;
	}

}
