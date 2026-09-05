package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.biblioteca.dao.RecensioneScambioDAO;
import it.biblioteca.model.RecensioneScambio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/recensioni")
public class RecensioniServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final RecensioneScambioDAO recensioneDAO =
            new RecensioneScambioDAO();

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

        gestisciRecensione(
                request,
                response,
                idUtente);
    }

    private void caricaPagina(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        try {
            List<RecensioneScambio> recensioni =
                    recensioneDAO.trovaRicevute(idUtente);

            request.setAttribute(
                    "recensioniRicevute",
                    recensioni);

            request.getRequestDispatcher(
                    "/recensioni.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            gestisciErroreDatabase(e);
        }
    }

    private void gestisciRecensione(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        String errore = validaInput(request);

        if (errore != null) {
            mostraErrore(
                    request,
                    response,
                    idUtente,
                    errore);
            return;
        }

        salvaRecensione(
                request,
                response,
                idUtente);
    }

    private void salvaRecensione(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        int idRichiesta = Integer.parseInt(
                request.getParameter("idRichiesta"));

        int voto = Integer.parseInt(
                request.getParameter("voto"));

        String testo =
                pulisci(request.getParameter("testo"));

        verificaESalva(
                request,
                response,
                idUtente,
                idRichiesta,
                voto,
                testo);
    }

    private void verificaESalva(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente,
            int idRichiesta,
            int voto,
            String testo)
            throws ServletException, IOException {

        try {
            if (recensioneDAO.esiste(idRichiesta, idUtente)) {
                mostraErroreGiaPresente(
                        request, response, idUtente);
                return;
            }

            inserisciRecensione(
                    request, response, idUtente,
                    idRichiesta, voto, testo);

        } catch (SQLException e) {
            gestisciErroreDatabase(e);
        }
    }

    private void mostraErroreGiaPresente(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        mostraErrore(
                request,
                response,
                idUtente,
                "Hai gia recensito questa transazione.");
    }

    private void inserisciRecensione(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente,
            int idRichiesta,
            int voto,
            String testo)
            throws ServletException, IOException, SQLException {

        boolean inserita = recensioneDAO.inserisci(
                idRichiesta,
                idUtente,
                voto,
                testo);

        if (!inserita) {
            mostraErroreInserimento(
                    request, response, idUtente);
            return;
        }

        vaiAlleRecensioni(request, response);
    }

    private void mostraErroreInserimento(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        mostraErrore(
                request,
                response,
                idUtente,
                "Recensione non consentita. "
                + "La transazione deve essere completata.");
    }

    private String validaInput(
            HttpServletRequest request) {

        Integer idRichiesta = leggiIntero(
                request.getParameter("idRichiesta"));

        Integer voto = leggiIntero(
                request.getParameter("voto"));

        if (idRichiesta == null || idRichiesta <= 0) {
            return "Richiesta non valida.";
        }

        if (voto == null || voto < 1 || voto > 5) {
            return "Il voto deve essere compreso tra 1 e 5.";
        }

        return validaTesto(
                request.getParameter("testo"));
    }

    private String validaTesto(String testo) {

        if (testo != null && testo.length() > 1000) {
            return "La recensione non puo superare 1000 caratteri.";
        }

        return null;
    }

    private Integer leggiIntero(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        try {
            return Integer.valueOf(valore);

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
                "Errore gestione recensioni",
                e);

        throw new ServletException(
                "Errore durante la gestione delle recensioni.",
                e);
    }

    private void vaiAlLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp");
    }

    private void vaiAlleRecensioni(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/recensioni");
    }
}