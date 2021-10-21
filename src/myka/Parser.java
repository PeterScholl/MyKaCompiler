package myka;

/**
 * Sprache erkennen, die in der Tokenliste genutzt wird
 * TODO: grobe Skizze der Grammatik hier einfügen!!!
 * 
 * TODO: Kelleralphabet hier notieren
 * @author peter
 *
 */
public class Parser {
	private static String fehlertext="";
	private static Stack<Character> keller = new Stack<Character>();
	private static final boolean debug = true;
	private static int curpos = 0;
	private static Token[] tokenArray = null;

	private Parser() {
		// Vom Parser wird kein Objekt erzeugt - statische Klasse
	}
	
	public static boolean parse(Token[] tokenliste) {
		fehlertext="Success!!";
		keller = new Stack<Character>();
		keller.push('#'); //leerer Kellerspeicher
		curpos = 0;
		tokenArray = tokenliste;
		if (tokenArray==null) return true;
		return pruefeProgramm();
	}
	
	public static boolean parse(List<Token> tokenliste) {
		//Falls eine Tokenliste übergeben wird, wird diese
		//in eine Array konvertiert
		return parse(Hilfsfunktionen.convertTokenListToArray(tokenliste));
	}

	private static boolean pruefeProgramm() {
		if (hasAccess()) {  // wenn noch nicht am Ende
			Token akt = tokenArray[curpos];
			debug("In pruefeProgramm mit Token:"+akt); //Beispiel für debug-Ausgaben
			if (akt.getTyp()==Token.T_Typ3 && keller.top()=='#') {
				//einfaches Bewegungstoken - also im nächsten Schritt weiter machen
				curpos++;
				return pruefeProgramm();
			} else if (akt.getTyp()==Token.T_Typ4 && akt.getWert().equals("wiederhole") && keller.top()=='#') {
				//Wiederholung begonnen
				curpos++;
				keller.push('w'); //wiederholung eintragen
				return pruefeOpenWhile();
			// TODO wenn verarbeiten (wie bei wiederhole)
			} else {
				fehlertext = "Unerwartetes Token: "+akt;
				return false;
			}
		}
		return true; //leeres (Rest-)Programm
	}

	private static boolean pruefeOpenWhile() {
		if (!hasAccess()) return eofWhileParsing(); //Eigentlich ueberfluessig
		Token akt = tokenArray[curpos];
		debug("In pruefe OpenWhile mit Token:"+akt);
		//TODO folgende Zeilen löschen und durch sinnvolle Prüfung ersetzen
		//     d.h. je nach Typ von while-Schleife unterschiedliche Tokenfolge prüfen
		curpos+=2; // zu "Testzwecken" in die Schleife springen 
		return pruefeInWhile();
	}
		
	private static boolean pruefeInWhile() {
		if (keller.top()=='#') { // Wenn wir eine Schleife geschlossen haben und dann aus allen raus sind
			return pruefeProgramm();
		}
		//TODO prüfen ob nach verlassen von while in einer Wenn-Bedingung -> pruefeInIf
		if (hasAccess()) {
			Token akt = tokenArray[curpos];
			debug("In pruefe InWhile mit Token:"+akt);
			if ((akt.getTyp()==Token.T_Typ3) && keller.top()=='w') {
				curpos++;
				return pruefeInWhile();
			} else if (akt.getTyp()==Token.T_Typ4 && akt.getWert().equals("endewiederhole") && keller.top()=='w') {
				//eine Wiederholung beendet
				keller.pop(); //wiederholung aus Keller entfernen
				curpos++;
				return pruefeInWhile();
			//TODO: prüfen ob wenn Begonnen wird - return pruefeOpenIf
			//TODO: prüfen ob neue wiederhole-schleife begonnen wird - return pruefeOpenWhile
			} else {
				fehlertext = "Unerwartetes Token: "+akt;
				return false;
			}
		}
		fehlertext = "Programm endet in Schleife";
		return false; 
	}
	private static boolean pruefeOpenIf() {
		if (!hasAccess()) return eofWhileParsing(); //Eigentlich ueberfluessig
		//TODO: analog pruefeOpenWhile implementieren
		return false;
	}
		
	private static boolean pruefeInIf() {
		//TODO: Analog pruefeInWhile implementieren
		fehlertext = "Programm endet in If-Anweisung";
		return false; 
	}


	private static boolean eofWhileParsing() {
		fehlertext = "Reached End Of File while parsing";
		return false;
	}

	public static String getFehlerText() {
		return fehlertext;
	}
	
	private static void debug(String text) {
		if (debug) System.out.println("P:"+text);
	}
	
	private static boolean hasAccess() {
		return curpos < tokenArray.length;
	}
	
	

}
