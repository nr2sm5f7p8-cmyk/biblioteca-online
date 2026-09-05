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
    if (!Boolean.TRUE.equals(
            session.getAttribute("autenticato"))) {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp"
        );
        return;
    }

    String username =
            (String) session.getAttribute("username");

    if (username == null || username.isBlank()) {
        username = "Utente";
    }

    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>Chat - Biblioteca Online</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 30px auto;
        }

        #messaggi {
            height: 400px;
            overflow-y: auto;
            border: 1px solid #ccc;
            padding: 15px;
            margin-bottom: 15px;
        }

        .messaggio {
            padding: 8px;
            margin-bottom: 8px;
            border-bottom: 1px solid #eee;
            overflow-wrap: anywhere;
        }

        #testoMessaggio {
            width: 70%;
            padding: 10px;
        }

        #inviaMessaggio {
            padding: 10px 20px;
        }

        #stato {
            color: red;
        }
    </style>
</head>

<body>

    <h1>Chat Biblioteca</h1>

    <p>
        Utente collegato:
        <strong id="utenteCorrente">
            <%= escapeHtml(username) %>
        </strong>
    </p>

    <hr>

    <div id="messaggi"></div>

    <input
        type="text"
        id="testoMessaggio"
        placeholder="Scrivi un messaggio..."
        maxlength="500"
        autocomplete="off"
    >

    <button
        type="button"
        id="inviaMessaggio">
        Invia
    </button>

    <p id="stato"></p>

    <br>

    <a href="<%= contextPath %>/home.jsp">
        Torna alla Home
    </a>

    <script type="module">

        import {
            initializeApp
        } from "https://www.gstatic.com/firebasejs/12.18.0/firebase-app.js";

        import {
            getAuth,
            signInAnonymously
        } from "https://www.gstatic.com/firebasejs/12.18.0/firebase-auth.js";

        import {
            getDatabase,
            ref,
            push,
            onChildAdded,
            serverTimestamp,
            query,
            limitToLast
        } from "https://www.gstatic.com/firebasejs/12.18.0/firebase-database.js";

        const firebaseConfig = {
            apiKey: "AIzaSyAgrTpy5JwUQ1AiZjti-At2PDETMG57YbI",
            authDomain: "biblioteca-online-e2d2f.firebaseapp.com",
            databaseURL:
                "https://biblioteca-online-e2d2f-default-rtdb.europe-west1.firebasedatabase.app",
            projectId: "biblioteca-online-e2d2f",
            storageBucket:
                "biblioteca-online-e2d2f.firebasestorage.app",
            messagingSenderId: "697697512345",
            appId:
                "1:697697512345:web:68a987214955e19065c108",
            measurementId: "G-SXYS62SFN9"
        };

        const app = initializeApp(firebaseConfig);
        const auth = getAuth(app);
        const database = getDatabase(app);

        const username =
            document.getElementById("utenteCorrente")
                    .textContent
                    .trim();

        const contenitore =
            document.getElementById("messaggi");

        const input =
            document.getElementById("testoMessaggio");

        const pulsante =
            document.getElementById("inviaMessaggio");

        const stato =
            document.getElementById("stato");

        pulsante.disabled = true;

        avviaChat();

        async function avviaChat() {

            try {
                await signInAnonymously(auth);

                const chatRef = ref(
                    database,
                    "chat/messaggi"
                );

                const ultimiMessaggi = query(
                    chatRef,
                    limitToLast(100)
                );

                ascoltaMessaggi(ultimiMessaggi);
                configuraInvio(chatRef);

                pulsante.disabled = false;
                stato.textContent = "";

            } catch (errore) {

                console.error(
                    "Errore avvio chat:",
                    errore
                );

                stato.textContent =
                    "Errore durante l'accesso alla chat.";
            }
        }

        function ascoltaMessaggi(ultimiMessaggi) {

            onChildAdded(
                ultimiMessaggi,
                (snapshot) => {

                    const messaggio =
                        snapshot.val();

                    if (!messaggioValido(messaggio)) {
                        return;
                    }

                    mostraMessaggio(messaggio);
                }
            );
        }

        function messaggioValido(messaggio) {

            return messaggio
                && typeof messaggio.username === "string"
                && typeof messaggio.testo === "string"
                && messaggio.username.trim() !== ""
                && messaggio.testo.trim() !== "";
        }

        function mostraMessaggio(messaggio) {

            const div =
                document.createElement("div");

            div.className = "messaggio";

            const autore =
                document.createElement("strong");

            autore.textContent =
                messaggio.username + ": ";

            const testo =
                document.createElement("span");

            testo.textContent =
                messaggio.testo;

            div.appendChild(autore);
            div.appendChild(testo);

            contenitore.appendChild(div);

            contenitore.scrollTop =
                contenitore.scrollHeight;
        }

        function configuraInvio(chatRef) {

            pulsante.addEventListener(
                "click",
                () => inviaMessaggio(chatRef)
            );

            input.addEventListener(
                "keydown",
                (event) => {

                    if (event.key === "Enter") {
                        event.preventDefault();
                        inviaMessaggio(chatRef);
                    }
                }
            );
        }

        async function inviaMessaggio(chatRef) {

            const testo =
                input.value.trim();

            if (testo === "" || testo.length > 500) {
                return;
            }

            pulsante.disabled = true;
            stato.textContent = "";

            try {

                await push(chatRef, {
                    username: username,
                    testo: testo,
                    timestamp: serverTimestamp()
                });

                input.value = "";
                input.focus();

            } catch (errore) {

                console.error(
                    "Errore invio messaggio:",
                    errore
                );

                stato.textContent =
                    "Errore durante l'invio del messaggio.";

            } finally {

                pulsante.disabled = false;
            }
        }

    </script>

</body>
</html>