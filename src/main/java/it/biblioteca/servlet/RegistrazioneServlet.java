package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import it.biblioteca.dao.UtenteDAO;
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

        try {
            registraUtente(request, response);
        } catch (SQLException e) {
            gestisciErroreRegistrazione(request, response, e);
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

        impostaEsitoRegistrazione(request, inserito);

        request.getRequestDispatcher(
                "/registrazione.jsp")
                .forward(request, response);
    }

    private void impostaEsitoRegistrazione(
            HttpServletRequest request,
            boolean inserito) {

        if (inserito) {
            request.setAttribute(
                    "messaggio",
                    "Registrazione completata con successo!");
            return;
        }

        request.setAttribute(
                "errore",
                "Registrazione non riuscita.");
    }

    private void gestisciErroreRegistrazione(
            HttpServletRequest request,
            HttpServletResponse response,
            SQLException e)
            throws ServletException, IOException {

        getServletContext().log(
                "Errore durante la registrazione dell'utente",
                e);

        mostraErrore(
                request,
                response,
                "Si e verificato un errore durante la registrazione.");
    }

    private void mostraErrore(
            HttpServletRequest request,
            HttpServletResponse response,
            String messaggio)
            throws ServletException, IOException {

        request.setAttribute("errore", messaggio);

        request.getRequestDispatcher(
                "/registrazione.jsp")
                .forward(request, response);
    }

    private String validaInput(
            HttpServletRequest request) {

        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (campoVuoto(nome)
                || campoVuoto(cognome)
                || campoVuoto(username)
                || campoVuoto(email)
                || campoVuoto(password)) {
            return "Tutti i campi sono obbligatori.";
        }

        if (!emailValida(email)) {
            return "Inserisci un indirizzo email valido.";
        }

        return validaPassword(password);
    }

    private boolean campoVuoto(String valore) {
        return valore == null || valore.isBlank();
    }

    private boolean emailValida(String email) {
        return email.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private String validaPassword(String password) {

        if (password.length() < 8
                || password.length() > 72) {
            return "La password deve contenere da 8 a 72 caratteri.";
        }

        return null;
    }

    private Utente creaUtente(
            HttpServletRequest request) {

        String password = request.getParameter("password");
        String hash = creaHashPassword(password);

        return new Utente(
                parametroPulito(request, "nome"),
                parametroPulito(request, "cognome"),
                parametroPulito(request, "username"),
                parametroPulito(request, "email"),
                hash,
                RUOLO_UTENTE);
    }

    private String creaHashPassword(String password) {
        return BCrypt.hashpw(
                password,
                BCrypt.gensalt());
    }

    private String parametroPulito(
            HttpServletRequest request,
            String nomeParametro) {

        return request.getParameter(nomeParametro).trim();
    }
}