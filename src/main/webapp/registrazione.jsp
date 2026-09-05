<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Registrazione - Biblioteca Online</title>
</head>
<body>

    <h1>Registrazione</h1>
    
    <% if (request.getAttribute("messaggio") != null) { %>
    	<p style="color: green;">
        	<%= request.getAttribute("messaggio") %>
    	</p>
	<% } %>

	<% if (request.getAttribute("errore") != null) { %>
    	<p style="color: red;">
        	<%= request.getAttribute("errore") %>
    	</p>
	<% } %>

    <form action="registrazione" method="post">

        <label>Nome:</label>
        <input type="text" name="nome" required>
        <br><br>

        <label>Cognome:</label>
        <input type="text" name="cognome" required>
        <br><br>

        <label>Username:</label>
        <input type="text" name="username" required>
        <br><br>

        <label>Email:</label>
        <input type="email" name="email" required>
        <br><br>

        <label>Password:</label>
        <input type="password" name="password" required>
        <br><br>

        <button type="submit">
            Registrati
        </button>

    </form>

</body>
</html>