package it.biblioteca.model;

public class OffertaLibro {

    private int idOfferta;
    private int idLibro;
    private int idProprietario;
    private String tipoOfferta;
    private String condizioni;
    private boolean attiva;

    private String titoloLibro;
    private String autoreLibro;
    private String nomeProprietario;
    private String usernameProprietario;

    public OffertaLibro() {
    }

    public int getIdOfferta() {
        return idOfferta;
    }

    public void setIdOfferta(int idOfferta) {
        this.idOfferta = idOfferta;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public int getIdProprietario() {
        return idProprietario;
    }

    public void setIdProprietario(int idProprietario) {
        this.idProprietario = idProprietario;
    }

    public String getTipoOfferta() {
        return tipoOfferta;
    }

    public void setTipoOfferta(String tipoOfferta) {
        this.tipoOfferta = tipoOfferta;
    }

    public String getCondizioni() {
        return condizioni;
    }

    public void setCondizioni(String condizioni) {
        this.condizioni = condizioni;
    }

    public boolean isAttiva() {
        return attiva;
    }

    public void setAttiva(boolean attiva) {
        this.attiva = attiva;
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

    public String getNomeProprietario() {
        return nomeProprietario;
    }

    public void setNomeProprietario(String nomeProprietario) {
        this.nomeProprietario = nomeProprietario;
    }

    public String getUsernameProprietario() {
        return usernameProprietario;
    }

    public void setUsernameProprietario(
            String usernameProprietario) {

        this.usernameProprietario =
                usernameProprietario;
    }
}