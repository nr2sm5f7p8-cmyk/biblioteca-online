<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="it.biblioteca.model.RecensioneScambio" %>

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
    List<RecensioneScambio> recensioniRicevute =
            (List<RecensioneScambio>)
                    request.getAttribute("recensioniRicevute");

    String contextPath =
            request.getContextPath();

    Object errore =
            request.getAttribute("errore");
%>

<!DOCTYPE html>
<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        Recensioni - Biblioteca Online
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

        .errore {
            color: red;
        }

        .voto {
            font-weight: bold;
        }

    </style>

</head>

<body>

    <h1>Recensioni</h1>

    <a href="<%= contextPath %>/home.jsp">
        Torna alla Home
    </a>

    &nbsp;|&nbsp;

    <a href="<%= contextPath %>/richieste">
        Richieste di prestito e scambio
    </a>

    <br><br>

    <% if (errore != null) { %>

        <p class="errore">
            <%= escapeHtml(errore) %>
        </p>

    <% } %>

    <h2>Recensioni ricevute</h2>

    <% if (recensioniRicevute == null
            || recensioniRicevute.isEmpty()) { %>

        <p>
            Non hai ancora ricevuto recensioni.
        </p>

    <% } else { %>

        <table>

            <thead>

                <tr>
                    <th>Libro</th>
                    <th>Autore recensione</th>
                    <th>Voto</th>
                    <th>Commento</th>
                </tr>

            </thead>

            <tbody>

                <% for (RecensioneScambio recensione
                        : recensioniRicevute) { %>

                    <tr>

                        <td>
                            <%= escapeHtml(
                                    recensione.getTitoloLibro()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    recensione.getUsernameAutore()) %>
                        </td>

                        <td class="voto">
                            <%= recensione.getVoto() %> / 5
                        </td>

                        <td>
                            <%= escapeHtml(
                                    recensione.getTesto()) %>
                        </td>

                    </tr>

                <% } %>

            </tbody>

        </table>

    <% } %>

</body>

</html>