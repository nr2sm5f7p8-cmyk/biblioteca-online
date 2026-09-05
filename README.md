# Biblioteca Online

Applicazione web per la gestione di una biblioteca online sviluppata in Java con JSP, Servlet, MySQL e Firebase.

Il progetto permette agli utenti di registrarsi, effettuare il login con password e OTP, gestire un catalogo di libri e comunicare tramite una chat realtime.

---

## Tecnologie utilizzate

### Backend

- Java 21
- Jakarta Servlet
- JSP
- JDBC
- Maven
- BCrypt
- Apache Tomcat 10.1

### Database

- MySQL 8
- MySQL Workbench

### Chat realtime

- Firebase Realtime Database
- Firebase Authentication
- JavaScript

### Versionamento

- Git
- GitHub

---

## Funzionalità

L'applicazione implementa:

- registrazione utenti;
- login con username e password;
- password protette tramite BCrypt;
- verifica OTP a 6 cifre;
- OTP con scadenza di 5 minuti;
- gestione delle sessioni HTTP;
- logout;
- controllo degli accessi;
- gestione dei ruoli;
- visualizzazione dei libri;
- inserimento di nuovi libri;
- modifica dei libri;
- chat realtime tra utenti;
- autenticazione Firebase anonima;
- regole di sicurezza Firebase.

---

## Ruoli

Il sistema prevede tre ruoli.

| ID | Ruolo |
|---|---|
| 1 | ADMIN |
| 2 | UTENTE |
| 3 | SERVIZIO_TECNICO |

### ADMIN

Può accedere all'area:

`/admin/`

### UTENTE

Può utilizzare le normali funzionalità dell'applicazione, ma non può accedere alle aree riservate.

### SERVIZIO_TECNICO

Può accedere all'area:

`/tecnico/`

Il controllo degli accessi viene effettuato tramite `RoleFilter`.

---

## Architettura

Il progetto utilizza una separazione logica client/server.

### Client

La parte client si trova principalmente in:

`src/main/webapp`

e comprende:

- JSP;
- HTML;
- CSS;
- JavaScript;
- integrazione Firebase lato browser.

### Server

La parte server si trova principalmente in:

`src/main/java`

e comprende:

- Servlet;
- DAO;
- Model;
- Filter;
- configurazione database.

Il flusso principale è:

Browser  
↓  
JSP / HTML / JavaScript  
↓  
Servlet  
↓  
DAO  
↓  
JDBC  
↓  
MySQL

---

## Struttura backend

Il codice Java è organizzato nei package:

### `it.biblioteca.config`

Contiene la configurazione dell'applicazione.

Esempio:

`DatabaseConnection.java`

### `it.biblioteca.model`

Contiene gli oggetti del dominio.

Esempi:

- `Utente.java`
- `Libro.java`

### `it.biblioteca.dao`

Contiene le classi che comunicano con MySQL.

Esempi:

- `UtenteDAO.java`
- `LibroDAO.java`

Le query utilizzano `PreparedStatement`.

### `it.biblioteca.servlet`

Contiene i controller HTTP.

Tra le Servlet principali:

- `RegistrazioneServlet`
- `LoginServlet`
- `VerificaOtpServlet`
- `LogoutServlet`
- `ListaLibriServlet`
- `InserisciLibroServlet`
- `ModificaLibroServlet`

### `it.biblioteca.filter`

Contiene i filtri utilizzati per autenticazione e autorizzazione.

- `AuthFilter`
- `RoleFilter`

---

## Database

Il database utilizzato dal progetto è:

`biblioteca_online`

Le principali tabelle sono:

- `utenti`
- `ruoli`
- `libri`

La struttura del database è presente nel repository:

`database/schema.sql`

I dati iniziali dei ruoli sono presenti in:

`database/seed.sql`

---

## Creazione del database

Per configurare un nuovo ambiente MySQL:

1. eseguire:

`database/schema.sql`

2. eseguire:

`database/seed.sql`

Il secondo script inserisce i ruoli:

- ADMIN
- UTENTE
- SERVIZIO_TECNICO

---

## Configurazione password MySQL

La password MySQL non viene salvata direttamente nel codice sorgente e non viene versionata nel repository Git.

`DatabaseConnection.java` legge la password dalla variabile d'ambiente:

`BIBLIOTECA_DB_PASSWORD`

Su Windows è quindi necessario creare una variabile d'ambiente utente chiamata:

`BIBLIOTECA_DB_PASSWORD`

e assegnarle la password MySQL locale.

Dopo aver creato o modificato la variabile è necessario riavviare Eclipse, in modo che Tomcat possa leggerla.

---

## Registrazione

Durante la registrazione vengono richiesti:

- nome;
- cognome;
- username;
- email;
- password.

Il backend controlla:

- campi obbligatori;
- formato email;
- password da 8 a 72 caratteri;
- username o email già utilizzati.

La password non viene salvata in chiaro.

Viene generato un hash tramite BCrypt prima dell'inserimento nel database.

---

## Login

Il login utilizza:

1. username;
2. password;
3. verifica della password tramite BCrypt;
4. generazione OTP;
5. verifica OTP;
6. creazione della sessione autenticata.

---

## OTP

Dopo username e password corretti viene generato un OTP composto da 6 cifre.

L'OTP:

- viene generato tramite `SecureRandom`;
- viene salvato nel database come hash BCrypt;
- ha una durata di 5 minuti;
- viene eliminato dal database dopo un utilizzo corretto.

### Modalità OTP di test

Attualmente l'OTP viene scritto nei log di Tomcat/Eclipse per permettere il test locale dell'applicazione.

