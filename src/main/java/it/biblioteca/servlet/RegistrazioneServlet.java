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
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String erroreValidazione = validaInput(request);

        if (erroreValidazione != null) {
            mostraErrore(request, response, erroreValidazione);
            return;
        }

        try {
            registraUtente(request, response);
        } catch (SQLException e) {
            getServletContext().log(
                    "Errore durante la registrazione dell'utente", e
            );

            mostraErrore(
                    request,
                    response,
                    "Si e verificato un errore durante la registrazione."
            );
        }
    }

    private void registraUtente(HttpServletRequest request,
                                HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String username = request.getParameter("username").trim();
        String email = request.getParameter("email").trim();

        if (utenteDAO.esisteUsernameOEmail(username, email)) {
            mostraErrore(
                    request,
                    response,
                    "Username o email gia utilizzati."
            );
            return;
        }

        Utente utente = creaUtente(request);
        boolean inserito = utenteDAO.inserisci(utente);

        if (inserito) {
            request.setAttribute(
                    "messaggio",
                    "Registrazione completata con successo!"
            );
        } else {
            request.setAttribute(
                    "errore",
                    "Registrazione non riuscita."
            );
        }

        request.getRequestDispatcher("/registrazione.jsp")
               .forward(request, response);
    }

    private void mostraErrore(HttpServletRequest request,
                              HttpServletResponse response,
                              String messaggio)
            throws ServletException, IOException {

        request.setAttribute("errore", messaggio);

        request.getRequestDispatcher("/registrazione.jsp")
               .forward(request, response);
    }

    private String validaInput(HttpServletRequest request) {

        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (nome == null || nome.isBlank()
                || cognome == null || cognome.isBlank()
                || username == null || username.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {

            return "Tutti i campi sono obbligatori.";
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return "Inserisci un indirizzo email valido.";
        }

        if (password.length() < 8 || password.length() > 72) {
            return "La password deve contenere da 8 a 72 caratteri.";
        }

        return null;
    }

    private Utente creaUtente(HttpServletRequest request) {

        String nome = request.getParameter("nome").trim();
        String cognome = request.getParameter("cognome").trim();
        String username = request.getParameter("username").trim();
        String email = request.getParameter("email").trim();
        String password = request.getParameter("password");

        String passwordHash = BCrypt.hashpw(
                password,
                BCrypt.gensalt()
        );

        return new Utente(
                nome,
                cognome,
                username,
                email,
                passwordHash,
                RUOLO_UTENTE
        );
    }
}