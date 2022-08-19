package eu.fays.rockbox.jpa;

import static eu.fays.rockbox.jpa.Tag.*;
import static jakarta.persistence.Persistence.createEntityManagerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class MappingEssay {
	public static void main(String[] args) throws Exception {
		final EntityManagerFactory entityManagerFactory = createEntityManagerFactory("h2pu");
		final EntityManager em = entityManagerFactory.createEntityManager();

		// Delete all stories
		{
//			final Query query = em.createQuery("DELETE FROM kanban.story");
//			em.getTransaction().begin();
//			query.executeUpdate();
//			em.getTransaction().commit();
		}

		// Create story1
		{
			final Story story1 = new Story("story1");
			final Task task1 = new Task(story1, "task1");
			final Task task2 = new Task(story1, "task2");
			new Subtask(task1, "subtask1.1");
			new Subtask(task1, "subtask1.2");
			final Subtask subtask = new Subtask(task2, "subtask2.1");
			new Subtask(task2, "subtask2.2");
			new Subtask(task2, "subtask2.3");
			new Subtask(task2, "subtask2.4");
			System.out.println(story1);
			story1.tags.add(BUG_FIX);
			story1.tags.add(FAST_TRACK);
			story1.tags.add(QUICK_WIN);
			story1.tags2.add(BUG_FIX);
			story1.tags2.add(FAST_TRACK);
			story1.tags2.add(QUICK_WIN);
			subtask.todos.add("a");
			subtask.todos.add("b");
			subtask.todos.add("c");

			em.getTransaction().begin();
			em.persist(story1);
			em.getTransaction().commit();
		}

		// Reload and Update story1
		{
			final Story story1 = em.find(Story.class, "story1");
			System.out.println(story1);

			story1.tasks.get(0).subtasks.remove(0);
			story1.tasks.get(0).progress = (int) (Math.random() * 100d);
			story1.tasks.get(1).progress = (int) (Math.random() * 100d);
			System.out.println(story1);
			em.getTransaction().begin();
			em.flush();
			em.getTransaction().commit();
		}

		// Reload story1
		{
			em.getEntityManagerFactory().getCache().evict(Task.class, new TaskPK("story1", "task1"));
			final Story story1 = em.find(Story.class, "story1");
			System.out.println(story1);
		}

		em.close();
	}
}
