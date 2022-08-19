package eu.fays.rockbox.jpa3;

import static eu.fays.rockbox.jpa3.Tickness.LARGE;
import static eu.fays.rockbox.jpa3.Tickness.MEDIUM;
import static eu.fays.rockbox.jpa3.Tickness.SMALL;
import static jakarta.persistence.Persistence.createEntityManagerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

public class MappingEssay3 {

	public static void main(String[] args) {
		final EntityManagerFactory emf = createEntityManagerFactory("h2pu");
		final EntityManager em = emf.createEntityManager();
		Query query = em.createNativeQuery("CREATE SCHEMA IF NOT EXISTS forest");
		em.getTransaction().begin();
		query.executeUpdate();
		em.getTransaction().commit();

		Forest forest = new Forest("sherwood");
		Tree tree = new Tree(forest, "robin", LARGE);
		new Tree(forest, "tuck", LARGE);
		new Tree(forest, "much", MEDIUM);
		new Branch(tree, LARGE);
		new Branch(tree, LARGE);
		new Branch(tree, MEDIUM);
		new Branch(tree, MEDIUM);
		new Branch(tree, MEDIUM);
		new Branch(tree, SMALL);
		new Branch(tree, SMALL);
		new Branch(tree, SMALL);
		new Branch(tree, SMALL);
		new Branch(tree, SMALL);

		em.getTransaction().begin();
		em.persist(forest);
		em.getTransaction().commit();

		// Reload Job
		forest = em.find(Forest.class, "sherwood");
		System.out.println(forest);
		em.close();

	}
}
