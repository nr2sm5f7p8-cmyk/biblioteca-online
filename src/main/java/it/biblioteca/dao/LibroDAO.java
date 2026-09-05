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

    private static final String INSERT_LIBRO =
            "INSERT INTO libri "
            + "(titolo, autore, isbn, anno_pubblicazione, genere, "
            + "disponibile, id_utente_inserimento) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ID =
            "SELECT * FROM libri WHERE id_libro = ?";

    private static final String UPDATE_LIBRO =
            "UPDATE libri SET "
            + "titolo = ?, autore = ?, isbn = ?, "
            + "anno_pubblicazione = ?, genere = ?, disponibile = ? "
            + "WHERE id_libro = ?";

    public List<Libro> trovaTutti() throws SQLException {

        List<Libro> libri = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_TUTTI);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libri.add(creaLibro(rs));
            }
        }

        return libri;
    }

    public boolean inserisci(Libro libro) throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_LIBRO)) {

            impostaParametriInserimento(ps, libro);

            return ps.executeUpdate() == 1;
        }
    }

    public Libro trovaPerId(int idLibro) throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ID)) {

            ps.setInt(1, idLibro);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? creaLibro(rs) : null;
            }
        }
    }

    public boolean aggiorna(Libro libro) throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_LIBRO)) {

            ps.setString(1, libro.getTitolo());
            ps.setString(2, libro.getAutore());
            ps.setString(3, libro.getIsbn());
            ps.setObject(4, libro.getAnnoPubblicazione());
            ps.setString(5, libro.getGenere());
            ps.setBoolean(6, libro.isDisponibile());
            ps.setInt(7, libro.getIdLibro());

            return ps.executeUpdate() == 1;
        }
    }

    private void impostaParametriInserimento(
            PreparedStatement ps, Libro libro) throws SQLException {

        ps.setString(1, libro.getTitolo());
        ps.setString(2, libro.getAutore());
        ps.setString(3, libro.getIsbn());
        ps.setObject(4, libro.getAnnoPubblicazione());
        ps.setString(5, libro.getGenere());
        ps.setBoolean(6, libro.isDisponibile());
        ps.setInt(7, libro.getIdUtenteInserimento());
    }

    private Libro creaLibro(ResultSet rs) throws SQLException {

        Libro libro = new Libro();

        libro.setIdLibro(rs.getInt("id_libro"));
        libro.setTitolo(rs.getString("titolo"));
        libro.setAutore(rs.getString("autore"));
        libro.setIsbn(rs.getString("isbn"));

        int anno = rs.getInt("anno_pubblicazione");

        if (rs.wasNull()) {
            libro.setAnnoPubblicazione(null);
        } else {
            libro.setAnnoPubblicazione(anno);
        }

        libro.setGenere(rs.getString("genere"));
        libro.setDisponibile(rs.getBoolean("disponibile"));
        libro.setIdUtenteInserimento(
                rs.getInt("id_utente_inserimento")
        );

        return libro;
    }
}