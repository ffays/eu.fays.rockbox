package eu.fays.rockbox.jpa;

import javax.persistence.Embeddable;

@Embeddable
public class SubtaskPK {

	public String story_name;

	public String task_name;

	public String subtask_name;

	@Override
	public int hashCode() {
		return story_name.hashCode() * 31 + task_name.hashCode() * 7 + subtask_name.hashCode();

	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof SubtaskPK)) {
			return false;
		}

		return story_name.equals(((SubtaskPK) o).story_name) && task_name.equals(((SubtaskPK) o).task_name) && subtask_name.equals(((SubtaskPK) o).subtask_name);
	}

	public SubtaskPK() {

	}

	public SubtaskPK(final String storyName, final String taskName, final String subtaskName) {
		story_name = storyName;
		task_name = taskName;
		subtask_name = subtaskName;
	}

}
