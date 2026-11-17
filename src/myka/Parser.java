package myka;

/**
 * Der Parser der Sprache MyKa.
 * Er prüft, ob die Tokenfolge den Regeln der Sprache entspricht. Er ist als
 * Kellerautomat (Zustand + Keller) aufgebaut; die Zustandsübergänge werden mit
 * switch-case umgesetzt.
 *
 * Alles, was mit "TODO" markiert ist, ergänzt du. Der Rest ist fertig.
 *
 * Diese Klasse ist statisch: Es wird nie ein Parser-Objekt erzeugt.
 */
public class Parser {
	private static String fehlertext = "";
	private static final boolean debug = false;

	// Kelleralphabet:
	//   #  Kellerboden (leerer Keller)
	//   w  eine Wiederholung ist offen   (wiederhole ... endewiederhole)
	//   i  ein wenn-Block ist offen, noch kein sonst gesehen
	//   s  ein wenn-Block ist offen, der sonst-Teil läuft
	//   a  eine Anweisungsdefinition ist offen (Anweisung ... endeAnweisung)
	//
	// Zustände:
	private static final int Z_Block = 0; // normale Ebene: Befehle, Aufrufe, Beginn/Ende von Strukturen
	private static final int Z_NachWiederhole = 1; // nach "wiederhole": Zahl oder "solange" erwartet
	private static final int Z_NachZahl = 2; // nach "wiederhole <Zahl>": "mal" erwartet
	private static final int Z_NachSolange = 3; // nach "solange": Bedingung erwartet
	private static final int Z_NachWenn = 4; // nach "wenn": Bedingung erwartet
	private static final int Z_NachWennBed = 5; // nach "wenn <Bedingung>": "dann" erwartet
	private static final int Z_NachAnweisung = 6; // nach "Anweisung": Bezeichner erwartet
	private static final int Z_Sink = 7; // Fehlersenke

	private Parser() { // es wird kein Parser-Objekt erzeugt - statische Klasse
	}

	/**
	 * Prüft die Tokenfolge.
	 *
	 * @param tokens die Tokenfolge (vom Controller als Array übergeben)
	 * @return true, wenn das Programm den Regeln der Sprache entspricht
	 */
	public static boolean parse(Token[] tokens) {
		// WICHTIG: Bei Erfolg muss der Fehlertext genau "Success!!" lauten.
		// Die Oberfläche prüft diesen Text und nicht den Rückgabewert!
		fehlertext = "Success!!";
		if (tokens == null) {
			return true;
		}
		Stack<Character> keller = new Stack<Character>();
		keller.push('#');
		int zustand = Z_Block;

		for (int pos = 0; pos < tokens.length && zustand != Z_Sink; pos++) {
			Token t = tokens[pos];
			debug("Zustand " + zustand + ", Keller oben " + keller.top() + ", Token " + t);
			switch (zustand) {
			case Z_Block:
				if (t.getTyp() == Token.T_Move || t.getTyp() == Token.T_Bez) {
					// Befehl oder Aufruf einer eigenen Anweisung: bleibt im Zustand
				} else if (ist(t, "wiederhole")) {
					keller.push('w');
					zustand = Z_NachWiederhole;
				} else if (ist(t, "wenn")) {
					keller.push('i');
					zustand = Z_NachWenn;
				} else if (ist(t, "Anweisung") && keller.top() == '#') { // nur auf oberster Ebene
					keller.push('a');
					zustand = Z_NachAnweisung;
				} else if (ist(t, "endewiederhole") && keller.top() == 'w') {
					keller.pop();
				} else if (ist(t, "sonst") && keller.top() == 'i') {
					keller.pop();
					keller.push('s');
				} else if (ist(t, "endewenn") && (keller.top() == 'i' || keller.top() == 's')) {
					keller.pop();
				} else if (ist(t, "endeAnweisung") && keller.top() == 'a') {
					keller.pop();
				} else {
					zustand = fehler("Unerwartetes Token", t);
				}
				break;
			case Z_NachWiederhole:
				if (t.getTyp() == Token.T_Zahl) {
					zustand = Z_NachZahl;
				} else if (ist(t, "solange")) {
					zustand = Z_NachSolange;
				} else {
					zustand = fehler("Nach wiederhole fehlt eine Zahl oder solange", t);
				}
				break;
			case Z_NachZahl:
				zustand = ist(t, "mal") ? Z_Block : fehler("wiederhole <Zahl> ohne mal", t);
				break;
			case Z_NachSolange:
				zustand = (t.getTyp() == Token.T_Cond) ? Z_Block : fehler("wiederhole solange ohne Bedingung", t);
				break;
			case Z_NachWenn:
				zustand = (t.getTyp() == Token.T_Cond) ? Z_NachWennBed : fehler("wenn ohne Bedingung", t);
				break;
			case Z_NachWennBed:
				zustand = ist(t, "dann") ? Z_Block : fehler("wenn <Bedingung> ohne dann", t);
				break;
			case Z_NachAnweisung:
				zustand = (t.getTyp() == Token.T_Bez) ? Z_Block : fehler("Anweisung ohne Bezeichner", t);
				break;
			default:
				zustand = Z_Sink;
			}
		}

		if (zustand == Z_Sink) {
			return false;
		}
		// Alle Token sind gelesen: darf der Automat akzeptieren?
		if (zustand != Z_Block) {
			fehlertext = "Programm endet mitten in einer Anweisung (Reached End Of File while parsing)";
			return false;
		}
		switch (keller.top()) {
		case 'w':
			fehlertext = "Programm endet in einer Schleife (endewiederhole fehlt)";
			return false;
		case 'i':
		case 's':
			fehlertext = "Programm endet in einem wenn-Block (endewenn fehlt)";
			return false;
		case 'a':
			fehlertext = "Programm endet in einer Anweisungsdefinition (endeAnweisung fehlt)";
			return false;
		default:
			return true; // Keller leer (nur #)
		}
	}

	/** setzt den Fehlertext und liefert den Folgezustand Fehlersenke */
	private static int fehler(String text, Token t) {
		fehlertext = text + ": " + t;
		return Z_Sink;
	}

	/** Fertig: Ist t ein Steuerwort mit genau diesem Wert? */
	private static boolean ist(Token t, String wert) {
		return t.getTyp() == Token.T_Cont && t.getWert().equals(wert);
	}

	/** @return der Fehlertext; bei Erfolg genau "Success!!" (wird von der Oberfläche angezeigt) */
	public static String getFehlerText() {
		return fehlertext;
	}

	private static void debug(String text) {
		if (debug) {
			System.out.println("P:" + text);
		}
	}
}
