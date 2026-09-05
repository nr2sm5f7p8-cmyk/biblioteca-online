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

        Integer idUtente =
                recuperaIdUtente(request.getSession(false));

        if (idUtente == null) {
            vaiAlLogin(request, response);
            return;
        }

        String errore = validaInput(request);

        if (errore != null) {
            mostraErrore(request, response, errore);
            return;
        }

        eseguiInserimento(request, response, idUtente);
    }

    private void eseguiInserimento(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        try {
            inserisciLibro(request, response, idUtente);
        } catch (SQLException e) {
            getServletContext().log(
                    "Errore durante l'inserimento del libro",
                    e);

            mostraErrore(
                    request,
                    response,
                    "Si e verificato un errore durante l'inserimento.");
        }
    }

    private void inserisciLibro(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws SQLException, IOException, ServletException {

        Libro libro = creaLibro(request, idUtente);

        if (!libroDAO.inserisci(libro)) {
            mostraErrore(
                    request,
                    response,
                    "Inserimento del libro non riuscito.");
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/libri");
    }

    private String validaInput(
            HttpServletRequest request) {

        String titolo = request.getParameter("titolo");
        String autore = request.getParameter("autore");
        String anno = request.getParameter("annoPubblicazione");
        String descrizione = request.getParameter("descrizione");

        if (titolo == null || titolo.isBlank()) {
            return "Il titolo e obbligatorio.";
        }

        if (autore == null || autore.isBlank()) {
            return "L'autore e obbligatorio.";
        }

        if (descrizione != null && descrizione.length() > 1000) {
            return "La descrizione non puo superare 1000 caratteri.";
        }

        return validaAnno(anno);
    }

    private String validaAnno(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        try {
            int anno = Integer.parseInt(valore);
            int massimo = Year.now().getValue() + 1;

            if (anno <= 0 || anno > massimo) {
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

        Libro libro = new Libro();

        impostaDatiPrincipali(libro, request);
        impostaDatiAggiuntivi(libro, request, idUtente);

        return libro;
    }

    private void impostaDatiPrincipali(
            Libro libro,
            HttpServletRequest request) {

        libro.setTitolo(
                request.getParameter("titolo").trim());

        libro.setAutore(
                request.getParameter("autore").trim());

        libro.setIsbn(
                pulisci(request.getParameter("isbn")));

        libro.setAnnoPubblicazione(
                leggiAnno(
                        request.getParameter("annoPubblicazione")));
    }

    private void impostaDatiAggiuntivi(
            Libro libro,
            HttpServletRequest request,
            int idUtente) {

        libro.setGenere(
                pulisci(request.getParameter("genere")));

        libro.setDescrizione(
                pulisci(request.getParameter("descrizione")));

        libro.setDisponibile(
                request.getParameter("disponibile") != null);

        libro.setIdUtenteInserimento(idUtente);
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

        Object valore = session.getAttribute("utenteId");

        return valore instanceof Integer
                ? (Integer) valore
                : null;
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

    private void vaiAlLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp");
    }

    private void mostraErrore(
            HttpServletRequest request,
            HttpServletResponse response,
            String messaggio)
            throws ServletException, IOException {

        request.setAttribute("errore", messaggio);

        request.getRequestDispatcher(
                "/inserisci_libro.jsp")
                .forward(request, response);
    }
}