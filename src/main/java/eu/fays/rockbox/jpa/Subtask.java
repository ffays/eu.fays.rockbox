package eu.fays.rockbox.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "subtask")
//@IdClass(SubtaskPK.class)
public class Subtask {

	@ManyToOne
	@JoinColumn(name = "task_name")
	public Task task;

	@Id
	@Column(name = "subtask_name")
	public String name;

	@Column(name = "description")
	public String description;

	public Subtask() {

	}

	public Subtask(Task task, String name) {
		this.task = task;
		this.name = name;
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

		return task.scenario.name.equals(((Subtask) o).task.scenario.name) && task.name.equals(((Subtask) o).task.name) && name.equals(((Task) o).name);
	}
}
