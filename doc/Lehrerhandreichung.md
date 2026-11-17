# MyKa – Lehrerhandreichung zu Lexer, Parser, Interpreter

Grundlage: `Arbeitsblatt.pdf` (3 Seiten, Nov. 2021) und `src.zip` (Quellcode, Stand: Musterlösung).
Ich habe den gesamten Code kompiliert und Lexer, Parser und Interpreter mit Testprogrammen geprüft (Interpreter mit einem GUI-freien Ersatz-Controller). Was ich nur aus dem Code gelesen und **nicht** ausgeführt habe, ist ausdrücklich so markiert.

---

## 1. Das Wichtigste vorab

1. **Das ZIP enthielt nur die fertige Musterlösung, keine Vorlagen.** Das Arbeitsblatt spricht in Aufgabe 3, 5 und 6 von „der Dateivorlage". `Lexer.java`, `Parser.java` und `Interpreter.java` waren im ZIP vollständig ausgefüllt. Die Vorlagen habe ich deshalb aus der Musterlösung erzeugt (Ordner `schueler/`, Beschreibung in Abschnitt 6). Die korrigierte Lösung liegt in `src/`.
2. **Die Befehlsübersicht auf dem Arbeitsblatt ist unvollständig.** Eigene Anweisungen (`Anweisung Name … endeAnweisung`, Aufruf per Name) fehlen dort, obwohl Lexer (Token `T_Bez`), Parser und Interpreter sie behandeln. Aufgabe 1 („welche Token werden benötigt?") ist so nicht vollständig lösbar. Das neue Arbeitsblatt enthält die eigenen Anweisungen.
3. **Zwei Forderungen des Arbeitsblatts löst die Musterlösung anders:**
   - Aufgabe 3e verlangt `System.err.println`. Die alte Musterlösung setzt nur einen Statuscode, und die Oberfläche zeigt `Lexer.getStatusText()` an. In der neuen Lösung wird der Text zusätzlich am Ende von `lex()` auf der Konsole ausgegeben.
   - Aufgabe 5 verlangt einen Kellerautomaten mit `switch-case`. Der alte `Parser.java` bestand aus gegenseitig rekursiven Methoden. **Der `Parser.java` in `src/` ist jetzt die `switch`-Variante.** Auf 600 000 zufälligen und mutierten Token-Folgen liefert er dasselbe Ergebnis wie der alte (0 Abweichungen).
4. **Der Controller erwartet versteckte Schnittstellen** (Abschnitt 3). Wichtigste Falle: Er wertet beim Parser den Text `"Success!!"` aus, nicht den Rückgabewert.
5. **Die alte Musterlösung enthielt Fehler** (Abschnitt 5), z. B. brach `wiederhole 0 mal …` ab. Die Korrekturen sind in `src/` eingearbeitet und getestet.
6. **`Parser2.java` ist im neuen Repo-Stand nicht mehr enthalten.** Sie war unfertig, und der Controller nutzte sie nicht.

---

## 2. Startpaket und Ablage im Repository

Da die Lösung öffentlich sein darf und das Repository auch anderen Lehrkräften dienen soll, genügt **ein** Repository:

```
MyKaCompiler/
  README.md                    ← Abschnitt „Für Lehrkräfte" ergänzen (Verweis auf doc/ und schueler/)
  src/myka/…                   ← Musterlösung (korrigiert), mit Bildern in src/myka/img
  schueler/src/myka/…          ← Startpaket für die SchülerInnen (Vorlagen mit TODOs), mit Bildern
  doc/Arbeitsblatt.md          ← Arbeitsblatt für SchülerInnen (IntelliJ)
  doc/Lehrerhandreichung.md    ← diese Datei
  doc/Arbeitsblatt.pdf         ← das alte Arbeitsblatt (nach Belieben ersetzen oder archivieren)
```

Das Startpaket ist der Ordner **`schueler/`**. SchülerInnen laden ihn herunter (Code → Download ZIP, oder `git clone` und dann nur den Ordner nutzen). `schachbrett2.txt` liegt darin direkt unter `schueler/`.

Das Arbeitsblatt liegt vorerst als Markdown vor. Für den Ausdruck kannst du es aus IntelliJ, Typora oder per Pandoc in ein PDF umwandeln.

Inhalt von `schueler/` im Einzelnen:

| Datei | Weitergeben? | Anmerkung |
|---|---|---|
| `Token.java` | ja, unverändert | Gegenstand von Aufgabe 1 |
| `List.java`, `Stack.java` | ja, unverändert | Abiturklassen |
| `Hilfsfunktionen.java` | ja, unverändert | Enthält `convertTokenListToArray`, die der Controller braucht. Zusatzaufgabe 3f („in Array umwandeln") ist damit vorweggenommen. Im neuen Arbeitsblatt steht sie als „selbst schreiben und vergleichen". |
| `MyKaController.java`, `MyKaView.java`, `RobotArea.java`, `RobotCanvas.java`, `Dateiaktionen.java`, `Executor.java` | ja, unverändert | Oberfläche, Roboter, Ausführungs-Thread. `Executor.java` enthält das `try/finally` aus Abschnitt 5, Punkt 6, damit die Oberfläche auch nach einer Exception im Schüler-Interpreter wieder bedienbar ist. |
| `myka/img/` (5 PNG-Dateien) | ja | Der Code lädt `img/…` relativ zur Klasse, der Ordner muss also **`src/myka/img/`** heißen. `src/img/` ist ein identisches Duplikat und kann entfallen. |
| `schachbrett2.txt` | ja | Abschlusstest für den Interpreter |
| **Vorlage** `Lexer.java` | ja | neu erzeugt, siehe Abschnitt 6 |
| **Vorlage** `Parser.java` | ja | neu erzeugt, siehe Abschnitt 6 |
| **Vorlage** `Interpreter.java` | ja | neu erzeugt, siehe Abschnitt 6 |
| `release/myka.jar` bzw. `release/myka-java8.jar` | optional | Fertiges Programm zum Ausprobieren des Zielverhaltens (Java 21+ bzw. ab Java 8). Da die Lösung ohnehin öffentlich ist, spricht nichts dagegen. |

Nicht mehr enthalten ist `Parser2.java` (siehe Abschnitt 5, Punkt 7), und `src/img/` entfällt, weil es ein Duplikat von `src/myka/img/` war.

**Projekt einrichten (IntelliJ):** neues Java-Projekt (Build system IntelliJ), Paket `myka`, alle Dateien nach `src/myka` kopieren (Bilder nach `src/myka/img`). Kodierung UTF-8 (File → Settings → Editor → File Encodings), weil `MarkeLöschen` ein `ö` enthält (nicht getestet, aber sicherheitshalber). Gestartet wird `Lexer.java` über das grüne Dreieck an `main`: `Lexer.main` ruft die Oberfläche auf, solange `debug` auf `false` steht. Das Arbeitsblatt ist auf IntelliJ zugeschnitten und trägt oben einen entsprechenden Vermerk. Dass IntelliJ die PNG-Dateien aus `src` automatisch in den Ausgabeordner kopiert, habe ich nach meinem Wissen angenommen, aber nicht ausprobiert. Falls die Bilder fehlen, hilft „Build → Rebuild Project" (steht im Arbeitsblatt).

---

## 3. Schnittstellen zur Oberfläche (der „Vertrag")

Diese Stellen sind auf dem Arbeitsblatt nirgends erwähnt, ohne sie läuft die Oberfläche nicht.

| Klasse | Methode | Wer ruft auf / Was wird erwartet |
|---|---|---|
| `Lexer` | `static List<Token> lex(String)` | Controller. Liefert die Tokenliste, **`null` bei Fehler**. |
| `Lexer` | `static int getStatus()` | Controller. **`0` = fehlerfrei**, negativ = Fehler. |
| `Lexer` | `static String getStatusText()` | Controller zeigt den Text in der Statuszeile. |
| `Parser` | `static boolean parse(Token[])` | Controller übergibt ein **Array** (nicht die Liste). Die im Arbeitsblatt versprochene freie Wahl „Liste oder Array" gibt es nur, wenn der Controller angepasst wird. |
| `Parser` | `static String getFehlerText()` | Der Controller prüft `getFehlerText().equals("Success!!")` und **ignoriert den boolean-Rückgabewert**. Bei Erfolg muss der Text exakt `Success!!` lauten. |
| `Interpreter` | `static void execute(Token[])` | Executor-Thread |
| `Interpreter` | `static void setResult(boolean)` | Der Controller meldet hier das Ergebnis einer Bedingungsabfrage zurück. |
| `Interpreter` | `getFail()`, `stop()`, `setWaitTime(int)`, `getWaitTime()` | Oberfläche (Anhalten, Wartezeit) |

**Befehle und Bedingungen laufen beide über denselben Aufruf:**
`controller.execute(MyKaController.RBefehl, new String[] { token.getWert() });`
Bei einem Befehl bewegt sich der Roboter. Bei einer Bedingung schreibt der Controller das Ergebnis per `Interpreter.setResult(...)` in das Attribut `result`. Das ist für SchülerInnen nicht erschließbar und muss in der Vorlage stehen.

---

## 4. Aufgabe für Aufgabe: Was wird erwartet?

### Aufgabe 1 – Token

**Erwartet:** Gruppen von Wörtern mit gleichem Verhalten, jeweils mit Typ und Werten, und ein Abgleich mit `Token.java`.

| Typ | Konstante | Werte (`wert`) |
|---|---|---|
| Zahl | `T_Zahl = 1` | Ziffernfolge; zusätzlich `value` als `int` |
| Bedingung | `T_Cond = 2` | IstWand, NichtIstWand, IstMarke, NichtIstMarke, IstZiegel, NichtIstZiegel |
| Befehl | `T_Move = 3` | Schritt, LinksDrehen, RechtsDrehen, Hinlegen, Aufheben, MarkeSetzen, MarkeLöschen |
| Steuerwort | `T_Cont = 4` | wiederhole, mal, solange, endewiederhole, wenn, dann, sonst, endewenn, Anweisung, endeAnweisung |
| Bezeichner | `T_Bez = 5` | selbst gewählte Namen eigener Anweisungen |
| unbekannt | `T_Unknown = 0` (private) | nur Vorgabewert |

- Feinere Einteilungen (ein Typ pro Schlüsselwort) sind fachlich gleichwertig. Da Parser und Interpreter aber Typ **und** Wert der Musterlösung benutzen, sollte `Token.java` unverändert ausgegeben werden.
- Zum Nachvollziehen: Der Konstruktor wandelt bei `T_Zahl` den String per `Integer.parseInt` in `value` um. `zeile` und `pos` werden nur über Setter gesetzt.
- Stolperstein: Eine Zahl über 2 147 483 647 löst eine `NumberFormatException` aus (nicht abgefangen, geprüft).

### Aufgabe 2 – Lexer als Automat

**Erwartet:** Ein Automat mit vier Zuständen plus Fehlersenke. Token entstehen genau an den Übergängen **Wort → Trenner** und **Zahl → Trenner**.

| Zustand | Ziffer z | Buchstabe b | Trenner t | `{` | `}` / sonst |
|---|---|---|---|---|---|
| **Trenner** (Start) | → Zahl | → Wort | → Trenner | → Kommentar | → Senke |
| **Zahl** | → Zahl | → Senke („Buchstabe in Zahl") | → Trenner, **Token** | → Senke | → Senke |
| **Wort** | → Senke („Ziffer im Wort") | → Wort | → Trenner, **Token** | → Senke | → Senke |
| **Kommentar** | → Kommentar | → Kommentar | → Kommentar | → Kommentar | `}` → Trenner, sonst Kommentar |
| **Senke** | → Senke | → Senke | → Senke | → Senke | → Senke |

- **Warum hängt `lex()` ein Leerzeichen an den Text?** Das letzte Token entsteht erst, wenn ein Trennzeichen folgt. Ohne das angehängte Leerzeichen ginge es verloren. Das ist eine gute Denkfrage.
- Der Automat kennt nur „Wort" und „Zahl". Ob ein Wort Befehl, Bedingung oder Steuerwort ist, entscheidet erst `generateTokenFromString` per Vergleich mit Listen.
- **Zusatz Kommentare:** eigener Zustand ohne Token-Erzeugung. Ein Kommentar direkt am Wort (`Schritt{x}`) führt zum Fehler, und genau das meint der Satz „nicht unmittelbar anhängen" auf dem Arbeitsblatt.
- Lücke: Ein nicht geschlossener Kommentar (`Schritt {nie zu`) wird still akzeptiert.

### Aufgabe 3 – Lexer implementieren

| Teil | Erwartung (nach Musterlösung) |
|---|---|
| a) static | `private Lexer() {}` (verhindert `new Lexer()`) und `static` bei allen Attributen und Methoden. Attribute daher `private static`. |
| b) Zustand, Senke | `private static int zustand`; Konstanten `Z_Trenner=0, Z_Alpha=1, Z_Zahl=2, Z_Kommentar=3, Z_Sink=4`. Zusätzlich `status` mit `STAT_GOOD=0, ERR_charunknown=-1, ERR_number_in_word=-2, ERR_char_in_number=-3, ERR_terminal_unknown=-4, ERR_bezUnzulaessig=-5, ERR_zahl_zu_gross=-6` (die Codes sind in der Vorlage fertig). Die Schleife in `lex()` läuft nur, solange `status == STAT_GOOD`. |
| c) Liste | `List<Token>`, Anhängen mit `append` |
| d) Zwischenspeicher | `terminalstring`: beim Beginn eines Wortes/einer Zahl `= "" + c`, danach `+= c`, nach der Token-Erzeugung `= ""` |
| e) Fehler | In `lexstep` (von den SchülerInnen): Ziffer im Wort, Buchstabe in Zahl, unbekanntes Zeichen → `status` setzen und `zustand = Z_Sink` (Musterlösung: Hilfsmethode `fehler(int code)`). Unbekanntes Wort (`wederhole`) und unzulässiger Bezeichner meldet die fertige Methode `erzeugeToken`. Der Text kommt aus `getStatusText()` (fertig) und wird am Ende von `lex()` mit `System.err.println` ausgegeben. |
| f) Zusatz | `Token` hat `zeile`/`pos` schon. Liste → Array: `toFirst()`/`hasAccess()`/`next()`, erst zählen, dann füllen. Ausgabe über `Token.toString()`. |

