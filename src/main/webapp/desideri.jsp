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
        Lista desideri - Biblioteca Online
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

    <h1>Lista desideri</h1>

    <p>
        Qui trovi i libri che hai aggiunto
        alla tua lista dei desideri.
    </p>

    <a href="<%= contextPath %>/home.jsp">
        Torna alla Home
    </a>

    &nbsp;|&nbsp;

    <a href="<%= contextPath %>/libri">
        Cerca libri
    </a>

    <br><br>

    <% if (libri == null || libri.isEmpty()) { %>

        <p>
            La tua lista desideri è vuota.
        </p>

    <% } else { %>

        <p>
            Libri desiderati:
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

                            <form
                                action="<%= contextPath %>/desideri"
                                method="post">

                                <input
                                    type="hidden"
                                    name="idLibro"
                                    value="<%= libro.getIdLibro() %>">

                                <input
                                    type="hidden"
                                    name="azione"
                                    value="rimuovi">

                                <button type="submit">
                                    Rimuovi
                                </button>

                            </form>

                        </td>

                    </tr>

                <% } %>

            </tbody>

        </table>

    <% } %>

</body>

</html>