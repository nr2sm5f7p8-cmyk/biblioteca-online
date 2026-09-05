package it.biblioteca.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Year;

import it.biblioteca.dao.LibroDAO;
import it.biblioteca.model.Libro;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/modifica-libro")
public class ModificaLibroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!utenteAutenticato(request)) {
            vaiAlLogin(request, response);
            return;
        }

        Integer idLibro = leggiIdLibro(
                request.getParameter("id")
        );

        if (idLibro == null) {
            vaiAllaLista(request, response);
            return;
        }

        try {
            Libro libro = libroDAO.trovaPerId(idLibro);

            if (libro == null) {
                vaiAllaLista(request, response);
                return;
            }

            request.setAttribute("libro", libro);

            request.getRequestDispatcher("/modifica_libro.jsp")
                   .forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Errore durante il caricamento del libro",
                    e
            );

            vaiAllaLista(request, response);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!utenteAutenticato(request)) {
            vaiAlLogin(request, response);
            return;
        }

        String errore = validaInput(request);

        if (errore != null) {
            mostraErrore(request, response, errore);
            return;
        }

        try {
            Libro libro = creaLibroDaForm(request);

            boolean aggiornato = libroDAO.aggiorna(libro);

            if (!aggiornato) {
                mostraErrore(
                        request,
                        response,
                        "Modifica del libro non riuscita."
                );
                return;
            }

            vaiAllaLista(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Errore durante la modifica del libro",
                    e
            );

            mostraErrore(
                    request,
                    response,
                    "Si e verificato un errore durante la modifica."
            );
        }
    }

    private String validaInput(HttpServletRequest request) {

        if (leggiIdLibro(
                request.getParameter("idLibro")) == null) {
            return "Libro non valido.";
        }

        String titolo = request.getParameter("titolo");
        String autore = request.getParameter("autore");
        String anno = request.getParameter("annoPubblicazione");

        if (titolo == null || titolo.isBlank()) {
            return "Il titolo e obbligatorio.";
        }

        if (autore == null || autore.isBlank()) {
            return "L'autore e obbligatorio.";
        }

        return validaAnno(anno);
    }

    private String validaAnno(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        try {
            int anno = Integer.parseInt(valore);
            int annoMassimo = Year.now().getValue() + 1;

            if (anno <= 0 || anno > annoMassimo) {
                return "Anno di pubblicazione non valido.";
            }

        } catch (NumberFormatException e) {
            return "L'anno di pubblicazione deve essere un numero.";
        }

        return null;
    }

    private Libro creaLibroDaForm(
            HttpServletRequest request) {

        Libro libro = new Libro();

        libro.setIdLibro(
                Integer.parseInt(
                        request.getParameter("idLibro")
                )
        );

        libro.setTitolo(
                request.getParameter("titolo").trim()
        );

        libro.setAutore(
                request.getParameter("autore").trim()
        );

        libro.setIsbn(
                pulisci(request.getParameter("isbn"))
        );

        libro.setGenere(
                pulisci(request.getParameter("genere"))
        );

        libro.setAnnoPubblicazione(
                leggiAnno(
                        request.getParameter("annoPubblicazione")
                )
        );

        libro.setDisponibile(
                request.getParameter("disponibile") != null
        );

        return libro;
    }

    private Integer leggiIdLibro(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        try {
            int id = Integer.parseInt(valore);
            return id > 0 ? id : null;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer leggiAnno(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        return Integer.valueOf(valore);
    }

    private String pulisci(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        return valore.trim();
    }

    private boolean utenteAutenticato(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        return session != null
                && Boolean.TRUE.equals(
                        session.getAttribute("autenticato")
                );
    }

    private void mostraErrore(
            HttpServletRequest request,
            HttpServletResponse response,
            String messaggio)
            throws ServletException, IOException {

        request.setAttribute("errore", messaggio);

        Integer idLibro = leggiIdLibro(
                request.getParameter("idLibro")
        );

        if (idLibro != null) {
            try {
                Libro libro = libroDAO.trovaPerId(idLibro);
                request.setAttribute("libro", libro);
            } catch (SQLException e) {
                getServletContext().log(
                        "Errore durante il recupero del libro",
                        e
                );
            }
        }

        request.getRequestDispatcher("/modifica_libro.jsp")
               .forward(request, response);
    }

    private void vaiAlLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp"
        );
    }

    private void vaiAllaLista(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/libri"
        );
    }
}