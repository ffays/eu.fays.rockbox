package eu.fays.rockbox.jpa2;

import static javax.persistence.Persistence.createEntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class MappingEssay2 {
	public static void main(String[] args) throws Exception {
		final EntityManagerFactory emf = createEntityManagerFactory("h2pu");
		final EntityManager em = emf.createEntityManager();

		// Create Job
		Job j1 = new Job("j1");
		new Step(j1, "s1"); 
		new Step(j1, "s2"); 
		em.getTransaction().begin();
		em.persist(j1);
		em.getTransaction().commit();
		
		// Reload Job
		j1 = em.find(Job.class, "j1");
		System.out.println(j1);
		em.close();
	}
}
