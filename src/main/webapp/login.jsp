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

    String username = request.getParameter("username");

    Object errore = request.getAttribute("errore");
%>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>Login - Biblioteca Online</title>
</head>

<body>

    <h1>Login</h1>

    <% if (errore != null) { %>

        <p style="color: red;">
            <%= escapeHtml(errore) %>
        </p>

    <% } %>

    <form
        action="<%= contextPath %>/login"
        method="post">

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

        <label for="password">
            Password:
        </label>

        <input
            type="password"
            id="password"
            name="password"
            autocomplete="current-password"
            required
        >

        <br><br>

        <button type="submit">
            Accedi
        </button>

    </form>

    <br>

    <a href="<%= contextPath %>/registrazione.jsp">
        Non hai un account? Registrati
    </a>

</body>
</html>