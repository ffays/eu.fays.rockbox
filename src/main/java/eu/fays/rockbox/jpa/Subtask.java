package eu.fays.rockbox.jpa;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(schema = "kanban", name = "subtask")
public class Subtask {

	@EmbeddedId
	public SubtaskPK pk;

	@Column
	public int index;

	// @formatter:off
	@ManyToOne
	@JoinColumns({ 
		@JoinColumn(name = "story_name", referencedColumnName = "story_name", insertable = false, updatable = false),
		@JoinColumn(name = "task_name", referencedColumnName = "task_name", insertable = false, updatable = false)
	})
	public Task task;
	// @formatter:on

	@Column(name = "subtask_name", insertable = false, updatable = false)
	public String name;

	@Column(name = "description")
	public String description;
	
	@Convert(converter = ListOfStringAdapter.class)
	@Column(name = "todos")
	public List<String> todos = new ArrayList<>();

	public Subtask() {

	}

	public Subtask(Task task, String name) {
		this.task = task;
		this.name = name;

		pk = new SubtaskPK(task.story.name, task.name, name);
		this.task.subtasks.add(this);
	}

	@Override
	public int hashCode() {
		return task.story.name.hashCode() * 31 + task.name.hashCode() * 7 + name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Subtask)) {
			return false;
		}

		if (name.equals(((Subtask) o).name)) {
			if (task.name.equals(((Subtask) o).task.name)) {
				if (task.story.name.equals(((Subtask) o).task.story.name)) {
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
