package eu.fays.rockbox.jpa;

import static javax.persistence.CascadeType.ALL;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(schema = "kanban", name = "scenario")
public class Scenario {

	@Id
	@Column(name = "scenario_name")
	public String name;

	@Column(name = "description")
	public String description;

	@Column
	@Enumerated(EnumType.STRING)
	public Board board = Board.BACKLOG;

	@Column
	public boolean archived = false;

	@Column
	public LocalDateTime created = LocalDateTime.now();

	@OneToMany(mappedBy = "scenario", cascade = ALL, orphanRemoval = true)
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
