package eu.fays.rockbox.jpa;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.CascadeOnDelete;

@Entity
@Table(schema = "kanban", name = "task")
public class Task {

	@EmbeddedId
	public TaskPK pk;

	@ManyToOne(cascade = ALL)
	@JoinColumn(name = "scenario_name", insertable = false, updatable = false)
	public Scenario scenario;

	@Column(name = "task_name", insertable = false, updatable = false)
	public String name;

	@Column
	public int progress;

	@Column(name = "description")
	public String description;

	@OneToMany(mappedBy = "task", cascade = ALL, orphanRemoval = true)
	@CascadeOnDelete
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
		return name + " \u21d2 " + subtasks.toString();
	}

}
