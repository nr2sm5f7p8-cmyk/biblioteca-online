package it.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

import org.mindrot.jbcrypt.BCrypt;

import it.biblioteca.config.DatabaseConnection;
import it.biblioteca.model.Utente;

public class UtenteDAO {

    private static final String INSERT_UTENTE =
            "INSERT INTO utenti "
            + "(nome, cognome, username, email, telefono, "
            + "password_hash, id_ruolo, id_comunita, "
            + "generi_preferiti, attivo) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_USERNAME =
            "SELECT * FROM utenti "
            + "WHERE username = ? AND attivo = TRUE";

    private static final String SELECT_USERNAME_EMAIL =
            "SELECT 1 FROM utenti "
            + "WHERE username = ? OR email = ? "
            + "LIMIT 1";

    public boolean inserisci(Utente utente)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(INSERT_UTENTE)) {

            impostaParametriInserimento(ps, utente);

            return ps.executeUpdate() == 1;
        }
    }

    private void impostaParametriInserimento(
            PreparedStatement ps,
            Utente utente) throws SQLException {

        ps.setString(1, utente.getNome());
        ps.setString(2, utente.getCognome());
        ps.setString(3, utente.getUsername());
        ps.setString(4, utente.getEmail());
        ps.setString(5, utente.getTelefono());
        ps.setString(6, utente.getPasswordHash());
        ps.setInt(7, utente.getIdRuolo());

        impostaComunita(ps, 8, utente.getIdComunita());

        ps.setString(9, utente.getGeneriPreferiti());
        ps.setBoolean(10, utente.isAttivo());
    }

    private void impostaComunita(
            PreparedStatement ps,
            int indice,
            Integer idComunita) throws SQLException {

        if (idComunita == null) {
            ps.setNull(indice, Types.INTEGER);
            return;
        }

        ps.setInt(indice, idComunita);
    }

    public boolean esisteUsernameOEmail(
            String username,
            String email) throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SELECT_USERNAME_EMAIL)) {

            ps.setString(1, username);
            ps.setString(2, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Utente trovaPerUsername(String username)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SELECT_USERNAME)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? creaUtente(rs) : null;
            }
        }
    }

    public Utente trovaPerId(int idUtente)
            throws SQLException {

        String sql =
                "SELECT * FROM utenti "
                + "WHERE id_utente = ? AND attivo = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUtente);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? creaUtente(rs) : null;
            }
        }
    }

    private Utente creaUtente(ResultSet rs)
            throws SQLException {

        Utente utente = new Utente();

        impostaDatiBase(utente, rs);
        impostaDatiProfilo(utente, rs);
        impostaDatiSistema(utente, rs);

        return utente;
    }

    private void impostaDatiBase(
            Utente utente,
            ResultSet rs) throws SQLException {

        utente.setIdUtente(rs.getInt("id_utente"));
        utente.setNome(rs.getString("nome"));
        utente.setCognome(rs.getString("cognome"));
        utente.setUsername(rs.getString("username"));
        utente.setEmail(rs.getString("email"));
        utente.setTelefono(rs.getString("telefono"));
    }

    private void impostaDatiProfilo(
            Utente utente,
            ResultSet rs) throws SQLException {

        Integer idComunita =
                (Integer) rs.getObject("id_comunita");

        utente.setIdComunita(idComunita);
        utente.setGeneriPreferiti(
                rs.getString("generi_preferiti"));
    }

    private void impostaDatiSistema(
            Utente utente,
            ResultSet rs) throws SQLException {

        utente.setPasswordHash(
                rs.getString("password_hash"));

        utente.setIdRuolo(
                rs.getInt("id_ruolo"));

        utente.setAttivo(
                rs.getBoolean("attivo"));
    }

    public boolean aggiornaProfilo(Utente utente)
            throws SQLException {

        String sql =
                "UPDATE utenti SET nome = ?, cognome = ?, "
                + "email = ?, telefono = ?, id_comunita = ?, "
                + "generi_preferiti = ? WHERE id_utente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            impostaParametriProfilo(ps, utente);

            return ps.executeUpdate() == 1;
        }
    }

    private void impostaParametriProfilo(
            PreparedStatement ps,
            Utente utente) throws SQLException {

        ps.setString(1, utente.getNome());
        ps.setString(2, utente.getCognome());
        ps.setString(3, utente.getEmail());
        ps.setString(4, utente.getTelefono());

        impostaComunita(ps, 5, utente.getIdComunita());

        ps.setString(6, utente.getGeneriPreferiti());
        ps.setInt(7, utente.getIdUtente());
    }

    public void salvaOtp(
            int idUtente,
            String otpHash,
            LocalDateTime scadenza) throws SQLException {

        String sql =
                "UPDATE utenti "
                + "SET otp_hash = ?, otp_scadenza = ? "
                + "WHERE id_utente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, otpHash);
            ps.setTimestamp(2, Timestamp.valueOf(scadenza));
            ps.setInt(3, idUtente);
            ps.executeUpdate();
        }
    }

    public boolean verificaOtp(
            int idUtente,
            String otp) throws SQLException {

        String sql =
                "SELECT otp_hash, otp_scadenza "
                + "FROM utenti WHERE id_utente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUtente);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                return otpValido(rs, otp);
            }
        }
    }

    private boolean otpValido(
            ResultSet rs,
            String otp) throws SQLException {

        String hash = rs.getString("otp_hash");
        Timestamp scadenza =
                rs.getTimestamp("otp_scadenza");

        if (hash == null || scadenza == null) {
            return false;
        }

        if (otpScaduto(scadenza)) {
            return false;
        }

        return BCrypt.checkpw(otp, hash);
    }

    private boolean otpScaduto(Timestamp scadenza) {

        Timestamp adesso =
                new Timestamp(System.currentTimeMillis());

        return scadenza.before(adesso);
    }

    public void cancellaOtp(int idUtente)
            throws SQLException {

        String sql =
                "UPDATE utenti "
                + "SET otp_hash = NULL, otp_scadenza = NULL "
                + "WHERE id_utente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUtente);
            ps.executeUpdate();
        }
    }
}