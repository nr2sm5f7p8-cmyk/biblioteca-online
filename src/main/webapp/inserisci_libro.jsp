<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ page import="java.time.Year" %>

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

    String contextPath = request.getContextPath();

    String titolo = request.getParameter("titolo");
    String autore = request.getParameter("autore");
    String isbn = request.getParameter("isbn");

    String annoPubblicazione =
            request.getParameter("annoPubblicazione");

    String genere = request.getParameter("genere");
    String descrizione = request.getParameter("descrizione");

    boolean disponibileChecked =
            !"POST".equalsIgnoreCase(request.getMethod())
            || request.getParameter("disponibile") != null;

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
        Inserisci Libro - Biblioteca Online
    </title>

</head>

<body>

    <h1>Inserisci nuovo libro</h1>

    <% if (errore != null) { %>

        <p style="color: red;">
            <%= escapeHtml(errore) %>
        </p>

    <% } %>

    <form
        action="<%= contextPath %>/inserisci-libro"
        method="post">

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
            cols="50"
            placeholder="Inserisci una breve descrizione del libro"><%= escapeHtml(descrizione) %></textarea>

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
            Inserisci libro
        </button>

    </form>

    <br>

    <a href="<%= contextPath %>/home.jsp">
        Torna alla Home
    </a>

</body>
</html>