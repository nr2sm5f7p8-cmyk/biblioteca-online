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
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("utenteOtpId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String otp = request.getParameter("otp");
        int idUtente = (int) session.getAttribute("utenteOtpId");

        try {
            if (!utenteDAO.verificaOtp(idUtente, otp)) {
                mostraErrore(request, response);
                return;
            }

            Utente utente = utenteDAO.trovaPerId(idUtente);

            if (utente == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            completaLogin(session, utente);

            response.sendRedirect("home.jsp");

        } catch (SQLException e) {
            throw new ServletException(
                    "Errore durante la verifica OTP", e);
        }
    }

    private void completaLogin(HttpSession session, Utente utente) {

        session.removeAttribute("utenteOtpId");

        session.setAttribute("autenticato", true);
        session.setAttribute("utenteId", utente.getIdUtente());
        session.setAttribute("nome", utente.getNome());
        session.setAttribute("cognome", utente.getCognome());
        session.setAttribute("username", utente.getUsername());
        session.setAttribute("idRuolo", utente.getIdRuolo());
    }

    private void mostraErrore(HttpServletRequest request,
                              HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(
                "errore",
                "Codice OTP errato o scaduto."
        );

        request.getRequestDispatcher("/otp.jsp")
               .forward(request, response);
    }
}