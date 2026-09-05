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
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!inputValido(username, password)) {
            mostraErroreLogin(request, response);
            return;
        }

        eseguiLogin(request, response, username.trim(), password);
    }

    private void eseguiLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            String username,
            String password)
            throws ServletException, IOException {

        try {
            Utente utente = utenteDAO.trovaPerUsername(username);

            if (!credenzialiValide(utente, password)) {
                mostraErroreLogin(request, response);
                return;
            }

            preparaOtp(utente, request);
            vaiAllaVerificaOtp(request, response);

        } catch (SQLException e) {
            gestisciErroreDatabase(request, response, e);
        }
    }

    private boolean inputValido(
            String username,
            String password) {

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
                    utente.getPasswordHash());
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void preparaOtp(
            Utente utente,
            HttpServletRequest request)
            throws SQLException {

        String otp = generaOtp();
        String otpHash = creaHashOtp(otp);

        LocalDateTime scadenza =
                LocalDateTime.now()
                        .plusMinutes(DURATA_OTP_MINUTI);

        utenteDAO.salvaOtp(
                utente.getIdUtente(),
                otpHash,
                scadenza);

        creaSessioneOtp(request, utente.getIdUtente());
        registraOtpDiTest(utente, otp);
    }

    private String creaHashOtp(String otp) {
        return BCrypt.hashpw(
                otp,
                BCrypt.gensalt());
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
                idUtente);
    }

    private void registraOtpDiTest(
            Utente utente,
            String otp) {

        getServletContext().log(
                "OTP DI TEST PER "
                + utente.getUsername()
                + ": "
                + otp);
    }

    private String generaOtp() {
        int numero =
                random.nextInt(NUMERO_VALORI_OTP);

        return String.format("%06d", numero);
    }

    private void vaiAllaVerificaOtp(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/otp.jsp");
    }

    private void gestisciErroreDatabase(
            HttpServletRequest request,
            HttpServletResponse response,
            SQLException e)
            throws ServletException, IOException {

        getServletContext().log(
                "Errore database durante il login",
                e);

        mostraErroreSistema(request, response);
    }

    private void mostraErroreLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        mostraErrore(
                request,
                response,
                "Username o password non corretti.");
    }

    private void mostraErroreSistema(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        mostraErrore(
                request,
                response,
                "Si e verificato un errore. Riprova piu tardi.");
    }

    private void mostraErrore(
            HttpServletRequest request,
            HttpServletResponse response,
            String messaggio)
            throws ServletException, IOException {

        request.setAttribute("errore", messaggio);

        request.getRequestDispatcher("/login.jsp")
                .forward(request, response);
    }
}