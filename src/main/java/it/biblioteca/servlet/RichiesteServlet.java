package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.biblioteca.dao.RichiestaLibroDAO;
import it.biblioteca.model.RichiestaLibro;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/richieste")
public class RichiesteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final RichiestaLibroDAO richiestaDAO =
            new RichiestaLibroDAO();

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
                    "/richieste.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            gestisciErroreDatabase(e);
        }
    }

    private void caricaDati(
            HttpServletRequest request,
            int idUtente)
            throws SQLException {

        List<RichiestaLibro> inviate =
                richiestaDAO.trovaInviate(idUtente);

        List<RichiestaLibro> ricevute =
                richiestaDAO.trovaRicevute(idUtente);

        request.setAttribute(
                "richiesteInviate",
                inviate);

        request.setAttribute(
                "richiesteRicevute",
                ricevute);
    }

    private void gestisciAzione(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        String azione =
                request.getParameter("azione");

        if ("crea".equalsIgnoreCase(azione)) {
            creaRichiesta(request, response, idUtente);
            return;
        }

        gestisciCambioStato(
                request,
                response,
                idUtente,
                azione);
    }

    private void creaRichiesta(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        String errore =
                validaNuovaRichiesta(request);

        if (errore != null) {
            mostraErrore(
                    request,
                    response,
                    idUtente,
                    errore);
            return;
        }

        eseguiCreazione(
                request,
                response,
                idUtente);
    }

    private void eseguiCreazione(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        try {
            boolean inserita =
                    inserisciRichiesta(request, idUtente);

            if (!inserita) {
                mostraErrore(
                        request,
                        response,
                        idUtente,
                        "Richiesta non valida o gia presente.");
                return;
            }

            vaiAlleRichieste(request, response);

        } catch (SQLException e) {
            gestisciErroreDatabase(e);
        }
    }

    private boolean inserisciRichiesta(
            HttpServletRequest request,
            int idUtente)
            throws SQLException {

        int idOfferta =
                Integer.parseInt(
                        request.getParameter("idOfferta"));

        String tipo =
                request.getParameter("tipoRichiesta");

        String messaggio =
                request.getParameter(
                        "messaggioModalita").trim();

        return richiestaDAO.inserisci(
                idOfferta,
                idUtente,
                tipo,
                messaggio);
    }

    private void gestisciCambioStato(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente,
            String azione)
            throws ServletException, IOException {

        Integer idRichiesta =
                leggiId(
                        request.getParameter("idRichiesta"));

        if (idRichiesta == null) {
            vaiAlleRichieste(request, response);
            return;
        }

        eseguiCambioStato(
                request,
                response,
                idUtente,
                idRichiesta,
                azione);
    }

    private void eseguiCambioStato(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente,
            int idRichiesta,
            String azione)
            throws ServletException, IOException {

        try {
            applicaAzione(
                    idRichiesta,
                    idUtente,
                    azione);

            vaiAlleRichieste(request, response);

        } catch (SQLException e) {
            gestisciErroreDatabase(e);
        }
    }

    private void applicaAzione(
            int idRichiesta,
            int idUtente,
            String azione)
            throws SQLException {

        if ("accetta".equalsIgnoreCase(azione)) {
            richiestaDAO.rispondi(
                    idRichiesta, idUtente, "ACCETTATA");
        } else if ("rifiuta".equalsIgnoreCase(azione)) {
            richiestaDAO.rispondi(
                    idRichiesta, idUtente, "RIFIUTATA");
        } else if ("annulla".equalsIgnoreCase(azione)) {
            richiestaDAO.annulla(
                    idRichiesta, idUtente);
        } else if ("completa".equalsIgnoreCase(azione)) {
            richiestaDAO.completa(
                    idRichiesta, idUtente);
        }
    }

    private String validaNuovaRichiesta(
            HttpServletRequest request) {

        Integer idOfferta =
                leggiId(request.getParameter("idOfferta"));

        if (idOfferta == null) {
            return "Offerta non valida.";
        }

        String tipo =
                request.getParameter("tipoRichiesta");

        if (!tipoValido(tipo)) {
            return "Tipo di richiesta non valido.";
        }

        return validaMessaggio(
                request.getParameter("messaggioModalita"));
    }

    private String validaMessaggio(
            String messaggio) {

        if (messaggio == null || messaggio.isBlank()) {
            return "Devi indicare la modalita proposta.";
        }

        if (messaggio.length() > 1000) {
            return "Il messaggio non puo superare 1000 caratteri.";
        }

        return null;
    }

    private boolean tipoValido(String tipo) {

        return "PRESTITO".equals(tipo)
                || "SCAMBIO".equals(tipo);
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
                "Errore gestione richieste libri",
                e);

        throw new ServletException(
                "Errore durante la gestione delle richieste.",
                e);
    }

    private void vaiAlLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp");
    }

    private void vaiAlleRichieste(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/richieste");
    }
}