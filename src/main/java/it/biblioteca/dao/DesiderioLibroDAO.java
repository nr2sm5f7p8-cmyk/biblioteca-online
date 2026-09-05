package it.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.biblioteca.config.DatabaseConnection;
import it.biblioteca.model.Libro;

public class DesiderioLibroDAO {

    private static final String INSERT_DESIDERIO =
            "INSERT INTO desideri_libri "
            + "(id_utente, id_libro) VALUES (?, ?)";

    private static final String DELETE_DESIDERIO =
            "DELETE FROM desideri_libri "
            + "WHERE id_utente = ? AND id_libro = ?";

    private static final String SELECT_DESIDERI =
            "SELECT l.* "
            + "FROM desideri_libri d "
            + "JOIN libri l ON l.id_libro = d.id_libro "
            + "WHERE d.id_utente = ? "
            + "ORDER BY d.data_inserimento DESC";

    private static final String SELECT_ESISTE =
            "SELECT 1 FROM desideri_libri "
            + "WHERE id_utente = ? AND id_libro = ?";

    public boolean aggiungi(
            int idUtente,
            int idLibro)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(INSERT_DESIDERIO)) {

            impostaUtenteLibro(ps, idUtente, idLibro);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean rimuovi(
            int idUtente,
            int idLibro)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(DELETE_DESIDERIO)) {

            impostaUtenteLibro(ps, idUtente, idLibro);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean esiste(
            int idUtente,
            int idLibro)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SELECT_ESISTE)) {

            impostaUtenteLibro(ps, idUtente, idLibro);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Libro> trovaPerUtente(
            int idUtente)
            throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SELECT_DESIDERI)) {

            ps.setInt(1, idUtente);

            try (ResultSet rs = ps.executeQuery()) {
                return creaLista(rs);
            }
        }
    }

    private void impostaUtenteLibro(
            PreparedStatement ps,
            int idUtente,
            int idLibro)
            throws SQLException {

        ps.setInt(1, idUtente);
        ps.setInt(2, idLibro);
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

        libro.setIdLibro(rs.getInt("id_libro"));
        libro.setTitolo(rs.getString("titolo"));
        libro.setAutore(rs.getString("autore"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setAnnoPubblicazione(leggiAnno(rs));
        libro.setGenere(rs.getString("genere"));
        libro.setDescrizione(rs.getString("descrizione"));
        libro.setDisponibile(rs.getBoolean("disponibile"));

        libro.setIdUtenteInserimento(
                rs.getInt("id_utente_inserimento"));

        return libro;
    }

    private Integer leggiAnno(ResultSet rs)
            throws SQLException {

        int anno = rs.getInt("anno_pubblicazione");

        return rs.wasNull()
                ? null
                : anno;
    }
}