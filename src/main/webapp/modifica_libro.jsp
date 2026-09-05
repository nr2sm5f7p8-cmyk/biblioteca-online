<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="it.biblioteca.model.Libro" %>

<%
    Object autenticato = session.getAttribute("autenticato");

    if (autenticato == null || !(Boolean) autenticato) {
        response.sendRedirect("login.jsp");
        return;
    }

    Libro libro = (Libro) request.getAttribute("libro");

    if (libro == null) {
        response.sendRedirect("libri");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Modifica Libro - Biblioteca Online</title>
</head>
<body>

    <h1>Modifica libro</h1>

    <form action="modifica-libro" method="post">

        <input type="hidden"
               name="idLibro"
               value="<%= libro.getIdLibro() %>">

        <label>Titolo:</label>
        <input type="text"
               name="titolo"
               value="<%= libro.getTitolo() %>"
               required>

        <br><br>

        <label>Autore:</label>
        <input type="text"
               name="autore"
               value="<%= libro.getAutore() %>"
               required>

        <br><br>

        <label>ISBN:</label>
        <input type="text"
               name="isbn"
               value="<%= libro.getIsbn() != null ? libro.getIsbn() : "" %>">

        <br><br>

        <label>Anno pubblicazione:</label>
        <input type="number"
               name="annoPubblicazione"
               value="<%= libro.getAnnoPubblicazione() != null
                       ? libro.getAnnoPubblicazione()
                       : "" %>">

        <br><br>

        <label>Genere:</label>
        <input type="text"
               name="genere"
               value="<%= libro.getGenere() != null ? libro.getGenere() : "" %>">

        <br><br>

        <label>Disponibile:</label>
        <input type="checkbox"
               name="disponibile"
               value="true"
               <%= libro.isDisponibile() ? "checked" : "" %>>

        <br><br>

        <button type="submit">
            Salva modifiche
        </button>

    </form>

    <br>

    <a href="libri">Torna alla lista libri</a>

</body>
</html>