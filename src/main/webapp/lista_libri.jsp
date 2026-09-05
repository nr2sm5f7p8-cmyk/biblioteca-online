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

    String filtroTitolo =
            request.getParameter("titolo");

    String filtroAutore =
            request.getParameter("autore");

    String filtroGenere =
            request.getParameter("genere");
%>

<!DOCTYPE html>
<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        Lista Libri - Biblioteca Online
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

        .filtri {
            margin-top: 20px;
            margin-bottom: 20px;
        }

        .filtri input {
            margin-right: 10px;
        }

        .descrizione {
            max-width: 350px;
        }

    </style>

</head>

<body>

    <h1>Lista Libri</h1>

    <a href="<%= contextPath %>/home.jsp">
        Torna alla Home
    </a>

    <br><br>

    <div class="filtri">

        <h2>Ricerca libri</h2>

        <form
            action="<%= contextPath %>/libri"
            method="get">

            <label for="titolo">
                Titolo:
            </label>

            <input
                type="text"
                id="titolo"
                name="titolo"
                value="<%= escapeHtml(filtroTitolo) %>"
                placeholder="Cerca per titolo">

            <label for="autore">
                Autore:
            </label>

            <input
                type="text"
                id="autore"
                name="autore"
                value="<%= escapeHtml(filtroAutore) %>"
                placeholder="Cerca per autore">

            <label for="genere">
                Genere:
            </label>

            <input
                type="text"
                id="genere"
                name="genere"
                value="<%= escapeHtml(filtroGenere) %>"
                placeholder="Cerca per genere">

            <button type="submit">
                Cerca
            </button>

            <a href="<%= contextPath %>/libri">
                Azzera filtri
            </a>

        </form>

    </div>

    <% if (libri == null || libri.isEmpty()) { %>

        <p>
            Nessun libro trovato.
        </p>

    <% } else { %>

        <p>
            Libri trovati:
            <strong><%= libri.size() %></strong>
        </p>

        <table>

            <thead>

                <tr>
                    <th>ID</th>
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
                            <%= libro.getIdLibro() %>
                        </td>

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