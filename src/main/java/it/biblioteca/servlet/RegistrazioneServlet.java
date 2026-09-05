package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import it.biblioteca.dao.ComunitaDAO;
import it.biblioteca.dao.UtenteDAO;
import it.biblioteca.model.Comunita;
import it.biblioteca.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/registrazione")
public class RegistrazioneServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int RUOLO_UTENTE = 2;

    private final UtenteDAO utenteDAO = new UtenteDAO();
    private final ComunitaDAO comunitaDAO = new ComunitaDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        mostraPaginaRegistrazione(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String errore = validaInput(request);

        if (errore != null) {
            mostraErrore(request, response, errore);
            return;
        }

        eseguiRegistrazione(request, response);
    }

    private void eseguiRegistrazione(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            registraUtente(request, response);
        } catch (SQLException e) {
            gestisciErroreDatabase(request, response, e);
        }
    }

    private void registraUtente(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String username = parametroPulito(request, "username");
        String email = parametroPulito(request, "email");

        if (utenteDAO.esisteUsernameOEmail(username, email)) {
            mostraErrore(
                    request,
                    response,
                    "Username o email gia utilizzati.");
            return;
        }

        salvaUtente(request, response);
    }

    private void salvaUtente(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        Utente utente = creaUtente(request);
        boolean inserito = utenteDAO.inserisci(utente);

        if (!inserito) {
            mostraErrore(
                    request,
                    response,
                    "Registrazione non riuscita.");
            return;
        }

        mostraSuccesso(request, response);
    }

    private Utente creaUtente(
            HttpServletRequest request) {

        String password = request.getParameter("password");
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        return new Utente(
                parametroPulito(request, "nome"),
                parametroPulito(request, "cognome"),
                parametroPulito(request, "username"),
                parametroPulito(request, "email"),
                parametroOpzionale(request, "telefono"),
                hash,
                RUOLO_UTENTE,
                leggiIdComunita(request),
                parametroOpzionale(request, "generiPreferiti"));
    }

    private String validaInput(
            HttpServletRequest request) {

        if (campiObbligatoriMancanti(request)) {
            return "Nome, cognome, username, email, telefono e password sono obbligatori.";
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (!emailValida(email)) {
            return "Inserisci un indirizzo email valido.";
        }

        if (password.length() < 8 || password.length() > 72) {
            return "La password deve contenere da 8 a 72 caratteri.";
        }

        return validaComunita(request);
    }

    private boolean campiObbligatoriMancanti(
            HttpServletRequest request) {

        return campoVuoto(request.getParameter("nome"))
                || campoVuoto(request.getParameter("cognome"))
                || campoVuoto(request.getParameter("username"))
                || campoVuoto(request.getParameter("email"))
                || campoVuoto(request.getParameter("telefono"))
                || campoVuoto(request.getParameter("password"));
    }

    private String validaComunita(
            HttpServletRequest request) {

        String valore = request.getParameter("idComunita");

        if (valore == null || valore.isBlank()) {
            return null;
        }

        try {
            int id = Integer.parseInt(valore);
            return id > 0 ? null : "Comunita non valida.";
        } catch (NumberFormatException e) {
            return "Comunita non valida.";
        }
    }

    private Integer leggiIdComunita(
            HttpServletRequest request) {

        String valore = request.getParameter("idComunita");

        if (valore == null || valore.isBlank()) {
            return null;
        }

        return Integer.valueOf(valore);
    }

    private boolean emailValida(String email) {
        return email.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private boolean campoVuoto(String valore) {
        return valore == null || valore.isBlank();
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

    private void mostraPaginaRegistrazione(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            caricaComunita(request);
            inoltraAllaPagina(request, response);
        } catch (SQLException e) {
            throw new ServletException(
                    "Impossibile caricare le comunita.",
                    e);
        }
    }

    private void caricaComunita(
            HttpServletRequest request)
            throws SQLException {

        List<Comunita> comunita =
                comunitaDAO.trovaTutte();

        request.setAttribute("comunita", comunita);
    }

    private void mostraErrore(
            HttpServletRequest request,
            HttpServletResponse response,
            String messaggio)
            throws ServletException, IOException {

        request.setAttribute("errore", messaggio);
        mostraPaginaRegistrazione(request, response);
    }

    private void mostraSuccesso(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(
                "messaggio",
                "Registrazione completata con successo!");

        mostraPaginaRegistrazione(request, response);
    }

    private void inoltraAllaPagina(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/registrazione.jsp")
                .forward(request, response);
    }

    private void gestisciErroreDatabase(
            HttpServletRequest request,
            HttpServletResponse response,
            SQLException e)
            throws ServletException, IOException {

        getServletContext().log(
                "Errore durante la registrazione",
                e);

        mostraErrore(
                request,
                response,
                "Si e verificato un errore durante la registrazione.");
    }
}