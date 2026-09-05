<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
    Object autenticato = session.getAttribute("autenticato");

    if (autenticato == null || !(Boolean) autenticato) {
        response.sendRedirect("login.jsp");
        return;
    }

    String nome = (String) session.getAttribute("nome");
    String cognome = (String) session.getAttribute("cognome");
    String username = (String) session.getAttribute("username");
    Integer idRuolo = (Integer) session.getAttribute("idRuolo");

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
%>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>Home - Biblioteca Online</title>
</head>

<body>

    <h1>Biblioteca Online</h1>

    <h2>
        Benvenuto <%= nome %> <%= cognome %>
    </h2>

    <p>
        Username:
        <strong><%= username %></strong>
    </p>

    <p>
        Ruolo:
        <strong><%= ruolo %></strong>
    </p>

    <hr>

    <h2>Menu</h2>

    <a href="libri">
        <button type="button">
            Visualizza libri
        </button>
    </a>

    <br><br>

    <a href="inserisci_libro.jsp">
        <button type="button">
            Inserisci nuovo libro
        </button>
    </a>

    <br><br>

    <a href="chat.jsp">
    	<button type="button">
        	Chat
    	</button>
	</a>

    <% if (idRuolo != null && idRuolo == 1) { %>

        <br><br>

        <h3>Funzioni amministratore</h3>

        <a href="admin/">
            <button type="button">
                Area Amministratore
            </button>
        </a>

    <% } %>

    <% if (idRuolo != null && idRuolo == 3) { %>

        <br><br>

        <h3>Area Servizio Tecnico</h3>

        <a href="tecnico/">
            <button type="button">
                Area Tecnica
            </button>
        </a>

    <% } %>

    <hr>

    <a href="logout">Logout</a>

</body>

</html>