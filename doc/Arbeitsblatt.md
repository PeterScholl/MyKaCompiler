# MyKa – Wir bauen einen Interpreter

> **Entwicklungsumgebung: IntelliJ IDEA.** Alle Hinweise zu Projekt, Start und Fehlermeldungen in diesem Arbeitsblatt beziehen sich auf IntelliJ. (Stand: Oktober 2026)

Du baust ein Programm, das Text wie

```
wiederhole 4 mal
  Schritt
endewiederhole
```

einliest, auf Fehler prüft und einen Roboter danach steuert. Dafür brauchst du drei Bausteine:

```
Quelltext ──▶ LEXER ──▶ Tokenliste ──▶ PARSER ──▶ INTERPRETER ──▶ Roboter
 (Text)      zerlegt     (Liste von     prüft die   führt die
             in Wörter    Wörtern)      Regeln      Befehle aus
```

| Baustein | Was er tut | Beispiel |
|---|---|---|
| **Lexer** | Zerlegt den Text in „Wörter" (Token) und sortiert sie nach Art. | `wiederhole 4 mal` → drei Token |
| **Parser** | Prüft, ob die Token in einer erlaubten Reihenfolge stehen. | `wiederhole 4 mal Schritt` ohne `endewiederhole` → Fehler |
| **Interpreter** | Führt die Token nacheinander aus, mit Schleifen, Verzweigungen und eigenen Anweisungen. | Roboter geht 4 Schritte |

> **Zum Nachlesen:** Die Idee zu MyKa stammt von der Lernplattform **inf-schule.de**. Dort findest du ergänzende Erklärungen zu Syntax, Semantik, Automaten, Compiler und Interpreter, die dir bei den Aufgaben helfen können:
> <https://www.inf-schule.de/automaten-sprachen/interpretercompiler/syntaxsemantikueberblick/einstieg_myka>

## So ist das Arbeitsblatt aufgebaut

Jede Aufgabe hat denselben Aufbau:

- **Ziel:** Das kannst du danach.
- **Gegeben:** Damit arbeitest du.
- **Auftrag:** Nummerierte Schritte. Arbeite sie **der Reihe nach** ab.
- **Fertig, wenn …:** So erkennst du, dass die Aufgabe erledigt ist.
- **Tipps:** Wenn du nicht weiterkommst.
- ★ **Zusatz:** freiwillig

Aufgabe 1, 2 und 4 löst du **zuerst auf Papier**. Erst wenn die Skizze stimmt, schreibst du Code.

---

## Aufgabe 0 – Projekt einrichten

**Ziel:** Das Roboterfenster startet bei dir.

**Auftrag:**
1. Lege in IntelliJ ein neues Projekt an: **File → New → Project**, Sprache **Java**, Build system **IntelliJ**, Name `MyKa`. Wähle unter **JDK** eine installierte Java-Version (oder lade eine über das Auswahlfeld herunter).
2. Lege im Ordner `src` ein Paket `myka` an (Rechtsklick auf `src` → **New → Package**) und kopiere alle Dateien aus dem Startpaket (Ordner `schueler/src/myka`, die Bilder liegen darin im Ordner `img`) hinein (per Drag & Drop in den Projektbaum). Die Bilder (`.png`) gehören in den Unterordner `src/myka/img`.
3. Prüfe die Textkodierung: **File → Settings → Editor → File Encodings**. Dort sollte bei *Global Encoding* und *Project Encoding* **UTF-8** stehen. Das ist wichtig wegen des `ö` in `MarkeLöschen`.
4. Öffne `Lexer.java` und klicke auf das grüne Dreieck neben `public static void main`. Wähle **Run 'Lexer.main()'**.

**Fertig, wenn:** ein Fenster mit Textfeld und Roboterfläche erscheint und du den Roboter über das Menü **Roboter** bewegen kannst.

**Wenn etwas nicht klappt:**
- Roboterbilder fehlen: Der Ordner `img` muss `src/myka/img` heißen. Danach **Build → Rebuild Project** und neu starten.
- Rote Wellenlinien unter fast allem: Dem Projekt fehlt ein JDK (**File → Project Structure → Project → SDK**).
- Umlaute sehen kaputt aus: Kodierung nicht UTF-8 (siehe Schritt 3).

