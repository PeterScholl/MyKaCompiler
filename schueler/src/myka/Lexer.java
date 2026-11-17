package myka;

/**
 * Der Lexer der Sprache MyKa.
 * Er liest den Quelltext Zeichen für Zeichen (als endlicher Automat) und
 * erzeugt daraus eine Liste von Token.
 *
 * Alles, was mit "TODO" markiert ist, ergänzt du. Der Rest ist fertig.
 *
 * Diese Klasse ist statisch: Es wird nie ein Lexer-Objekt erzeugt.
 */
public class Lexer {
	// ===== Fehlercodes (fertig) =========================================
	private static final int STAT_GOOD = 0;
	private static final int ERR_charunknown = -1; // unbekanntes Zeichen
	private static final int ERR_number_in_word = -2; // Ziffer in einem Wort
	private static final int ERR_char_in_number = -3; // Buchstabe in einer Zahl
	private static final int ERR_terminal_unknown = -4; // unbekanntes Wort
	private static final int ERR_bezUnzulaessig = -5; // Name für eigene Anweisung nicht erlaubt
	private static final int ERR_zahl_zu_gross = -6; // Zahl passt nicht in einen int

	// ===== Schlüsselwörter der Sprache (fertig) ==========================
	private static final String[] conditions = new String[] { "IstWand", "NichtIstWand", "IstMarke", "NichtIstMarke",
			"IstZiegel", "NichtIstZiegel" };
	private static final String[] moves = new String[] { "Schritt", "LinksDrehen", "RechtsDrehen", "Hinlegen",
			"Aufheben", "MarkeSetzen", "MarkeLöschen" };
	private static final String[] control = new String[] { "wiederhole", "mal", "solange", "endewiederhole", "wenn",
			"dann", "sonst", "endewenn", "Anweisung", "endeAnweisung" };

	// ===== Hilfsattribute (fertig) ======================================
	private static int status = STAT_GOOD; // STAT_GOOD oder ein ERR_...-Code
	private static int aktzeile = 1; // aktuelle Zeile im Quelltext (beginnt bei 1)
	private static int pos = 0; // Index des NÄCHSTEN zu lesenden Zeichens im Text
	private static int zeilenpos = 0; // Index des ersten Zeichens der aktuellen Zeile
	private static int fehlerzeile = 0; // Zeile, in der ein Wort-Fehler erkannt wurde
	private static String fehlerwort = ""; // das Wort, bei dem ein Wort-Fehler auftrat
	private static boolean nextTokenBez = false; // true: das nächste Wort ist der Name einer eigenen Anweisung
	private static List<String> bezeichner = new List<String>(); // alle bisher definierten Namen eigener Anweisungen
	private static final boolean debug = false;

	// ===== Dein Teil: Zustand und Zwischenspeicher ======================
	// TODO 1 - Zustände und Fehlersenke (Aufgabe 3.2)
	//   Lege für jeden Zustand deines Automaten aus Aufgabe 2 eine Konstante an,
	//   z. B.  private static final int Z_Wort = 1;  (Zahlen von 0 an durchnummerieren).
	//   Du brauchst auch einen Zustand für die Fehlersenke.
	//   Lege dann das Attribut  private static int zustand  an.
	//   Als Startwert dient der Zustand "zwischen zwei Wörtern".

	// TODO 2 - Zwischenspeicher (Aufgabe 3.4)
	//   Lege ein String-Attribut  terminalstring  an. Es sammelt die Buchstaben bzw. Ziffern
	//   des Wortes oder der Zahl, die gerade gelesen wird.
	//   ★ Zusatz 1: Lege auch  startZeile  und  startSpalte  an (Beginn des Wortes im Quelltext).

	private Lexer() { // es wird kein Lexer-Objekt erzeugt - statische Klasse
	}

	public static void main(String[] args) {
		if (debug) {
			// Testfunktion: hier kannst du den Lexer ohne Oberfläche ausprobieren
			System.out.println("Zifferntest: " + isZiffer('5') + " a:" + isZiffer('a'));
			System.out.println("Alphatest: 5:" + isAlpha('5') + " a:" + isAlpha('a') + " R:" + isAlpha('R'));
			System.out.println("Trennertest: 5:" + isTrenner('5') + " \\n:" + isTrenner('\n') + " ' ':" + isTrenner(' '));
			String test = "Schritt     LinksDrehen\n234";
			List<Token> temp = lex(test);
			if (temp != null) {
				temp.toFirst();
				while (temp.hasAccess()) {
					System.out.println(temp.getContent());
					temp.next();
				}
			}
		} else {
			MyKaController.main(args); // startet die Oberfläche
		}
	}

