USE biblioteca_online;

-- =========================================================
-- 1. COMUNITA
-- =========================================================

CREATE TABLE comunita (
    id_comunita INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    descrizione VARCHAR(500) DEFAULT NULL,
    data_creazione TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_comunita),
    UNIQUE KEY uk_comunita_nome (nome)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 2. ESTENSIONE PROFILO UTENTE
-- =========================================================

ALTER TABLE utenti
    ADD COLUMN telefono VARCHAR(30) DEFAULT NULL
        AFTER email,
    ADD COLUMN id_comunita INT DEFAULT NULL
        AFTER id_ruolo,
    ADD COLUMN generi_preferiti VARCHAR(500) DEFAULT NULL
        AFTER id_comunita;

ALTER TABLE utenti
    ADD KEY idx_utenti_comunita (id_comunita);

ALTER TABLE utenti
    ADD CONSTRAINT fk_utenti_comunita
        FOREIGN KEY (id_comunita)
        REFERENCES comunita (id_comunita)
        ON DELETE SET NULL;


-- =========================================================
-- 3. ESTENSIONE LIBRI
-- =========================================================

ALTER TABLE libri
    ADD COLUMN descrizione VARCHAR(1000) DEFAULT NULL
        AFTER genere;

-- Uno stesso ISBN puo essere posseduto da utenti diversi.
ALTER TABLE libri
    DROP INDEX isbn;

ALTER TABLE libri
    ADD KEY idx_libri_isbn (isbn);

ALTER TABLE libri
    ADD KEY idx_libri_titolo (titolo);

ALTER TABLE libri
    ADD KEY idx_libri_autore (autore);

ALTER TABLE libri
    ADD KEY idx_libri_genere (genere);


-- =========================================================
-- 4. LISTA DESIDERI
-- =========================================================

CREATE TABLE desideri_libri (
    id_desiderio INT NOT NULL AUTO_INCREMENT,
    id_utente INT NOT NULL,
    id_libro INT NOT NULL,
    data_inserimento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_desiderio),

    UNIQUE KEY uk_desiderio_utente_libro (
        id_utente,
        id_libro
    ),

    KEY idx_desideri_utente (id_utente),
    KEY idx_desideri_libro (id_libro),

    CONSTRAINT fk_desideri_utente
        FOREIGN KEY (id_utente)
        REFERENCES utenti (id_utente)
        ON DELETE CASCADE,

    CONSTRAINT fk_desideri_libro
        FOREIGN KEY (id_libro)
        REFERENCES libri (id_libro)
        ON DELETE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 5. OFFERTE PRESTITO / SCAMBIO
-- =========================================================

CREATE TABLE offerte_libri (
    id_offerta INT NOT NULL AUTO_INCREMENT,
    id_libro INT NOT NULL,
    id_proprietario INT NOT NULL,

    tipo_offerta ENUM(
        'PRESTITO',
        'SCAMBIO',
        'ENTRAMBI'
    ) NOT NULL,

    condizioni VARCHAR(1000) DEFAULT NULL,

    attiva TINYINT(1) NOT NULL DEFAULT 1,

    data_creazione TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_modifica TIMESTAMP NULL
        DEFAULT NULL
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id_offerta),

    KEY idx_offerte_libro (id_libro),
    KEY idx_offerte_proprietario (id_proprietario),
    KEY idx_offerte_attiva (attiva),

    CONSTRAINT fk_offerte_libro
        FOREIGN KEY (id_libro)
        REFERENCES libri (id_libro)
        ON DELETE CASCADE,

    CONSTRAINT fk_offerte_proprietario
        FOREIGN KEY (id_proprietario)
        REFERENCES utenti (id_utente)
        ON DELETE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 6. RICHIESTE PRESTITO / SCAMBIO
-- =========================================================

CREATE TABLE richieste_libri (
    id_richiesta INT NOT NULL AUTO_INCREMENT,
    id_offerta INT NOT NULL,
    id_richiedente INT NOT NULL,

    tipo_richiesta ENUM(
        'PRESTITO',
        'SCAMBIO'
    ) NOT NULL,

    messaggio_modalita VARCHAR(1000) NOT NULL,

    stato ENUM(
        'IN_ATTESA',
        'ACCETTATA',
        'RIFIUTATA',
        'COMPLETATA',
        'ANNULLATA'
    ) NOT NULL DEFAULT 'IN_ATTESA',

    data_richiesta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_risposta TIMESTAMP NULL DEFAULT NULL,
    data_completamento TIMESTAMP NULL DEFAULT NULL,

    PRIMARY KEY (id_richiesta),

    KEY idx_richieste_offerta (id_offerta),
    KEY idx_richieste_richiedente (id_richiedente),
    KEY idx_richieste_stato (stato),

    CONSTRAINT fk_richieste_offerta
        FOREIGN KEY (id_offerta)
        REFERENCES offerte_libri (id_offerta)
        ON DELETE CASCADE,

    CONSTRAINT fk_richieste_richiedente
        FOREIGN KEY (id_richiedente)
        REFERENCES utenti (id_utente)
        ON DELETE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 7. RECENSIONI
-- =========================================================

CREATE TABLE recensioni_scambi (
    id_recensione INT NOT NULL AUTO_INCREMENT,
    id_richiesta INT NOT NULL,
    id_autore INT NOT NULL,
    id_destinatario INT NOT NULL,

    voto TINYINT NOT NULL,
    testo VARCHAR(1000) DEFAULT NULL,

    data_recensione TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_recensione),

    UNIQUE KEY uk_recensione_richiesta_autore (
        id_richiesta,
        id_autore
    ),

    KEY idx_recensioni_destinatario (id_destinatario),

    CONSTRAINT fk_recensioni_richiesta
        FOREIGN KEY (id_richiesta)
        REFERENCES richieste_libri (id_richiesta)
        ON DELETE CASCADE,

    CONSTRAINT fk_recensioni_autore
        FOREIGN KEY (id_autore)
        REFERENCES utenti (id_utente)
        ON DELETE CASCADE,

    CONSTRAINT fk_recensioni_destinatario
        FOREIGN KEY (id_destinatario)
        REFERENCES utenti (id_utente)
        ON DELETE CASCADE,

    CONSTRAINT chk_recensioni_voto
        CHECK (voto BETWEEN 1 AND 5)

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 8. MESSAGGI PRIVATI
-- =========================================================

CREATE TABLE messaggi_privati (
    id_messaggio INT NOT NULL AUTO_INCREMENT,
    id_mittente INT NOT NULL,
    id_destinatario INT NOT NULL,

    testo VARCHAR(2000) NOT NULL,

    letto TINYINT(1) NOT NULL DEFAULT 0,

    data_invio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_lettura TIMESTAMP NULL DEFAULT NULL,

    PRIMARY KEY (id_messaggio),

    KEY idx_messaggi_mittente (id_mittente),
    KEY idx_messaggi_destinatario (id_destinatario),
    KEY idx_messaggi_lettura (
        id_destinatario,
        letto
    ),

    CONSTRAINT fk_messaggi_mittente
        FOREIGN KEY (id_mittente)
        REFERENCES utenti (id_utente)
        ON DELETE CASCADE,

    CONSTRAINT fk_messaggi_destinatario
        FOREIGN KEY (id_destinatario)
        REFERENCES utenti (id_utente)
        ON DELETE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;