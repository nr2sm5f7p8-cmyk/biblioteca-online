<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ page import="java.time.Year" %>
<%@ page import="it.biblioteca.model.Libro" %>

<%!
    private String escapeHtml(Object valore) {

        if (valore == null) {
            return "";
        }

        return String.valueOf(valore)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<%
    if (!Boolean.TRUE.equals(
            session.getAttribute("autenticato"))) {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp"
        );
        return;
    }

    Libro libro =
            (Libro) request.getAttribute("libro");

    if (libro == null) {

        response.sendRedirect(
                request.getContextPath() + "/libri"
        );
        return;
    }

    String contextPath = request.getContextPath();

    boolean post =
            "POST".equalsIgnoreCase(request.getMethod());

    String titolo = post
            ? request.getParameter("titolo")
            : libro.getTitolo();

    String autore = post
            ? request.getParameter("autore")
            : libro.getAutore();

    String isbn = post
            ? request.getParameter("isbn")
            : libro.getIsbn();

    String genere = post
            ? request.getParameter("genere")
            : libro.getGenere();

    String descrizione = post
            ? request.getParameter("descrizione")
            : libro.getDescrizione();

    String annoPubblicazione;

    if (post) {

        annoPubblicazione =
                request.getParameter("annoPubblicazione");

    } else {

        annoPubblicazione =
                libro.getAnnoPubblicazione() != null
                ? String.valueOf(libro.getAnnoPubblicazione())
                : "";
    }

    boolean disponibileChecked = post
            ? request.getParameter("disponibile") != null
            : libro.isDisponibile();

    int annoMassimo =
            Year.now().getValue() + 1;

    Object errore =
            request.getAttribute("errore");
%>

<!DOCTYPE html>
<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        Modifica Libro - Biblioteca Online
    </title>

</head>

<body>

    <h1>Modifica libro</h1>

    <% if (errore != null) { %>

        <p style="color: red;">
            <%= escapeHtml(errore) %>
        </p>

    <% } %>

    <form
        action="<%= contextPath %>/modifica-libro"
        method="post">

        <input
            type="hidden"
            name="idLibro"
            value="<%= libro.getIdLibro() %>">

        <label for="titolo">
            Titolo:
        </label>

        <input
            type="text"
            id="titolo"
            name="titolo"
            value="<%= escapeHtml(titolo) %>"
            maxlength="255"
            required>

        <br><br>

        <label for="autore">
            Autore:
        </label>

        <input
            type="text"
            id="autore"
            name="autore"
            value="<%= escapeHtml(autore) %>"
            maxlength="255"
            required>

        <br><br>

        <label for="isbn">
            ISBN:
        </label>

        <input
            type="text"
            id="isbn"
            name="isbn"
            value="<%= escapeHtml(isbn) %>"
            maxlength="20">

        <br><br>

        <label for="annoPubblicazione">
            Anno pubblicazione:
        </label>

        <input
            type="number"
            id="annoPubblicazione"
            name="annoPubblicazione"
            min="1"
            max="<%= annoMassimo %>"
            value="<%= escapeHtml(annoPubblicazione) %>">

        <br><br>

        <label for="genere">
            Genere:
        </label>

        <input
            type="text"
            id="genere"
            name="genere"
            value="<%= escapeHtml(genere) %>"
            maxlength="100">

        <br><br>

        <label for="descrizione">
            Descrizione:
        </label>

        <br>

        <textarea
            id="descrizione"
            name="descrizione"
            maxlength="1000"
            rows="6"
            cols="50"><%= escapeHtml(descrizione) %></textarea>

        <p>
            Massimo 1000 caratteri.
        </p>

        <label for="disponibile">
            Disponibile:
        </label>

        <input
            type="checkbox"
            id="disponibile"
            name="disponibile"
            value="true"
            <%= disponibileChecked ? "checked" : "" %>>

        <br><br>

        <button type="submit">
            Salva modifiche
        </button>

    </form>

    <br>

    <a href="<%= contextPath %>/libri">
        Torna alla lista libri
    </a>

</body>
</html>