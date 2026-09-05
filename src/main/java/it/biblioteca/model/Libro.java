package it.biblioteca.model;

public class Libro {

    private int idLibro;
    private String titolo;
    private String autore;
    private String isbn;
    private Integer annoPubblicazione;
    private String genere;
    private boolean disponibile;
    private int idUtenteInserimento;

    public Libro() {
    }

    public Libro(String titolo, String autore, String isbn,
                 Integer annoPubblicazione, String genere,
                 boolean disponibile, int idUtenteInserimento) {

        this.titolo = titolo;
        this.autore = autore;
        this.isbn = isbn;
        this.annoPubblicazione = annoPubblicazione;
        this.genere = genere;
        this.disponibile = disponibile;
        this.idUtenteInserimento = idUtenteInserimento;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public void setAnnoPubblicazione(Integer annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }

    public int getIdUtenteInserimento() {
        return idUtenteInserimento;
    }

    public void setIdUtenteInserimento(int idUtenteInserimento) {
        this.idUtenteInserimento = idUtenteInserimento;
    }
}