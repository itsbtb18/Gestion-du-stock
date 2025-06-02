package org.example.model;

public class rayon {
    private int id;
    private String nom;

    public rayon(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    @Override
    public String toString() {
        return "Rayon{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
