package it.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.biblioteca.config.DatabaseConnection;
import it.biblioteca.model.OffertaLibro;

public class OffertaLibroDAO {

    private static final String INSERT_OFFERTA =
            "INSERT INTO offerte_libri "
            + "(id_libro, id_proprietario, tipo_offerta, condizioni, attiva) "
            + "SELECT id_libro, ?, ?, ?, 1 FROM libri "
            + "WHERE id_libro = ? AND id_utente_inserimento = ?";

    private static final String SELECT_ATTIVE =
            "SELECT o.*, l.titolo, l.autore, "
            + "u.nome, u.cognome, u.username "
            + "FROM offerte_libri o "
            + "JOIN libri l ON l.id_libro = o.id_libro "
            + "JOIN utenti u ON u.id_utente = o.id_proprietario "
            + "WHERE o.attiva = 1 "
            + "ORDER BY o.data_creazione DESC";

    private static final String SELECT_PROPRIETARIO =
            "SELECT o.*, l.titolo, l.autore, "
            + "u.nome, u.cognome, u.username "
            + "FROM offerte_libri o "
            + "JOIN libri l ON l.id_libro = o.id_libro "
            + "JOIN utenti u ON u.id_utente = o.id_proprietario "
            + "WHERE o.id_proprietario = ? "
            + "ORDER BY o.data_creazione DESC";

    private static final String SELECT_ATTIVA_LIBRO =
            "SELECT 1 FROM offerte_libri "
            + "WHERE id_libro = ? "
            + "AND id_proprietario = ? "
            + "AND attiva = 1";

    private static final String DISATTIVA =
            "UPDATE offerte_libri SET attiva = 0 "
            + "WHERE id_offerta = ? "
            + "AND id_proprietario = ?";

    public boolean inserisci(
            int idLibro,
            int idProprietario,
            String tipoOfferta,
            String condizioni)
            throws SQLException {

        if (esisteOffertaAttiva(idLibro, idProprietario)) {
            return false;
        }

        return eseguiInserimento(
                idLibro,
                idProprietario,
                tipoOfferta,
                condizioni);
    }

    private boolean eseguiInserimento(
            int idLibro,
            int idProprietario,
            String tipoOfferta,
            String condizioni)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(INSERT_OFFERTA)) {

            impostaInserimento(
                    ps,
                    idLibro,
                    idProprietario,
                    tipoOfferta,
                    condizioni);

            return ps.executeUpdate() == 1;
        }
    }

    private void impostaInserimento(
            PreparedStatement ps,
            int idLibro,
            int idProprietario,
            String tipoOfferta,
            String condizioni)
            throws SQLException {

        ps.setInt(1, idProprietario);
        ps.setString(2, tipoOfferta);
        ps.setString(3, condizioni);
        ps.setInt(4, idLibro);
        ps.setInt(5, idProprietario);
    }

    public boolean esisteOffertaAttiva(
            int idLibro,
            int idProprietario)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SELECT_ATTIVA_LIBRO)) {

            ps.setInt(1, idLibro);
            ps.setInt(2, idProprietario);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<OffertaLibro> trovaAttive()
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SELECT_ATTIVE);
             ResultSet rs = ps.executeQuery()) {

            return creaLista(rs);
        }
    }

    public List<OffertaLibro> trovaPerProprietario(
            int idProprietario)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SELECT_PROPRIETARIO)) {

            ps.setInt(1, idProprietario);

            try (ResultSet rs = ps.executeQuery()) {
                return creaLista(rs);
            }
        }
    }

    public boolean disattiva(
            int idOfferta,
            int idProprietario)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(DISATTIVA)) {

            ps.setInt(1, idOfferta);
            ps.setInt(2, idProprietario);

            return ps.executeUpdate() == 1;
        }
    }

    private List<OffertaLibro> creaLista(
            ResultSet rs)
            throws SQLException {

        List<OffertaLibro> offerte =
                new ArrayList<>();

        while (rs.next()) {
            offerte.add(creaOfferta(rs));
        }

        return offerte;
    }

    private OffertaLibro creaOfferta(
            ResultSet rs)
            throws SQLException {

        OffertaLibro offerta =
                new OffertaLibro();

        impostaDatiOfferta(offerta, rs);
        impostaDatiVisualizzazione(offerta, rs);

        return offerta;
    }

    private void impostaDatiOfferta(
            OffertaLibro offerta,
            ResultSet rs)
            throws SQLException {

        offerta.setIdOfferta(
                rs.getInt("id_offerta"));

        offerta.setIdLibro(
                rs.getInt("id_libro"));

        offerta.setIdProprietario(
                rs.getInt("id_proprietario"));

        offerta.setTipoOfferta(
                rs.getString("tipo_offerta"));

        offerta.setCondizioni(
                rs.getString("condizioni"));

        offerta.setAttiva(
                rs.getBoolean("attiva"));
    }

    private void impostaDatiVisualizzazione(
            OffertaLibro offerta,
            ResultSet rs)
            throws SQLException {

        offerta.setTitoloLibro(
                rs.getString("titolo"));

        offerta.setAutoreLibro(
                rs.getString("autore"));

        offerta.setNomeProprietario(
                creaNomeCompleto(rs));

        offerta.setUsernameProprietario(
                rs.getString("username"));
    }

    private String creaNomeCompleto(
            ResultSet rs)
            throws SQLException {

        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");

        return (nome + " " + cognome).trim();
    }
}