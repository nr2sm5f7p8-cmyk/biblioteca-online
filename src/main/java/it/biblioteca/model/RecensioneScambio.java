package it.biblioteca.model;

public class RecensioneScambio {

    private int idRecensione;
    private int idRichiesta;
    private int idAutore;
    private int idDestinatario;
    private int voto;
    private String testo;

    private String usernameAutore;
    private String usernameDestinatario;
    private String titoloLibro;

    public RecensioneScambio() {
    }

    public int getIdRecensione() {
        return idRecensione;
    }

    public void setIdRecensione(int idRecensione) {
        this.idRecensione = idRecensione;
    }

    public int getIdRichiesta() {
        return idRichiesta;
    }

    public void setIdRichiesta(int idRichiesta) {
        this.idRichiesta = idRichiesta;
    }

    public int getIdAutore() {
        return idAutore;
    }

    public void setIdAutore(int idAutore) {
        this.idAutore = idAutore;
    }

    public int getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(int idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public int getVoto() {
        return voto;
    }

    public void setVoto(int voto) {
        this.voto = voto;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getUsernameAutore() {
        return usernameAutore;
    }

    public void setUsernameAutore(
            String usernameAutore) {

        this.usernameAutore =
                usernameAutore;
    }

    public String getUsernameDestinatario() {
        return usernameDestinatario;
    }

    public void setUsernameDestinatario(
            String usernameDestinatario) {

        this.usernameDestinatario =
                usernameDestinatario;
    }

    public String getTitoloLibro() {
        return titoloLibro;
    }

    public void setTitoloLibro(String titoloLibro) {
        this.titoloLibro = titoloLibro;
    }
}