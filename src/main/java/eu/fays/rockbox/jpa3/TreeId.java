package eu.fays.rockbox.jpa3;

public class TreeId {

	String forest;

	String name;

	public TreeId() {
	}

	public TreeId(String forest, String name) {
		this.forest = forest;
		this.name = name;
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
		if (!(o instanceof TreeId)) {
			return false;
		}
		if (!name.equals(((TreeId) o).name)) {
			return false;
		}
		if (!forest.equals(((TreeId) o).forest)) {
			return false;
		}

		return true;
	}
}