**Stolpersteine, die auf dem Arbeitsblatt fehlen:**
- `isAlpha` muss das **`ö`** erlauben, sonst scheitert `MarkeLöschen` mit „unbekanntes Zeichen".
- Zeilenzählung: Die alte Musterlösung zählte Zeilenwechsel vor dem `switch`. Dadurch bekam ein Token am Zeilenende die nächste Zeile. In der Vorlage ist die Zählung deshalb die fertige Methode `zeilenzaehlung(c)`, die in `lex()` **nach** dem Erzeugen des Tokens aufgerufen werden muss (steht im Arbeitsblatt und in den Hinweisen). Wer sie vergisst oder zu früh aufruft, sieht falsche Zeilennummern.
- Bezeichner: Nach dem Steuerwort `Anweisung` ist das nächste Wort ein Bezeichner. Das erledigt die fertige Methode `erzeugeToken` (Attribut `nextTokenBez`, Liste `bezeichner`). Der Name darf kein Schlüsselwort sein, und ein Wort, das weder Schlüsselwort noch bereits definierter Name ist, ist ein Fehler.

### Aufgabe 4 – Grammatik und Kellerautomat (Papier)

**Erwartet:** diese Grammatik (aus den Kommentaren von `Parser`/`Parser2`, bereinigt und gegen das Verhalten der Parser geprüft):

