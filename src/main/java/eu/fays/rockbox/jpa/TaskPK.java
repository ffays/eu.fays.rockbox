package eu.fays.rockbox.jpa;

import javax.persistence.Embeddable;

@Embeddable
public class TaskPK {

	public String story_name;

	public String task_name;

	@Override
	public int hashCode() {
		return story_name.hashCode() * 31 + task_name.hashCode() * 7;

	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof TaskPK)) {
			return false;
		}

		return story_name.equals(((TaskPK) o).story_name) && task_name.equals(((TaskPK) o).task_name);
	}

	public TaskPK() {
		
	}
	
	public TaskPK(final String storyName, final String taskName) {
		story_name = storyName;
		task_name = taskName;
	}

}
