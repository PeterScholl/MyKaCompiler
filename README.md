# MyKaCompiler
![MyKaScreenShot](https://user-images.githubusercontent.com/20445926/137966829-82076940-6bf2-4121-885d-0a9e2cd12593.PNG)

## Idee / Ziel
Dieses Projekt dient in erster Linie dazu, einen Lexer und einen Parser als einfachen endlichen Automaten bzw. Kellerautomaten zu implementieren (Diese Aufgabe soll an SchülerInnen der Oberstufe delegiert werden). Der Lexer erzeugt eine Tokenliste, die vom Parser auf Stimmigkeit bzgl. der Grammatik der Sprache geprüft wird. Diese Tokenliste kann dann von einem "Interpreter" in Aktionen des Roboters umgesetzt werden.

Die Idee habe ich von der Webseite [inf-schule.de](https://www.inf-schule.de/automaten-sprachen/interpretercompiler/syntaxsemantikueberblick/einstieg_myka).

Weitere Anregungen stammen aus dem Projekt [robot-karol-web](https://github.com/Entkenntnis/robot-karol-web) hier auf github. Von dort stammen auch die Bilder (Roboter und Ziegel)

## Schnellstart zum Ausprobieren
Jar-Datei herunterladen und starten, im Hilfemenü den Beipsielcode einfügen und diesen dann über das Compilermenü ausführen.

Oder die Datei schachbrett2.txt laden (Dateimenü) und diese ausführen.

Mit dem Robotermenü kann man ein bisschen spielen - die Keycodes (Buchstaben) funktionieren, wenn die Zeichenfläche den Focus hat (anklicken)

## Befehlsübersicht
Befehle/Anweisungen: 
* Schritt
* LinksDrehen
* RechtsDrehen
* Hinlegen
* Aufheben
* MarkeSetzen
* MarkeLöschen

Bedingungen
* IstWand / NichtIstWand
* IstMarke / NichtIstMarke
* IstZiegel / NichtIstZiegel

Steuerung
* wiederhole {n} mal {Anweisungen} endewiederhole
* wiederhole solange {Bedingung} {Anweisungen} endewiederhole
* wenn {Bed} dann {Anweisungen} [sonst {Anweisungen}]

eigene Anweisung (müssen vor dem ersten Aufrufen angelegt werden)
* Anweisung {Bezeichner} endeAnweisung
* {Bezeichner}

**Kommentare** können in geschweiften Klammern eingefügt werden {Kommentar} und werden ignoriert. Sie dürfen jedoch nicht unmittelbar an eine Anweisung/Bedingung/Steuerung angehängt werden

## Beispielprogramm
~~~~
{Zeichnet ein Schachbrettmuster - dies ist ein Kommentar}
wiederhole solange NichtIstWand
  wiederhole solange NichtIstWand
    MarkeSetzen
    Schritt
    wenn NichtIstWand dann
      Schritt
      MarkeSetzen
    endewenn
  endewiederhole
  wenn IstMarke dann
    LinksDrehen
    LinksDrehen
    Schritt
    RechtsDrehen
  sonst
    LinksDrehen
  endewenn
  wenn NichtIstWand dann
    Schritt
    LinksDrehen
    wiederhole solange NichtIstWand
      MarkeSetzen
      Schritt
      Schritt
    endewiederhole
    RechtsDrehen
    wenn NichtIstWand dann
      Schritt
      RechtsDrehen
    endewenn
  endewenn
endewiederhole
~~~~
