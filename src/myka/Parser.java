package myka;

/**
 * Sprache erkennen, die in der Tokenliste genutzt wird
 * Start: Programm
 * Programm -> leer | Anweisung Programm | eAnweisung eigAnweisung Programm
 * Anweisung -> befehl | wiederhole Wiederholung | wenn Bedingung | {Bez} 
 * Wiederholung -> zahl mal Programm endewiederhole | solange ...
 * Bedingung -> {Bedingung} dann Programm endewenn | {Bedingung} dann Programm sonst Programm endewenn
 * eigAnweisung -> {Bez} Programm endeAnweisung
 * Das ist nur mit einem Kellerspeicher moeglich
 * Kelleralphabet # , w - while Schleife, i - if, s - sonstZweig von if, a - eigeneanweisung
 * @author peter
 *
 */
public class Parser {
	private static String fehlertext="";
	private static Stack<Character> keller = new Stack<Character>();
	private static final boolean debug = false;
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

	private static boolean pruefeProgramm() {
		if (hasAccess()) {
			Token akt = tokenArray[curpos];
			debug("In pruefe Programm mit Token:"+akt);
			if (akt.getTyp()==Token.T_Move && keller.top()=='#') {
				curpos++;
				return pruefeProgramm();
			} else if (akt.getTyp()==Token.T_Bez && keller.top()=='#') {
				curpos++;
				return pruefeProgramm();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wiederhole") && keller.top()=='#') {
				//Wiederholung begonnen
				curpos++;
				keller.push('w'); //wiederholung eintragen
				return pruefeOpenWhile();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wenn") && keller.top()=='#') {
				//WennDann begonnen
				curpos++;
				keller.push('i'); //if eintragen
				return pruefeOpenIf();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("Anweisung") && keller.top()=='#') {
				//Anweisung wird definiert
				curpos++;
				keller.push('a'); //Anweisung eintragen
				return pruefeOpenAnweisung();
			} else {
				fehlertext = "Unerwartetes Token: "+akt;
				return false;
			}
		}
		return true; //leeres (Rest-)Programm
	}

	private static boolean pruefeOpenAnweisung() {
		if (!hasAccess()) return eofWhileParsing(); //Eigentlich ueberfluessig
		Token akt = tokenArray[curpos];
		debug("In pruefe Anwisung mit Token:"+akt);
		if (akt.getTyp()==Token.T_Bez) {
			curpos++;
			if (!hasAccess()) return eofWhileParsing();
			boolean result = pruefeInAnweisung();
			return result;
		} else {
			fehlertext = "Anweisung erstellt aber kein Bezeichner genannt: "+akt;
			return false;
		}		
	}

	private static boolean pruefeInAnweisung() {
		if (keller.top()=='#') { //wieder auf Ausgangsebene a beendet
			return pruefeProgramm();
		} //Andere Optionen gibt es nicht, da Anweisungen nicht innerhalb von if oder while deklariert werden
		if (hasAccess()) {
			Token akt = tokenArray[curpos];
			debug("In pruefe InAnweisung mit Token:"+akt);
			if (akt.getTyp()==Token.T_Move && keller.top()=='a') {
				curpos++;
				return pruefeInAnweisung();
			} else if (akt.getTyp()==Token.T_Bez && keller.top()=='a') {
				curpos++;
				return pruefeInAnweisung();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("endeAnweisung") && keller.top()=='a') {
				//Anweisungsbeschreibung beendet
				keller.pop(); //Anweisung aus Keller entfernen
				curpos++;
				return pruefeInAnweisung();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wenn")) {
				//WennDann begonnen
				keller.push('i'); //if eintragen
				curpos++;
				return pruefeOpenIf();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wiederhole")) {
				//Neue Schleife begonnen
				keller.push('w'); //schleife eintragen
				curpos++;
				return pruefeOpenWhile();
			} else {
				fehlertext = "Unerwartetes Token: "+akt;
				return false;
			}
		}
		fehlertext = "Programm endet in Anweisungserstellung";
		return false; 
	}
	
