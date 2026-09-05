package it.biblioteca.model;

public class Utente {

    private int idUtente;
    private String nome;
    private String cognome;
    private String username;
    private String email;
    private String telefono;
    private String passwordHash;
    private int idRuolo;
    private Integer idComunita;
    private String generiPreferiti;
    private boolean attivo;

    public Utente() {
    }

    public Utente(
            String nome,
            String cognome,
            String username,
            String email,
            String passwordHash,
            int idRuolo) {

        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.idRuolo = idRuolo;
        this.attivo = true;
    }

    public Utente(
            String nome,
            String cognome,
            String username,
            String email,
            String telefono,
            String passwordHash,
            int idRuolo,
            Integer idComunita,
            String generiPreferiti) {

        this(nome, cognome, username, email, passwordHash, idRuolo);
        this.telefono = telefono;
        this.idComunita = idComunita;
        this.generiPreferiti = generiPreferiti;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getIdRuolo() {
        return idRuolo;
    }

    public void setIdRuolo(int idRuolo) {
        this.idRuolo = idRuolo;
    }

    public Integer getIdComunita() {
        return idComunita;
    }

    public void setIdComunita(Integer idComunita) {
        this.idComunita = idComunita;
    }

    public String getGeneriPreferiti() {
        return generiPreferiti;
    }

    public void setGeneriPreferiti(String generiPreferiti) {
        this.generiPreferiti = generiPreferiti;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }
}