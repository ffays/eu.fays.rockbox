package eu.fays.rockbox.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class MappingEssay {
	public static void main(String[] args) throws Exception {
		final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("h2pu");
		final EntityManager em = entityManagerFactory.createEntityManager();

		// Delete all scenarios
		{
//			final Query query = em.createQuery("DELETE FROM kanban.scenario");
//			em.getTransaction().begin();
//			query.executeUpdate();
//			em.getTransaction().commit();
		}

		// Create scenario1
		{
			final Scenario scenario1 = new Scenario("scenario1");
			final Task task1 = new Task(scenario1, "task1");
			final Task task2 = new Task(scenario1, "task2");
			new Subtask(task1, "subtask1.1");
			new Subtask(task1, "subtask1.2");
			new Subtask(task2, "subtask2.1");
			new Subtask(task2, "subtask2.2");
			new Subtask(task2, "subtask2.3");
			new Subtask(task2, "subtask2.4");
			System.out.println(scenario1);

			em.getTransaction().begin();
			em.persist(scenario1);
			em.getTransaction().commit();
		}

		// Reload and Update scenario1
		{
			final Scenario scenario1 = em.find(Scenario.class, "scenario1");
			System.out.println(scenario1);

			scenario1.tasks.get(0).subtasks.remove(0);
			scenario1.tasks.get(0).progress = (int) (Math.random() * 100d);
			scenario1.tasks.get(1).progress = (int) (Math.random() * 100d);
			em.getTransaction().begin();
			em.refresh(scenario1);
			em.getTransaction().commit();
		}
		// Reload scenario1

		{
			final Scenario scenario1 = em.find(Scenario.class, "scenario1");
			System.out.println(scenario1);
		}

		em.close();
	}
}