*Hinweis: Die Menüpunkte unter **Compiler** („Lexer aufrufen", „Parser aufrufen", „Ausführen") tun noch nichts Sinnvolles. Das ändert sich mit jeder Aufgabe.*

---

## Die Sprache MyKa

**Befehle** (der Roboter tut etwas)

| Befehl | Wirkung |
|---|---|
| `Schritt` | ein Feld vorwärts |
| `LinksDrehen`, `RechtsDrehen` | Roboter dreht sich |
| `Hinlegen`, `Aufheben` | Ziegel ablegen bzw. aufnehmen |
| `MarkeSetzen`, `MarkeLöschen` | Marke auf dem Feld setzen bzw. entfernen |

**Bedingungen** (Ja/Nein-Fragen an den Roboter)

`IstWand` · `NichtIstWand` · `IstMarke` · `NichtIstMarke` · `IstZiegel` · `NichtIstZiegel`

**Steuerung**

```
wiederhole 3 mal
  Anweisungen
endewiederhole

wiederhole solange Bedingung
  Anweisungen
endewiederhole

wenn Bedingung dann
  Anweisungen
endewenn

wenn Bedingung dann
  Anweisungen
sonst
  Anweisungen
endewenn
```

**Eigene Anweisungen**

```
Anweisung Quadrat
  wiederhole 4 mal
    Schritt
    RechtsDrehen
  endewiederhole
endeAnweisung

Quadrat
Quadrat
```

- Eine eigene Anweisung wird **vor** ihrem ersten Aufruf definiert.
- Sie darf nur **außerhalb** von Schleifen, `wenn`-Blöcken und anderen Anweisungen stehen.
- Ihr Name darf kein Wort der Sprache sein (also nicht `Schritt`, `wenn` usw.).

**Kommentare** stehen in geschweiften Klammern: `{Das ist ein Kommentar}`

**Spielregeln für den Quelltext**

- Zwischen zwei Wörtern steht mindestens ein Leerzeichen, Tabulator oder Zeilenumbruch.
- Erlaubt sind nur Buchstaben (`a–z`, `A–Z`, `ö`), Ziffern und `{ }`. Alles andere (z. B. `;`) ist ein Fehler.
- Groß- und Kleinschreibung zählt: `schritt` ist kein Befehl.
- Ein Wort besteht nur aus Buchstaben, eine Zahl nur aus Ziffern. `Schritt2` und `3x` sind Fehler.
- Ein Kommentar darf nicht direkt an einem Wort kleben: `Schritt {ok}` ist erlaubt, `Schritt{ok}` ist ein Fehler.

---

## Aufgabe 1 – Was ist ein Token?

**Ziel:** Du weißt, in welche „Wortarten" der Lexer den Quelltext einteilt.

**Gegeben:** das Beispielprogramm unten, die Sprachübersicht und die Datei `Token.java` (**nicht verändern**).

```
Anweisung Quadrat
  wiederhole 4 mal
    Schritt
    RechtsDrehen
  endewiederhole
endeAnweisung
Quadrat
```

**Auftrag:**
1. Schreibe das Beispielprogramm als Liste von Wörtern, **ein Wort pro Zeile**.
2. Bilde Gruppen von Wörtern, die sich im Programm gleich verhalten. Gib jeder Gruppe einen Namen und trage sie in diese Tabelle ein:

   | Name der Gruppe (Tokentyp) | Wörter aus dem Beispiel | Weitere Wörter aus der Sprachübersicht |
   |---|---|---|
   |  |  |  |

3. Öffne `Token.java` und beantworte:
   - a) Welche Typen sind dort festgelegt? Vergleiche mit deiner Tabelle. Was passt, was fehlt, was ist anders?
   - b) Wozu gibt es sowohl `wert` als auch `value`? Wann hat `value` einen sinnvollen Inhalt?
   - c) Was macht der Konstruktor, wenn der Typ `T_Zahl` ist?
   - d) Zeichne die Token-Objekte (`typ`, `wert`, `value`) für den Text `wiederhole 3 mal`.