	private static boolean pruefeOpenWhile() {
		if (!hasAccess()) return eofWhileParsing(); //Eigentlich ueberfluessig
		Token akt = tokenArray[curpos];
		debug("In pruefe OpenWhile mit Token:"+akt);
		if (akt.getTyp()==Token.T_Zahl) {
			//Wiederhole n mal schleife
			curpos++;
			if (!hasAccess()) return eofWhileParsing();
			akt = tokenArray[curpos];
			if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("mal")) {
				curpos++;
				if (!hasAccess()) return eofWhileParsing();
				//hier startet die eigentliche Pruefung auf Input
				boolean result = pruefeInWhile();
				return result;
			}
			fehlertext = "while ohne Wort mal";
			return false;
		} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("solange")) { 
			//Wiederhole solange Schleife
			curpos++;
			if (!hasAccess()) return eofWhileParsing();
			akt = tokenArray[curpos];
			if (akt.getTyp()==Token.T_Cond) {
				curpos++;
				if (!hasAccess()) return eofWhileParsing();
				//hier startet die eigentliche Pruefung auf Input
				return pruefeInWhile();
			}
			fehlertext = "wiederhole solange ohne Bedingung";
			return false;
		
		} else {
			fehlertext = "While schleife nicht richtig erstellt: "+akt;
			return false;
		}
	}
		
	private static boolean pruefeInWhile() {
		if (keller.top()=='#') {
			return pruefeProgramm();
		}
		if (keller.top()=='a') {
			return pruefeInAnweisung();
		}
		if (keller.top()=='i' || keller.top()=='s') {
			return pruefeInIf();
		}	
		if (hasAccess()) {
			Token akt = tokenArray[curpos];
			debug("In pruefe InWhile mit Token:"+akt);
			if ((akt.getTyp()==Token.T_Move || akt.getTyp()==Token.T_Bez) && keller.top()=='w') {
				curpos++;
				return pruefeInWhile();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("endewiederhole") && keller.top()=='w') {
				//eine Wiederholung beendet
				keller.pop(); //wiederholung aus Keller entfernen
				curpos++;
				return pruefeInWhile();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wenn")) {
				//WennDann begonnen
				keller.push('i'); //if eintragen
				curpos++;
				return pruefeOpenIf();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wiederhole")) {
				//Neue Schleife begonnen
				keller.push('w'); //schleife eintragen
				curpos++;
				return pruefeOpenWhile();
			} else {
				fehlertext = "Unerwartetes Token: "+akt;
				return false;
			}
		}
		fehlertext = fehlertextMitToken("Programm endet in Schleife");
		return false; 
	}
	private static boolean pruefeOpenIf() {
		if (!hasAccess()) return eofWhileParsing(); //Eigentlich ueberfluessig
		Token akt = tokenArray[curpos];
		debug("In pruefe OpenIf mit Token:"+akt);
		if (akt.getTyp()==Token.T_Cond) {
			//in Bedingung
			curpos++;
			if (!hasAccess()) return eofWhileParsing();
			akt = tokenArray[curpos];
			if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("dann")) {
				curpos++;
				if (!hasAccess()) return eofWhileParsing();
				return pruefeInIf();
			}
			fehlertext = fehlertextMitToken("wenn ohne dann!");
			return false;
		} else {
			fehlertext = fehlertextMitToken("wenn-Verzeweigung nicht richtig erstellt");
			return false;
		}
	}
		
	private static boolean pruefeInIf() {
		if (keller.top()=='#') {
			return pruefeProgramm();
		}
		if (keller.top()=='a') {
			return pruefeInAnweisung();
		}
		if (keller.top()=='w') {
			//return false;
			return pruefeInWhile();
		}	
		if (hasAccess()) {
			Token akt = tokenArray[curpos];
			debug("In pruefe InIf mit Token:"+akt);
			if ((akt.getTyp()==Token.T_Move || akt.getTyp()==Token.T_Bez)) {
				curpos++;
				return pruefeInIf();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("sonst") && keller.top()=='i') {
				//eine Bedingungsbearbeitung beendet
				keller.pop();
				keller.push('s');
				curpos++;
				return pruefeInIf();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("endewenn")) {
				//eine Bedingung beendet
				keller.pop(); //Bedingung aus Keller entfernen
				curpos++;
				return pruefeInIf();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wenn")) {
				//WennDann begonnen
				keller.push('i'); //if eintragen
				curpos++;
				return pruefeOpenIf();
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wiederhole")) {
				//Wiederhole begonnen
				keller.push('w'); //wiederhole eintragen
				curpos++;
				return pruefeOpenWhile();
			} else {
				fehlertext = "Unerwartetes Token: "+akt;
				return false;
			}
		}
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
	
	private static String fehlertextMitToken(String text) {
		if (hasAccess()) {
			return text+": "+tokenArray[curpos];
		} else {
			return text;
		}
	}
	
	

}
