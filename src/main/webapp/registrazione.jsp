<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

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
%>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>Registrazione - Biblioteca Online</title>
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
            required
        >

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
            required
        >

        <br><br>

        <label for="username">
            Username:
        </label>

        <input
            type="text"
            id="username"
            name="username"
            value="<%= escapeHtml(username) %>"
            autocomplete="username"
            required
        >

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
            required
        >

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
            required
        >

        <p>
            La password deve contenere da 8 a 72 caratteri.
        </p>

        <button type="submit">
            Registrati
        </button>

    </form>

    <br>

    <a href="<%= contextPath %>/login.jsp">
        Hai già un account? Accedi
    </a>

</body>
</html>