<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.List" %>
<%@ page import="it.biblioteca.model.Libro" %>

<%
    Object autenticato = session.getAttribute("autenticato");

    if (autenticato == null || !(Boolean) autenticato) {
        response.sendRedirect("login.jsp");
        return;
    }

    @SuppressWarnings("unchecked")
    List<Libro> libri =
            (List<Libro>) request.getAttribute("libri");
%>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>Lista Libri - Biblioteca Online</title>

    <style>
        table {
            border-collapse: collapse;
        }

        th,
        td {
            border: 1px solid black;
            padding: 8px;
        }
    </style>

</head>

<body>

    <h1>Lista Libri</h1>

    <a href="home.jsp">Torna alla Home</a>

    <br><br>

    <% if (libri == null || libri.isEmpty()) { %>

        <p>Nessun libro presente.</p>

    <% } else { %>

        <table>

            <tr>
                <th>ID</th>
                <th>Titolo</th>
                <th>Autore</th>
                <th>ISBN</th>
                <th>Anno</th>
                <th>Genere</th>
                <th>Disponibile</th>
                <th>Azioni</th>
            </tr>

            <% for (Libro libro : libri) { %>

                <tr>

                    <td><%= libro.getIdLibro() %></td>

                    <td><%= libro.getTitolo() %></td>

                    <td><%= libro.getAutore() %></td>

                    <td>
                        <%= libro.getIsbn() != null
                                ? libro.getIsbn()
                                : "" %>
                    </td>

                    <td>
                        <%= libro.getAnnoPubblicazione() != null
                                ? libro.getAnnoPubblicazione()
                                : "" %>
                    </td>

                    <td>
                        <%= libro.getGenere() != null
                                ? libro.getGenere()
                                : "" %>
                    </td>

                    <td>
                        <%= libro.isDisponibile()
                                ? "Sì"
                                : "No" %>
                    </td>

                    <td>
                        <a href="modifica-libro?id=<%= libro.getIdLibro() %>">
                            Modifica
                        </a>
                    </td>

                </tr>

            <% } %>

        </table>

    <% } %>

</body>

</html>