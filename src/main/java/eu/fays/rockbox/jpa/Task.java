package eu.fays.rockbox.jpa;

import static javax.persistence.CascadeType.PERSIST;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity(name = "task")
public class Task {

	@EmbeddedId
	public TaskPK pk;

	@ManyToOne
	@JoinColumn(name = "scenario_name", insertable = false, updatable = false)
	public Scenario scenario;

	@Column(name = "task_name", insertable = false, updatable = false)
	public String name;

	@Column(name = "description")
	public String description;

	@OneToMany(mappedBy = "task", cascade = PERSIST)
	public List<Subtask> subtasks = new ArrayList<>();

	public Task() {

	}

	public Task(Scenario scenario, String name) {
		this.scenario = scenario;
		this.name = name;
		pk = new TaskPK(scenario.name, name);
		this.scenario.tasks.add(this);
	}

	@Override
	public int hashCode() {
		return scenario.name.hashCode() * 31 + name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Task)) {
			return false;
		}

		if (name.equals(((Task) o).name)) {
			if (scenario.name.equals(((Task) o).scenario.name)) {
				return true;
			}
		}

		return false;
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
		for (Subtask subtask : subtasks) {
			if (flag) {
				result.append(',');
			} else {
				flag = true;
			}
			result.append(subtask.toString());
		}
		result.append(']');
		result.append(']');
		return result.toString();
	}

}
