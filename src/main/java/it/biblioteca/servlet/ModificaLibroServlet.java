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

@WebServlet("/modifica-libro")
public class ModificaLibroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (!utenteAutenticato(request)) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int idLibro = Integer.parseInt(
                    request.getParameter("id"));

            Libro libro = libroDAO.trovaPerId(idLibro);

            if (libro == null) {
                response.sendRedirect("libri");
                return;
            }

            request.setAttribute("libro", libro);

            request.getRequestDispatcher("/modifica_libro.jsp")
                   .forward(request, response);

        } catch (SQLException | NumberFormatException e) {
            throw new ServletException(
                    "Errore durante il caricamento del libro", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        if (!utenteAutenticato(request)) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            Libro libro = creaLibroDaForm(request);

            libroDAO.aggiorna(libro);

            response.sendRedirect("libri");

        } catch (SQLException | NumberFormatException e) {
            throw new ServletException(
                    "Errore durante la modifica del libro", e);
        }
    }

    private Libro creaLibroDaForm(HttpServletRequest request) {

        Libro libro = new Libro();

        libro.setIdLibro(
                Integer.parseInt(request.getParameter("idLibro")));

        libro.setTitolo(request.getParameter("titolo"));
        libro.setAutore(request.getParameter("autore"));
        libro.setIsbn(request.getParameter("isbn"));
        libro.setGenere(request.getParameter("genere"));

        libro.setAnnoPubblicazione(
                leggiAnno(request.getParameter("annoPubblicazione")));

        libro.setDisponibile(
                request.getParameter("disponibile") != null);

        return libro;
    }

    private Integer leggiAnno(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        return Integer.valueOf(valore);
    }

    private boolean utenteAutenticato(
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        return session != null
                && Boolean.TRUE.equals(
                        session.getAttribute("autenticato"));
    }
}