package eu.fays.rockbox.jpa;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity(name = "task")
public class Task {

	@ManyToOne
	@JoinColumn(name = "scenario_name")
	public Scenario scenario;

	@Id
	@Column(name = "task_name")
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

		return scenario.name.equals(((Task) o).scenario.name) && name.equals(((Task) o).name);
	}

}