	/**
	 * Zerlegt den Quelltext in eine Liste von Token.
	 *
	 * @param text der Quelltext
	 * @return die Tokenliste oder null, wenn ein Fehler aufgetreten ist (dann
	 *         steht der Fehler in status und getStatusText() beschreibt ihn)
	 */
	public static List<Token> lex(String text) {
		// --- Startwerte (fertig) ---
		status = STAT_GOOD;
		aktzeile = 1;
		pos = 0;
		zeilenpos = 0;
		nextTokenBez = false;
		bezeichner = new List<String>();

		// TODO 3 - die Hauptschleife (Aufgabe 3.3 bis 3.6)
		//   a) Lege eine leere Tokenliste an:  List<Token> tokenliste = new List<Token>();
		//   b) Setze zustand auf den Startzustand und terminalstring auf "".
		//   c) Das letzte Token entsteht erst, wenn ein Trennzeichen folgt.
		//      Hänge deshalb an den Text ein Trennzeichen an (siehe Denkfrage in Aufgabe 2).
		//   d) Wiederhole, solange status == STAT_GOOD und noch Zeichen im Text sind:
		//        - Zeichen holen:  char c = text.charAt(pos++);   (pos zeigt danach schon hinter c)
		//        - lexstep(c) aufrufen. Liefert es true, ist ein Token fertig:
		//            Token t = erzeugeToken(terminalstring);
		//            Ist t nicht null: an die Tokenliste anhängen.
		//            Danach terminalstring zurücksetzen.
		//        - zum Schluss zeilenzaehlung(c) aufrufen (erst NACH dem Erzeugen des Tokens!)
		//   e) Ist status nicht STAT_GOOD: Fehlermeldung mit System.err.println(getStatusText())
		//      ausgeben und null zurückgeben. Sonst die Tokenliste zurückgeben.
		//   ★ Zusatz 1: Speichere im Token Zeile und Spalte, in der das Wort beginnt:
		//      t.setZeile(...)  und  t.setPos(...)
		return null; // TODO: ersetzen
	}

	/**
	 * Zählt Zeilenwechsel (fertig). Rufe diese Methode in lex() für jedes Zeichen
	 * auf, nachdem es verarbeitet wurde.
	 */
	private static void zeilenzaehlung(char c) {
		if (c == '\n') {
			aktzeile++;
			zeilenpos = pos; // pos zeigt schon hinter das '\n': hier beginnt die nächste Zeile
		}
	}

	// TODO 4 - Fehler melden (Aufgabe 3.6)
	//   Schreibe eine kleine Hilfsmethode  private static void fehler(int code),
	//   die den Fehlercode in status einträgt und in die Fehlersenke wechselt.
	//   (Die Fehlermeldung selbst liefert getStatusText(), siehe unten.)

	/**
	 * Der Lexer als Automat: verarbeitet EIN Zeichen und wechselt dabei den
	 * Zustand.
	 *
	 * @param c das Zeichen
	 * @return true, wenn jetzt ein Token entstehen muss (also wenn ein Wort oder
	 *         eine Zahl zu Ende ist), sonst false
	 */
	private static boolean lexstep(char c) {
		// TODO 5 - die Zustandsübergänge (Aufgabe 3.5)
		//   Schreibe ein  switch (zustand)  mit einem  case  für jeden Zustand.
		//   Dein Automat aus Aufgabe 2 ist der Bauplan. In jedem Fall gilt:
		//     - Beginnt ein Wort oder eine Zahl: terminalstring = "" + c;
		//     - Läuft ein Wort oder eine Zahl weiter: terminalstring += c;
		//     - Ist ein Wort oder eine Zahl zu Ende (Trennzeichen): Zustand wechseln und true zurückgeben.
		//     - Unzulässiger Übergang: fehler(...) mit dem passenden Fehlercode aufrufen
		//       (ERR_charunknown, ERR_number_in_word, ERR_char_in_number).
		//   Benutze die fertigen Methoden isZiffer, isAlpha und isTrenner.
		//   Kommentare stehen in { } und erzeugen keine Token.
		//   ★ Zusatz 1: Beginnt ein Wort bzw. eine Zahl, merke dir die Stelle:
		//      startZeile = aktzeile;  startSpalte = pos - zeilenpos;
		return false; // TODO: ersetzen
	}

