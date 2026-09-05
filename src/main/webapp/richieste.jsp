<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="it.biblioteca.model.RichiestaLibro" %>

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

    private String statoLeggibile(String stato) {

        if ("IN_ATTESA".equals(stato)) {
            return "In attesa";
        }

        if ("ACCETTATA".equals(stato)) {
            return "Accettata";
        }

        if ("RIFIUTATA".equals(stato)) {
            return "Rifiutata";
        }

        if ("COMPLETATA".equals(stato)) {
            return "Completata";
        }

        if ("ANNULLATA".equals(stato)) {
            return "Annullata";
        }

        return stato;
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
    List<RichiestaLibro> richiesteInviate =
            (List<RichiestaLibro>)
                    request.getAttribute("richiesteInviate");

    @SuppressWarnings("unchecked")
    List<RichiestaLibro> richiesteRicevute =
            (List<RichiestaLibro>)
                    request.getAttribute("richiesteRicevute");

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
        Richieste - Biblioteca Online
    </title>

    <style>

        table {
            border-collapse: collapse;
            width: 100%;
            margin-bottom: 30px;
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

        form {
            margin-bottom: 5px;
        }

    </style>

</head>

<body>

    <h1>Richieste di prestito e scambio</h1>

    <a href="<%= contextPath %>/home.jsp">
        Torna alla Home
    </a>

    &nbsp;|&nbsp;

    <a href="<%= contextPath %>/offerte">
        Vedi offerte
    </a>

    <br><br>

    <% if (errore != null) { %>

        <p class="errore">
            <%= escapeHtml(errore) %>
        </p>

    <% } %>

    <h2>Richieste ricevute</h2>

    <% if (richiesteRicevute == null
            || richiesteRicevute.isEmpty()) { %>

        <p>
            Non hai ricevuto richieste.
        </p>

    <% } else { %>

        <table>

            <thead>

                <tr>
                    <th>Libro</th>
                    <th>Autore</th>
                    <th>Richiedente</th>
                    <th>Nickname</th>
                    <th>Tipo</th>
                    <th>Modalità proposta</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                </tr>

            </thead>

            <tbody>

                <% for (RichiestaLibro richiesta
                        : richiesteRicevute) { %>

                    <tr>

                        <td>
                            <%= escapeHtml(
                                    richiesta.getTitoloLibro()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    richiesta.getAutoreLibro()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    richiesta.getNomeRichiedente()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    richiesta.getUsernameRichiedente()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    richiesta.getTipoRichiesta()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    richiesta.getMessaggioModalita()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    statoLeggibile(
                                            richiesta.getStato())) %>
                        </td>

                        <td>

                            <% if ("IN_ATTESA".equals(
                                    richiesta.getStato())) { %>

                                <form
                                    action="<%= contextPath %>/richieste"
                                    method="post">

                                    <input
                                        type="hidden"
                                        name="azione"
                                        value="accetta">

                                    <input
                                        type="hidden"
                                        name="idRichiesta"
                                        value="<%= richiesta.getIdRichiesta() %>">

                                    <button type="submit">
                                        Accetta
                                    </button>

                                </form>

                                <form
                                    action="<%= contextPath %>/richieste"
                                    method="post">

                                    <input
                                        type="hidden"
                                        name="azione"
                                        value="rifiuta">

                                    <input
                                        type="hidden"
                                        name="idRichiesta"
                                        value="<%= richiesta.getIdRichiesta() %>">

                                    <button type="submit">
                                        Rifiuta
                                    </button>

                                </form>

                            <% } else if ("ACCETTATA".equals(
                                    richiesta.getStato())) { %>

                                <form
                                    action="<%= contextPath %>/richieste"
                                    method="post">

                                    <input
                                        type="hidden"
                                        name="azione"
                                        value="completa">

                                    <input
                                        type="hidden"
                                        name="idRichiesta"
                                        value="<%= richiesta.getIdRichiesta() %>">

                                    <button type="submit">
                                        Segna come completata
                                    </button>

                                </form>

                            <% } else { %>

                                Nessuna azione

                            <% } %>

                        </td>

                    </tr>

                <% } %>

            </tbody>

        </table>

    <% } %>

    <h2>Richieste inviate</h2>

    <% if (richiesteInviate == null
            || richiesteInviate.isEmpty()) { %>

        <p>
            Non hai ancora inviato richieste.
        </p>

    <% } else { %>

        <table>

            <thead>

                <tr>
                    <th>Libro</th>
                    <th>Autore</th>
                    <th>Proprietario</th>
                    <th>Tipo</th>
                    <th>Modalità proposta</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                </tr>

            </thead>

            <tbody>

                <% for (RichiestaLibro richiesta
                        : richiesteInviate) { %>

                    <tr>

                        <td>
                            <%= escapeHtml(
                                    richiesta.getTitoloLibro()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    richiesta.getAutoreLibro()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    richiesta.getUsernameProprietario()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    richiesta.getTipoRichiesta()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    richiesta.getMessaggioModalita()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    statoLeggibile(
                                            richiesta.getStato())) %>
                        </td>

                        <td>

                            <% if ("IN_ATTESA".equals(
                                    richiesta.getStato())) { %>

                                <form
                                    action="<%= contextPath %>/richieste"
                                    method="post">

                                    <input
                                        type="hidden"
                                        name="azione"
                                        value="annulla">

                                    <input
                                        type="hidden"
                                        name="idRichiesta"
                                        value="<%= richiesta.getIdRichiesta() %>">

                                    <button type="submit">
                                        Annulla
                                    </button>

                                </form>

                            <% } else if ("ACCETTATA".equals(
                                    richiesta.getStato())) { %>

                                <form
                                    action="<%= contextPath %>/richieste"
                                    method="post">

                                    <input
                                        type="hidden"
                                        name="azione"
                                        value="completa">

                                    <input
                                        type="hidden"
                                        name="idRichiesta"
                                        value="<%= richiesta.getIdRichiesta() %>">

                                    <button type="submit">
                                        Segna come completata
                                    </button>

                                </form>

                            <% } else { %>

                                Nessuna azione

                            <% } %>

                        </td>

                    </tr>

                <% } %>

            </tbody>

        </table>

    <% } %>

</body>

</html>