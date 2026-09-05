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

	private final UtenteDAO utenteDAO = new UtenteDAO();
	private final SecureRandom random = new SecureRandom();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		try {
			Utente utente = utenteDAO.trovaPerUsername(username);

			if (!credenzialiValide(utente, password)) {
				mostraErrore(request, response);
				return;
			}

			preparaOtp(utente, request);

			response.sendRedirect("otp.jsp");

		} catch (SQLException e) {
			throw new ServletException("Errore durante il login", e);
		}
	}

	private boolean credenzialiValide(Utente utente, String password) {

		return utente != null && BCrypt.checkpw(password, utente.getPasswordHash());
	}

	private void preparaOtp(Utente utente, HttpServletRequest request) throws SQLException {

		String otp = generaOtp();

		String otpHash = BCrypt.hashpw(otp, BCrypt.gensalt());

		LocalDateTime scadenza = LocalDateTime.now().plusMinutes(5);

		utenteDAO.salvaOtp(utente.getIdUtente(), otpHash, scadenza);

		HttpSession session = request.getSession();

		session.setAttribute("utenteOtpId", utente.getIdUtente());

		System.out.println("OTP DI TEST PER " + utente.getUsername() + ": " + otp);
	}

	private String generaOtp() {

		int numero = random.nextInt(1_000_000);

		return String.format("%06d", numero);
	}

	private void mostraErrore(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setAttribute("errore", "Username o password non corretti.");

		request.getRequestDispatcher("/login.jsp").forward(request, response);
	}
}