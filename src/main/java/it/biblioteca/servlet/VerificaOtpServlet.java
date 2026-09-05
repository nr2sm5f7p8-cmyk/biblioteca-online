package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;

import it.biblioteca.dao.UtenteDAO;
import it.biblioteca.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/verifica-otp")
public class VerificaOtpServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UtenteDAO utenteDAO = new UtenteDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session =
                request.getSession(false);

        Integer idUtente =
                recuperaIdUtente(session);

        if (idUtente == null) {
            vaiAlLogin(request, response);
            return;
        }

        String otp =
                request.getParameter("otp");

        if (!otpValido(otp)) {
            mostraErrore(request, response);
            return;
        }

        try {

            verificaECompletaLogin(
                    request,
                    response,
                    session,
                    idUtente,
                    otp
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Errore durante la verifica OTP",
                    e
            );

            mostraErroreSistema(
                    request,
                    response
            );
        }
    }

    private void verificaECompletaLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            int idUtente,
            String otp)
            throws SQLException,
                   ServletException,
                   IOException {

        if (!utenteDAO.verificaOtp(idUtente, otp)) {
            mostraErrore(request, response);
            return;
        }

        Utente utente =
                utenteDAO.trovaPerId(idUtente);

        if (utente == null) {
            vaiAlLogin(request, response);
            return;
        }

        utenteDAO.cancellaOtp(idUtente);

        request.changeSessionId();

        completaLogin(session, utente);

        response.sendRedirect(
                request.getContextPath()
                + "/home.jsp"
        );
    }

    private Integer recuperaIdUtente(
            HttpSession session) {

        if (session == null) {
            return null;
        }

        Object valore =
                session.getAttribute("utenteOtpId");

        if (valore instanceof Integer) {
            return (Integer) valore;
        }

        return null;
    }

    private boolean otpValido(String otp) {

        return otp != null
                && otp.matches("\\d{6}");
    }

    private void completaLogin(
            HttpSession session,
            Utente utente) {

        session.removeAttribute("utenteOtpId");

        session.setAttribute(
                "autenticato",
                true
        );

        session.setAttribute(
                "utenteId",
                utente.getIdUtente()
        );

        session.setAttribute(
                "nome",
                utente.getNome()
        );

        session.setAttribute(
                "cognome",
                utente.getCognome()
        );

        session.setAttribute(
                "username",
                utente.getUsername()
        );

        session.setAttribute(
                "idRuolo",
                utente.getIdRuolo()
        );
    }

    private void mostraErrore(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(
                "errore",
                "Codice OTP errato o scaduto."
        );

        request.getRequestDispatcher("/otp.jsp")
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

        request.getRequestDispatcher("/otp.jsp")
               .forward(request, response);
    }

    private void vaiAlLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/login.jsp"
        );
    }
}