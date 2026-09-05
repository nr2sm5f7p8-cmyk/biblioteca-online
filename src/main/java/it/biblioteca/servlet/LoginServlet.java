package it.biblioteca.servlet;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.mindrot.jbcrypt.BCrypt;

import it.biblioteca.dao.UtenteDAO;
import it.biblioteca.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final int DURATA_OTP_MINUTI = 5;
    private static final int NUMERO_VALORI_OTP = 1_000_000;

    private final UtenteDAO utenteDAO = new UtenteDAO();
    private final SecureRandom random = new SecureRandom();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!inputValido(username, password)) {
            mostraErroreLogin(request, response);
            return;
        }

        username = username.trim();

        try {
            Utente utente = utenteDAO.trovaPerUsername(username);

            if (!credenzialiValide(utente, password)) {
                mostraErroreLogin(request, response);
                return;
            }

            preparaOtp(utente, request);

            response.sendRedirect(
                    request.getContextPath() + "/otp.jsp"
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Errore database durante il login",
                    e
            );

            mostraErroreSistema(request, response);
        }
    }

    private boolean inputValido(String username, String password) {

        return username != null
                && !username.isBlank()
                && password != null
                && !password.isBlank();
    }

    private boolean credenzialiValide(
            Utente utente,
            String password) {

        if (utente == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(
                    password,
                    utente.getPasswordHash()
            );
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void preparaOtp(
            Utente utente,
            HttpServletRequest request) throws SQLException {

        String otp = generaOtp();
        String otpHash = BCrypt.hashpw(
                otp,
                BCrypt.gensalt()
        );

        LocalDateTime scadenza = LocalDateTime.now()
                .plusMinutes(DURATA_OTP_MINUTI);

        utenteDAO.salvaOtp(
                utente.getIdUtente(),
                otpHash,
                scadenza
        );

        creaSessioneOtp(request, utente.getIdUtente());

        getServletContext().log(
                "OTP DI TEST PER "
                + utente.getUsername()
                + ": "
                + otp
        );
    }

    private void creaSessioneOtp(
            HttpServletRequest request,
            int idUtente) {

        HttpSession vecchiaSessione =
                request.getSession(false);

        if (vecchiaSessione != null) {
            vecchiaSessione.invalidate();
        }

        HttpSession nuovaSessione =
                request.getSession(true);

        nuovaSessione.setAttribute(
                "utenteOtpId",
                idUtente
        );
    }

    private String generaOtp() {

        int numero = random.nextInt(NUMERO_VALORI_OTP);

        return String.format("%06d", numero);
    }

    private void mostraErroreLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(
                "errore",
                "Username o password non corretti."
        );

        request.getRequestDispatcher("/login.jsp")
               .forward(request, response);
    }

    private void mostraErroreSistema(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(
                "errore",
                "Si e verificato un errore. Riprova piu tardi."
        );

        request.getRequestDispatcher("/login.jsp")
               .forward(request, response);
    }
}