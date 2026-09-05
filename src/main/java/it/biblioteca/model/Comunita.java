package it.biblioteca.model;

public class Comunita {

    private int idComunita;
    private String nome;
    private String descrizione;

    public Comunita() {
    }

    public Comunita(
            int idComunita,
            String nome,
            String descrizione) {

        this.idComunita = idComunita;
        this.nome = nome;
        this.descrizione = descrizione;
    }

    public int getIdComunita() {
        return idComunita;
    }

    public void setIdComunita(int idComunita) {
        this.idComunita = idComunita;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}