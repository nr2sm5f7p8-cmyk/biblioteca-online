package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.biblioteca.dao.LibroDAO;
import it.biblioteca.model.Libro;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/miei-libri")
public class MieiLibriServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Integer idUtente =
                recuperaIdUtente(request.getSession(false));

        if (idUtente == null) {
            vaiAlLogin(request, response);
            return;
        }

        caricaLibri(request, response, idUtente);
    }

    private void caricaLibri(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        try {
            List<Libro> libri =
                    libroDAO.trovaPerUtente(idUtente);

            request.setAttribute("libri", libri);

            request.getRequestDispatcher(
                    "/miei_libri.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            gestisciErroreDatabase(e);
        }
    }

    private Integer recuperaIdUtente(
            HttpSession session) {

        if (session == null) {
            return null;
        }

        if (!Boolean.TRUE.equals(
                session.getAttribute("autenticato"))) {
            return null;
        }

        Object valore =
                session.getAttribute("utenteId");

        return valore instanceof Integer
                ? (Integer) valore
                : null;
    }

    private void gestisciErroreDatabase(
            SQLException e)
            throws ServletException {

        getServletContext().log(
                "Errore durante il caricamento dei libri utente",
                e);

        throw new ServletException(
                "Impossibile caricare i tuoi libri.",
                e);
    }

    private void vaiAlLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp");
    }
}