package eu.fays.rockbox.jpa;

import static jakarta.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.CascadeOnDelete;

@Entity
@Table(schema = "kanban", name = "task")
public class Task {

	@EmbeddedId
	public TaskPK pk;

	@Column
	public int index;
	
	@ManyToOne(cascade = ALL)
	@JoinColumn(name = "story_name", insertable = false, updatable = false)
	public Story story;

	@Column(name = "task_name", insertable = false, updatable = false)
	public String name;

	@Column
	public int progress;

	@Column(name = "description")
	public String description;

	@OneToMany(mappedBy = "task", cascade = ALL, orphanRemoval = true)
	@OrderColumn(name="index")
	@CascadeOnDelete
	public List<Subtask> subtasks = new ArrayList<>();

	public Task() {
	}

	public Task(Story story, String name) {
		this.story = story;
		this.name = name;
		pk = new TaskPK(story.name, name);
		this.story.tasks.add(this);
	}

	@Override
	public int hashCode() {
		return story.name.hashCode() * 31 + name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Task)) {
			return false;
		}

		if (name.equals(((Task) o).name)) {
			if (story.name.equals(((Task) o).story.name)) {
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
