<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Verifica OTP - Biblioteca Online</title>
</head>
<body>

    <h1>Verifica OTP</h1>

    <p>
        Inserisci il codice OTP generato per completare il login.
    </p>

    <% if (request.getAttribute("errore") != null) { %>
        <p style="color: red;">
            <%= request.getAttribute("errore") %>
        </p>
    <% } %>

    <form action="verifica-otp" method="post">

        <label>Codice OTP:</label>
        <input
            type="text"
            name="otp"
            maxlength="6"
            required
        >

        <br><br>

        <button type="submit">
            Verifica OTP
        </button>

    </form>

</body>
</html>