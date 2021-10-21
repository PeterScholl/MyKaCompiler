package myka;

public class Lexer {
	//TODO: Die Zustands- und Fehlerbezeichnungen solltest du an deinen Automaten anpassen
	// Verwende Refactor-Rename zum umbenennen - Tastenkombination Shift-Alt-R
	private static final int Z_1 = 0; // Zustandsbeschreibung1
	private static final int Z_2 = 1; // Zustandsbeschreibung2
	private static final int Z_3 = 2; // Zustandsbeschreibung3
	private static final int Z_4 = 3; // Zustandsbeschreibung4
	private static final int Z_5 = 4; // Zustandsbeschreibung5 (Sink ;-) )
	//TODO: Ebenso sollten hier sinnvolle Fehlerbezeichungen gewählt werden
	/**
	 * 
	 */
	private static final int STAT_GOOD = 0;	
	private static final int ERR_1 = -1;
	private static final int ERR_2 = -2;
	private static final int ERR_3 = -3;
	private static final int ERR_4 = -4;
	// TODO: Vielleicht kannst du diese Arrays gebrauchen - sonst löschen
	private static String[] conditions = new String[] { "IstWand", "NichtIstWand", "IstMarke", "NichtIstMarke", "IstZiegel", "NichtIstZiegel" };
	private static String[] moves = new String[] { "Schritt", "LinksDrehen", "RechtsDrehen", "Hinlegen", "Aufheben",
			"MarkeSetzen", "MarkeLöschen" };
	private static String[] control = new String[] { "wiederhole", "mal", "solange", "endewiederhole", "wenn", "dann", "sonst",
			"endewenn", "Beenden", "Anweisung", "endeAnweisung" };
	private static int status = STAT_GOOD;
	private static int tokentyp = 0;
	private static int zustand = Z_1;
	private static int pos = 0;
	private static String terminalstring = "";
	private static final boolean debug = true;

	private Lexer() { //es wird kein Lexer-Objekt erzeugt - statische Klasse
	}

	public static void main(String[] args) { //Diese main-Methode dient nur dazu, den Lexer ohen GUI zu testen!!
		//TODO gerne nach bedarf anpassen
		if (debug) {
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
		} else { //wenn nicht im debug-Modus dann die eigentliche main-Methode für das GUI aufrufen
			MyKaController.main(args);
		}
	}

	/**
	 * Diese Methode ruft die Zeichenweise lesende funktion lexstep auf
	 * TODO Diese Methode muss eigentlich nicht geändert werden - es sei denn du möchtest die Zeile im Token speichern
	 * TODO Diese beiden TODO-Zeilen löschen ;-)
	 * @param text - der zu lexende Text
	 * @return die sich ergebende Tokenliste - null bei Fehler
	 */
	public static List<Token> lex(String text) {
		List<Token> tokenlist = new List<Token>();
		// Leerzeichen anhaengen um alle Terminale richtig zu interpretieren
		text += " ";
		zustand = Z_1; //Startzustand
		status = STAT_GOOD;
		pos = 0;
		while (status == STAT_GOOD && pos < text.length()) { // go on lexing
			// die Methode lexstep gibt true zurück, wenn ein Übergang des
			// Automaten stattgefunden hat, der zur Erzeugung eines Tokens führt
			if (lexstep(text.charAt(pos++))) { // token muss gebildet werden
				// Token erzeugen und an die Liste anhaengen
				Token t = generateTokenFromString(terminalstring);
				if (t != null) {
					t.setPos(pos); // Um Fehler finden zu können wird die pos im Token gespeichert
					terminalstring = ""; // String zuruecksetzen
					tokenlist.append(t);
				}
				debug("Token generiert: " + t);
			}
		}
		if (status != STAT_GOOD) //kein erfolgreiches Lexen
			return null;
		return tokenlist;
	}

	/**
	 * methode erzeugt aus dem übergebenen String das zugehörige Token
	 * also z.B. aus Schritt ein Token vom Typ T_Move mit Wert "Schritt"
	 * @param text - der Text aus dem ein Token geformt wird
	 * @return das passende Token
	 */
	private static Token generateTokenFromString(String text) {
		debug("in generateTokenFromString: " + text);
		//TODO: aus dem gelesenen String das richtige Token erzeugen!
		if (tokentyp == Z_2) { // Token aus einem Wort erzeugen
			if (text.equals("Schritt")) {
				return new Token(Token.T_Typ3, text);
			}
			if (text.equals("LinksDrehen")) {
				return new Token(Token.T_Typ3, text);
			}
			status = ERR_4;
			return null;
		} else if (tokentyp == Z_3) {
			return new Token(Token.T_Typ1, text);
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
		//TODO die switch-case-Anweisung muss deinem Automaten entsprechen
		switch (zustand) {
		case Z_4: // Im Kommentar 
			if (c=='}') zustand = Z_1;
			return false;
		case Z_1: // Noch kein neues Terminal begonnen
			//TODO implementieren - alle Übergänge deines Automaten!
			if (c=='{') {
				zustand = Z_4;
			} else if (isAlpha(c)) {
				zustand = Z_2;
				terminalstring = "" + c;
			} else if (isTrenner(c)) {
				//TODO: Muss hier wirklich nichts hin? ;-)
			} else {
				status = ERR_1;
				zustand = Z_5;
			}
			return false;
		case Z_2: // Wort wird gelesen
			if (isAlpha(c)) {
				terminalstring += c;
			} else if (isTrenner(c)) {
				zustand = Z_1;
				tokentyp = Z_2;
				return true; // Token muss gebildet werden
			} else {
				status = ERR_1;
				zustand = Z_5;
			}
			return false;
		default: // Unbekannter Zustand -> Fehlersenke
			zustand = Z_5;
		}
		return false;
	}

	public static boolean isZiffer(char c) {
		return (c >= '0' && c <= '9');
	}

	public static boolean isAlpha(char c) {
		// ö ist erlaubt - puh - wegen MarkeLöschen
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z' || c == 'ö');
	}

	public static boolean isTrenner(char c) {
		return (c == ' ' || c == '\n' || c == '\t');
	}

	public static void debug(String text) {
		if (debug) {
			System.out.println("L: "+text);
		}
	}

	public static String getStatusText() {
		String fehlertext = "";
		switch (status) {
		case STAT_GOOD:
			fehlertext = "Lexing Fehlerfrei";
			break;
		case ERR_3:
			fehlertext = "Lexing fehlerhaft: Zeichen in einer Zahl";
			break;
		case ERR_1:
			fehlertext = "Lexing fehlerhaft: Unbekanntes Zeichen";
			break;
		case ERR_2:
			fehlertext = "Lexing fehlerhaft: Ziffer innerhalb eines Wortes";
			break;
		case ERR_4:
			fehlertext = "Lexing fehlerhaft: Unbekanntes Schluesselwort (Terminal): " + terminalstring;
			break;
		}
		if (status != STAT_GOOD) {
			fehlertext += "Zeile: " + "?" + " Pos: " + pos;
		}
		return fehlertext;
	}

	public static int getStatus() {
		return status;
	}

}
