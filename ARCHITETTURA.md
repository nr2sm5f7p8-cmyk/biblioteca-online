\# Architettura Client / Server



Il progetto Biblioteca Online utilizza una separazione tra frontend e backend.



\## Client



Il codice client si trova in:



`src/main/webapp`



Comprende:



\- JSP

\- HTML

\- CSS

\- JavaScript

\- Firebase JavaScript SDK



Il client gestisce l'interfaccia utente e invia richieste HTTP al server.



\## Server



Il codice server si trova in:



`src/main/java`



È organizzato nei package:



\- `it.biblioteca.config`

\- `it.biblioteca.model`

\- `it.biblioteca.dao`

\- `it.biblioteca.servlet`

\- `it.biblioteca.filter`



\## Flusso principale



Client:



`JSP / HTML`



↓



HTTP



↓



Server:



`Servlet`



↓



`DAO`



↓



JDBC



↓



`MySQL`



\## Autenticazione



Il login principale utilizza:



\- username e password

\- BCrypt

\- OTP

\- HttpSession

\- AuthFilter

\- RoleFilter



\## Chat realtime



La chat utilizza:



`chat.jsp`



↓



Firebase Authentication



↓



Firebase Realtime Database



I messaggi vengono sincronizzati in tempo reale tra gli utenti.



\## Ruoli



L'applicazione supporta:



\- ADMIN

\- UTENTE

\- SERVIZIO TECNICO



L'accesso alle risorse protette viene controllato tramite Filter Java.

