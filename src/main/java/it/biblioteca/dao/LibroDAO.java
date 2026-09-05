package it.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.biblioteca.config.DatabaseConnection;
import it.biblioteca.model.Libro;

public class LibroDAO {

    private static final String SELECT_TUTTI =
            "SELECT * FROM libri ORDER BY titolo";

    private static final String SELECT_ID =
            "SELECT * FROM libri WHERE id_libro = ?";

    private static final String SELECT_PER_UTENTE =
            "SELECT * FROM libri "
            + "WHERE id_utente_inserimento = ? "
            + "ORDER BY titolo";

    private static final String INSERT_LIBRO =
            "INSERT INTO libri "
            + "(titolo, autore, isbn, anno_pubblicazione, genere, "
            + "descrizione, disponibile, id_utente_inserimento) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_LIBRO =
            "UPDATE libri SET "
            + "titolo = ?, autore = ?, isbn = ?, "
            + "anno_pubblicazione = ?, genere = ?, "
            + "descrizione = ?, disponibile = ? "
            + "WHERE id_libro = ?";

    private static final String CERCA_LIBRI =
            "SELECT * FROM libri "
            + "WHERE titolo LIKE ? "
            + "AND autore LIKE ? "
            + "AND COALESCE(genere, '') LIKE ? "
            + "ORDER BY titolo";

    public List<Libro> trovaTutti()
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_TUTTI);
             ResultSet rs = ps.executeQuery()) {

            return creaLista(rs);
        }
    }

    public List<Libro> cerca(
            String titolo,
            String autore,
            String genere)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CERCA_LIBRI)) {

            impostaFiltriRicerca(ps, titolo, autore, genere);

            try (ResultSet rs = ps.executeQuery()) {
                return creaLista(rs);
            }
        }
    }

    public List<Libro> trovaPerUtente(
            int idUtente)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SELECT_PER_UTENTE)) {

            ps.setInt(1, idUtente);

            try (ResultSet rs = ps.executeQuery()) {
                return creaLista(rs);
            }
        }
    }

    public Libro trovaPerId(int idLibro)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ID)) {

            ps.setInt(1, idLibro);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? creaLibro(rs)
                        : null;
            }
        }
    }

    public boolean inserisci(Libro libro)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_LIBRO)) {

            impostaParametriInserimento(ps, libro);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean aggiorna(Libro libro)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_LIBRO)) {

            impostaParametriAggiornamento(ps, libro);

            return ps.executeUpdate() == 1;
        }
    }

    private void impostaFiltriRicerca(
            PreparedStatement ps,
            String titolo,
            String autore,
            String genere)
            throws SQLException {

        ps.setString(1, creaFiltro(titolo));
        ps.setString(2, creaFiltro(autore));
        ps.setString(3, creaFiltro(genere));
    }

    private String creaFiltro(String valore) {

        if (valore == null || valore.isBlank()) {
            return "%";
        }

        return "%" + valore.trim() + "%";
    }

    private void impostaParametriInserimento(
            PreparedStatement ps,
            Libro libro)
            throws SQLException {

        ps.setString(1, libro.getTitolo());
        ps.setString(2, libro.getAutore());
        ps.setString(3, libro.getIsbn());
        ps.setObject(4, libro.getAnnoPubblicazione());
        ps.setString(5, libro.getGenere());
        ps.setString(6, libro.getDescrizione());
        ps.setBoolean(7, libro.isDisponibile());
        ps.setInt(8, libro.getIdUtenteInserimento());
    }

    private void impostaParametriAggiornamento(
            PreparedStatement ps,
            Libro libro)
            throws SQLException {

        ps.setString(1, libro.getTitolo());
        ps.setString(2, libro.getAutore());
        ps.setString(3, libro.getIsbn());
        ps.setObject(4, libro.getAnnoPubblicazione());
        ps.setString(5, libro.getGenere());
        ps.setString(6, libro.getDescrizione());
        ps.setBoolean(7, libro.isDisponibile());
        ps.setInt(8, libro.getIdLibro());
    }

    private List<Libro> creaLista(ResultSet rs)
            throws SQLException {

        List<Libro> libri = new ArrayList<>();

        while (rs.next()) {
            libri.add(creaLibro(rs));
        }

        return libri;
    }

    private Libro creaLibro(ResultSet rs)
            throws SQLException {

        Libro libro = new Libro();

        impostaDatiPrincipali(libro, rs);
        impostaDatiAggiuntivi(libro, rs);

        return libro;
    }

    private void impostaDatiPrincipali(
            Libro libro,
            ResultSet rs)
            throws SQLException {

        libro.setIdLibro(rs.getInt("id_libro"));
        libro.setTitolo(rs.getString("titolo"));
        libro.setAutore(rs.getString("autore"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setAnnoPubblicazione(leggiAnno(rs));
    }

    private void impostaDatiAggiuntivi(
            Libro libro,
            ResultSet rs)
            throws SQLException {

        libro.setGenere(rs.getString("genere"));
        libro.setDescrizione(rs.getString("descrizione"));
        libro.setDisponibile(rs.getBoolean("disponibile"));

        libro.setIdUtenteInserimento(
                rs.getInt("id_utente_inserimento"));
    }

    private Integer leggiAnno(ResultSet rs)
            throws SQLException {

        int anno = rs.getInt("anno_pubblicazione");

        return rs.wasNull()
                ? null
                : anno;
    }
}