package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Year;

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
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer idUtente = recuperaIdUtente(session);

        if (idUtente == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp"
            );
            return;
        }

        String errore = validaInput(request);

        if (errore != null) {
            mostraErrore(request, response, errore);
            return;
        }

        try {
            inserisciLibro(request, response, idUtente);

        } catch (SQLException e) {

            getServletContext().log(
                    "Errore durante l'inserimento del libro",
                    e
            );

            mostraErrore(
                    request,
                    response,
                    "Si e verificato un errore durante l'inserimento."
            );
        }
    }

    private void inserisciLibro(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws SQLException, IOException,
                   ServletException {

        Libro libro = creaLibro(request, idUtente);

        if (!libroDAO.inserisci(libro)) {
            mostraErrore(
                    request,
                    response,
                    "Inserimento del libro non riuscito."
            );
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/libri"
        );
    }

    private String validaInput(HttpServletRequest request) {

        String titolo = request.getParameter("titolo");
        String autore = request.getParameter("autore");
        String anno = request.getParameter("annoPubblicazione");

        if (titolo == null || titolo.isBlank()) {
            return "Il titolo e obbligatorio.";
        }

        if (autore == null || autore.isBlank()) {
            return "L'autore e obbligatorio.";
        }

        return validaAnno(anno);
    }

    private String validaAnno(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        try {
            int anno = Integer.parseInt(valore);
            int annoMassimo = Year.now().getValue() + 1;

            if (anno <= 0 || anno > annoMassimo) {
                return "Anno di pubblicazione non valido.";
            }

        } catch (NumberFormatException e) {
            return "L'anno di pubblicazione deve essere un numero.";
        }

        return null;
    }

    private Libro creaLibro(
            HttpServletRequest request,
            int idUtente) {

        String titolo =
                request.getParameter("titolo").trim();

        String autore =
                request.getParameter("autore").trim();

        String isbn =
                pulisci(request.getParameter("isbn"));

        String genere =
                pulisci(request.getParameter("genere"));

        Integer anno =
                leggiAnno(
                        request.getParameter("annoPubblicazione")
                );

        boolean disponibile =
                request.getParameter("disponibile") != null;

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

    private Integer recuperaIdUtente(HttpSession session) {

        if (session == null
                || !Boolean.TRUE.equals(
                        session.getAttribute("autenticato"))) {
            return null;
        }

        Object valore =
                session.getAttribute("utenteId");

        if (valore instanceof Integer) {
            return (Integer) valore;
        }

        return null;
    }

    private Integer leggiAnno(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        return Integer.valueOf(valore);
    }

    private String pulisci(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        return valore.trim();
    }

    private void mostraErrore(
            HttpServletRequest request,
            HttpServletResponse response,
            String messaggio)
            throws ServletException, IOException {

        request.setAttribute("errore", messaggio);

        request.getRequestDispatcher("/inserisci_libro.jsp")
               .forward(request, response);
    }
}