**Fertig, wenn:** die Tabelle und die Antworten a–d vorliegen und du erklären kannst, warum `Quadrat` und `Schritt` verschiedene Typen haben.

**Tipps:**
- Frage dich bei jedem Wort: „Welche Rolle spielt es für den Parser?" Ein Wort, das nur ein Name ist, verhält sich anders als eines, das etwas auslöst.
- Weicht deine Einteilung von `Token.java` ab, ist das nicht schlimm. Im weiteren Projekt wird aber `Token.java` benutzt.

---

## Aufgabe 2 – Der Lexer als Automat (Papier)

**Ziel:** Du hast einen Automaten gezeichnet, der Text in Wörter und Zahlen zerlegt und Fehler erkennt.

**Gegeben:** Das Eingabealphabet besteht aus **b** (Buchstabe), **z** (Ziffer) und **t** (Trennzeichen: Leerzeichen, Tabulator, Zeilenwechsel). Der Mini-Automat unten akzeptiert jeden solchen Text, **erzeugt aber keine Token**:

```
        b, t, z
       ┌──────┐
       ▼      │
Start ▶( q0 )─┘
```

**Auftrag:**
1. Lies die Spielregel: Ein Wort oder eine Zahl ist erst **fertig**, wenn danach ein Trennzeichen kommt. In diesem Moment entsteht ein Token.
2. Zeichne einen Automaten mit diesen Eigenschaften:
   - Er erkennt, ob du gerade **zwischen** Wörtern bist, **in** einem Wort oder **in** einer Zahl.
   - Immer wenn ein Token entsteht, **wechselt der Zustand**. Markiere diese Pfeile besonders (z. B. mit ★).
   - Zeichen, die nicht erlaubt sind oder falsch gemischt werden, führen in eine **Fehlersenke**, aus der es keinen Weg zurück gibt.
3. Spiele deinen Automaten mit dem Text `Hi 42` (danach ein Leerzeichen) durch:

   | Zeichen | Art (b/z/t) | Zustand vorher | Zustand nachher | Token entsteht? |
   |---|---|---|---|---|
   | `H` | b | Start | | |
   | `i` | | | | |
   | ` ` | | | | |
   | `4` | | | | |
   | `2` | | | | |
   | ` ` | | | | |

4. Was macht dein Automat mit `Schri3tt` und mit `3x`? Verfolge beides.
5. **Denkfrage:** Der Text `Hi 42` endet ohne Trennzeichen. Was geschieht dann mit dem letzten Token? Wie löst man das Problem?

**Fertig, wenn:** Zustandsdiagramm, ausgefüllte Tabelle und Antworten zu 4 und 5 vorliegen und du jemandem erklären kannst, **wo** Token entstehen.

**Tipps:**
- Der Automat unterscheidet nur „Wort" und „Zahl". Ob ein Wort `Schritt` oder `wenn` ist, wird erst nach dem Automaten durch einen Vergleich mit einer Liste entschieden.
- Du brauchst mindestens vier Zustände.

★ **Zusatz:** Wie baust du Kommentare `{ … }` ein? Sie sollen keine Token erzeugen. Was soll mit `{` mitten in einem Wort passieren?

---

## Aufgabe 3 – Den Lexer programmieren

**Ziel:** `Lexer.java` zerlegt Quelltext in eine Tokenliste und meldet Fehler.

**Gegeben:** `Lexer.java` als Vorlage. Fertig sind die Listen der Schlüsselwörter, die Fehlercodes, die Hilfsmethoden `isZiffer`, `isAlpha` und `isTrenner`, `zeilenzaehlung(c)`, `getStatusText()` und die Methode `erzeugeToken(...)`, die einem fertig gelesenen Wort die richtige Token-Art zuordnet. Alles, was mit `// TODO 1` bis `// TODO 5` markiert ist, ergänzt du. Bei jedem TODO stehen Hinweise im Kommentar. Außerdem `List.java` (die Abiturklasse) und dein Automat aus Aufgabe 2.

