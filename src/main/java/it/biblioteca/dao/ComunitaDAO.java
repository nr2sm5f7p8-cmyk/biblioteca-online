package it.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.biblioteca.config.DatabaseConnection;
import it.biblioteca.model.Comunita;

public class ComunitaDAO {

    public List<Comunita> trovaTutte()
            throws SQLException {

        String sql =
                "SELECT id_comunita, nome, descrizione "
                + "FROM comunita "
                + "ORDER BY nome";

        List<Comunita> comunita = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                comunita.add(creaComunita(rs));
            }
        }

        return comunita;
    }

    private Comunita creaComunita(ResultSet rs)
            throws SQLException {

        return new Comunita(
                rs.getInt("id_comunita"),
                rs.getString("nome"),
                rs.getString("descrizione"));
    }
}