\# Biblioteca Online



Applicazione web per la gestione di una biblioteca online sviluppata in Java con JSP e Servlet.



\## Tecnologie



\- Java 21

\- Jakarta Servlet

\- JSP

\- Maven

\- Apache Tomcat 10.1

\- MySQL

\- BCrypt

\- Firebase Realtime Database

\- Firebase Authentication

\- Git e GitHub



\## Funzionalità



\- Registrazione utenti

\- Login con username e password

\- Password protette con BCrypt

\- Verifica OTP

\- Gestione sessione

\- Logout

\- Ruoli:

&#x20; - ADMIN

&#x20; - UTENTE

&#x20; - SERVIZIO TECNICO

\- Controllo accessi con Filter

\- Visualizzazione libri

\- Inserimento libri

\- Modifica libri

\- Chat realtime tra utenti tramite Firebase



\## Struttura backend



Il progetto utilizza una struttura organizzata in:



\- `config` - configurazione database

\- `model` - oggetti del dominio

\- `dao` - accesso ai dati

\- `servlet` - controller HTTP

\- `filter` - autenticazione e autorizzazione



\## Database



Il database utilizzato è MySQL.



La password del database non è salvata nel repository.



Viene letta dalla variabile d'ambiente:



`BIBLIOTECA\_DB\_PASSWORD`



\## Branch Git



\- `master` - versione stabile

\- `develop` - sviluppo e integrazione

\- `feature/\*` - sviluppo delle singole funzionalità