**Auftrag:**
1. **Statische Klasse.** Finde die Stelle, die verhindert, dass man `new Lexer()` schreiben kann. Warum steht bei jedem Attribut und jeder Methode `static`? Was meldet IntelliJ (rote Markierung, Meldung beim Daraufzeigen), wenn du es bei einem Attribut weglässt? Probiere es aus.
2. **Zustand und Fehlersenke.** Lege das Attribut `zustand` an. Für jeden Zustand gibt es eine Konstante (z. B. `Z_Wort`), damit `zustand = Z_Wort` lesbarer ist als `zustand = 1`. Lege auch die Fehlersenke an.
3. **Tokenliste.** In `lex()` entsteht eine `List<Token>`. Neue Token hängst du mit `append` an.
4. **Zwischenspeicher.** Lege ein String-Attribut `terminalstring` an. Es sammelt die Buchstaben oder Ziffern des aktuellen Wortes. Nach jedem erzeugten Token musst du es **zurücksetzen**.
5. **Zustandsübergänge.** Schreibe `lexstep(char c)` als `switch (zustand)`. Dein Automat aus Aufgabe 2 ist der Bauplan. Die Methode gibt `true` zurück, wenn jetzt ein Token entstehen soll.
6. **Fehlermeldungen.** Bei einem unzulässigen Übergang setzt du den passenden Fehlercode (`status`) und wechselst in die Fehlersenke. Folgende Fälle brauchst du: Ziffer im Wort, Buchstabe in Zahl, unbekanntes Zeichen. (Ein unbekanntes Wort meldet die fertige Methode `erzeugeToken` selbst.) Den Text der Meldung samt Zeilennummer liefert `getStatusText()`. Gib ihn am Ende von `lex()` mit `System.err.println` auf der Konsole aus.

**Fertig, wenn:** alle Fälle in dieser Tabelle stimmen. Teste in der Oberfläche über **Compiler → Lexer aufrufen** und lies die Statuszeile ab.

| Eingabe | Erwartung |
|---|---|
| `wiederhole 3 mal Schritt endewiederhole` | 5 Token, kein Fehler |
| `{Kommentar} Schritt {noch einer}` und in der nächsten Zeile `Schritt` | 2 Token (Kommentare erzeugen keine) |
| `Anweisung Quadrat Schritt endeAnweisung Quadrat` | 5 Token, `Quadrat` jeweils als Bezeichner |
| `Schri3tt` | Fehler: Ziffer im Wort |
| `wiederhole 3x mal` | Fehler: Buchstabe in Zahl |
| `wederhole 3 mal Schritt endewiederhole` | Fehler: unbekanntes Wort `wederhole` |
| `Schritt;` | Fehler: unbekanntes Zeichen |
| `Schritt{x}` | Fehler: Kommentar klebt am Wort |
| `Hallo` | Fehler: Name nicht deklariert |

**Tipps:**
- Das Wort **`MarkeLöschen`** enthält ein `ö`. Prüfe, ob `isAlpha` es erlaubt.
- Das letzte Token entsteht erst, wenn ein Trennzeichen folgt (siehe Denkfrage in Aufgabe 2). Wie bringst du das in `lex()` unter?
- Die Zeilenzählung steckt in der fertigen Methode `zeilenzaehlung(c)`. Rufe sie in `lex()` für jedes Zeichen auf, aber **erst nach** dem Erzeugen des Tokens. Sonst stimmt die Zeilennummer in den Meldungen nicht.
- Stelle in `Lexer.java` `debug = true`, dann startet `main` Testausgaben statt der Oberfläche.

★ **Zusatz 1:** Speichere in jedem Token die **Zeile** und die **Spalte**, in der das Wort **beginnt**. Prüfe mit dem Text `Schritt`, Zeilenwechsel, zwei Leerzeichen, `Schritt`: Das zweite Token muss in Zeile 2, Spalte 3 stehen.