	/**
	 * Macht aus einem fertig gelesenen Wort oder einer fertig gelesenen Zahl ein
	 * Token (fertig). Gibt null zurück und setzt status, wenn kein Token entstehen
	 * kann (z. B. bei einem unbekannten Wort).
	 *
	 * @param text das Wort bzw. die Zahl (mindestens ein Zeichen)
	 */
	private static Token erzeugeToken(String text) {
		fehlerwort = text;
		fehlerzeile = aktzeile;
		boolean istZahl = isZiffer(text.charAt(0));
		if (nextTokenBez) { // nach "Anweisung" folgt der Name der neuen Anweisung
			nextTokenBez = false;
			if (istZahl || Hilfsfunktionen.isStringInArray(text, moves)
					|| Hilfsfunktionen.isStringInArray(text, conditions)
					|| Hilfsfunktionen.isStringInArray(text, control)) {
				status = ERR_bezUnzulaessig;
				return null;
			}
			bezeichner.append(text);
			return new Token(Token.T_Bez, text);
		}
		if (istZahl) {
			try {
				return new Token(Token.T_Zahl, text);
			} catch (NumberFormatException e) {
				status = ERR_zahl_zu_gross;
				return null;
			}
		}
		if (Hilfsfunktionen.isStringInArray(text, moves)) {
			return new Token(Token.T_Move, text);
		}
		if (Hilfsfunktionen.isStringInArray(text, conditions)) {
			return new Token(Token.T_Cond, text);
		}
		if (text.equals("Anweisung")) { // dieses Steuerwort kündigt einen Namen an
			nextTokenBez = true;
			return new Token(Token.T_Cont, text);
		}
		if (Hilfsfunktionen.isStringInArray(text, control)) {
			return new Token(Token.T_Cont, text);
		}
		bezeichner.toFirst(); // ein bereits definierter Name einer eigenen Anweisung?
		while (bezeichner.hasAccess()) {
			if (text.equals(bezeichner.getContent())) {
				return new Token(Token.T_Bez, text);
			}
			bezeichner.next();
		}
		status = ERR_terminal_unknown;
		return null;
	}

	// ===== Hilfsmethoden (fertig) =========================================

	public static boolean isZiffer(char c) {
		return (c >= '0' && c <= '9');
	}

	public static boolean isAlpha(char c) {
		// ö ist erlaubt, denn es kommt in "MarkeLöschen" vor
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == 'ö';
	}

	public static boolean isTrenner(char c) {
		return (c == ' ' || c == '\n' || c == '\t');
	}

	public static void debug(String text) {
		if (debug) {
			System.out.println("L: " + text);
		}
	}

	/**
	 * Fehlertext für die Statuszeile der Oberfläche (fertig).
	 */
	public static String getStatusText() {
		switch (status) {
		case STAT_GOOD:
			return "Lexing fehlerfrei";
		case ERR_charunknown:
			return "Lexing fehlerhaft: Unbekanntes Zeichen (Zeile " + aktzeile + ", Spalte " + (pos - zeilenpos) + ")";
		case ERR_number_in_word:
			return "Lexing fehlerhaft: Ziffer innerhalb eines Wortes (Zeile " + aktzeile + ", Spalte "
					+ (pos - zeilenpos) + ")";
		case ERR_char_in_number:
			return "Lexing fehlerhaft: Buchstabe innerhalb einer Zahl (Zeile " + aktzeile + ", Spalte "
					+ (pos - zeilenpos) + ")";
		case ERR_terminal_unknown:
			return "Lexing fehlerhaft: Unbekanntes Wort '" + fehlerwort + "' (Zeile " + fehlerzeile + ")";
		case ERR_bezUnzulaessig:
			return "Lexing fehlerhaft: '" + fehlerwort + "' ist als Name einer eigenen Anweisung nicht erlaubt (Zeile "
					+ fehlerzeile + ")";
		case ERR_zahl_zu_gross:
			return "Lexing fehlerhaft: Die Zahl '" + fehlerwort + "' ist zu groß (Zeile " + fehlerzeile + ")";
		default:
			return "Lexing fehlerhaft: unbekannter Fehlercode " + status;
		}
	}

	/**
	 * @return 0, wenn alles in Ordnung ist, sonst ein negativer Fehlercode (wird
	 *         von der Oberfläche abgefragt)
	 */
	public static int getStatus() {
		return status;
	}

}
