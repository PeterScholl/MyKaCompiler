# MyKa – Lehrerhandreichung zu Lexer, Parser, Interpreter

Diese Handreichung begleitet das Arbeitsblatt `doc/Arbeitsblatt.md`. Darin entwickeln SchülerInnen der Oberstufe für die kleine Robotersprache MyKa einen Lexer (endlicher Automat), einen Parser (Kellerautomat) und einen Interpreter. Die Handreichung beschreibt das Material, die Schnittstellen zur fertigen Oberfläche, den Erwartungshorizont zu jeder Aufgabe und Prüffälle für die Korrektur.

Die Idee zu MyKa stammt von [inf-schule.de](https://www.inf-schule.de/automaten-sprachen/interpretercompiler/syntaxsemantikueberblick/einstieg_myka).

---

## 1. Material im Überblick

```
MyKaCompiler/
  src/myka/…                   ← Musterlösung, Bilder in src/myka/img
  schueler/src/myka/…          ← Startpaket: Vorlagen mit TODOs, Oberfläche, Bilder
  schueler/schachbrett2.txt    ← Abschlusstest für den Interpreter
  doc/Arbeitsblatt.md          ← Arbeitsblatt für SchülerInnen (IntelliJ)
  doc/Lehrerhandreichung.md    ← diese Datei
  release/myka.jar             ← fertiges Programm (Java 21+)
  release/myka-java8.jar       ← fertiges Programm (ab Java 8)
  myKaFiles/                   ← weitere Beispielprogramme und Welten
```

**Startpaket:** SchülerInnen erhalten den Ordner `schueler/` (auf GitHub über „Code → Download ZIP“ oder per `git clone`) und das Arbeitsblatt.

**Arbeitsblatt drucken:** Es liegt als Markdown vor und lässt sich z. B. mit IntelliJ, Typora oder Pandoc in ein PDF umwandeln.

**Fertiges Programm:** Mit den Jar-Dateien in `release/` können SchülerInnen vorab ausprobieren, wie sich das fertige Programm verhalten soll. Da die Musterlösung ohnehin öffentlich ist, spricht nichts dagegen.

Inhalt von `schueler/src/myka/`:

| Datei | Anmerkung |
|---|---|
| `Token.java` | Gegenstand von Aufgabe 1, wird nicht verändert |
| `List.java`, `Stack.java` | Abiturklassen |
| `Hilfsfunktionen.java` | Enthält `convertTokenListToArray`, die der Controller braucht. Zusatz 2 in Aufgabe 3 lässt die Umwandlung selbst schreiben und damit vergleichen. |
| `MyKaController.java`, `MyKaView.java`, `RobotArea.java`, `RobotCanvas.java`, `Dateiaktionen.java`, `Executor.java` | Oberfläche, Roboterwelt und Ausführungs-Thread. Der Executor gibt die Oberfläche auch nach einer Exception im Interpreter wieder frei, sodass ein fehlerhafter Schüler-Interpreter das Programm nicht blockiert. |
| `img/` (5 PNG-Dateien) | Der Code lädt `img/…` relativ zur Klasse. Der Ordner muss deshalb **`src/myka/img/`** heißen. |
| **Vorlage** `Lexer.java`, `Parser.java`, `Interpreter.java` | mit nummerierten TODOs, siehe Abschnitt 4 |

**Projekt einrichten (IntelliJ):** Ein neues Java-Projekt anlegen (Build system IntelliJ), darin das Paket `myka` erstellen und alle Dateien nach `src/myka` kopieren, die Bilder nach `src/myka/img`. Als Kodierung UTF-8 einstellen (File → Settings → Editor → File Encodings), weil `MarkeLöschen` ein `ö` enthält. Gestartet wird `Lexer.java` über das grüne Dreieck an `main`: Solange `debug` auf `false` steht, öffnet `Lexer.main` die Oberfläche. Falls die Roboterbilder fehlen, hilft „Build → Rebuild Project“. Diese Hinweise stehen auch in Aufgabe 0 des Arbeitsblatts.

---

## 2. Schnittstellen zur Oberfläche (der „Vertrag“)

Die Oberfläche ruft Lexer, Parser und Interpreter über feste Methoden auf. In den Vorlagen sind diese Stellen bereits angelegt. Bei Fehlersuche oder eigenen Varianten sollte man sie kennen:

| Klasse | Methode | Wer ruft auf / Was wird erwartet |
|---|---|---|
| `Lexer` | `static List<Token> lex(String)` | Controller. Liefert die Tokenliste, **`null` bei Fehler**. |
| `Lexer` | `static int getStatus()` | Controller. **`0` = fehlerfrei**, negativ = Fehler. |
| `Lexer` | `static String getStatusText()` | Controller zeigt den Text in der Statuszeile. |
| `Parser` | `static boolean parse(Token[])` | Controller übergibt ein **Array** (nicht die Liste). |
| `Parser` | `static String getFehlerText()` | Der Controller prüft `getFehlerText().equals("Success!!")` und **ignoriert den boolean-Rückgabewert**. Bei Erfolg muss der Text exakt `Success!!` lauten. |
| `Interpreter` | `static void execute(Token[])` | Executor-Thread |
| `Interpreter` | `static void setResult(boolean)` | Der Controller meldet hier das Ergebnis einer Bedingungsabfrage zurück. |
| `Interpreter` | `getFail()`, `stop()`, `setWaitTime(int)`, `getWaitTime()` | Oberfläche (Anhalten, Wartezeit) |

**Befehle und Bedingungen laufen über denselben Aufruf:**
`controller.execute(MyKaController.RBefehl, new String[] { token.getWert() });`
Bei einem Befehl bewegt sich der Roboter. Bei einer Bedingung schreibt der Controller das Ergebnis per `Interpreter.setResult(...)` in das Attribut `result`. SchülerInnen können das nicht selbst erschließen, deshalb steht es in der Vorlage und im Arbeitsblatt.

---

## 3. Aufgabe für Aufgabe: Was wird erwartet?

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

- Feinere Einteilungen (ein Typ pro Schlüsselwort) sind fachlich gleichwertig. Parser und Interpreter benutzen aber Typ **und** Wert aus `Token.java`, deshalb bleibt die Datei unverändert.
- Zum Nachvollziehen: Bei `T_Zahl` wandelt der Konstruktor den String per `Integer.parseInt` in `value` um. `zeile` und `pos` werden nur über Setter gesetzt.
- Eine Zahl über 2 147 483 647 löst im Konstruktor eine `NumberFormatException` aus. Der Lexer fängt sie ab und meldet „Zahl zu groß“ (Status -6).

### Aufgabe 2 – Lexer als Automat

**Erwartet:** Ein Automat mit vier Zuständen plus Fehlersenke. Token entstehen genau an den Übergängen **Wort → Trenner** und **Zahl → Trenner**.

| Zustand | Ziffer z | Buchstabe b | Trenner t | `{` | `}` / sonst |
|---|---|---|---|---|---|
| **Trenner** (Start) | → Zahl | → Wort | → Trenner | → Kommentar | → Senke |
| **Zahl** | → Zahl | → Senke („Buchstabe in Zahl“) | → Trenner, **Token** | → Senke | → Senke |
| **Wort** | → Senke („Ziffer im Wort“) | → Wort | → Trenner, **Token** | → Senke | → Senke |
| **Kommentar** | → Kommentar | → Kommentar | → Kommentar | → Kommentar | `}` → Trenner, sonst Kommentar |
| **Senke** | → Senke | → Senke | → Senke | → Senke | → Senke |

- **Denkfrage: Warum hängt `lex()` ein Leerzeichen an den Text?** Das letzte Token entsteht erst, wenn ein Trennzeichen folgt. Ohne das angehängte Leerzeichen ginge es verloren.
- Der Automat kennt nur „Wort“ und „Zahl“. Ob ein Wort Befehl, Bedingung oder Steuerwort ist, entscheidet erst `erzeugeToken` per Vergleich mit den Schlüsselwortlisten.
- **Zusatz Kommentare:** eigener Zustand, in dem keine Token entstehen. Ein Kommentar direkt am Wort (`Schritt{x}`) führt zum Fehler. Genau das meint die Regel „nicht unmittelbar anhängen“.

### Aufgabe 3 – Lexer implementieren

| Teil | Erwartung (nach Musterlösung) |
|---|---|
| Statische Klasse | `private Lexer() {}` verhindert `new Lexer()`. Alle Attribute und Methoden sind `static`, Attribute daher `private static`. |
| Zustand, Senke | `private static int zustand`; Konstanten `Z_Trenner=0, Z_Alpha=1, Z_Zahl=2, Z_Kommentar=3, Z_Sink=4`. Die Fehlercodes `STAT_GOOD=0, ERR_charunknown=-1, ERR_number_in_word=-2, ERR_char_in_number=-3, ERR_terminal_unknown=-4, ERR_bezUnzulaessig=-5, ERR_zahl_zu_gross=-6` sind in der Vorlage fertig. Die Schleife in `lex()` läuft nur, solange `status == STAT_GOOD`. |
| Tokenliste | `List<Token>`, Anhängen mit `append` |
| Zwischenspeicher | `terminalstring`: beim Beginn eines Wortes oder einer Zahl `= "" + c`, danach `+= c`, nach der Token-Erzeugung `= ""` |
| Fehlermeldungen | In `lexstep`: Ziffer im Wort, Buchstabe in Zahl, unbekanntes Zeichen → `status` setzen und `zustand = Z_Sink` (in der Musterlösung über die Hilfsmethode `fehler(int code)`). Unbekannte Wörter (`wederhole`) und unzulässige Bezeichner meldet die fertige Methode `erzeugeToken`. Den Text liefert `getStatusText()` (fertig), ausgegeben wird er am Ende von `lex()` mit `System.err.println`. |
| ★ Zusatz | `Token` hat `zeile`/`pos` schon. Gespeichert wird der **Beginn** des Wortes (`startZeile`/`startSpalte`). Liste → Array: mit `toFirst()`/`hasAccess()`/`next()` erst zählen, dann füllen. Ausgabe über `Token.toString()`. |

**Stolpersteine:**
- `isAlpha` muss das **`ö`** erlauben, sonst scheitert `MarkeLöschen` mit „unbekanntes Zeichen“. In der Vorlage ist das bereits so.
- Zeilenzählung: Die fertige Methode `zeilenzaehlung(c)` muss in `lex()` **nach** dem Erzeugen des Tokens aufgerufen werden. Wer sie vergisst oder zu früh aufruft, bekommt falsche Zeilennummern: Ein Token am Zeilenende erhielte dann die Nummer der nächsten Zeile.
- Bezeichner: Nach dem Steuerwort `Anweisung` ist das nächste Wort ein Bezeichner. Das erledigt die fertige Methode `erzeugeToken` (Attribut `nextTokenBez`, Liste `bezeichner`). Der Name darf kein Schlüsselwort sein. Ein Wort, das weder Schlüsselwort noch bereits definierter Name ist, ist ein Fehler.

### Aufgabe 4 – Grammatik und Kellerautomat (Papier)

**Erwartet:** diese Grammatik

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

- **Definitionen nur auf oberster Ebene**, nicht in Schleifen, `wenn`-Blöcken oder anderen Definitionen. `wiederhole 2 mal Anweisung A … endeAnweisung endewiederhole` wird abgelehnt.
- Definitionen dürfen **zwischen** normalen Befehlen stehen (siehe Abschnitt 5).
- Dass ein Bezeichner vor dem Aufruf definiert sein muss, erzwingt schon der **Lexer** (unbekanntes Wort → Fehler).

**Kellerautomat:** Kelleralphabet `# w i s a`.

| Eingabe | Bedingung am Keller | Aktion |
|---|---|---|
| `wiederhole` | – | push `w`, danach Zahl + `mal` oder `solange` + Bedingung erwarten |
| `endewiederhole` | oben `w` | pop |
| `wenn` | – | push `i`, danach Bedingung und `dann` erwarten |
| `sonst` | oben `i` | pop, push `s` |
| `endewenn` | oben `i` oder `s` | pop |
| `Anweisung` | oben `#` (nur oberste Ebene) | push `a`, danach Bezeichner erwarten |
| `endeAnweisung` | oben `a` | pop |
| Befehl / Bezeichner | – | nichts |

- Verschachtelung: Ein Endwort ist nur erlaubt, wenn oben das passende Kellerzeichen liegt. Daher wird `wiederhole … wenn … endewiederhole endewenn` abgelehnt.
- Akzeptiert wird, wenn die Eingabe zu Ende ist, der Automat im Normalzustand steht und nur `#` im Keller liegt.
- **Warum ein Keller?** Die Schachtelungstiefe ist unbegrenzt, und ein endlicher Automat kann nicht beliebig weit mitzählen.
- Wer einen rekursiv absteigenden Parser kennt: Dort übernimmt der Java-Aufrufstack die Rolle des Kellers.

### Aufgabe 5 – Parser implementieren

**Erwartet:** Zustand und Keller, Zustandsübergänge mit `switch-case`, verständliche Fehlermeldungen beim Wechsel in die Fehlersenke.

- **Referenz:** `src/myka/Parser.java` mit den Zuständen `Z_Block`, `Z_NachWiederhole`, `Z_NachZahl`, `Z_NachSolange`, `Z_NachWenn`, `Z_NachWennBed`, `Z_NachAnweisung` und `Z_Sink`.
- Typische Fehler von SchülerInnen:
  1. Endprüfung vergessen: Dann wird `wiederhole 3 mal Schritt` akzeptiert.
  2. Fehlertext bei Erfolg nicht genau `"Success!!"`: Dann bleibt „Ausführen“ gesperrt.
  3. `Anweisung` auch innerhalb von Schleifen erlaubt.
  4. `sonst` doppelt erlaubt.
- Didaktischer Ausblick: Ein Parser, der sich pro Token rekursiv selbst aufruft, bricht bei einigen tausend Token mit `StackOverflowError` ab. Die iterative `switch`-Variante hat diese Grenze nicht. Bei Unterrichtsprogrammen spielt das keine Rolle, eignet sich aber gut als Gesprächsanlass.

### Aufgabe 6 – Interpreter

**Erwartet:** Die Methoden der Vorlage anhand ihrer Kommentare implementieren: `execute`, `executeToken`, `bed`, `loop`, `anweisung` und `vorlaufPassendes`.

**Zentrale Spielregel (Invariante):** Jede dieser Methoden lässt `curpos` auf dem **letzten Token der Struktur** stehen, also auf `endewenn`, `endewiederhole`, `endeAnweisung` bzw. auf dem Befehl selbst. Erst `executeToken` erhöht danach um 1. Hier liegt die häufigste Fehlerquelle: Eine Verletzung führt zu Endlosschleifen oder übersprungenen Befehlen.

- **Sprungmarken:** `HashMap<String,Integer>` vom Bezeichner auf den Index des ersten Tokens nach dem Namen in der Definition. Beim Aufruf die Position merken, springen, `anweisung()` ausführen und zurückspringen.
- **`vorlaufPassendes`:** überspringt Token bis zum passenden Endwort und beachtet dabei verschachtelte `wenn`/`wiederhole`. Das erste Token wird nicht darauf geprüft, ob es eine Struktur öffnet. Der Aufruf muss deshalb auf einem Token starten, das keine öffnet (Bedingung, `mal`, Bezeichner).
- **Sonderfall `wiederhole 0 mal`:** Der Schleifenrumpf muss übersprungen werden, sonst landet der Interpreter auf `mal` oder im Rumpf.
- `depth`/`MAX_REK_DEPTH` fängt endlos rekursive Anweisungen ab (`Anweisung A A endeAnweisung A`).

---

## 4. Die Vorlagen in `schueler/src/myka/`

Die Vorlagen entsprechen der Musterlösung in `src/`. Ersetzt sind nur die markierten TODO-Bereiche, an ihrer Stelle stehen Hinweise. Deshalb passen Vorlage, Musterlösung und Arbeitsblatt zusammen. Die TODOs sind nummeriert und im Arbeitsblatt den Aufgabenteilen zugeordnet.

| Datei | Fertig vorgegeben | TODOs der SchülerInnen |
|---|---|---|
| `Lexer.java` | `private Lexer()`, `main` (bei `debug = true` Testausgaben, sonst Oberfläche), Fehlercodes, Schlüsselwortlisten, Hilfsattribute, **`erzeugeToken`** (Typzuordnung, Bezeichner, zu große Zahlen), **`zeilenzaehlung`**, `isZiffer`/`isAlpha` (mit `ö`)/`isTrenner`, `debug`, `getStatusText`, `getStatus` | **1** Zustände + `zustand` · **2** `terminalstring` (★ `startZeile`/`startSpalte`) · **3** `lex()` (Hauptschleife, Leerzeichen anhängen, Token anhängen, Fehlerausgabe) · **4** Hilfsmethode `fehler(int)` · **5** `lexstep()` (der `switch`) |
| `Parser.java` | `parse`-Rahmen mit `fehlertext = "Success!!"` und Behandlung von `tokens == null`, `ist(Token, String)`, `getFehlerText`, `debug` | **1** Zustände (die aus Aufgabe 4) · **2** Rumpf von `parse` (Keller, `switch`, Endprüfung) · **3** Hilfsmethode für Fehlermeldungen |
| `Interpreter.java` | alle Attribute, Klassenkommentar mit Roboter-Aufruf und Spielregel für `curpos`, `warten()`, `vorlaufPassendes(String)`, `getFail`, `setResult`, `fehler`, `stop`, `setWaitTime`/`getWaitTime`, `debug` | **1** `execute` · **2** `executeToken` · **3** `bed` · **4** `loop` · **5** `anweisung` · **6** `vorlaufPassendes(String, String)` |

- Auch ohne ausgefüllte TODOs startet die Oberfläche. Der Lexer liefert dann `null`, der Parser meldet „noch nicht implementiert“, und der Interpreter tut nichts.
- Bewusst **nicht** vorgegeben ist das Anhängen eines Leerzeichens an den Text, denn das ist die Denkfrage aus Aufgabe 2.

---

## 5. Gestaltungsspielräume

Die Musterlösung legt sich an einigen Stellen fest. Wer im Unterricht anders vorgehen möchte, kann folgende Punkte anpassen:

1. **Definitionen überall oder nur am Anfang?** Die Musterlösung erlaubt eigene Anweisungen an jeder Stelle der obersten Ebene. Wer alle Definitionen am Programmanfang verlangt, braucht eine strengere Grammatik und eine zusätzliche Prüfung im Parser. Die Beispiele im Arbeitsblatt funktionieren mit beiden Varianten.
2. **Weitere Schlüsselwörter** (z. B. `Beenden`): Sie müssen in der Liste `control` des Lexers sowie im Parser und im Interpreter ergänzt werden.
3. **Fehlerausgabe des Lexers:** Die Musterlösung setzt einen Statuscode, den die Oberfläche über `getStatusText()` anzeigt, und schreibt die Meldung zusätzlich mit `System.err.println` auf die Konsole.
4. **Parser-Eingabe:** Der Controller übergibt ein `Token[]`. Wer den Parser auf der `List<Token>` arbeiten lassen möchte, muss den Aufruf in `MyKaController` anpassen.

---

## 6. Bekannte Grenzen

- Ein nicht geschlossener Kommentar (`Schritt {nie zu`) wird ohne Fehlermeldung akzeptiert. Das eignet sich als Zusatzaufgabe: Am Ende von `lex()` den Zustand prüfen.
- Rekursive eigene Anweisungen brechen nach `MAX_REK_DEPTH` (200) Ebenen mit „Maximale Rekursionstiefe erreicht“ ab.
- Einige Dateien in `myKaFiles/` enthalten Fehler, z. B. `robot.txt` mit dem unbekannten Wort `DreheLinks`. Sie eignen sich zum Testen der Fehlermeldungen.

---

## 7. Prüffälle

Alle Ergebnisse stammen aus Testläufen mit der Musterlösung.

**Lexer – gültig**

| Eingabe | Ergebnis |
|---|---|
| `wiederhole 3 mal Schritt endewiederhole` | 5 Token (Steuerwort, Zahl 3, Steuerwort, Befehl, Steuerwort) |
| `{Kommentar} Schritt {noch einer}` + Zeilenwechsel + `Schritt` | 2 Token (Kommentare erzeugen keine) |
| `Anweisung Quadrat wiederhole 4 mal Schritt RechtsDrehen endewiederhole endeAnweisung Quadrat Quadrat` | 11 Token, `Quadrat` jeweils Typ Bezeichner |
| `Schritt`, Zeilenwechsel, zwei Leerzeichen, `Schritt` (★ Zusatz) | Token 1 in Zeile 1, Spalte 1; Token 2 in Zeile 2, Spalte 3 |

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

**Interpreter** (Testwelt mit einer Wand nach 5 freien Feldern; S = Schritt, L = LinksDrehen, R = RechtsDrehen, M = MarkeSetzen)

| Programm | Erwartete Befehlsfolge |
|---|---|
| `wiederhole 3 mal Schritt endewiederhole` | SSS |
| `wiederhole 0 mal Schritt endewiederhole LinksDrehen` | L |
| `wiederhole solange NichtIstWand Schritt endewiederhole` | SSSSS |
| `wiederhole solange IstWand Schritt endewiederhole RechtsDrehen` | R |
| `wenn IstWand dann LinksDrehen sonst Schritt endewenn` | S |
| `wenn IstWand dann Schritt endewenn LinksDrehen` | L |
| `wiederhole 2 mal wenn NichtIstWand dann wiederhole 2 mal Schritt endewiederhole sonst LinksDrehen endewenn endewiederhole` | SSSS |
| `Anweisung A Schritt endeAnweisung Anweisung B A A endeAnweisung B` | SS |
| `Anweisung Q wiederhole 2 mal LinksDrehen endewiederhole endeAnweisung Q Q` | LLLL |
| `wiederhole solange NichtIstWand MarkeSetzen Schritt endewiederhole wenn IstMarke dann LinksDrehen sonst RechtsDrehen endewenn` | MSMSMSMSMSR |
| `Anweisung A A endeAnweisung A` | Abbruch mit Meldung „Maximale Rekursionstiefe“ |

**Oberfläche (Startwelt):** Nach „Welt zurücksetzen“ ist das Feld 5 × 10 groß. Der Roboter steht bei (0, 0) und schaut nach Süden, vor ihm liegen 9 freie Felder. Darauf beruhen die Erwartungen in Aufgabe 6 des Arbeitsblatts.

**Abschlusstest `schachbrett2.txt`** (identisch mit dem Beispielprogramm der README): Lexer fehlerfrei, Parser `Success!!`. In der Startwelt entsteht ein vollständiges Schachbrettmuster aus Marken, und der Roboter steht am Ende in der gegenüberliegenden Ecke.

---

<sub>✦ Entwickelt mit KI-Unterstützung durch Claude</sub>
