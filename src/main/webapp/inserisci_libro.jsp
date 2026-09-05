<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
    Object autenticato = session.getAttribute("autenticato");

    if (autenticato == null || !(Boolean) autenticato) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Inserisci Libro - Biblioteca Online</title>
</head>
<body>

    <h1>Inserisci nuovo libro</h1>

    <% if (request.getAttribute("errore") != null) { %>
        <p style="color: red;">
            <%= request.getAttribute("errore") %>
        </p>
    <% } %>

    <form action="inserisci-libro" method="post">

        <label>Titolo:</label>
        <input type="text" name="titolo" required>

        <br><br>

        <label>Autore:</label>
        <input type="text" name="autore" required>

        <br><br>

        <label>ISBN:</label>
        <input type="text" name="isbn">

        <br><br>

        <label>Anno pubblicazione:</label>
        <input type="number" name="annoPubblicazione">

        <br><br>

        <label>Genere:</label>
        <input type="text" name="genere">

        <br><br>

        <label>Disponibile:</label>
        <input type="checkbox"
               name="disponibile"
               value="true"
               checked>

        <br><br>

        <button type="submit">
            Inserisci libro
        </button>

    </form>

    <br>

    <a href="home.jsp">Torna alla Home</a>

</body>
</html>