```
Programm    → ε | Element Programm | Definition Programm
Element     → Befehl | Bezeichner | Schleife | Verzweigung
Schleife    → wiederhole Zahl mal Block endewiederhole
            | wiederhole solange Bedingung Block endewiederhole
Verzweigung → wenn Bedingung dann Block endewenn
            | wenn Bedingung dann Block sonst Block endewenn
Block       → ε | Element Block
Definition  → Anweisung Bezeichner Block endeAnweisung
```

- **Definitionen nur auf oberster Ebene**, nicht in Schleifen, `wenn` oder anderen Definitionen (geprüft: `wiederhole 2 mal Anweisung A … endeAnweisung endewiederhole` wird abgelehnt).
- `Parser.java` erlaubt Definitionen **zwischen** normalen Befehlen. `Parser2` verlangt sie alle am Anfang. Beides ist vertretbar, entscheide es bewusst (siehe Abschnitt 7).
- Dass ein Bezeichner vor dem Aufruf definiert sein muss, erzwingt schon der **Lexer** (unbekanntes Wort → Fehler).

**Kellerautomat:** Kelleralphabet `# w i s a`.

| Eingabe | Bedingung am Keller | Aktion |
|---|---|---|
| `wiederhole` | – | push `w`, danach Zahl+`mal` oder `solange`+Bedingung erwarten |
| `endewiederhole` | oben `w` | pop |
| `wenn` | – | push `i`, danach Bedingung, `dann` |
| `sonst` | oben `i` | pop, push `s` |
| `endewenn` | oben `i` oder `s` | pop |
| `Anweisung` | oben `#` (nur oberste Ebene) | push `a`, danach Bezeichner |
| `endeAnweisung` | oben `a` | pop |
| Befehl / Bezeichner | – | nichts |

