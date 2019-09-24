package eu.fays.rockbox.jpa;

import javax.persistence.Embeddable;

@Embeddable
public class TaskPK {

	public String scenario_name;

	public String task_name;

	@Override
	public int hashCode() {
		return scenario_name.hashCode() * 31 + task_name.hashCode() * 7;

	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof TaskPK)) {
			return false;
		}

		return scenario_name.equals(((TaskPK) o).scenario_name) && task_name.equals(((TaskPK) o).task_name);
	}

	public TaskPK() {
		
	}
	
	public TaskPK(final String scenarioName, final String taskName) {
		scenario_name = scenarioName;
		task_name = taskName;
	}

}
