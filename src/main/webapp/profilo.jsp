<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="it.biblioteca.model.Utente" %>
<%@ page import="it.biblioteca.model.Comunita" %>

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

    String contextPath =
            request.getContextPath();

    Utente utente =
            (Utente) request.getAttribute("utente");

    if (utente == null) {
        response.sendRedirect(
                contextPath + "/home.jsp"
        );
        return;
    }

    @SuppressWarnings("unchecked")
    List<Comunita> comunita =
            (List<Comunita>)
                    request.getAttribute("comunita");

    if (comunita == null) {
        comunita = Collections.emptyList();
    }

    Object errore =
            request.getAttribute("errore");

    Object messaggio =
            request.getAttribute("messaggio");

    String nome =
            request.getParameter("nome") != null
            ? request.getParameter("nome")
            : utente.getNome();

    String cognome =
            request.getParameter("cognome") != null
            ? request.getParameter("cognome")
            : utente.getCognome();

    String email =
            request.getParameter("email") != null
            ? request.getParameter("email")
            : utente.getEmail();

    String telefono =
            request.getParameter("telefono") != null
            ? request.getParameter("telefono")
            : utente.getTelefono();

    String generiPreferiti =
            request.getParameter("generiPreferiti") != null
            ? request.getParameter("generiPreferiti")
            : utente.getGeneriPreferiti();

    String idComunitaSelezionata =
            request.getParameter("idComunita");

    if (idComunitaSelezionata == null
            && utente.getIdComunita() != null) {

        idComunitaSelezionata =
                String.valueOf(
                        utente.getIdComunita());
    }
%>

<!DOCTYPE html>

<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        Il mio profilo - Biblioteca Online
    </title>

</head>

<body>

    <h1>Il mio profilo</h1>

    <p>
        Username:
        <strong>
            <%= escapeHtml(utente.getUsername()) %>
        </strong>
    </p>

    <% if (messaggio != null) { %>

        <p style="color: green;">
            <%= escapeHtml(messaggio) %>
        </p>

    <% } %>

    <% if (errore != null) { %>

        <p style="color: red;">
            <%= escapeHtml(errore) %>
        </p>

    <% } %>

    <form
        action="<%= contextPath %>/profilo"
        method="post">

        <label for="nome">
            Nome:
        </label>

        <input
            type="text"
            id="nome"
            name="nome"
            value="<%= escapeHtml(nome) %>"
            maxlength="100"
            required>

        <br><br>

        <label for="cognome">
            Cognome:
        </label>

        <input
            type="text"
            id="cognome"
            name="cognome"
            value="<%= escapeHtml(cognome) %>"
            maxlength="100"
            required>

        <br><br>

        <label for="email">
            Email:
        </label>

        <input
            type="email"
            id="email"
            name="email"
            value="<%= escapeHtml(email) %>"
            maxlength="255"
            required>

        <br><br>

        <label for="telefono">
            Numero di telefono:
        </label>

        <input
            type="tel"
            id="telefono"
            name="telefono"
            value="<%= escapeHtml(telefono) %>"
            maxlength="30">

        <br><br>

        <label for="idComunita">
            Comunità:
        </label>

        <select
            id="idComunita"
            name="idComunita">

            <option value="">
                Nessuna comunità
            </option>

            <% for (Comunita c : comunita) {

                String id =
                        String.valueOf(c.getIdComunita());

                boolean selezionata =
                        id.equals(idComunitaSelezionata);
            %>

                <option
                    value="<%= c.getIdComunita() %>"
                    <%= selezionata ? "selected" : "" %>>

                    <%= escapeHtml(c.getNome()) %>

                </option>

            <% } %>

        </select>

        <br><br>

        <label for="generiPreferiti">
            Generi preferiti:
        </label>

        <br>

        <textarea
            id="generiPreferiti"
            name="generiPreferiti"
            maxlength="500"
            rows="4"
            cols="40"><%= escapeHtml(generiPreferiti) %></textarea>

        <p>
            Esempio: Fantasy, Thriller, Fantascienza
        </p>

        <button type="submit">
            Salva modifiche
        </button>

    </form>

    <hr>

    <p>
        <a href="<%= contextPath %>/home.jsp">
            Torna alla Home
        </a>
    </p>

</body>

</html>