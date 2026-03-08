package com.example.service;

import com.example.model.Auteur;
import com.example.model.Categorie;
import com.example.model.Livre;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class DataInitService {

    private final EntityManagerFactory emf;

    public DataInitService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void initData() {

        EntityManager em = emf.createEntityManager();

        try {

            em.getTransaction().begin();

            // Création des catégories
            Categorie informatique = new Categorie("Informatique", "Livres sur la programmation et les technologies");
            Categorie histoire = new Categorie("Histoire", "Livres sur les événements historiques");
            Categorie aventure = new Categorie("Aventure", "Histoires pleines d'action et d'exploration");
            Categorie science = new Categorie("Science", "Ouvrages scientifiques et éducatifs");
            Categorie developpement = new Categorie("Développement personnel", "Livres pour améliorer ses compétences");

            em.persist(informatique);
            em.persist(histoire);
            em.persist(aventure);
            em.persist(science);
            em.persist(developpement);

            // Auteur 1
            Auteur auteur1 = new Auteur("Martin", "Paul", "paul.martin@example.com");

            Livre livre1 = new Livre("Introduction à Java", 2015, "9781000000001");
            livre1.setResume("Un guide complet pour apprendre la programmation Java.");
            livre1.addCategorie(informatique);

            Livre livre2 = new Livre("Structures de données", 2018, "9781000000002");
            livre2.setResume("Présentation des structures de données utilisées en informatique.");
            livre2.addCategorie(informatique);

            auteur1.addLivre(livre1);
            auteur1.addLivre(livre2);

            // Auteur 2
            Auteur auteur2 = new Auteur("Durand", "Claire", "claire.durand@example.com");

            Livre livre3 = new Livre("Les grandes civilisations", 2005, "9781000000003");
            livre3.setResume("Une exploration des grandes civilisations de l'histoire.");
            livre3.addCategorie(histoire);

            Livre livre4 = new Livre("Secrets du passé", 2010, "9781000000004");
            livre4.setResume("Découverte des événements marquants de l'histoire mondiale.");
            livre4.addCategorie(histoire);

            auteur2.addLivre(livre3);
            auteur2.addLivre(livre4);

            // Auteur 3
            Auteur auteur3 = new Auteur("Bernard", "Lucas", "lucas.bernard@example.com");

            Livre livre5 = new Livre("Voyage au cœur de la jungle", 2012, "9781000000005");
            livre5.setResume("Une aventure captivante dans une jungle mystérieuse.");
            livre5.addCategorie(aventure);

            Livre livre6 = new Livre("Expédition perdue", 2016, "9781000000006");
            livre6.setResume("Une équipe d'explorateurs part à la recherche d'un trésor oublié.");
            livre6.addCategorie(aventure);

            auteur3.addLivre(livre5);
            auteur3.addLivre(livre6);

            // Auteur 4 (plusieurs livres pour tester N+1)
            Auteur auteur4 = new Auteur("Petit", "Sophie", "sophie.petit@example.com");

            for (int i = 1; i <= 20; i++) {

                Livre livre = new Livre("Découverte scientifique " + i, 2000 + i, "978200000000" + i);
                livre.setResume("Présentation d'une découverte scientifique importante.");
                livre.addCategorie(science);

                auteur4.addLivre(livre);
            }

            em.persist(auteur1);
            em.persist(auteur2);
            em.persist(auteur3);
            em.persist(auteur4);

            em.getTransaction().commit();

            System.out.println("Initialisation des nouvelles données terminée !");

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {

            em.close();

        }
    }

    public List<Auteur> getAuteurs() {

        EntityManager em = emf.createEntityManager();

        try {

            return em.createQuery(
                    "SELECT a FROM Auteur a",
                    Auteur.class
            ).getResultList();

        } finally {

            em.close();

        }
    }

    public List<Categorie> getCategories() {

        EntityManager em = emf.createEntityManager();

        try {

            return em.createQuery(
                    "SELECT c FROM Categorie c",
                    Categorie.class
            ).getResultList();

        } finally {

            em.close();

        }
    }
}