L'invio reale tramite email o SMS non è implementato.

In un ambiente di produzione sarebbe necessario collegare un servizio email, SMS o un provider di autenticazione.

---

## Sessioni

Dopo il completamento dell'OTP vengono memorizzate nella sessione informazioni come:

- stato di autenticazione;
- ID utente;
- nome;
- cognome;
- username;
- ruolo.

Dopo il login viene inoltre rinnovato l'identificativo della sessione.

Con il logout la sessione viene invalidata.

---

## Controllo accessi

### AuthFilter

`AuthFilter` impedisce agli utenti non autenticati di accedere direttamente alle pagine protette.

Tra le risorse protette sono presenti:

- Home;
- lista libri;
- inserimento libro;
- modifica libro;
- chat.

### RoleFilter

`RoleFilter` protegge:

`/admin/*`

e:

`/tecnico/*`

Gli utenti con un ruolo non autorizzato vengono reindirizzati alla Home.

---

## Gestione libri

Gli utenti autenticati possono:

- visualizzare il catalogo;
- inserire nuovi libri;
- modificare libri esistenti.

I dati gestiti comprendono:

- titolo;
- autore;
- ISBN;
- anno di pubblicazione;
- genere;
- disponibilità.

Sono presenti controlli backend per impedire valori non validi.

---

## Sicurezza output HTML

I dati provenienti dagli utenti e dal database vengono sottoposti a escaping prima di essere mostrati nelle principali JSP.

Questo riduce il rischio che contenuti HTML o JavaScript inseriti nei campi vengano interpretati dal browser.

---

## Firebase Chat

La chat utilizza Firebase Realtime Database.

Gli utenti autenticati nell'applicazione possono aprire `chat.jsp`.

Il browser esegue quindi l'autenticazione Firebase anonima tramite:

`signInAnonymously()`

I messaggi vengono salvati nel nodo:

`chat/messaggi`

e ricevuti in tempo reale tramite Firebase.

La chat mostra gli ultimi 100 messaggi.

La lunghezza massima di un messaggio è di 500 caratteri.

---

## Firebase Security Rules

Le regole del Realtime Database sono versionate nel repository nel file:

`firebase/database.rules.json`

Le regole richiedono un utente Firebase autenticato per leggere e scrivere nella chat.

Vengono inoltre validati:

- username;
- testo del messaggio;
- timestamp;
- lunghezza massima del messaggio.

Le stesse regole devono essere pubblicate nella Firebase Console.

---

## Nota sull'identità Firebase

La sessione Java dell'applicazione e l'utente Firebase anonimo sono due sistemi di autenticazione separati.

Questa soluzione è sufficiente per il progetto didattico.

In un'applicazione di produzione sarebbe preferibile utilizzare Firebase Custom Tokens o un'altra integrazione backend che colleghi direttamente l'identità Java all'identità Firebase.

---

## Avvio del progetto

Prerequisiti:

- Java 21;
- Maven;
- Apache Tomcat 10.1;
- MySQL 8;
- database `biblioteca_online`;
- variabile `BIBLIOTECA_DB_PASSWORD`.

### Compilazione

Dalla cartella principale del progetto:

`mvn clean package`

Se la compilazione è corretta Maven restituisce:

`BUILD SUCCESS`

### Avvio con Eclipse

1. importare il progetto Maven;
2. configurare Tomcat 10.1;
3. aggiungere `biblioteca-online` al server;
4. avviare Tomcat;
5. aprire il browser.

Indirizzo locale:

`http://localhost:8080/biblioteca-online/`

---

## Git

Il progetto utilizza un workflow basato su più branch.

### `master`

Contiene la versione stabile.

### `develop`

Contiene la versione di integrazione e sviluppo.

### `feature/*`

Ogni funzionalità o gruppo di modifiche viene sviluppato su un branch feature.

Esempi utilizzati durante lo sviluppo:

- `feature/documentazione`
- `feature/architettura-client-server`
- `feature/database-schema`
- `feature/revisione-finale`
- `feature/configurazione-finale`

Flusso utilizzato:

feature  
↓  
develop  
↓  
master

---

## File esclusi da Git

Il file `.gitignore` impedisce di versionare elementi locali o generati, tra cui:

- `target/`;
- configurazioni IDE locali;
- file `.env`;
- file locali contenenti segreti;
- log;
- file di sistema.

Le password personali non devono essere inserite nel repository.

---

## Test effettuati

Durante lo sviluppo sono stati verificati:

- registrazione nuovo utente;
- login con password;
- OTP;
- accesso alla Home;
- inserimento libro;
- visualizzazione libri;
- modifica libro;
- chat realtime Firebase;
- logout;
- blocco delle pagine dopo logout;
- blocco `/admin/` per UTENTE;
- blocco `/tecnico/` per UTENTE;
- accesso `/admin/` per ADMIN;
- accesso `/tecnico/` per SERVIZIO_TECNICO;
- blocco `/admin/` per SERVIZIO_TECNICO;
- compilazione Maven con `BUILD SUCCESS`.

---

## Azure

Azure CLI è stato configurato nell'ambiente di sviluppo.

Il deployment dell'applicazione su Azure Web App non è incluso nella versione attuale perché richiede una subscription Azure attiva.

L'applicazione è attualmente configurata e testata per l'esecuzione locale tramite Apache Tomcat.

---

## Stato del progetto

Le principali funzionalità richieste sono implementate e testate:

- autenticazione;
- OTP;
- sessioni;
- ruoli;
- gestione libri;
- MySQL;
- Firebase realtime chat;
- sicurezza Firebase;
- Git/GitHub;
- struttura client/server;
- documentazione database.