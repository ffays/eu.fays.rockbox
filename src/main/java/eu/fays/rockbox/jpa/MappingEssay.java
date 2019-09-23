package eu.fays.rockbox.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class MappingEssay {
	public static void main(String[] args) throws Exception {
		final Scenario scenario1 = new Scenario("scenario1");
		final Task task1 = new Task(scenario1, "task1");
		final Task task2 = new Task(scenario1, "task2");
//		new Subtask(task1, "subtask1.1");
//		new Subtask(task1, "subtask1.2");
//		new Subtask(task2, "subtask2.1");
//		new Subtask(task2, "subtask2.2");
//		new Subtask(task2, "subtask2.3");
		
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("h2pu");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(scenario1);
        em.getTransaction().commit();
        em.close();

	}
}
