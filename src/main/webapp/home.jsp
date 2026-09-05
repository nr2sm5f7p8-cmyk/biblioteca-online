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
    if (!Boolean.TRUE.equals(
            session.getAttribute("autenticato"))) {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp"
        );
        return;
    }

    String nome =
            (String) session.getAttribute("nome");

    String cognome =
            (String) session.getAttribute("cognome");

    String username =
            (String) session.getAttribute("username");

    Object valoreRuolo =
            session.getAttribute("idRuolo");

    Integer idRuolo = null;

    if (valoreRuolo instanceof Integer) {
        idRuolo = (Integer) valoreRuolo;
    }

    String ruolo = "SCONOSCIUTO";

    if (idRuolo != null) {

        if (idRuolo == 1) {
            ruolo = "ADMIN";

        } else if (idRuolo == 2) {
            ruolo = "UTENTE";

        } else if (idRuolo == 3) {
            ruolo = "SERVIZIO TECNICO";
        }
    }

    String contextPath =
            request.getContextPath();
%>

<!DOCTYPE html>
<html lang="it">

<head>
    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>Home - Biblioteca Online</title>
</head>

<body>

    <h1>Biblioteca Online</h1>

    <h2>
        Benvenuto
        <%= escapeHtml(nome) %>
        <%= escapeHtml(cognome) %>
    </h2>

    <p>
        Username:
        <strong>
            <%= escapeHtml(username) %>
        </strong>
    </p>

    <p>
        Ruolo:
        <strong>
            <%= escapeHtml(ruolo) %>
        </strong>
    </p>

    <hr>

    <h2>Menu</h2>

    <p>
        <a href="<%= contextPath %>/profilo">
            Il mio profilo
        </a>
    </p>

    <p>
        <a href="<%= contextPath %>/libri">
            Visualizza libri
        </a>
    </p>

    <p>
        <a href="<%= contextPath %>/inserisci_libro.jsp">
            Inserisci nuovo libro
        </a>
    </p>

    <p>
        <a href="<%= contextPath %>/chat.jsp">
            Chat
        </a>
    </p>

    <% if (idRuolo != null && idRuolo == 1) { %>

        <hr>

        <h3>Funzioni amministratore</h3>

        <p>
            <a href="<%= contextPath %>/admin/">
                Area Amministratore
            </a>
        </p>

    <% } %>

    <% if (idRuolo != null && idRuolo == 3) { %>

        <hr>

        <h3>Area Servizio Tecnico</h3>

        <p>
            <a href="<%= contextPath %>/tecnico/">
                Area Tecnica
            </a>
        </p>

    <% } %>

    <hr>

    <a href="<%= contextPath %>/logout">
        Logout
    </a>

</body>
</html>