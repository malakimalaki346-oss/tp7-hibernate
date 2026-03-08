package com.example.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "livres")
@Cacheable
@NamedEntityGraph(
        name = "graph.Livre.categoriesEtAuteur",
        attributeNodes = {
                @NamedAttributeNode("auteur"),
                @NamedAttributeNode("categories")
        }
)
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    private int anneePublication;

    private String isbn;

    @Column(length = 1000)
    private String resume;

    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private Auteur auteur;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "livre_categorie",
            joinColumns = @JoinColumn(name = "livre_id"),
            inverseJoinColumns = @JoinColumn(name = "categorie_id")
    )
    private Set<Categorie> categories = new HashSet<>();

    // Constructeurs
    public Livre() {}

    public Livre(String titre, int anneePublication, String isbn) {
        this.titre = titre;
        this.anneePublication = anneePublication;
        this.isbn = isbn;
    }

    // Méthodes utilitaires
    public void addCategorie(Categorie categorie) {
        categories.add(categorie);
    }

    public void removeCategorie(Categorie categorie) {
        categories.remove(categorie);
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public int getAnneePublication() {
        return anneePublication;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getResume() {
        return resume;
    }

    public Auteur getAuteur() {
        return auteur;
    }

    public Set<Categorie> getCategories() {
        return categories;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setAnneePublication(int anneePublication) {
        this.anneePublication = anneePublication;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
    }

    public void setCategories(List<Categorie> categories) {
        this.categories = (Set<Categorie>) categories;
    }

    @Override
    public String toString() {
        return "Livre{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", anneePublication=" + anneePublication +
                ", isbn='" + isbn + '\'' +
                '}';
    }

    public void setCategorie(Object o) {
    }
}