- Verschachtelung: Ein Endwort ist nur erlaubt, wenn oben das passende Kellerzeichen liegt. Daher wird `wiederhole … wenn … endewiederhole endewenn` abgelehnt (geprüft).
- Akzeptiert wird, wenn die Eingabe zu Ende ist, der Automat im Normalzustand steht und nur `#` im Keller liegt.
- **Warum ein Keller?** Die Schachtelungstiefe ist unbegrenzt, ein endlicher Automat kann nicht mitzählen.
- Hinweis: Der Kommentar in `Parser2` („müsste ohne Kellerspeicher möglich sein") ist irreführend. Rekursiver Abstieg nutzt den Java-Aufrufstack als Keller.

### Aufgabe 5 – Parser implementieren

**Erwartet:** Zustand + Keller, Zustandsübergänge mit `switch-case` (laut Arbeitsblatt), sinnvolle Fehlermeldungen beim Wechsel in die Fehlersenke.

- **Referenz:** `src/myka/Parser.java`. Zustände: `Z_Block`, `Z_NachWiederhole`, `Z_NachZahl`, `Z_NachSolange`, `Z_NachWenn`, `Z_NachWennBed`, `Z_NachAnweisung`, `Z_Sink`. Dieser Parser verhält sich auf **600 000 zufälligen und mutierten Token-Folgen identisch** zum alten rekursiven `Parser.java` (0 Abweichungen).
- Typische Fehler der SchülerInnen: (1) Endprüfung vergessen, dann wird `wiederhole 3 mal Schritt` akzeptiert. (2) Fehlertext bei Erfolg nicht `"Success!!"`. (3) `Anweisung` auch in Schleifen erlaubt. (4) `sonst` doppelt erlaubt.
- Didaktischer Bonus: Der alte rekursive Parser ruft sich pro Token selbst auf und stürzt bei 5 000 Token mit `StackOverflowError` ab (1 000 Token gingen noch; bei Unterrichtsprogrammen irrelevant). Die iterative `switch`-Variante schafft 100 000 Token.

### Aufgabe 6 – Interpreter

**Erwartet:** Die Methoden aus der Vorlage anhand ihrer Kommentare (Javadoc steht schon in der Musterlösung) implementieren: `execute`, `executeToken`, `bed`, `loop`, `anweisung`, `vorlaufPassendes`.

**Zentrale Spielregel (Invariante):** Jede dieser Methoden lässt `curpos` auf dem **letzten Token der Struktur** stehen (`endewenn`, `endewiederhole`, `endeAnweisung` bzw. der Befehl selbst). Erst `executeToken` erhöht danach um 1. Das ist die häufigste Fehlerquelle (Endlosschleife oder Überspringen).

- **Sprungmarken:** `HashMap<String,Integer>`, Bezeichner → Index des ersten Tokens nach dem Namen in der Definition. Beim Aufruf: Position merken, springen, `anweisung()`, zurückspringen.
- **`vorlaufPassendes`:** überspringt bis zum passenden Endwort und beachtet dabei verschachtelte `wenn`/`wiederhole`. Es prüft das erste Token nicht auf „öffnet eine Struktur", der Aufruf muss deshalb auf einem Nicht-Öffner starten (Condition, `mal`, Bezeichner).
- `depth`/`MAX_REK_DEPTH` fängt rekursive Anweisungen ab (`Anweisung A A endeAnweisung A`).

---

## 5. Befunde zur alten Musterlösung (geprüft, in `src/` behoben)

| # | Befund | Folge | Korrektur (getestet) |
|---|---|---|---|
| 1 | `Lexer`: `zeilenpos` wird in `lex()` nicht zurückgesetzt | Position wird negativ (z. B. `Pos: -31`), sobald vor dem Token kein Zeilenwechsel liegt | `zeilenpos = 0;` in `lex()` |
| 2 | `Lexer`: Token bekommt `aktzeile` und `pos` **des Trennzeichens** | Ein Token am Zeilenende erhält die **nächste** Zeile, Position 0 (`Schritt\n  Schritt` → `2/0` und `2/10`). Betrifft Zusatzaufgabe 3f. | Beginn merken: beim Start eines Wortes/einer Zahl `startZeile = aktzeile; startSpalte = pos - zeilenpos;`, im Token `startZeile`/`startSpalte` setzen. Ergebnis: `1/1`, `2/3`, `3/1`. |
| 3 | `Lexer`: `nextTokenBez` bleibt `true`, wenn ein Programm mit `Anweisung` endet | Der **nächste** Lauf scheitert (`Schritt` wird als Bezeichner gewertet, Status -5) | `nextTokenBez = false; terminalstring = "";` in `lex()` |
| 4 | `Lexer`: Fehlercode -5 hat in `getStatusText()` keinen Text | Statuszeile zeigt nur „Zeile: … Pos: …" | `case ERR_bezUnzulaessig:` ergänzen. **Umgesetzt:** `getStatusText()` hat jetzt für jeden Code einen Text mit Zeile (und Spalte bei Zeichenfehlern). |
| 5 | `Interpreter.loop`: `wiederhole 0 mal …` | Abbruch mit „Ausführen einer nicht ausführbaren Kontrollstruktur" (`mal`) | nach der `for`-Schleife `if (anz <= 0) { curpos = pos + 1; vorlaufPassendes("endewiederhole"); }` |
| 6 | Rekursive Anweisung (`Anweisung A A endeAnweisung A`) | `Maximale Rekursionstiefe` wird gemeldet, danach `ArrayIndexOutOfBoundsException` in der Fehlerausgabe. Allgemeiner: `Executor.run` hatte kein `try/finally`. Nach jeder Exception im Interpreter blieb das Executor-Singleton belegt, und kein weiteres Programm ließ sich ausführen (in der Oberfläche geprüft). | Ausgabe absichern: `(curpos < tokenliste.length ? tokenliste[curpos] : "(hinter dem Programmende)")`. **Umgesetzt** in `src/` und `schueler/`: `Interpreter.execute` läuft in `try { … } finally { … }`; Statuszeile, `enableInput()`, `running` und `s` werden immer zurückgesetzt. Getestet: Nach einer absichtlich ausgelösten Exception läuft das nächste Programm normal. |
| 7 | `Parser2.java` | `debug = true` (Konsolen-Flut). Akzeptiert **ungültige** Programme (`wiederhole 2 mal Schritt`, `wenn IstWand`, `wiederhole`) und stürzt bei `Anweisung` mit `NullPointerException` ab. Der Controller nutzt `Parser2` nicht. | nicht weitergeben oder löschen. **Umgesetzt:** im neuen Repo-Stand nicht mehr enthalten. |
| 8 | `Beenden` steht in der Schlüsselwortliste des Lexers, aber nicht in der Befehlsübersicht und weder im Parser noch im Interpreter | Wird als Token erkannt und dann vom Parser abgelehnt | **Umgesetzt:** aus der Liste gestrichen. `Beenden` ist jetzt ein „unbekanntes Wort" (Status -4). Wenn du es doch willst, ergänze es in `control` und im Parser/Interpreter. |
| 9 | Zahl > `Integer.MAX_VALUE` | `NumberFormatException` im Konstruktor von `Token`, ungefangen | **Umgesetzt:** `erzeugeToken` fängt sie ab und meldet Status -6 („Zahl zu groß"). |

Alle Korrekturen, einschließlich der Änderung am `Executor` (Zeile 6), sind in `src/myka/` eingearbeitet und mit den Testfällen aus Abschnitt 8 geprüft.

---

## 6. Die Vorlagen in `schueler/src/myka/`

Die Vorlagen und die Lösung in `src/` werden aus **derselben Quelle** erzeugt. In der Vorlage ist nur der markierte TODO-Bereich entfernt und durch Hinweise ersetzt. Dadurch passen beide garantiert zusammen. Jede Datei hat nummerierte TODOs mit Hinweisen im Kommentar. Alle Vorlagen kompilieren mit dem restlichen Projekt, und die Lösung besteht alle Tests aus Abschnitt 8.

| Datei | Fertig vorgegeben | TODOs der SchülerInnen |
|---|---|---|
| `Lexer.java` | `private Lexer()`, `main` (bei `debug = true` Testausgaben, sonst Oberfläche), Fehlercodes, Schlüsselwortlisten, Hilfsattribute, **`erzeugeToken`** (Typzuordnung, Bezeichner, zu große Zahlen), **`zeilenzaehlung`**, `isZiffer`/`isAlpha` (mit `ö`)/`isTrenner`, `debug`, `getStatusText`, `getStatus` | **1** Zustände + `zustand` · **2** `terminalstring` (★ `startZeile`/`startSpalte`) · **3** `lex()` (Hauptschleife, Leerzeichen anhängen, Token anhängen, Fehlerausgabe) · **4** Hilfsmethode `fehler(int)` · **5** `lexstep()` (der `switch`) |
| `Parser.java` | `parse`-Rahmen mit `fehlertext = "Success!!"` und `tokens == null`, `ist(Token, String)`, `getFehlerText`, `debug` | **1** Zustände (die aus Aufgabe 4) · **2** Rumpf von `parse` (Keller, `switch`, Endprüfung) · **3** Hilfsmethode für Fehlermeldungen |
| `Interpreter.java` | alle Attribute, Klassenkommentar mit Roboter-Zeile und Spielregel für `curpos`, `warten()`, `vorlaufPassendes(String)`, `getFail`, `setResult`, `fehler`, `stop`, `setWaitTime`/`getWaitTime`, `debug` | **1** `execute` · **2** `executeToken` · **3** `bed` · **4** `loop` · **5** `anweisung` · **6** `vorlaufPassendes(String, String)` |

Ohne ausgefüllte TODOs startet die Oberfläche trotzdem. Der Lexer liefert dann `null`, der Parser meldet „noch nicht implementiert", der Interpreter tut nichts.

**Geändert gegenüber deiner Musterlösung** (damit die Vorlagen klar bleiben):
- Die Token-Zuordnung steckt in `erzeugeToken(String)` statt in `generateTokenFromString`. Die Art (Zahl oder Wort) erkennt sie am ersten Zeichen, sodass sie nicht von den Zustandskonstanten der SchülerInnen abhängt.
- Das Anhängen eines Leerzeichens an den Text (Denkfrage aus Aufgabe 2) ist **nicht** vorgegeben.
- Im Interpreter heißt die `Thread.sleep`-Zeile jetzt `warten()`.

---

## 7. Entscheidungen, die bei dir liegen

Ich habe jeweils eine Voreinstellung gewählt. Sag Bescheid, wenn du es anders willst.

1. **Definitionen überall oder nur am Anfang?** Gewählt: wie in deiner Musterlösung überall auf oberster Ebene. Das Arbeitsblatt-Beispiel kommt mit beiden aus.
2. **`Beenden`:** Gewählt: gestrichen (siehe Abschnitt 5, Punkt 8).
3. **Fehlerausgabe:** Gewählt: Status setzen (die Oberfläche zeigt `getStatusText()`) **und** am Ende von `lex()` auf die Konsole schreiben, wie im Arbeitsblatt verlangt.
4. **Parser-Eingabe:** Die „freie Wahl Liste/Array" wurde im neuen Arbeitsblatt gestrichen, weil der Controller ein Array übergibt.

---

## 8. Prüffälle

Alle Ergebnisse stammen aus echten Läufen (Lexer/Parser mit der Musterlösung, Interpreter mit Ersatz-Controller).

**Lexer – gültig**

| Eingabe | Ergebnis |
|---|---|
| `wiederhole 3 mal Schritt endewiederhole` | 5 Token (Steuerwort, Zahl 3, Steuerwort, Befehl, Steuerwort) |
| `{Kommentar} Schritt {noch einer}` + Zeilenwechsel + `Schritt` | 2 Token (Kommentare erzeugen keine) |
| `Anweisung Quadrat wiederhole 4 mal Schritt RechtsDrehen endewiederhole endeAnweisung Quadrat Quadrat` | 11 Token, `Quadrat` jeweils Typ Bezeichner |

**Lexer – Fehler**

| Eingabe | Status | Bedeutung |
|---|---|---|
| `Schri3tt` | -2 | Ziffer im Wort |
| `wiederhole 3x mal` | -3 | Buchstabe in Zahl |
| `wederhole 3 mal Schritt endewiederhole` | -4 | unbekanntes Schlüsselwort |
| `Hallo` | -4 | Bezeichner nicht deklariert |
| `Schritt;` | -1 | unbekanntes Zeichen |
| `Schritt{x}` | -1 | Kommentar klebt am Wort |
| `Anweisung Schritt endeAnweisung` | -5 | Bezeichner ist Schlüsselwort |
| `Schritt Beenden` | -4 | `Beenden` gibt es nicht (mehr) |
| `wiederhole 99999999999 mal` | -6 | Zahl zu groß |

**Parser – gültig:** leeres Programm · `Schritt` · `wiederhole 2 mal wenn IstWand dann Schritt endewenn endewiederhole` · `wenn IstWand dann Schritt sonst Schritt endewenn` · `Anweisung A Schritt endeAnweisung A` · `wiederhole solange IstWand wiederhole 3 mal wenn IstMarke dann Schritt endewenn endewiederhole endewiederhole`

**Parser – ungültig**

| Eingabe | Grund |
|---|---|
| `wiederhole 3 mal Schritt` | `endewiederhole` fehlt |
| `wiederhole 3 Schritt endewiederhole` | `mal` fehlt |
| `wiederhole solange Schritt endewiederhole` | Bedingung fehlt |
| `wenn IstWand Schritt endewenn` | `dann` fehlt |
| `wenn IstWand dann Schritt sonst Schritt` | `endewenn` fehlt |
| `wenn IstWand dann Schritt sonst Schritt sonst Schritt endewenn` | zweites `sonst` |
| `Schritt endewenn` | `endewenn` ohne `wenn` |
| `wiederhole 2 mal wenn IstWand dann Schritt endewiederhole endewenn` | falsch verschachtelt |
| `wiederhole 2 mal Anweisung A Schritt endeAnweisung endewiederhole` | Definition in Schleife |
| `Anweisung A Schritt` | `endeAnweisung` fehlt |
| `5` | Zahl allein |

**Interpreter** (1D-Testwelt: Wand ab Feld 5; S = Schritt, L = LinksDrehen, R = RechtsDrehen, M = MarkeSetzen)

| Programm | Erwartete Befehlsfolge |
|---|---|
| `wiederhole 3 mal Schritt endewiederhole` | SSS |
| `wiederhole 0 mal Schritt endewiederhole LinksDrehen` | L (in der alten Musterlösung Abbruch, jetzt behoben) |
| `wiederhole solange NichtIstWand Schritt endewiederhole` | SSSSS |
| `wiederhole solange IstWand Schritt endewiederhole RechtsDrehen` | R |
| `wenn IstWand dann LinksDrehen sonst Schritt endewenn` | S |
| `wenn IstWand dann Schritt endewenn LinksDrehen` | L |
| `wiederhole 2 mal wenn NichtIstWand dann wiederhole 2 mal Schritt endewiederhole sonst LinksDrehen endewenn endewiederhole` | SSSS |
| `Anweisung A Schritt endeAnweisung Anweisung B A A endeAnweisung B` | SS |
| `Anweisung Q wiederhole 2 mal LinksDrehen endewiederhole endeAnweisung Q Q` | LLLL |
| `wiederhole solange NichtIstWand MarkeSetzen Schritt endewiederhole wenn IstMarke dann LinksDrehen sonst RechtsDrehen endewenn` | MSMSMSMSMSR |
| `Anweisung A A endeAnweisung A` | Abbruch mit Meldung „Maximale Rekursionstiefe" |

**Oberfläche (Startwelt):** Nach „Welt zurücksetzen" ist das Feld 5 × 10 groß, der Roboter steht bei (0, 0) und schaut nach Süden, vor ihm liegen 9 freie Felder (in der laufenden Oberfläche geprüft). Darauf beruhen die Erwartungen in Aufgabe 6 des Arbeitsblatts.

**Beispielprogramm aus der README** (Schachbrettmuster, 46 Token): Lexer fehlerfrei, der neue `Parser` liefert `Success!!`. `schachbrett2.txt` enthält dasselbe Programm. Beide zeichnen in der Oberfläche (Startwelt 5 × 10) ein vollständiges Schachbrettmuster aus Marken, der Roboter steht am Ende in der gegenüberliegenden Ecke (Aufgabe 6, letzte Zeile).