<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

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
    String contextPath = request.getContextPath();

    if (Boolean.TRUE.equals(
            session.getAttribute("autenticato"))) {

        response.sendRedirect(
                contextPath + "/home.jsp"
        );
        return;
    }

    Object utenteOtpId =
            session.getAttribute("utenteOtpId");

    if (!(utenteOtpId instanceof Integer)) {

        response.sendRedirect(
                contextPath + "/login.jsp"
        );
        return;
    }

    Object errore =
            request.getAttribute("errore");
%>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>Verifica OTP - Biblioteca Online</title>
</head>

<body>

    <h1>Verifica OTP</h1>

    <p>
        Inserisci il codice OTP di 6 cifre
        per completare il login.
    </p>

    <% if (errore != null) { %>

        <p style="color: red;">
            <%= escapeHtml(errore) %>
        </p>

    <% } %>

    <form
        action="<%= contextPath %>/verifica-otp"
        method="post">

        <label for="otp">
            Codice OTP:
        </label>

        <input
            type="text"
            id="otp"
            name="otp"
            minlength="6"
            maxlength="6"
            pattern="[0-9]{6}"
            inputmode="numeric"
            autocomplete="one-time-code"
            required
        >

        <br><br>

        <button type="submit">
            Verifica OTP
        </button>

    </form>

    <br>

    <a href="<%= contextPath %>/login.jsp">
        Torna al Login
    </a>

</body>
</html>