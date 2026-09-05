<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="it.biblioteca.model.Libro" %>
<%@ page import="it.biblioteca.model.OffertaLibro" %>

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
    List<Libro> mieiLibri =
            (List<Libro>) request.getAttribute("mieiLibri");

    @SuppressWarnings("unchecked")
    List<OffertaLibro> offerteAttive =
            (List<OffertaLibro>) request.getAttribute("offerteAttive");

    @SuppressWarnings("unchecked")
    List<OffertaLibro> mieOfferte =
            (List<OffertaLibro>) request.getAttribute("mieOfferte");

    String contextPath =
            request.getContextPath();

    Object errore =
            request.getAttribute("errore");

    String tipoSelezionato =
            request.getParameter("tipoOfferta");

    String condizioni =
            request.getParameter("condizioni");
%>

<!DOCTYPE html>
<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        Offerte - Biblioteca Online
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

        textarea {
            width: 400px;
            max-width: 100%;
        }

        .errore {
            color: red;
        }

    </style>

</head>

<body>

    <h1>Prestiti e scambi</h1>

    <a href="<%= contextPath %>/home.jsp">
        Torna alla Home
    </a>

    &nbsp;|&nbsp;

    <a href="<%= contextPath %>/miei-libri">
        I miei libri
    </a>

    <br><br>

    <% if (errore != null) { %>

        <p class="errore">
            <%= escapeHtml(errore) %>
        </p>

    <% } %>

    <h2>Crea una nuova offerta</h2>

    <% if (mieiLibri == null || mieiLibri.isEmpty()) { %>

        <p>
            Devi prima inserire almeno un libro.
        </p>

    <% } else { %>

        <form
            action="<%= contextPath %>/offerte"
            method="post">

            <input
                type="hidden"
                name="azione"
                value="crea">

            <label for="idLibro">
                Libro:
            </label>

            <select
                id="idLibro"
                name="idLibro"
                required>

                <option value="">
                    Seleziona un libro
                </option>

                <% for (Libro libro : mieiLibri) { %>

                    <option
                        value="<%= libro.getIdLibro() %>">

                        <%= escapeHtml(libro.getTitolo()) %>
                        -
                        <%= escapeHtml(libro.getAutore()) %>

                    </option>

                <% } %>

            </select>

            <br><br>

            <label for="tipoOfferta">
                Tipo di offerta:
            </label>

            <select
                id="tipoOfferta"
                name="tipoOfferta"
                required>

                <option value="">
                    Seleziona
                </option>

                <option
                    value="PRESTITO"
                    <%= "PRESTITO".equals(tipoSelezionato)
                            ? "selected"
                            : "" %>>

                    Prestito

                </option>

                <option
                    value="SCAMBIO"
                    <%= "SCAMBIO".equals(tipoSelezionato)
                            ? "selected"
                            : "" %>>

                    Scambio

                </option>

                <option
                    value="ENTRAMBI"
                    <%= "ENTRAMBI".equals(tipoSelezionato)
                            ? "selected"
                            : "" %>>

                    Prestito o scambio

                </option>

            </select>

            <br><br>

            <label for="condizioni">
                Condizioni:
            </label>

            <br>

            <textarea
                id="condizioni"
                name="condizioni"
                maxlength="1000"
                rows="5"
                placeholder="Esempio: restituzione entro 30 giorni, libro in buone condizioni..."><%= escapeHtml(condizioni) %></textarea>

            <br><br>

            <button type="submit">
                Pubblica offerta
            </button>

        </form>

    <% } %>

    <hr>

    <h2>Offerte disponibili</h2>

    <% if (offerteAttive == null || offerteAttive.isEmpty()) { %>

        <p>
            Non ci sono offerte attive.
        </p>

    <% } else { %>

        <table>

            <thead>

                <tr>
                    <th>Libro</th>
                    <th>Autore</th>
                    <th>Proprietario</th>
                    <th>Nickname</th>
                    <th>Tipo</th>
                    <th>Condizioni</th>
                </tr>

            </thead>

            <tbody>

                <% for (OffertaLibro offerta : offerteAttive) { %>

                    <tr>

                        <td>
                            <%= escapeHtml(
                                    offerta.getTitoloLibro()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    offerta.getAutoreLibro()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    offerta.getNomeProprietario()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    offerta.getUsernameProprietario()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    offerta.getTipoOfferta()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    offerta.getCondizioni()) %>
                        </td>

                    </tr>

                <% } %>

            </tbody>

        </table>

    <% } %>

    <h2>Le mie offerte</h2>

    <% if (mieOfferte == null || mieOfferte.isEmpty()) { %>

        <p>
            Non hai ancora pubblicato offerte.
        </p>

    <% } else { %>

        <table>

            <thead>

                <tr>
                    <th>Libro</th>
                    <th>Tipo</th>
                    <th>Condizioni</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                </tr>

            </thead>

            <tbody>

                <% for (OffertaLibro offerta : mieOfferte) { %>

                    <tr>

                        <td>
                            <%= escapeHtml(
                                    offerta.getTitoloLibro()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    offerta.getTipoOfferta()) %>
                        </td>

                        <td>
                            <%= escapeHtml(
                                    offerta.getCondizioni()) %>
                        </td>

                        <td>
                            <%= offerta.isAttiva()
                                    ? "Attiva"
                                    : "Disattivata" %>
                        </td>

                        <td>

                            <% if (offerta.isAttiva()) { %>

                                <form
                                    action="<%= contextPath %>/offerte"
                                    method="post">

                                    <input
                                        type="hidden"
                                        name="azione"
                                        value="disattiva">

                                    <input
                                        type="hidden"
                                        name="idOfferta"
                                        value="<%= offerta.getIdOfferta() %>">

                                    <button type="submit">
                                        Disattiva
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