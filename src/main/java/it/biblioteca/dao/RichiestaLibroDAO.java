package it.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.biblioteca.config.DatabaseConnection;
import it.biblioteca.model.RichiestaLibro;

public class RichiestaLibroDAO {

    private static final String INSERT_RICHIESTA =
            "INSERT INTO richieste_libri "
            + "(id_offerta, id_richiedente, tipo_richiesta, "
            + "messaggio_modalita, stato) "
            + "SELECT o.id_offerta, ?, ?, ?, 'IN_ATTESA' "
            + "FROM offerte_libri o "
            + "WHERE o.id_offerta = ? "
            + "AND o.attiva = 1 "
            + "AND o.id_proprietario <> ? "
            + "AND (o.tipo_offerta = ? "
            + "OR o.tipo_offerta = 'ENTRAMBI')";

    private static final String SELECT_ESISTE =
            "SELECT 1 FROM richieste_libri "
            + "WHERE id_offerta = ? "
            + "AND id_richiedente = ? "
            + "AND stato IN ('IN_ATTESA', 'ACCETTATA')";

    private static final String SELECT_BASE =
            "SELECT r.*, l.titolo, l.autore, "
            + "o.id_proprietario, "
            + "up.username AS username_proprietario, "
            + "ur.nome AS nome_richiedente, "
            + "ur.cognome AS cognome_richiedente, "
            + "ur.username AS username_richiedente "
            + "FROM richieste_libri r "
            + "JOIN offerte_libri o "
            + "ON o.id_offerta = r.id_offerta "
            + "JOIN libri l "
            + "ON l.id_libro = o.id_libro "
            + "JOIN utenti up "
            + "ON up.id_utente = o.id_proprietario "
            + "JOIN utenti ur "
            + "ON ur.id_utente = r.id_richiedente ";

    private static final String SELECT_INVIATE =
            SELECT_BASE
            + "WHERE r.id_richiedente = ? "
            + "ORDER BY r.data_richiesta DESC";

    private static final String SELECT_RICEVUTE =
            SELECT_BASE
            + "WHERE o.id_proprietario = ? "
            + "ORDER BY r.data_richiesta DESC";

    private static final String UPDATE_RISPOSTA =
            "UPDATE richieste_libri r "
            + "JOIN offerte_libri o "
            + "ON o.id_offerta = r.id_offerta "
            + "SET r.stato = ?, "
            + "r.data_risposta = CURRENT_TIMESTAMP "
            + "WHERE r.id_richiesta = ? "
            + "AND o.id_proprietario = ? "
            + "AND r.stato = 'IN_ATTESA'";

    private static final String UPDATE_ANNULLA =
            "UPDATE richieste_libri "
            + "SET stato = 'ANNULLATA' "
            + "WHERE id_richiesta = ? "
            + "AND id_richiedente = ? "
            + "AND stato = 'IN_ATTESA'";

    private static final String UPDATE_COMPLETA =
            "UPDATE richieste_libri r "
            + "JOIN offerte_libri o "
            + "ON o.id_offerta = r.id_offerta "
            + "SET r.stato = 'COMPLETATA', "
            + "r.data_completamento = CURRENT_TIMESTAMP "
            + "WHERE r.id_richiesta = ? "
            + "AND r.stato = 'ACCETTATA' "
            + "AND (r.id_richiedente = ? "
            + "OR o.id_proprietario = ?)";

    public boolean inserisci(
            int idOfferta,
            int idRichiedente,
            String tipoRichiesta,
            String messaggio)
            throws SQLException {

        if (esisteRichiestaAttiva(
                idOfferta,
                idRichiedente)) {

            return false;
        }

        return eseguiInserimento(
                idOfferta,
                idRichiedente,
                tipoRichiesta,
                messaggio);
    }

