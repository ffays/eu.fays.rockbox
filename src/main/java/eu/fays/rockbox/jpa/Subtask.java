package eu.fays.rockbox.jpa;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(schema = "kanban", name = "subtask")
public class Subtask {

	@EmbeddedId
	public SubtaskPK pk;

	// @formatter:off
	@ManyToOne
	@JoinColumns({ 
		@JoinColumn(name = "scenario_name", referencedColumnName = "scenario_name", insertable = false, updatable = false),
		@JoinColumn(name = "task_name", referencedColumnName = "task_name", insertable = false, updatable = false)
	})
	public Task task;
	// @formatter:on

	@Column(name = "subtask_name", insertable = false, updatable = false)
	public String name;

	@Column(name = "description")
	public String description;

	public Subtask() {

	}

	public Subtask(Task task, String name) {
		this.task = task;
		this.name = name;

		pk = new SubtaskPK(task.scenario.name, task.name, name);
		this.task.subtasks.add(this);
	}

	@Override
	public int hashCode() {
		return task.scenario.name.hashCode() * 31 + task.name.hashCode() * 7 + name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Subtask)) {
			return false;
		}

		if (name.equals(((Subtask) o).name)) {
			if (task.name.equals(((Subtask) o).task.name)) {
				if (task.scenario.name.equals(((Subtask) o).task.scenario.name)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return name;
	}
}
