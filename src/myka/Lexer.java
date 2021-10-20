package myka;

public class Lexer {
	private static final int Z_Trenner = 0; // Zustand Trenner
	private static final int Z_Alpha = 1; // Wort wird gebildet
	private static final int Z_Zahl = 2; // Zahl wird gebildet
	private static final int Z_Kommentar = 3; // Mitten in einem Kommentar
	private static final int Z_Sink = 4; // Fehlersenke
	private static final int STAT_GOOD = 0;
	private static final int ERR_charunknown = -1;
	private static final int ERR_number_in_word = -2;
	private static final int ERR_char_in_number = -3;
	private static final int ERR_terminal_unknown = -4;
	private static final int ERR_bezUnzulaessig = -5; //Bezeichner nicht zulässig
	private static String[] conditions = new String[] { "IstWand", "NichtIstWand", "IstMarke", "NichtIstMarke", "IstZiegel", "NichtIstZiegel" };
	private static String[] moves = new String[] { "Schritt", "LinksDrehen", "RechtsDrehen", "Hinlegen", "Aufheben",
			"MarkeSetzen", "MarkeLöschen" };
	private static String[] control = new String[] { "wiederhole", "mal", "solange", "endewiederhole", "wenn", "dann", "sonst",
			"endewenn", "Beenden", "Anweisung", "endeAnweisung" };
	private static int status = STAT_GOOD;
	private static int zustand = Z_Trenner;
	private static int tokentyp = 0;
	private static int aktzeile = 0;
	private static int pos = 0;
	private static int zeilenpos = 0;
	private static String terminalstring = "";
	private static final boolean debug = true;
	private static boolean nextTokenBez = false; // Auf true setzen, wenn das nächste Token ein Bezeichner ist
	private static List<String> bezeichner = new List<String>();

	private Lexer() { //es wird kein Lexer-Objekt erzeugt - statische Klasse
	}

	public static void main(String[] args) {
		if (!debug) {
		// Testfunktion
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
			MyKaController.main(args);
		}
	}

	public static List<Token> lex(String text) {
		List<Token> tokenlist = new List<Token>();
		// Leerzeichen anhaengen um alle Terminale richtig zu interpretieren
		text += " ";
		zustand = 0;
		status = STAT_GOOD;
		aktzeile = 1; // Wir beginnen in Zeile 1
		pos = 0;
		bezeichner = new List<String>(); // Sammlung von erzeugten Bezeichnern
		while (status == STAT_GOOD && pos < text.length()) { // go on lexing
			if (lexstep(text.charAt(pos++))) { // token muss gebildet werden
				// Token erzeugen und an die Liste anhaengen
				Token t = generateTokenFromString(terminalstring);
				if (t != null) {
					t.setZeile(aktzeile);
					t.setPos(pos-zeilenpos);
					terminalstring = ""; // String zuruecksetzen
					tokenlist.append(t);
				}
				debug("Token generiert: " + t);
			}
		}
		if (status != STAT_GOOD)
			return null;
		return tokenlist;
	}

	private static Token generateTokenFromString(String text) {
		debug("in generateTokenFromString: " + text);
		if (nextTokenBez) {
			debug("Bezeichner anlegen");
			nextTokenBez = false; // zurücksetzen
			if (tokentyp != Z_Alpha || Hilfsfunktionen.isStringInArray(text, moves) 
					|| Hilfsfunktionen.isStringInArray(text, conditions)
					|| Hilfsfunktionen.isStringInArray(text, control)) {
				status = ERR_bezUnzulaessig;
				return null;
			} else {
				bezeichner.append(text);
				return new Token(Token.T_Bez, text);
			}
		} else if (tokentyp == Z_Alpha) {
			for (int i = 0; i < moves.length; i++) {
				debug("Vergleiche mit " + moves[i]);
				if (text.equals(moves[i])) {
					return new Token(Token.T_Move, text);
				}
			}
			for (int i = 0; i < conditions.length; i++) {
				if (text.equals(conditions[i])) {
					return new Token(Token.T_Cond, text);
				}
			}
			// Dieses if muss vor die Prüfung auf control, da Anweisung ein ctrl-Token ist
			if (text.equals("Anweisung")) { //Neue Anweisung
				nextTokenBez = true; //Das nächste Token sollte ein Bezeichner sein
				return new Token(Token.T_Cont, text);				
			}
			for (int i = 0; i < control.length; i++) {
				if (text.equals(control[i])) {
					return new Token(Token.T_Cont, text);
				}
			}
			bezeichner.toFirst();
			while (bezeichner.hasAccess()) {
				if (text.equals(bezeichner.getContent())) {
					return new Token(Token.T_Bez, text);
				}
				bezeichner.next();
			}
			status = ERR_terminal_unknown;
			return null;
		} else if (tokentyp == Z_Zahl) {
			return new Token(Token.T_Zahl, text);
		}
		return null;
	}

