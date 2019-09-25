package eu.fays.rockbox.jpa2;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.CascadeOnDelete;

@Entity
public class Job {

	@Id 
	public String job_name;
	
	@Column 
	public String description;

	@OneToMany(mappedBy = "job", cascade = {ALL}, orphanRemoval = true)
	@CascadeOnDelete
	public List<Step> steps = new ArrayList<>();

	public Job() {}
	public Job(String n) { job_name = n; }
	@Override public int hashCode() { return job_name.hashCode(); }
	@Override public boolean equals(Object o) { return o != null && o instanceof Job && job_name.equals(((Job) o).job_name); }
	@Override public String toString() { return job_name + ":" + steps; }
}
