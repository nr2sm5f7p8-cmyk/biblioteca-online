package it.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.biblioteca.config.DatabaseConnection;
import it.biblioteca.model.RecensioneScambio;

public class RecensioneScambioDAO {

    private static final String INSERT_RECENSIONE =
            "INSERT INTO recensioni_scambi "
            + "(id_richiesta, id_autore, id_destinatario, voto, testo) "
            + "SELECT r.id_richiesta, ?, "
            + "CASE WHEN r.id_richiedente = ? "
            + "THEN o.id_proprietario "
            + "ELSE r.id_richiedente END, ?, ? "
            + "FROM richieste_libri r "
            + "JOIN offerte_libri o "
            + "ON o.id_offerta = r.id_offerta "
            + "WHERE r.id_richiesta = ? "
            + "AND r.stato = 'COMPLETATA' "
            + "AND (? = r.id_richiedente "
            + "OR ? = o.id_proprietario) "
            + "AND NOT EXISTS ("
            + "SELECT 1 FROM recensioni_scambi x "
            + "WHERE x.id_richiesta = r.id_richiesta "
            + "AND x.id_autore = ?)";

    private static final String SELECT_ESISTE =
            "SELECT 1 FROM recensioni_scambi "
            + "WHERE id_richiesta = ? "
            + "AND id_autore = ?";

    private static final String SELECT_RICEVUTE =
            "SELECT rec.*, "
            + "ua.username AS username_autore, "
            + "ud.username AS username_destinatario, "
            + "l.titolo "
            + "FROM recensioni_scambi rec "
            + "JOIN utenti ua "
            + "ON ua.id_utente = rec.id_autore "
            + "JOIN utenti ud "
            + "ON ud.id_utente = rec.id_destinatario "
            + "JOIN richieste_libri r "
            + "ON r.id_richiesta = rec.id_richiesta "
            + "JOIN offerte_libri o "
            + "ON o.id_offerta = r.id_offerta "
            + "JOIN libri l "
            + "ON l.id_libro = o.id_libro "
            + "WHERE rec.id_destinatario = ? "
            + "ORDER BY rec.data_recensione DESC";

    public boolean inserisci(
            int idRichiesta,
            int idAutore,
            int voto,
            String testo)
            throws SQLException {

        if (!votoValido(voto)) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(INSERT_RECENSIONE)) {

            impostaInserimento(
                    ps,
                    idRichiesta,
                    idAutore,
                    voto,
                    testo);

            return ps.executeUpdate() == 1;
        }
    }

    private void impostaInserimento(
            PreparedStatement ps,
            int idRichiesta,
            int idAutore,
            int voto,
            String testo)
            throws SQLException {

        ps.setInt(1, idAutore);
        ps.setInt(2, idAutore);
        ps.setInt(3, voto);
        ps.setString(4, testo);
        ps.setInt(5, idRichiesta);
        ps.setInt(6, idAutore);
        ps.setInt(7, idAutore);
        ps.setInt(8, idAutore);
    }

    public boolean esiste(
            int idRichiesta,
            int idAutore)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SELECT_ESISTE)) {

            ps.setInt(1, idRichiesta);
            ps.setInt(2, idAutore);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<RecensioneScambio> trovaRicevute(
            int idUtente)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SELECT_RICEVUTE)) {

            ps.setInt(1, idUtente);

            try (ResultSet rs = ps.executeQuery()) {
                return creaLista(rs);
            }
        }
    }

    private List<RecensioneScambio> creaLista(
            ResultSet rs)
            throws SQLException {

        List<RecensioneScambio> recensioni =
                new ArrayList<>();

        while (rs.next()) {
            recensioni.add(creaRecensione(rs));
        }

        return recensioni;
    }

    private RecensioneScambio creaRecensione(
            ResultSet rs)
            throws SQLException {

        RecensioneScambio recensione =
                new RecensioneScambio();

        impostaDatiPrincipali(recensione, rs);
        impostaDatiVisualizzazione(recensione, rs);

        return recensione;
    }

    private void impostaDatiPrincipali(
            RecensioneScambio recensione,
            ResultSet rs)
            throws SQLException {

        recensione.setIdRecensione(
                rs.getInt("id_recensione"));

        recensione.setIdRichiesta(
                rs.getInt("id_richiesta"));

        recensione.setIdAutore(
                rs.getInt("id_autore"));

        recensione.setIdDestinatario(
                rs.getInt("id_destinatario"));

        recensione.setVoto(
                rs.getInt("voto"));

        recensione.setTesto(
                rs.getString("testo"));
    }

    private void impostaDatiVisualizzazione(
            RecensioneScambio recensione,
            ResultSet rs)
            throws SQLException {

        recensione.setUsernameAutore(
                rs.getString("username_autore"));

        recensione.setUsernameDestinatario(
                rs.getString("username_destinatario"));

        recensione.setTitoloLibro(
                rs.getString("titolo"));
    }

    private boolean votoValido(int voto) {

        return voto >= 1 && voto <= 5;
    }
}