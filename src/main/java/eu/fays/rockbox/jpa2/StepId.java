package eu.fays.rockbox.jpa2;

import jakarta.persistence.Embeddable;

@Embeddable
public class StepId {
	public String job_name;
	public String step_name;

	public StepId() {};
	public StepId(Step t) { job_name = t.job.job_name; step_name = t.step_name; }
	@Override public int hashCode() { return job_name.hashCode() * 7 + step_name.hashCode(); }
	@Override public boolean equals(Object o) {return o != null && o instanceof StepId && job_name.equals(((StepId) o).job_name) && step_name.equals(((StepId) o).step_name);}
}
