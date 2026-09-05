<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="it.biblioteca.model.Comunita" %>

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
    if (Boolean.TRUE.equals(
            session.getAttribute("autenticato"))) {

        response.sendRedirect(
                request.getContextPath() + "/home.jsp"
        );

        return;
    }

    String contextPath = request.getContextPath();

    Object messaggio =
            request.getAttribute("messaggio");

    Object errore =
            request.getAttribute("errore");

    boolean registrazioneFallita =
            errore != null;

    String nome = registrazioneFallita
            ? request.getParameter("nome")
            : "";

    String cognome = registrazioneFallita
            ? request.getParameter("cognome")
            : "";

    String username = registrazioneFallita
            ? request.getParameter("username")
            : "";

    String email = registrazioneFallita
            ? request.getParameter("email")
            : "";

    String telefono = registrazioneFallita
            ? request.getParameter("telefono")
            : "";

    String idComunitaSelezionata = registrazioneFallita
            ? request.getParameter("idComunita")
            : "";

    String generiPreferiti = registrazioneFallita
            ? request.getParameter("generiPreferiti")
            : "";

    @SuppressWarnings("unchecked")
    List<Comunita> comunita =
            (List<Comunita>) request.getAttribute("comunita");

    if (comunita == null) {
        comunita = Collections.emptyList();
    }
%>

<!DOCTYPE html>
<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        Registrazione - Biblioteca Online
    </title>

</head>

<body>

    <h1>Registrazione</h1>

    <% if (messaggio != null) { %>

        <p style="color: green;">
            <%= escapeHtml(messaggio) %>
        </p>

        <p>
            <a href="<%= contextPath %>/login.jsp">
                Vai al Login
            </a>
        </p>

    <% } %>

    <% if (errore != null) { %>

        <p style="color: red;">
            <%= escapeHtml(errore) %>
        </p>

    <% } %>

    <% if (messaggio == null) { %>

        <form
            action="<%= contextPath %>/registrazione"
            method="post">

            <label for="nome">
                Nome:
            </label>

            <input
                type="text"
                id="nome"
                name="nome"
                value="<%= escapeHtml(nome) %>"
                autocomplete="given-name"
                maxlength="100"
                required>

            <br><br>

            <label for="cognome">
                Cognome:
            </label>

            <input
                type="text"
                id="cognome"
                name="cognome"
                value="<%= escapeHtml(cognome) %>"
                autocomplete="family-name"
                maxlength="100"
                required>

            <br><br>

            <label for="username">
                Nickname / Username:
            </label>

            <input
                type="text"
                id="username"
                name="username"
                value="<%= escapeHtml(username) %>"
                autocomplete="username"
                maxlength="100"
                required>

            <br><br>

            <label for="email">
                Email:
            </label>

            <input
                type="email"
                id="email"
                name="email"
                value="<%= escapeHtml(email) %>"
                autocomplete="email"
                maxlength="255"
                required>

            <br><br>

            <label for="telefono">
                Numero di telefono:
            </label>

            <input
                type="tel"
                id="telefono"
                name="telefono"
                value="<%= escapeHtml(telefono) %>"
                autocomplete="tel"
                maxlength="30"
                required>

            <br><br>

            <label for="password">
                Password:
            </label>

            <input
                type="password"
                id="password"
                name="password"
                minlength="8"
                maxlength="72"
                autocomplete="new-password"
                required>

            <p>
                La password deve contenere da 8 a 72 caratteri.
            </p>

            <label for="idComunita">
                Comunità di appartenenza:
            </label>

            <select
                id="idComunita"
                name="idComunita">

                <option value="">
                    Nessuna comunità
                </option>

                <% for (Comunita c : comunita) {

                    String id =
                            String.valueOf(c.getIdComunita());

                    boolean selezionata =
                            id.equals(idComunitaSelezionata);
                %>

                    <option
                        value="<%= c.getIdComunita() %>"
                        <%= selezionata ? "selected" : "" %>>

                        <%= escapeHtml(c.getNome()) %>

                    </option>

                <% } %>

            </select>

            <br><br>

            <label for="generiPreferiti">
                Generi di libri preferiti:
            </label>

            <textarea
                id="generiPreferiti"
                name="generiPreferiti"
                maxlength="500"
                rows="4"
                cols="40"
                placeholder="Es. Fantasy, thriller, fantascienza"><%= escapeHtml(generiPreferiti) %></textarea>

            <p>
                Campo opzionale. Puoi indicare più generi separati da virgole.
            </p>

            <button type="submit">
                Registrati
            </button>

        </form>

    <% } %>

    <br>

    <a href="<%= contextPath %>/login.jsp">
        Hai già un account? Accedi
    </a>

</body>

</html>