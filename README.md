# MyKaCompiler
![MyKaScreenShot](https://user-images.githubusercontent.com/20445926/137966829-82076940-6bf2-4121-885d-0a9e2cd12593.PNG)

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
