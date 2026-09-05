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

        Utente utente = creaUtente(request);

        try {
            boolean inserito = utenteDAO.inserisci(utente);

            if (inserito) {
                request.setAttribute("messaggio",
                        "Registrazione completata con successo!");
            } else {
                request.setAttribute("errore",
                        "Registrazione non riuscita.");
            }

        } catch (SQLException e) {
            request.setAttribute("errore",
                    "Username o email gia utilizzati.");
        }

        request.getRequestDispatcher("/registrazione.jsp")
               .forward(request, response);
    }

    private Utente creaUtente(HttpServletRequest request) {

        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
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
