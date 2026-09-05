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

@WebServlet("/libri")
public class ListaLibriServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!utenteAutenticato(request)) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp"
            );
            return;
        }

        try {
            List<Libro> libri = libroDAO.trovaTutti();

            request.setAttribute("libri", libri);

            request.getRequestDispatcher("/lista_libri.jsp")
                   .forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Errore durante il caricamento dei libri",
                    e
            );

            throw new ServletException(
                    "Impossibile caricare la lista dei libri."
            );
        }
    }

    private boolean utenteAutenticato(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        return session != null
                && Boolean.TRUE.equals(
                        session.getAttribute("autenticato")
                );
    }
}