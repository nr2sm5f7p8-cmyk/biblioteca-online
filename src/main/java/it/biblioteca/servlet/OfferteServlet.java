package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.biblioteca.dao.LibroDAO;
import it.biblioteca.dao.OffertaLibroDAO;
import it.biblioteca.model.Libro;
import it.biblioteca.model.OffertaLibro;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/offerte")
public class OfferteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final OffertaLibroDAO offertaDAO =
            new OffertaLibroDAO();

    private final LibroDAO libroDAO =
            new LibroDAO();

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

        caricaPagina(request, response, idUtente);
    }

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

        gestisciAzione(request, response, idUtente);
    }

    private void caricaPagina(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        try {
            caricaDati(request, idUtente);

            request.getRequestDispatcher(
                    "/offerte.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            gestisciErroreDatabase(e);
        }
    }

    private void caricaDati(
            HttpServletRequest request,
            int idUtente)
            throws SQLException {

        List<Libro> mieiLibri =
                libroDAO.trovaPerUtente(idUtente);

        List<OffertaLibro> offerteAttive =
                offertaDAO.trovaAttive();

        List<OffertaLibro> mieOfferte =
                offertaDAO.trovaPerProprietario(idUtente);

        request.setAttribute("mieiLibri", mieiLibri);
        request.setAttribute("offerteAttive", offerteAttive);
        request.setAttribute("mieOfferte", mieOfferte);
    }

    private void gestisciAzione(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        String azione =
                request.getParameter("azione");

        if ("disattiva".equalsIgnoreCase(azione)) {
            disattivaOfferta(request, response, idUtente);
            return;
        }

        creaOfferta(request, response, idUtente);
    }

    private void creaOfferta(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        String errore =
                validaNuovaOfferta(request);

        if (errore != null) {
            mostraErrore(
                    request,
                    response,
                    idUtente,
                    errore);
            return;
        }

        eseguiCreazione(request, response, idUtente);
    }

    private void eseguiCreazione(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        try {
            boolean inserita =
                    inserisciOfferta(request, idUtente);

            if (!inserita) {
                mostraErrore(
                        request,
                        response,
                        idUtente,
                        "Offerta non inserita. Il libro potrebbe non appartenerti oppure avere gia un'offerta attiva.");
                return;
            }

            vaiAlleOfferte(request, response);

        } catch (SQLException e) {
            gestisciErroreDatabase(e);
        }
    }

    private boolean inserisciOfferta(
            HttpServletRequest request,
            int idUtente)
            throws SQLException {

        int idLibro =
                Integer.parseInt(
                        request.getParameter("idLibro"));

        String tipo =
                request.getParameter("tipoOfferta");

        String condizioni =
                pulisci(
                        request.getParameter("condizioni"));

        return offertaDAO.inserisci(
                idLibro,
                idUtente,
                tipo,
                condizioni);
    }

    private void disattivaOfferta(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        Integer idOfferta =
                leggiId(
                        request.getParameter("idOfferta"));

        if (idOfferta == null) {
            vaiAlleOfferte(request, response);
            return;
        }

        eseguiDisattivazione(
                request,
                response,
                idUtente,
                idOfferta);
    }

    private void eseguiDisattivazione(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente,
            int idOfferta)
            throws ServletException, IOException {

        try {
            offertaDAO.disattiva(
                    idOfferta,
                    idUtente);

            vaiAlleOfferte(request, response);

        } catch (SQLException e) {
            gestisciErroreDatabase(e);
        }
    }

    private String validaNuovaOfferta(
            HttpServletRequest request) {

        Integer idLibro =
                leggiId(request.getParameter("idLibro"));

        if (idLibro == null) {
            return "Libro non valido.";
        }

        String tipo =
                request.getParameter("tipoOfferta");

        if (!tipoValido(tipo)) {
            return "Tipo di offerta non valido.";
        }

        String condizioni =
                request.getParameter("condizioni");

        if (condizioni != null
                && condizioni.length() > 1000) {

            return "Le condizioni non possono superare 1000 caratteri.";
        }

        return null;
    }

    private boolean tipoValido(String tipo) {

        return "PRESTITO".equals(tipo)
                || "SCAMBIO".equals(tipo)
                || "ENTRAMBI".equals(tipo);
    }

    private Integer leggiId(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        try {
            int id = Integer.parseInt(valore);

            return id > 0
                    ? id
                    : null;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String pulisci(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        return valore.trim();
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

    private void mostraErrore(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente,
            String errore)
            throws ServletException, IOException {

        request.setAttribute("errore", errore);

        caricaPagina(
                request,
                response,
                idUtente);
    }

    private void gestisciErroreDatabase(
            SQLException e)
            throws ServletException {

        getServletContext().log(
                "Errore gestione offerte libri",
                e);

        throw new ServletException(
                "Errore durante la gestione delle offerte.",
                e);
    }

    private void vaiAlLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp");
    }

    private void vaiAlleOfferte(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/offerte");
    }
}