package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.Connection;

import it.biblioteca.config.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/test-db")
public class TestDatabaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            response.getWriter().println(
                "<h1>Connessione MySQL riuscita!</h1>"
            );

        } catch (Exception e) {

            response.getWriter().println(
                "<h1>Errore connessione MySQL</h1>"
            );

            response.getWriter().println(
                "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}