★ **Zusatz 2:** Schreibe eine eigene Methode, die eine `List<Token>` in ein `Token[]` umwandelt, und gib alle Token auf der Konsole aus. Vergleiche danach mit `Hilfsfunktionen.convertTokenListToArray`.

---

## Aufgabe 4 – Grammatik und Kellerautomat (Papier)

**Ziel:** Du hast die Regeln der Sprache als Grammatik aufgeschrieben und daraus einen Kellerautomaten entworfen.

**Gegeben:** Die Token-Arten aus Aufgabe 1. Hier die Anfangsregel der Grammatik, `ε` bedeutet „nichts":

```
Programm → ε | Element Programm | Definition Programm
```

`Definition` ist eine eigene Anweisung mit `Anweisung … endeAnweisung`.

**Auftrag:**
1. Schreibe die übrigen Regeln. Du brauchst Regeln für `Element`, `Schleife` (zwei Varianten), `Verzweigung` (zwei Varianten), `Block` und `Definition`. Orientiere dich an der Sprachübersicht.
2. Leite `wiederhole 2 mal Schritt endewiederhole` aus `Programm` ab. Schreibe jeden Ableitungsschritt auf.
3. **Warum reicht ein gewöhnlicher endlicher Automat nicht?** Denke an Programme, in denen `wiederhole` in `wiederhole` in `wenn` steckt, beliebig tief.
4. Entwirf den Kellerautomaten:
   - Welche Strukturen muss sich der Keller **merken**? Lege für jede ein Kellerzeichen fest. Dazu kommt ein Zeichen für den leeren Keller.
   - Fülle eine Übergangstabelle aus. Die ersten Zeilen sind vorgegeben:

   | Token | Bedingung am Keller | Aktion |
   |---|---|---|
   | `wiederhole` | – | Zeichen für Schleife auf den Keller legen |
   | `endewiederhole` | oben liegt das Schleifenzeichen | oberstes Zeichen entfernen |
   | `Schritt` | – | nichts |
   | `wenn` | | |
   | `sonst` | | |
   | `endewenn` | | |
   | `Anweisung` | | |
   | `endeAnweisung` | | |

5. Spiele dein Programm durch. Trage in jeder Zeile den Keller ein (unten → oben):

   `wiederhole 2 mal wenn IstWand dann Schritt sonst LinksDrehen endewenn endewiederhole`

   | Nr. | Token | Keller danach |
   |---|---|---|
   | 0 | (Start) | `#` |
   | 1 | `wiederhole` | |
   | 2 | `2` | |
   | 3 | `mal` | |
   | … | … | |

6. Was geschieht mit diesem Programm? An welcher Stelle und warum scheitert dein Automat?
   `wiederhole 2 mal wenn IstWand dann Schritt endewiederhole endewenn`
7. Wann **akzeptiert** dein Automat ein Programm? Überlege: Was muss am Ende der Eingabe im Keller liegen?

**Fertig, wenn:** Grammatik, Zustands- und Übergangstabelle sowie beide Durchläufe vorliegen.

**Tipps:**
- Ein Endwort (`endewenn` usw.) ist nur dann erlaubt, wenn **oben auf dem Keller** das passende Zeichen liegt.
- `sonst` darf nur einmal pro `wenn` vorkommen. Wie merkst du dir, dass das `sonst` schon da war?
- Eigene Anweisungen dürfen nur auf der obersten Ebene definiert werden. Woran erkennt der Automat die oberste Ebene?

---

## Aufgabe 5 – Den Parser programmieren

**Ziel:** `Parser.java` prüft, ob eine Tokenfolge den Regeln der Sprache entspricht, und meldet verständliche Fehler.

**Gegeben:** `Parser.java` als Vorlage mit den fertigen Methoden `getFehlerText()` und `ist(Token, String)` (prüft, ob ein Token ein bestimmtes Steuerwort ist). Was mit `// TODO 1` bis `// TODO 3` markiert ist, ergänzt du. Dazu `Stack.java` (Methoden `push`, `pop`, `top`, `isEmpty`) und dein Kellerautomat aus Aufgabe 4.

