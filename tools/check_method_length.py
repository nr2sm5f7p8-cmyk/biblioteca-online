from pathlib import Path
import re

ROOT = Path("src/main/java")
MAX_LINES = 30

CONTROL_WORDS = (
    "if", "for", "while", "switch", "catch",
    "try", "else", "do", "synchronized"
)

def possibile_metodo(testo):
    testo = " ".join(testo.split())

    if "(" not in testo or ")" not in testo or "{" not in testo:
        return False

    if any(testo.startswith(parola + " ") or
           testo.startswith(parola + "(")
           for parola in CONTROL_WORDS):
        return False

    if re.search(r"\b(class|interface|enum|record)\b", testo):
        return False

    return re.search(
        r"(public|protected|private|static|final|synchronized|native|\w+)"
        r".*\w+\s*\([^;]*\)\s*(throws\s+[^{]+)?\{",
        testo
    ) is not None


def nome_metodo(testo):
    testo = " ".join(testo.split())

    matches = re.findall(r"([A-Za-z_$][\w$]*)\s*\(", testo)

    if matches:
        return matches[-1]

    return "metodo_sconosciuto"


def analizza_file(percorso):
    righe = percorso.read_text(
        encoding="utf-8",
        errors="ignore"
    ).splitlines()

    risultati = []

    i = 0

    while i < len(righe):
        candidato = []
        inizio = i

        j = i

        while j < len(righe) and j < i + 12:
            riga = righe[j].strip()

            if riga.startswith("@"):
                j += 1
                inizio = j
                continue

            candidato.append(righe[j])

            testo = "\n".join(candidato)

            if "{" in testo:
                if possibile_metodo(testo):
                    profondita = 0
                    iniziato = False
                    k = inizio

                    while k < len(righe):
                        for carattere in righe[k]:
                            if carattere == "{":
                                profondita += 1
                                iniziato = True
                            elif carattere == "}":
                                profondita -= 1

                        if iniziato and profondita == 0:
                            numero_righe = k - inizio + 1

                            risultati.append(
                                (
                                    nome_metodo(testo),
                                    inizio + 1,
                                    k + 1,
                                    numero_righe
                                )
                            )

                            i = k
                            break

                        k += 1

                break

            if ";" in testo:
                break

            j += 1

        i += 1

    return risultati


def main():
    if not ROOT.exists():
        print("Cartella src/main/java non trovata.")
        return

    metodi_lunghi = []

    for file_java in ROOT.rglob("*.java"):
        for nome, inizio, fine, lunghezza in analizza_file(file_java):
            if lunghezza > MAX_LINES:
                metodi_lunghi.append(
                    (
                        file_java,
                        nome,
                        inizio,
                        fine,
                        lunghezza
                    )
                )

    print()
    print("=== CONTROLLO METODI BACKEND ===")
    print(f"Limite richiesto: {MAX_LINES} righe")
    print()

    if not metodi_lunghi:
        print("OK: nessun metodo supera le 30 righe.")
        return

    print("METODI DA SISTEMARE:")
    print()

    for file_java, nome, inizio, fine, lunghezza in metodi_lunghi:
        print(
            f"{file_java} | {nome} | "
            f"righe {inizio}-{fine} | "
            f"{lunghezza} righe"
        )


if __name__ == "__main__":
    main()