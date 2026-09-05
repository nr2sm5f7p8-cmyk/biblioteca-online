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

        Integer idLibro =
                leggiIdLibro(request.getParameter("id"));

        if (idLibro == null) {
            vaiAllaLista(request, response);
            return;
        }

        caricaPaginaModifica(request, response, idLibro);
    }

    private void caricaPaginaModifica(
            HttpServletRequest request,
            HttpServletResponse response,
            int idLibro)
            throws ServletException, IOException {

        try {
            Libro libro = libroDAO.trovaPerId(idLibro);

            if (libro == null) {
                vaiAllaLista(request, response);
                return;
            }

            mostraPaginaModifica(request, response, libro);

        } catch (SQLException e) {
            getServletContext().log(
                    "Errore durante il caricamento del libro",
                    e);

            vaiAllaLista(request, response);
        }
    }

    private void mostraPaginaModifica(
            HttpServletRequest request,
            HttpServletResponse response,
            Libro libro)
            throws ServletException, IOException {

        request.setAttribute("libro", libro);

        request.getRequestDispatcher(
                "/modifica_libro.jsp")
                .forward(request, response);
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

        eseguiModifica(request, response);
    }

    private void eseguiModifica(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Libro libro = creaLibroDaForm(request);

            if (!libroDAO.aggiorna(libro)) {
                mostraErrore(
                        request,
                        response,
                        "Modifica del libro non riuscita.");
                return;
            }

            vaiAllaLista(request, response);

        } catch (SQLException e) {
            gestisciErroreModifica(request, response, e);
        }
    }

    private void gestisciErroreModifica(
            HttpServletRequest request,
            HttpServletResponse response,
            SQLException e)
            throws ServletException, IOException {

        getServletContext().log(
                "Errore durante la modifica del libro",
                e);

        mostraErrore(
                request,
                response,
                "Si e verificato un errore durante la modifica.");
    }

    private String validaInput(
            HttpServletRequest request) {

        Integer idLibro =
                leggiIdLibro(request.getParameter("idLibro"));

        if (idLibro == null) {
            return "Libro non valido.";
        }

        String titolo = request.getParameter("titolo");
        String autore = request.getParameter("autore");
        String descrizione =
                request.getParameter("descrizione");

        String errore =
                validaCampi(titolo, autore, descrizione);

        if (errore != null) {
            return errore;
        }

        return validaAnno(
                request.getParameter("annoPubblicazione"));
    }

    private String validaCampi(
            String titolo,
            String autore,
            String descrizione) {

        if (titolo == null || titolo.isBlank()) {
            return "Il titolo e obbligatorio.";
        }

        if (autore == null || autore.isBlank()) {
            return "L'autore e obbligatorio.";
        }

        if (descrizione != null
                && descrizione.length() > 1000) {

            return "La descrizione non puo superare 1000 caratteri.";
        }

        return null;
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
                        request.getParameter("idLibro")));

        impostaDatiPrincipali(libro, request);
        impostaDatiAggiuntivi(libro, request);

        return libro;
    }

    private void impostaDatiPrincipali(
            Libro libro,
            HttpServletRequest request) {

        libro.setTitolo(
                request.getParameter("titolo").trim());

        libro.setAutore(
                request.getParameter("autore").trim());

        libro.setIsbn(
                pulisci(request.getParameter("isbn")));

        libro.setAnnoPubblicazione(
                leggiAnno(
                        request.getParameter("annoPubblicazione")));
    }

    private void impostaDatiAggiuntivi(
            Libro libro,
            HttpServletRequest request) {

        libro.setGenere(
                pulisci(request.getParameter("genere")));

        libro.setDescrizione(
                pulisci(request.getParameter("descrizione")));

        libro.setDisponibile(
                request.getParameter("disponibile") != null);
    }

    private Integer leggiIdLibro(String valore) {

        if (valore == null || valore.isBlank()) {
            return null;
        }

        try {
            int id = Integer.parseInt(valore);

            return id > 0
                    ? id
                    : null;

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
                        session.getAttribute("autenticato"));
    }

    private void mostraErrore(
            HttpServletRequest request,
            HttpServletResponse response,
            String messaggio)
            throws ServletException, IOException {

        request.setAttribute("errore", messaggio);

        Integer idLibro =
                leggiIdLibro(request.getParameter("idLibro"));

        recuperaLibroPerErrore(request, idLibro);

        request.getRequestDispatcher(
                "/modifica_libro.jsp")
                .forward(request, response);
    }

    private void recuperaLibroPerErrore(
            HttpServletRequest request,
            Integer idLibro) {

        if (idLibro == null) {
            return;
        }

        try {
            Libro libro =
                    libroDAO.trovaPerId(idLibro);

            request.setAttribute("libro", libro);

        } catch (SQLException e) {
            getServletContext().log(
                    "Errore durante il recupero del libro",
                    e);
        }
    }

    private void vaiAlLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp");
    }

    private void vaiAllaLista(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/libri");
    }
}