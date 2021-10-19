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
	private static final int MAX_REK_DEPTH = 20;
	private static int depth = 0;
	private static Stack<Character> keller = new Stack<Character>();
	private static boolean debug = true;
	private static MyKaController controller = null;
	private static int curpos=0;
	private static Token[] tokenliste = null;
	private static boolean fail = false;
	private static boolean result = true; //Result von Bedingungsanfragen
	private static int waitms=1000; //Waiting ms after every move

	private Interpreter() { //kein Objekt wird erzeugt - statischse Klasse
	}
	
	public static void execute(Token[] tokenliste) {
		fail = false;
		depth = 0; //Rekursionstiefe messen
		keller = new Stack<Character>();
		keller.push('#'); //leerer Kellerspeicher
		curpos=0;
		controller = MyKaController.getController();
		Interpreter.tokenliste = tokenliste;
		while(curpos < tokenliste.length && !fail) {
			executeToken();
		}
		if (fail) {
			System.err.println("Fehler in der Ausführung!!!");
		}
	}

	/**
	 * führt das aktuelle token aus und wenn es in eine Schleife
	 * oder Bedingung geht wird diese entsprechend rekursiv aufgerufen
	 * Anschliessend wird zum nächsten Token gesprungen
	 */
	private static void executeToken() {
		if (fail) return;
		Token akt = tokenliste[curpos];
		if (akt.getTyp()==Token.T_Move) {
			controller.execute(MyKaController.RBefehl, new String[] {akt.getWert()});
			try {
				Thread.sleep(waitms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if (akt.getTyp()==Token.T_Cont) {
			if (akt.getWert().equals("wiederhole")) {
				curpos++;
				loop();	
			} else if (akt.getWert().equals("wenn")) {
				curpos++;
				bed();
			} else { //impossible
				fehler("Fehler - Ausführen einer nicht ausführbaren Kontrollstruktur");
			}
		} else { //impossible
			fehler("Fehler - Ausführen eines nicht ausführbaren Tokens!!");
		}
		curpos++;
	}


	private static void bed() {
		depth++;
		if (depth>MAX_REK_DEPTH) fehler("Maximale Rekursionstiefe erreicht!");
		if (fail) return;
		controller.execute(MyKaController.RBefehl, new String[] {tokenliste[curpos].getWert()});
		if (result) { //Bedingung ist wahr
			curpos+=2; // Hinter das dann springen
			Token akt = tokenliste[curpos];
			while(!akt.getWert().equals("endewenn") && !akt.getWert().equals("sonst") && !fail ) {
				executeToken();
			}
			if (akt.getWert().equals("sonst")) vorlaufPassendes("endewenn");			
		} else { //Bedingung ist falsch
			vorlaufPassendes("sonst","endewenn");
			if (tokenliste[curpos].getWert().equals("sonst")) { //ausführen
				curpos++; // Hinter das sonst springen
				while(!tokenliste[curpos].getWert().equals("endewenn") && !fail) {
					executeToken();
				}
			}
		}
		depth--;
	}

	private static void loop() {
		depth++;
		if (depth>MAX_REK_DEPTH) fehler("Maximale Rekursionstiefe erreicht!");
		if (fail) return;
		int pos = curpos; //aktuelle Position für Loop speichern
		//Typ der Schleife bestimmen
		Token akt = tokenliste[curpos];
		if (akt.getTyp()==Token.T_Zahl) {
			int anz = akt.getValue(); // Anzahl loops speichern
			for (int i=0; i<anz; i++) {
				curpos=pos+2; // mal überspringen
				while(!tokenliste[curpos].getWert().equals("endewiederhole") && !fail) {
					executeToken();
				}
			}			
		} else if (akt.getWert().equals("solange")) {
			curpos=pos+1; // Zur Bedingung springen und übrerprüfen
			controller.execute(MyKaController.RBefehl, new String[] {tokenliste[curpos].getWert()});
			while (result && !fail) { //Bedingung zutreffend
				curpos++; //erstes ausführbares Token nach der Bedingung
				while(!tokenliste[curpos].getWert().equals("endewiederhole") && !fail) {
					executeToken();
				}				
				curpos=pos+1; // Wieder zur Bedingung springen und überprüfen
				controller.execute(MyKaController.RBefehl, new String[] {tokenliste[curpos].getWert()});
			}
			//Bedingung nicht mehr zutreffend
			vorlaufPassendes("endewiederhole");			
		} else { //impossible
			fehler("Schleife falsch!!! - eigentlich impossible nach Parsing");
		}
		depth--;
	}

	/**
	 * durchläuft die Tokenliste so lange bis das Token mit dem 
	 * gesuchten Wert gefunden ist. Bei wiederhole bzw. wenn wird
	 * zunaechst das dazu passende Ende gesucht.
	 * @param string1 - zu suchender Wert
	 */
	private static void vorlaufPassendes(String string) {
		vorlaufPassendes(string,null);
	}
	private static void vorlaufPassendes(String string1, String oder) {
		if (fail) return;
		depth++;
		if (depth>MAX_REK_DEPTH) {
			System.err.println("Maximale Rekursionstiefe erreicht!");
			return;
		}
		while (!tokenliste[curpos].getWert().equals(string1) && !tokenliste[curpos].getWert().equals(oder) ) {
			curpos++;
			Token akt = tokenliste[curpos];
			if (akt.getTyp()==Token.T_Cont) {
				if (akt.getWert().equals("wenn")) {
					vorlaufPassendes("endewenn");
				} else if (akt.getWert().equals("wiederhole")) {
					vorlaufPassendes("endewiederhole");					
				}
			}
		}
		depth--;		
	}

	public static boolean getFail() {
		return fail;
	}

	public static void setResult(boolean res) {
		result=res;
	}
	
	private static void debug(String text) {
		if (debug) System.out.println("P:"+text);
	}
	
	private static void fehler(String text) {
		text+=" Pos:"+curpos;
		if (curpos<tokenliste.length) {
			text+=": "+tokenliste[curpos];
		}
		System.err.println(text);
		fail = true;
	}
	
	public static void setWaitTime(int ms) {
		waitms = ms;
	}

}
