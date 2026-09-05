<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - Biblioteca Online</title>
</head>
<body>

    <h1>Login</h1>

    <% if (request.getAttribute("errore") != null) { %>
        <p style="color: red;">
            <%= request.getAttribute("errore") %>
        </p>
    <% } %>

    <form action="login" method="post">

        <label>Username:</label>
        <input type="text" name="username" required>

        <br><br>

        <label>Password:</label>
        <input type="password" name="password" required>

        <br><br>

        <button type="submit">
            Accedi
        </button>

    </form>

    <br>

    <a href="registrazione.jsp">
        Non hai un account? Registrati
    </a>

</body>
</html>