	/**
	 * Der Text wird zeichenweise gelext. Solange kein Fehler auftritt wird der
	 * Wordstring mit den Zeichen ergaenzt, die keien Leerzeichen sind
	 * 
	 * @param c zu lexendes Zeichen
	 * @return true wenn ein Token erzeugt werden muss
	 */
	private static boolean lexstep(char c) {
		debug("Lexing char: " + c + " Zustand: " + zustand);
		if (c == '\n') {
			aktzeile++;
			zeilenpos = pos;
		}
		switch (zustand) {
		case Z_Kommentar: // Im Kommentar 
			if (c=='}') zustand = Z_Trenner;
			return false;
		case Z_Trenner: // Noch kein neues Terminal begonnen
			if (isZiffer(c)) {
				zustand = Z_Zahl;
				terminalstring = "" + c;
			} else if (c=='{') {
				zustand = Z_Kommentar;
			} else if (isAlpha(c)) {
				zustand = Z_Alpha;
				terminalstring = "" + c;
			} else if (isTrenner(c)) {
			} else {
				status = ERR_charunknown;
				zustand = Z_Sink;
			}
			return false;
		case Z_Zahl: // Zahl wird gelesen
			if (isZiffer(c)) {
				terminalstring += c;
			} else if (isAlpha(c)) {
				status = ERR_char_in_number;
				zustand = Z_Sink;
			} else if (isTrenner(c)) {
				zustand = Z_Trenner;
				tokentyp = Z_Zahl;
				return true; // Token bilden
			} else {
				status = ERR_charunknown;
				zustand = Z_Sink;
			}
			return false;
		case Z_Alpha: // Wort wird gelesen
			if (isAlpha(c)) {
				terminalstring += c;
			} else if (isZiffer(c)) {
				status = ERR_number_in_word;
				zustand = Z_Sink;
			} else if (isTrenner(c)) {
				zustand = Z_Trenner;
				tokentyp = Z_Alpha;
				return true; // Token muss gebildet werden
			} else {
				status = ERR_charunknown;
				zustand = Z_Sink;
			}
			return false;
		default: // Unbekannter Zustand -> Fehlersenke
			zustand = Z_Sink;
		}
		return false;
	}

	public static boolean isZiffer(char c) {
		return (c >= '0' && c <= '9');
	}

	public static boolean isAlpha(char c) {
		// ö ist erlaubt - puh
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z' || c == 'ö');
	}

	public static boolean isTrenner(char c) {
		return (c == ' ' || c == '\n' || c == '\t');
	}

	public static void debug(String text) {
		if (debug) {
			System.out.println(text);
		}
	}

	public static String getStatusText() {
		String fehlertext = "";
		switch (status) {
		case STAT_GOOD:
			fehlertext = "Lexing Fehlerfrei";
			break;
		case ERR_char_in_number:
			fehlertext = "Lexing fehlerhaft: Zeichen in einer Zahl";
			break;
		case ERR_charunknown:
			fehlertext = "Lexing fehlerhaft: Unbekanntes Zeichen";
			break;
		case ERR_number_in_word:
			fehlertext = "Lexing fehlerhaft: Ziffer innerhalb eines Wortes";
			break;
		case ERR_terminal_unknown:
			fehlertext = "Lexing fehlerhaft: Unbekanntes Schluesselwort (Terminal): " + terminalstring;
			break;
		}
		if (status != STAT_GOOD) {
			fehlertext += "Zeile: " + aktzeile + " Pos: " + (pos - zeilenpos);
		}
		return fehlertext;
	}

	public static int getStatus() {
		return status;
	}

}
