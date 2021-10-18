package myka;

/**
 * Sprache erkennen, die in der Tokenliste genutzt wird
 * Start: Programm
 * Programm -> leer | Anweisung Programm
 * Anweisung -> befehl | wiederhole Wiederholung Programm | wenn Bedingung Programm
 * Wiederholung -> zahl mal Programm endewiederhole | solange ...
 * Bedingung -> {Bedingung} dann Programm endewenn | {Bedingung} dann Programm sonst Programm endewenn
 * Das ist nur mit einem Kellerspeicher moeglich
 * Kelleralphabet # , w - while Schleife, i - if, s - sonstZweig von if
 * @author peter
 *
 */
public class Interpreter {
	private static String fehlertext="";
	private static Stack<Character> keller = new Stack<Character>();
	private static boolean debug = true;
	private static MyKaController controller = null;

	public Interpreter() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean execute(List<Token> tokenliste) {
		fehlertext="Success!!";
		keller = new Stack<Character>();
		keller.push('#'); //leerer Kellerspeicher
		tokenliste.toFirst();
		controller = MyKaController.getController();
		executeProgramm(tokenliste);
	}

	private static void executeProgramm(List<Token> tokenliste) {
		if (tokenliste.hasAccess()) {
			Token akt = tokenliste.getContent();
			debug("In execute Programm mit Token:"+akt);
			if (akt.getTyp()==Token.T_Move && keller.top()=='#') {
				controller.execute(MyKaController.RBefehl, new String[] {akt.getWert()});
				tokenliste.next();
				executeProgramm(tokenliste);
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wiederhole") && keller.top()=='#') {
				//Wiederholung begonnen
				tokenliste.next();
				keller.push('w'); //wiederholung eintragen
				executeOpenWhile(tokenliste);
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wenn") && keller.top()=='#') {
				//WennDann begonnen
				tokenliste.next();
				keller.push('i'); //if eintragen
				executeOpenIf(tokenliste);
			} else {
				fehlertext = "Unerwartetes Token: "+akt;
				return;
			}
		}
	}

	private static void executeOpenWhile(List<Token> tokenliste) {
		Token akt = tokenliste.getContent();
		debug("In execute OpenWhile mit Token:"+akt);
		if (akt.getTyp()==Token.T_Zahl) {
			//Wiederhole n mal schleife
			int anz = akt.getValue();
			tokenliste.next();
			akt = tokenliste.getContent();
			if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("mal")) {
				tokenliste.next();
				if (!tokenliste.hasAccess()) return eofWhileParsing();
				//hier startet die eigentliche Pruefung auf Input
				return pruefeInWhile(tokenliste);
			}
			fehlertext = "while ohne Wort mal";
			return false;
		} else {
			fehlertext = "While schleife nicht richtig erstellt";
			return false;
		}
	}
		
	private static boolean pruefeInWhile(List<Token> tokenliste) {
		if (keller.top()=='#') {
			return executeProgramm(tokenliste);
		}
		if (keller.top()=='i' || keller.top()=='s') {
			return pruefeInIf(tokenliste);
		}	
		if (tokenliste.hasAccess()) {
			Token akt = tokenliste.getContent();
			debug("In pruefe InWhile mit Token:"+akt);
			if (akt.getTyp()==Token.T_Move && keller.top()=='w') {
				tokenliste.next();
				return pruefeInWhile(tokenliste);
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("endewiederhole") && keller.top()=='w') {
				//eine Wiederholung beendet
				keller.pop(); //wiederholung aus Keller entfernen
				tokenliste.next();
				return pruefeInWhile(tokenliste);
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wenn")) {
				//WennDann begonnen
				keller.push('i'); //if eintragen
				return executeOpenIf(tokenliste);
			} else {
				fehlertext = "Unerwartetes Token: "+akt;
				return false;
			}
		}
		fehlertext = "Programm endet in Schleife";
		return false; 
	}
	private static boolean executeOpenIf(List<Token> tokenliste) {
		if (!tokenliste.hasAccess()) return eofWhileParsing(); //Eigentlich ueberfluessig
		Token akt = tokenliste.getContent();
		debug("In pruefe OpenIf mit Token:"+akt);
		if (akt.getTyp()==Token.T_Cond) {
			//in Bedingung
			tokenliste.next();
			if (!tokenliste.hasAccess()) return eofWhileParsing();
			akt = tokenliste.getContent();
			if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("dann")) {
				tokenliste.next();
				if (!tokenliste.hasAccess()) return eofWhileParsing();
				return pruefeInIf(tokenliste);
			}
			fehlertext = "if ohne dann!";
			return false;
		} else {
			fehlertext = "if Abfrage nicht richtig erstellt";
			return false;
		}
	}
		
	private static boolean pruefeInIf(List<Token> tokenliste) {
		if (keller.top()=='#') {
			return executeProgramm(tokenliste);
		}
		if (keller.top()=='w') {
			//return false;
			return pruefeInWhile(tokenliste);
		}	
		if (tokenliste.hasAccess()) {
			Token akt = tokenliste.getContent();
			debug("In pruefe InIf mit Token:"+akt);
			if (akt.getTyp()==Token.T_Move) {
				tokenliste.next();
				return pruefeInIf(tokenliste);
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("sonst") && keller.top()=='i') {
				//eine Bedingungsbearbeitung beendet
				keller.pop();
				keller.push('s');
				tokenliste.next();
				return pruefeInIf(tokenliste);
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("endewenn")) {
				//eine Bedingung beendet
				keller.pop(); //Bedingung aus Keller entfernen
				tokenliste.next();
				return pruefeInIf(tokenliste);
			} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("wiederhole")) {
				//Wiederhole begonnen
				keller.push('w'); //if eintragen
				return executeOpenWhile(tokenliste);
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
	
	

}
