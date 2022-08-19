package eu.fays.rockbox.jpa4;

import static eu.fays.rockbox.jpa4.Epaiseur.FIN;
import static eu.fays.rockbox.jpa4.Epaiseur.LARGE;
import static eu.fays.rockbox.jpa4.Epaiseur.NORMAL;
import static jakarta.persistence.Persistence.createEntityManagerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

// https://github.com/ancoron/pg-inet-maven/blob/master/org.ancoron.postgresql.jpa/src/main/java/org/ancoron/postgresql/jpa/eclipselink/InetAddressConverter.java
public class MappingEssay4 {

	public static void main(String[] args) {
		final EntityManagerFactory emf = createEntityManagerFactory("h2pu");
		final EntityManager em = emf.createEntityManager();

		Foret foret = new Foret("Soignes");
		Arbre arbre = new Arbre(foret, "Ch\u00eane", LARGE);
		new Arbre(foret, "H\u00eatre", LARGE);
		new Arbre(foret, "Fr\u00eane", NORMAL);
		new Branche(arbre, LARGE);
		new Branche(arbre, LARGE);
		new Branche(arbre, NORMAL);
		new Branche(arbre, NORMAL);
		new Branche(arbre, NORMAL);
		new Branche(arbre, FIN);
		new Branche(arbre, FIN);
		new Branche(arbre, FIN);
		new Branche(arbre, FIN);
		new Branche(arbre, FIN);

		em.getTransaction().begin();
		em.persist(foret);
		em.getTransaction().commit();

		// Reload Job
		foret = em.find(Foret.class, foret.uuid);
		System.out.println(foret);
		em.close();

	}
}
