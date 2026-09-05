package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.biblioteca.dao.DesiderioLibroDAO;
import it.biblioteca.model.Libro;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/desideri")
public class DesideriServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final DesiderioLibroDAO desiderioDAO =
            new DesiderioLibroDAO();

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

        caricaDesideri(request, response, idUtente);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Integer idUtente =
                recuperaIdUtente(request.getSession(false));

        if (idUtente == null) {
            vaiAlLogin(request, response);
            return;
        }

        gestisciAzione(request, response, idUtente);
    }

    private void caricaDesideri(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        try {
            List<Libro> libri =
                    desiderioDAO.trovaPerUtente(idUtente);

            request.setAttribute("libri", libri);

            request.getRequestDispatcher(
                    "/desideri.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            gestisciErroreDatabase(e);
        }
    }

    private void gestisciAzione(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws IOException, ServletException {

        Integer idLibro =
                leggiId(request.getParameter("idLibro"));

        if (idLibro == null) {
            response.sendRedirect(
                    request.getContextPath() + "/desideri");
            return;
        }

        eseguiAzione(
                request,
                response,
                idUtente,
                idLibro);
    }

    private void eseguiAzione(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente,
            int idLibro)
            throws IOException, ServletException {

        try {
            String azione = request.getParameter("azione");

            if ("rimuovi".equalsIgnoreCase(azione)) {
                desiderioDAO.rimuovi(idUtente, idLibro);
            } else {
                aggiungiSeAssente(idUtente, idLibro);
            }

            response.sendRedirect(
                    request.getContextPath() + "/desideri");

        } catch (SQLException e) {
            gestisciErroreDatabase(e);
        }
    }

    private void aggiungiSeAssente(
            int idUtente,
            int idLibro)
            throws SQLException {

        if (!desiderioDAO.esiste(idUtente, idLibro)) {
            desiderioDAO.aggiungi(idUtente, idLibro);
        }
    }

    private Integer leggiId(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        try {
            int id = Integer.parseInt(valore);
            return id > 0 ? id : null;

        } catch (NumberFormatException e) {
            return null;
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
                "Errore gestione lista desideri",
                e);

        throw new ServletException(
                "Errore durante la gestione della lista desideri.",
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