**Auftrag:**
1. Lege Konstanten für **deine** Zustände an (wie beim Lexer, z. B. `Z_Block`).
2. Lege einen `Stack<Character>` an und lege zuerst das Zeichen für den leeren Keller darauf.
3. Gehe in einer Schleife das `Token[]` durch. In jedem Schritt entscheidet ein `switch (zustand)`, was mit dem aktuellen Token passiert. Innerhalb eines Falls prüfst du Token und `keller.top()`.
4. Landet der Automat in der Fehlersenke, **hörst du auf** und setzt `fehlertext` auf eine Meldung. Nenne die Art des Fehlers **und das Token** (Typ, Wert, Zeile).
5. **Prüfe am Ende**, ob die Eingabe zu Ende ist, der Zustand normal ist **und** der Keller nur noch den Bodenzeichen enthält. Sonst fehlt irgendwo ein Endwort.
6. Bei Erfolg muss der Text **genau** `Success!!` lauten und `parse` den Wert `true` liefern.

> **Wichtig:** Die Oberfläche prüft den **Text** `Success!!`, nicht den Rückgabewert. Schreibst du etwas anderes hinein, bleibt „Ausführen" gesperrt.

**Fertig, wenn:** alle Fälle in der Tabelle stimmen. Teste über **Compiler → Parser aufrufen**.

| Programm | Ergebnis |
|---|---|
| *(leer)* | `Success!!` |
| `Schritt` | `Success!!` |
| `wiederhole 2 mal wenn IstWand dann Schritt endewenn endewiederhole` | `Success!!` |
| `wenn IstWand dann Schritt sonst Schritt endewenn` | `Success!!` |
| `Anweisung A Schritt endeAnweisung A` | `Success!!` |
| `wiederhole 3 mal Schritt` | Fehler: `endewiederhole` fehlt |
| `wiederhole 3 Schritt endewiederhole` | Fehler: `mal` fehlt |
| `wiederhole solange Schritt endewiederhole` | Fehler: Bedingung fehlt |
| `wenn IstWand Schritt endewenn` | Fehler: `dann` fehlt |
| `wenn IstWand dann Schritt sonst Schritt` | Fehler: `endewenn` fehlt |
| `wenn IstWand dann Schritt sonst Schritt sonst Schritt endewenn` | Fehler: zweites `sonst` |
| `Schritt endewenn` | Fehler: `endewenn` ohne `wenn` |
| `wiederhole 2 mal wenn IstWand dann Schritt endewiederhole endewenn` | Fehler: falsch verschachtelt |
| `wiederhole 2 mal Anweisung A Schritt endeAnweisung endewiederhole` | Fehler: Definition in Schleife |
| `Anweisung A Schritt` | Fehler: `endeAnweisung` fehlt |
| `5` | Fehler: Zahl allein |

**Tipps:**
- Erst nur Befehle und Schleifen programmieren und testen, dann `wenn`, dann `Anweisung`.
- Teste die Fehlerfälle genauso gründlich wie die gültigen Programme.

---

## Aufgabe 6 – Den Interpreter programmieren

**Ziel:** `Interpreter.java` führt ein geprüftes Programm aus und steuert den Roboter.

**Gegeben:** `Interpreter.java` als Vorlage. Fertig sind die Attribute und Hilfsmethoden (u. a. `warten()` und `fehler(...)`). Die Methoden `execute`, `executeToken`, `bed`, `loop`, `anweisung` und `vorlaufPassendes` stehen mit Kopf und Beschreibung (Kommentar) da, der Rumpf fehlt (`// TODO 1` bis `// TODO 6`, mit Hinweisen). **Lies zu jeder Methode zuerst den Kommentar.**

**So spricht der Interpreter mit dem Roboter:** Befehle **und** Bedingungen werden mit derselben Zeile abgeschickt:

```java
controller.execute(MyKaController.RBefehl, new String[] { token.getWert() });
```

Ein Befehl (`Schritt`) lässt den Roboter etwas tun. Bei einer Bedingung (`IstWand`) schreibt der Controller die Antwort in das Attribut `result`.