    private boolean eseguiInserimento(
            int idOfferta,
            int idRichiedente,
            String tipoRichiesta,
            String messaggio)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(INSERT_RICHIESTA)) {

            impostaInserimento(
                    ps,
                    idOfferta,
                    idRichiedente,
                    tipoRichiesta,
                    messaggio);

            return ps.executeUpdate() == 1;
        }
    }

    private void impostaInserimento(
            PreparedStatement ps,
            int idOfferta,
            int idRichiedente,
            String tipoRichiesta,
            String messaggio)
            throws SQLException {

        ps.setInt(1, idRichiedente);
        ps.setString(2, tipoRichiesta);
        ps.setString(3, messaggio);
        ps.setInt(4, idOfferta);
        ps.setInt(5, idRichiedente);
        ps.setString(6, tipoRichiesta);
    }

    public boolean esisteRichiestaAttiva(
            int idOfferta,
            int idRichiedente)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SELECT_ESISTE)) {

            ps.setInt(1, idOfferta);
            ps.setInt(2, idRichiedente);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<RichiestaLibro> trovaInviate(
            int idUtente)
            throws SQLException {

        return eseguiLista(
                SELECT_INVIATE,
                idUtente);
    }

    public List<RichiestaLibro> trovaRicevute(
            int idUtente)
            throws SQLException {

        return eseguiLista(
                SELECT_RICEVUTE,
                idUtente);
    }

    private List<RichiestaLibro> eseguiLista(
            String sql,
            int idUtente)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUtente);

            try (ResultSet rs = ps.executeQuery()) {
                return creaLista(rs);
            }
        }
    }

    public boolean rispondi(
            int idRichiesta,
            int idProprietario,
            String stato)
            throws SQLException {

        if (!statoRispostaValido(stato)) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(UPDATE_RISPOSTA)) {

            ps.setString(1, stato);
            ps.setInt(2, idRichiesta);
            ps.setInt(3, idProprietario);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean annulla(
            int idRichiesta,
            int idRichiedente)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(UPDATE_ANNULLA)) {

            ps.setInt(1, idRichiesta);
            ps.setInt(2, idRichiedente);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean completa(
            int idRichiesta,
            int idUtente)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(UPDATE_COMPLETA)) {

            ps.setInt(1, idRichiesta);
            ps.setInt(2, idUtente);
            ps.setInt(3, idUtente);

            return ps.executeUpdate() == 1;
        }
    }

    private boolean statoRispostaValido(
            String stato) {

        return "ACCETTATA".equals(stato)
                || "RIFIUTATA".equals(stato);
    }

    private List<RichiestaLibro> creaLista(
            ResultSet rs)
            throws SQLException {

        List<RichiestaLibro> richieste =
                new ArrayList<>();

        while (rs.next()) {
            richieste.add(creaRichiesta(rs));
        }

        return richieste;
    }

    private RichiestaLibro creaRichiesta(
            ResultSet rs)
            throws SQLException {

        RichiestaLibro richiesta =
                new RichiestaLibro();

        impostaDatiRichiesta(richiesta, rs);
        impostaDatiLibro(richiesta, rs);
        impostaDatiUtenti(richiesta, rs);

        return richiesta;
    }

    private void impostaDatiRichiesta(
            RichiestaLibro richiesta,
            ResultSet rs)
            throws SQLException {

        richiesta.setIdRichiesta(
                rs.getInt("id_richiesta"));

        richiesta.setIdOfferta(
                rs.getInt("id_offerta"));

        richiesta.setIdRichiedente(
                rs.getInt("id_richiedente"));

        richiesta.setTipoRichiesta(
                rs.getString("tipo_richiesta"));

        richiesta.setMessaggioModalita(
                rs.getString("messaggio_modalita"));

        richiesta.setStato(
                rs.getString("stato"));
    }

    private void impostaDatiLibro(
            RichiestaLibro richiesta,
            ResultSet rs)
            throws SQLException {

        richiesta.setTitoloLibro(
                rs.getString("titolo"));

        richiesta.setAutoreLibro(
                rs.getString("autore"));

        richiesta.setIdProprietario(
                rs.getInt("id_proprietario"));

        richiesta.setUsernameProprietario(
                rs.getString("username_proprietario"));
    }

    private void impostaDatiUtenti(
            RichiestaLibro richiesta,
            ResultSet rs)
            throws SQLException {

        richiesta.setNomeRichiedente(
                creaNomeRichiedente(rs));

        richiesta.setUsernameRichiedente(
                rs.getString("username_richiedente"));
    }

    private String creaNomeRichiedente(
            ResultSet rs)
            throws SQLException {

        String nome =
                rs.getString("nome_richiedente");

        String cognome =
                rs.getString("cognome_richiedente");

        return (nome + " " + cognome).trim();
    }
}