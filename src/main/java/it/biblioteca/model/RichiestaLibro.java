package it.biblioteca.model;

public class RichiestaLibro {

    private int idRichiesta;
    private int idOfferta;
    private int idRichiedente;

    private String tipoRichiesta;
    private String messaggioModalita;
    private String stato;

    private String titoloLibro;
    private String autoreLibro;

    private int idProprietario;
    private String usernameProprietario;

    private String nomeRichiedente;
    private String usernameRichiedente;

    public RichiestaLibro() {
    }

    public int getIdRichiesta() {
        return idRichiesta;
    }

    public void setIdRichiesta(int idRichiesta) {
        this.idRichiesta = idRichiesta;
    }

    public int getIdOfferta() {
        return idOfferta;
    }

    public void setIdOfferta(int idOfferta) {
        this.idOfferta = idOfferta;
    }

    public int getIdRichiedente() {
        return idRichiedente;
    }

    public void setIdRichiedente(int idRichiedente) {
        this.idRichiedente = idRichiedente;
    }

    public String getTipoRichiesta() {
        return tipoRichiesta;
    }

    public void setTipoRichiesta(String tipoRichiesta) {
        this.tipoRichiesta = tipoRichiesta;
    }

    public String getMessaggioModalita() {
        return messaggioModalita;
    }

    public void setMessaggioModalita(
            String messaggioModalita) {

        this.messaggioModalita =
                messaggioModalita;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getTitoloLibro() {
        return titoloLibro;
    }

    public void setTitoloLibro(String titoloLibro) {
        this.titoloLibro = titoloLibro;
    }

    public String getAutoreLibro() {
        return autoreLibro;
    }

    public void setAutoreLibro(String autoreLibro) {
        this.autoreLibro = autoreLibro;
    }

    public int getIdProprietario() {
        return idProprietario;
    }

    public void setIdProprietario(int idProprietario) {
        this.idProprietario = idProprietario;
    }

    public String getUsernameProprietario() {
        return usernameProprietario;
    }

    public void setUsernameProprietario(
            String usernameProprietario) {

        this.usernameProprietario =
                usernameProprietario;
    }

    public String getNomeRichiedente() {
        return nomeRichiedente;
    }

    public void setNomeRichiedente(
            String nomeRichiedente) {

        this.nomeRichiedente =
                nomeRichiedente;
    }

    public String getUsernameRichiedente() {
        return usernameRichiedente;
    }

    public void setUsernameRichiedente(
            String usernameRichiedente) {

        this.usernameRichiedente =
                usernameRichiedente;
    }
}