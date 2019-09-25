package eu.fays.rockbox.jpa2;

import static javax.persistence.CascadeType.ALL;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity 
public class Step {

	@EmbeddedId
	public StepId id;

	@ManyToOne(cascade = ALL)
	@JoinColumn(name = "job_name", insertable = false, updatable = false)
	public Job job;

	@Column(insertable = false, updatable = false)
	public String step_name;

	public Step() {}
	public Step(Job j, String n) { job = j; step_name = n; id = new StepId(this); j.steps.add(this); }
	@Override public int hashCode() { return id.hashCode(); }
	@Override public boolean equals(Object o) { return o != null && o instanceof Step && id.equals(((Step)o).id); }
	@Override public String toString() { return step_name; }
}
