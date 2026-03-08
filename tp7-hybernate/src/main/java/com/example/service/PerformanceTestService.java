package com.example.service;

import com.example.model.Auteur;
import com.example.model.Livre;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;

import javax.persistence.*;
import java.util.List;

public class PerformanceTestService {

    private final EntityManagerFactory emf;

    public PerformanceTestService(EntityManagerFactory emf) {
        this.emf = emf;
    }
    public void testPerformanceComparison() {

        System.out.println("\n=== Comparaison des performances avec et sans cache ===");

        // Test sans cache
        EntityManager em = emf.createEntityManager();

        try {

            em.unwrap(org.hibernate.Session.class)
                    .getSessionFactory()
                    .getCache()
                    .evictAllRegions();

            long start = System.currentTimeMillis();

            for (int i = 0; i < 50; i++) {
                Auteur auteur = em.find(Auteur.class, 1L);
                auteur.getLivres().size();
            }

            long end = System.currentTimeMillis();

            System.out.println("Temps sans cache : " + (end - start) + " ms");

        } finally {
            em.close();
        }

        // Test avec cache
        long start = System.currentTimeMillis();

        for (int i = 0; i < 50; i++) {

            EntityManager em2 = emf.createEntityManager();

            try {

                Auteur auteur = em2.find(Auteur.class, 1L);
                auteur.getLivres().size();

            } finally {
                em2.close();
            }
        }

        long end = System.currentTimeMillis();

        System.out.println("Temps avec cache : " + (end - start) + " ms");

        printStatistics("Comparaison cache");
    }

    // Reset des statistiques Hibernate
    public void resetStatistics() {

        EntityManager em = emf.createEntityManager();

        try {
            Session session = em.unwrap(Session.class);
            Statistics stats = session.getSessionFactory().getStatistics();
            stats.clear();
        } finally {
            em.close();
        }
    }

    // Affichage des statistiques
    public void printStatistics(String testName) {

        EntityManager em = emf.createEntityManager();

        try {

            Session session = em.unwrap(Session.class);
            Statistics stats = session.getSessionFactory().getStatistics();

            System.out.println("\n=== Statistiques pour " + testName + " ===");

            System.out.println("Requêtes exécutées: " + stats.getQueryExecutionCount());

            System.out.println("Temps max d'exécution des requêtes: "
                    + stats.getQueryExecutionMaxTime() + " ms");

            System.out.println("Entités chargées: "
                    + stats.getEntityLoadCount());

            System.out.println("Hits du cache de second niveau: "
                    + stats.getSecondLevelCacheHitCount());

            System.out.println("Miss du cache de second niveau: "
                    + stats.getSecondLevelCacheMissCount());

        } finally {
            em.close();
        }
    }

    // ===============================
    // Test 1 : problème N+1
    // ===============================

    public void testN1Problem() {

        resetStatistics();

        long start = System.currentTimeMillis();

        EntityManager em = emf.createEntityManager();

        try {

            List<Auteur> auteurs =
                    em.createQuery("SELECT a FROM Auteur a", Auteur.class)
                            .getResultList();

            for (Auteur auteur : auteurs) {

                System.out.println("Auteur: "
                        + auteur.getNom() + " "
                        + auteur.getPrenom());

                System.out.println("Nombre de livres: "
                        + auteur.getLivres().size());

                for (Livre livre : auteur.getLivres()) {

                    System.out.println("  Livre: "
                            + livre.getTitre());

                    System.out.println("  Catégories: "
                            + livre.getCategories().size());
                }
            }

        } finally {
            em.close();
        }

        long end = System.currentTimeMillis();

        System.out.println("Temps d'exécution: "
                + (end - start) + " ms");

        printStatistics("Problème N+1");
    }

    // ===============================
    // Test 2 : solution JOIN FETCH
    // ===============================

    public void testJoinFetch() {

        resetStatistics();

        long start = System.currentTimeMillis();

        EntityManager em = emf.createEntityManager();

        try {

            List<Auteur> auteurs =
                    em.createQuery(
                            "SELECT DISTINCT a FROM Auteur a LEFT JOIN FETCH a.livres",
                            Auteur.class
                    ).getResultList();

            for (Auteur auteur : auteurs) {

                System.out.println("Auteur: "
                        + auteur.getNom());

                System.out.println("Nombre de livres: "
                        + auteur.getLivres().size());
            }

        } finally {
            em.close();
        }

        long end = System.currentTimeMillis();

        System.out.println("Temps d'exécution: "
                + (end - start) + " ms");

        printStatistics("JOIN FETCH");
    }

    // ===============================
    // Test 3 : EntityGraph
    // ===============================

    public void testEntityGraph() {

        resetStatistics();

        long start = System.currentTimeMillis();

        EntityManager em = emf.createEntityManager();

        try {

            EntityGraph<?> graph =
                    em.getEntityGraph("graph.Livre.categoriesEtAuteur");

            List<Livre> livres =
                    em.createQuery(
                                    "SELECT l FROM Livre l",
                                    Livre.class
                            )
                            .setHint("javax.persistence.fetchgraph", graph)
                            .getResultList();

            for (Livre livre : livres) {

                System.out.println("Livre: "
                        + livre.getTitre());

                System.out.println("Auteur: "
                        + livre.getAuteur().getNom());

                System.out.println("Catégories: "
                        + livre.getCategories().size());
            }

        } finally {
            em.close();
        }

        long end = System.currentTimeMillis();

        System.out.println("Temps d'exécution: "
                + (end - start) + " ms");

        printStatistics("EntityGraph");
    }

    // ===============================
    // Test 4 : cache second niveau
    // ===============================

    public void testSecondLevelCache() {

        System.out.println("\n=== Test Cache Second Niveau ===");

        resetStatistics();

        EntityManager em1 = emf.createEntityManager();

        try {

            Auteur a = em1.find(Auteur.class, 1L);

            System.out.println("Auteur: "
                    + a.getNom());

        } finally {
            em1.close();
        }

        printStatistics("Premier accès (MISS)");

        resetStatistics();

        EntityManager em2 = emf.createEntityManager();

        try {

            Auteur a = em2.find(Auteur.class, 1L);

            System.out.println("Auteur: "
                    + a.getNom());

        } finally {
            em2.close();
        }

        printStatistics("Deuxième accès (HIT)");
    }
}