package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.biblioteca.dao.ComunitaDAO;
import it.biblioteca.dao.UtenteDAO;
import it.biblioteca.model.Comunita;
import it.biblioteca.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/profilo")
public class ProfiloServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UtenteDAO utenteDAO = new UtenteDAO();
    private final ComunitaDAO comunitaDAO = new ComunitaDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Integer idUtente = recuperaIdUtente(request);

        if (idUtente == null) {
            vaiAlLogin(request, response);
            return;
        }

        mostraProfilo(request, response, idUtente);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Integer idUtente = recuperaIdUtente(request);

        if (idUtente == null) {
            vaiAlLogin(request, response);
            return;
        }

        String errore = validaInput(request);

        if (errore != null) {
            mostraErrore(request, response, idUtente, errore);
            return;
        }

        aggiornaProfilo(request, response, idUtente);
    }

    private void mostraProfilo(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        try {
            Utente utente = utenteDAO.trovaPerId(idUtente);
            caricaPagina(request, response, utente);
        } catch (SQLException e) {
            gestisciErrore(request, response, e);
        }
    }

    private void aggiornaProfilo(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        try {
            Utente utente = creaUtenteDaForm(request, idUtente);

            if (!utenteDAO.aggiornaProfilo(utente)) {
                mostraErrore(
                        request,
                        response,
                        idUtente,
                        "Aggiornamento del profilo non riuscito.");
                return;
            }

            aggiornaSessione(request, utente);
            mostraSuccesso(request, response, idUtente);

        } catch (SQLException e) {
            gestisciErrore(request, response, e);
        }
    }

    private Utente creaUtenteDaForm(
            HttpServletRequest request,
            int idUtente) {

        Utente utente = new Utente();

        utente.setIdUtente(idUtente);
        utente.setNome(parametroPulito(request, "nome"));
        utente.setCognome(parametroPulito(request, "cognome"));
        utente.setEmail(parametroPulito(request, "email"));
        utente.setTelefono(parametroOpzionale(request, "telefono"));
        utente.setIdComunita(leggiIdComunita(request));
        utente.setGeneriPreferiti(
                parametroOpzionale(request, "generiPreferiti"));

        return utente;
    }

    private String validaInput(
            HttpServletRequest request) {

        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");

        if (campoVuoto(nome)
                || campoVuoto(cognome)
                || campoVuoto(email)) {
            return "Nome, cognome ed email sono obbligatori.";
        }

        if (!email.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return "Inserisci un indirizzo email valido.";
        }

        return validaComunita(request);
    }

    private String validaComunita(
            HttpServletRequest request) {

        String valore = request.getParameter("idComunita");

        if (campoVuoto(valore)) {
            return null;
        }

        try {
            return Integer.parseInt(valore) > 0
                    ? null
                    : "Comunita non valida.";
        } catch (NumberFormatException e) {
            return "Comunita non valida.";
        }
    }

    private Integer recuperaIdUtente(
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null
                || !Boolean.TRUE.equals(
                        session.getAttribute("autenticato"))) {
            return null;
        }

        Object valore = session.getAttribute("utenteId");

        return valore instanceof Integer
                ? (Integer) valore
                : null;
    }

    private Integer leggiIdComunita(
            HttpServletRequest request) {

        String valore = request.getParameter("idComunita");

        if (campoVuoto(valore)) {
            return null;
        }

        return Integer.valueOf(valore);
    }

    private void caricaPagina(
            HttpServletRequest request,
            HttpServletResponse response,
            Utente utente)
            throws SQLException, ServletException, IOException {

        List<Comunita> comunita = comunitaDAO.trovaTutte();

        request.setAttribute("utente", utente);
        request.setAttribute("comunita", comunita);

        request.getRequestDispatcher("/profilo.jsp")
                .forward(request, response);
    }

    private void mostraErrore(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente,
            String messaggio)
            throws ServletException, IOException {

        request.setAttribute("errore", messaggio);
        mostraProfilo(request, response, idUtente);
    }

    private void mostraSuccesso(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        request.setAttribute(
                "messaggio",
                "Profilo aggiornato con successo.");

        mostraProfilo(request, response, idUtente);
    }

    private void aggiornaSessione(
            HttpServletRequest request,
            Utente utente) {

        HttpSession session = request.getSession(false);

        session.setAttribute("nome", utente.getNome());
        session.setAttribute("cognome", utente.getCognome());
    }

    private void gestisciErrore(
            HttpServletRequest request,
            HttpServletResponse response,
            SQLException e)
            throws ServletException, IOException {

        getServletContext().log(
                "Errore durante la gestione del profilo",
                e);

        throw new ServletException(
                "Impossibile gestire il profilo utente.",
                e);
    }

    private String parametroPulito(
            HttpServletRequest request,
            String nome) {

        return request.getParameter(nome).trim();
    }

    private String parametroOpzionale(
            HttpServletRequest request,
            String nome) {

        String valore = request.getParameter(nome);

        return campoVuoto(valore)
                ? null
                : valore.trim();
    }

    private boolean campoVuoto(String valore) {
        return valore == null || valore.isBlank();
    }

    private void vaiAlLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp");
    }
}