package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;

import it.biblioteca.dao.LibroDAO;
import it.biblioteca.model.Libro;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/inserisci-libro")
public class InserisciLibroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (!utenteAutenticato(session)) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            Libro libro = creaLibro(request, session);

            if (libroDAO.inserisci(libro)) {
                response.sendRedirect("libri");
                return;
            }

            mostraErrore(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Errore durante l'inserimento del libro", e);
        }
    }

    private boolean utenteAutenticato(HttpSession session) {

        return session != null
                && Boolean.TRUE.equals(
                        session.getAttribute("autenticato"));
    }

    private Libro creaLibro(HttpServletRequest request,
                            HttpSession session) {

        String titolo = request.getParameter("titolo");
        String autore = request.getParameter("autore");
        String isbn = request.getParameter("isbn");
        String genere = request.getParameter("genere");

        Integer anno = leggiAnno(
                request.getParameter("annoPubblicazione"));

        boolean disponibile =
                request.getParameter("disponibile") != null;

        int idUtente =
                (int) session.getAttribute("utenteId");

        return new Libro(
                titolo,
                autore,
                isbn,
                anno,
                genere,
                disponibile,
                idUtente
        );
    }

    private Integer leggiAnno(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        return Integer.valueOf(valore);
    }

    private void mostraErrore(HttpServletRequest request,
                              HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(
                "errore",
                "Inserimento del libro non riuscito."
        );

        request.getRequestDispatcher("/inserisci_libro.jsp")
               .forward(request, response);
    }
}