**Die wichtigste Spielregel:** `curpos` ist die Position im Tokenarray. Jede der Methoden `bed`, `loop` und `anweisung` lässt `curpos` auf dem **letzten Token ihrer Struktur** stehen, also auf `endewenn`, `endewiederhole` oder `endeAnweisung`. Erst `executeToken` zählt danach um 1 weiter.

**Auftrag:**
1. **Papier:** Nummeriere die Token von `wiederhole 2 mal Schritt endewiederhole` mit 0, 1, 2, … durch. Schreibe auf, welchen Wert `curpos` bei jedem Schritt der Ausführung hat.
2. Schreibe `execute` (Startwerte setzen, Token abarbeiten, solange kein Fehler vorliegt) und `executeToken` **nur für Befehle**. Teste mit `Schritt Schritt LinksDrehen`.
3. Ergänze `loop` für `wiederhole n mal`. Teste mit `wiederhole 3 mal Schritt endewiederhole`. Teste auch den Sonderfall `wiederhole 0 mal Schritt endewiederhole LinksDrehen`.
4. Ergänze in `loop` die Variante `wiederhole solange`. Die Bedingung muss **vor jedem Durchlauf** neu abgefragt werden.
5. Schreibe `vorlaufPassendes`: Sie überspringt Token bis zu einem Endwort und beachtet dabei verschachtelte Strukturen (ein inneres `endewiederhole` ist **nicht** das gesuchte).
6. Ergänze `bed` (`wenn … dann … [sonst …] endewenn`). Wird ein Teil nicht ausgeführt, überspringst du ihn mit `vorlaufPassendes`.
7. Ergänze eigene Anweisungen. Bei der **Definition** merkst du dir in einer `HashMap`, wo der Rumpf beginnt, und überspringst ihn. Beim **Aufruf** merkst du dir die aktuelle Position, springst zum Rumpf, führst ihn aus und springst zurück.

**Fertig, wenn:** alle Fälle in der Tabelle stimmen. Setze vor **jedem** Test die Welt zurück (**Einstellungen → Welt zurücksetzen**). Der Roboter steht dann in einer Ecke und schaut auf eine freie Strecke von 9 Feldern bis zur Wand. Starte mit **Compiler → Ausführen**.

| Programm | Der Roboter … |
|---|---|
| `wiederhole 3 mal Schritt endewiederhole` | geht 3 Felder vor |
| `wiederhole 0 mal Schritt endewiederhole LinksDrehen` | dreht sich nur einmal nach links |
| `wiederhole solange NichtIstWand Schritt endewiederhole` | läuft die 9 Felder bis zur Wand |
| `wiederhole solange IstWand Schritt endewiederhole RechtsDrehen` | dreht sich nur einmal nach rechts (die Schleife läuft nie) |
| `wenn IstWand dann LinksDrehen sonst Schritt endewenn` | geht genau 1 Schritt |
| `Anweisung Q wiederhole 2 mal LinksDrehen endewiederhole endeAnweisung Q Q` | dreht sich viermal links und schaut danach wieder in die Ausgangsrichtung |
| `Anweisung A A endeAnweisung A` | bricht ab, Meldung „Maximale Rekursionstiefe erreicht" |
| Datei `schachbrett2.txt` (**Datei → Datei öffnen**, dann ausführen) | zeichnet ein Schachbrettmuster |

**Tipps:**
- Findest du den Fehler nicht, setze in `Interpreter.java` `debug = true` und lies die Ausgaben.
- Steht der Roboter plötzlich still oder läuft endlos, prüfe zuerst, wo `curpos` nach der jeweiligen Methode steht.
- **Hilfe → Ausführung Anhalten** bricht eine Endlosschleife ab.

---

## Geschafft

Wenn `schachbrett2.txt` läuft, hast du einen kompletten Interpreter gebaut: **Lexer → Parser → Interpreter**. Das ist genau die Struktur, nach der Programmiersprachen wie Java oder Python arbeiten.
