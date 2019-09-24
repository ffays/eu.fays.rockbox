package eu.fays.rockbox.jpa;

import static javax.persistence.CascadeType.PERSIST;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name = "scenario")
public class Scenario {

	@Id
	@Column(name = "scenario_name")
	public String name;

	@Column(name = "description")
	public String description;

	@OneToMany(mappedBy = "scenario", cascade = PERSIST)
	public List<Task> tasks = new ArrayList<Task>();

	public Scenario() {

	}

	public Scenario(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Scenario)) {
			return false;
		}

		return name.equals(((Scenario) o).name);
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append('[');
		result.append('\'');
		result.append(name);
		result.append('\'');
		result.append(',');
		result.append('[');
		boolean flag = false;
		for (Task task : tasks) {
			if (flag) {
				result.append(',');
			} else {
				flag = true;
			}
			result.append(task.toString());
		}
		result.append(']');
		result.append(']');
		return result.toString();
	}

}
