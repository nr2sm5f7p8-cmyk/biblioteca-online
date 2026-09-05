<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
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

    @SuppressWarnings("unchecked")
    List<Libro> libri =
            (List<Libro>) request.getAttribute("libri");

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

    <title>
        I miei libri - Biblioteca Online
    </title>

    <style>

        table {
            border-collapse: collapse;
            width: 100%;
        }

        th,
        td {
            border: 1px solid black;
            padding: 8px;
            vertical-align: top;
        }

        .descrizione {
            max-width: 350px;
        }

    </style>

</head>

<body>

    <h1>I miei libri</h1>

    <p>
        Qui vengono mostrati solamente i libri
        inseriti dal tuo account.
    </p>

    <a href="<%= contextPath %>/home.jsp">
        Torna alla Home
    </a>

    &nbsp;|&nbsp;

    <a href="<%= contextPath %>/inserisci_libro.jsp">
        Inserisci nuovo libro
    </a>

    <br><br>

    <% if (libri == null || libri.isEmpty()) { %>

        <p>
            Non hai ancora inserito nessun libro.
        </p>

    <% } else { %>

        <p>
            Libri posseduti:
            <strong><%= libri.size() %></strong>
        </p>

        <table>

            <thead>

                <tr>
                    <th>Titolo</th>
                    <th>Autore</th>
                    <th>ISBN</th>
                    <th>Anno</th>
                    <th>Genere</th>
                    <th>Descrizione</th>
                    <th>Disponibile</th>
                    <th>Azioni</th>
                </tr>

            </thead>

            <tbody>

                <% for (Libro libro : libri) { %>

                    <tr>

                        <td>
                            <%= escapeHtml(
                                    libro.getTitolo()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    libro.getAutore()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    libro.getIsbn()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    libro.getAnnoPubblicazione()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    libro.getGenere()) %>
                        </td>

                        <td class="descrizione">
                            <%= escapeHtml(
                                    libro.getDescrizione()) %>
                        </td>

                        <td>
                            <%= libro.isDisponibile()
                                    ? "Sì"
                                    : "No" %>
                        </td>

                        <td>

                            <a href="<%= contextPath %>/modifica-libro?id=<%= libro.getIdLibro() %>">
                                Modifica
                            </a>

                        </td>

                    </tr>

                <% } %>

            </tbody>

        </table>

    <% } %>

</body>

</html>