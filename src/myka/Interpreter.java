package myka;

import java.util.HashMap;

/**
 * Das Tokenarray (Tokenliste), die vom Parser geprüft wurde, soll hier 
 * interpretiert werden
 * @author peter
 *
 */
public class Interpreter {
	//Die Schachtelungstiefe ist eigentlich nicht erforderlich solange die Sprache keine rekursiven 
	//aufrufe ermöglicht
	private static final int MAX_REK_DEPTH = 200; //Maximale Schachtelungstiefe
	private static int depth = 0; //aktuelle Schachtelungstiefe
	private static final boolean debug = false; //debug ausgaben ein und ausschalten
	private static MyKaController controller = null;
	private static int curpos=0; //aktuelle Position in der Tokenliste
	private static Token[] tokenliste = null; //Attributspeicher für die Tokenliste
	private static boolean fail = false; //Wenn ein Fehler eingetreten ist - bricht die Bearbeitung ab
	private static boolean result = true; //Result von Bedingungsanfragen - der Controller 
											//schreibt nach Aufruf das Ergebnis in dieses Attribut
	private static int waitms=20; //Waiting ms after every move - Damit man die Ausführung auch sehen kann
	private static HashMap<String,Integer> sprungmarken = new HashMap<String,Integer>(); //Sprungmarken für Bezeichner

	private Interpreter() { //kein Objekt wird erzeugt - statischse Klasse
	}
	
	/**
	 * Diese Methode startet die Ausführung der Tokenliste und setzt 
	 * ggf. die (statischen) Attribute der Klasse auf die 
	 * Ausgangswerte
	 * @param tokenliste - Abzuarbeitende Tokenliste (hoffentlich vom Parser geprüft)
	 */
	public static void execute(Token[] tokenliste) {
		fail = false; // noch kein Fehler
		depth = 0; //Schachtelungstiefe auf 0 setzen
		curpos=0; //Startposition der Tokenliste
		controller = MyKaController.getController(); //Zum Aufruf von Befehlen und Abfrage von Bedingungen
		Interpreter.tokenliste = tokenliste; //übergebene Tokenliste im Attribut speichern
		sprungmarken.clear(); //alte Sprungmarken löschen
		while(curpos < tokenliste.length && !fail) { //Solange kein Fehler und nicht am Ende 
			executeToken(); //aktuelles Token ausführen
		}
		if (fail) {
			System.err.println("Fehler in der Ausführung!!! pos: "+curpos+" Token: "+tokenliste[curpos]);
		}
	}

	/**
	 * führt das aktuelle token (an curpos) aus und wenn es in eine Schleife
	 * oder Bedingung geht wird eine entsprechende Methode (ggf. auch rekursiv) 
	 * aufgerufen.
	 * Anschliessend wird zum nächsten Token gesprungen
	 */
	private static void executeToken() {
		if (fail) return; //Wenn Fehlerzustand - keine weitere Ausführung
		Token akt = tokenliste[curpos]; //Aktuelles Token in Zwischenspeicher
		debug("ExecToken (Tiefe:"+depth+"): "+akt); //für debug ausgaben
		if (akt.getTyp()==Token.T_Move) { //ein ausführbares Token
			controller.execute(MyKaController.RBefehl, new String[] {akt.getWert()});
			//Nach jeder "Bewegung/Aktion" kurz warten
			try {
				Thread.sleep(waitms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if (akt.getTyp()==Token.T_Bez) { //Anweisung ausführen
			int pos = curpos; //Position merken
			Integer t = sprungmarken.get(akt.getWert());
			if (t == null) {
				fehler("Bezeichner existiert nicht!");
			} else {
				curpos = t;
				anweisung();
				curpos = pos; //Und an alter Stelle weiter machen
			}
		} else if (akt.getTyp()==Token.T_Cont) { //Steuerungstoken verarbeiten
			if (akt.getWert().equals("wiederhole")) {
				curpos++;
				loop();	
			} else if (akt.getWert().equals("wenn")) {
				curpos++;
				bed();
			} else if (akt.getWert().equals("Anweisung")) {
				curpos++;
				//Sprungmarke anlegen
				sprungmarken.put(tokenliste[curpos].getWert(), curpos+1);
				vorlaufPassendes("endeAnweisung");
			} else { //impossible
				fehler("Fehler - Ausführen einer nicht ausführbaren Kontrollstruktur");
			}
		} else { //impossible
			fehler("Fehler - Ausführen eines nicht ausführbaren Tokens!!");
		}
		curpos++;
	}


	/**
	 * Diese Methode wird aufgerufen sobald ein Token "wenn" erreicht wurde
	 * hier wird eine Bedingung gestartet - nach dem die Bedingung 
	 * vollständig abgearbeitet wurde wird zur ursprünglichen Ausführung 
	 * zurückgesprungen	 * 
	 */
	private static void bed() {
		depth++; //Schachtelungstiefe um eins erhöhen
		if (depth>MAX_REK_DEPTH) fehler("Maximale Rekursionstiefe erreicht!");
		if (fail) return;
		//Bedingung prüfen
		controller.execute(MyKaController.RBefehl, new String[] {tokenliste[curpos].getWert()});
		debug("Bedingung "+tokenliste[curpos].getWert()+" ist "+result);
		if (result) { //Bedingung ist wahr
			curpos+=2; // Hinter das dann springen
			Token akt = tokenliste[curpos];
			//solange das Ende des Bedingungsblocks nicht erricht ist...
			while(!akt.getWert().equals("endewenn") && !akt.getWert().equals("sonst") && !fail ) {
				//ausführen
				executeToken();
				akt = tokenliste[curpos]; //akt auf neues Token nach Ausführung setzen
			}
			//Wenn es einen sonst-Block geben sollte - diesen überspringen
			if (akt.getWert().equals("sonst")) vorlaufPassendes("endewenn");
		} else { //Bedingung ist falsch
			//dann Teil überspringen
			vorlaufPassendes("sonst","endewenn");
			if (tokenliste[curpos].getWert().equals("sonst")) { //ausführen
				curpos++; // Hinter das sonst springen
				while(!tokenliste[curpos].getWert().equals("endewenn") && !fail) {
					executeToken();
				}
			}
		}
		depth--; //Schachtelungstiefe wieder reduzieren
	}

	/**
	 * Diese Methode wird aufgerufen sobald ein Token "wiederhole" erreicht wurde.
	 * Es prüft die Art der Schleife und arbeitet den Inhalt der Schleife 
	 * entsprechend oft ab. Nachdem die Ausführung der Schleife beendet ist, wird zur
	 * ursprünglichen Ausführung zurückgesprungen.
	 */
	private static void loop() {
		depth++; //Schachtelungstiefe um eins erhöhen und prüfen
		if (depth>MAX_REK_DEPTH) fehler("Maximale Rekursionstiefe erreicht!");
		if (fail) return;
		int pos = curpos; //aktuelle Position für Loop speichern
		//Typ der Schleife bestimmen
		Token akt = tokenliste[curpos];
		if (akt.getTyp()==Token.T_Zahl) { //Schleifentyp n-mal
			int anz = akt.getValue(); // Anzahl loops speichern
			for (int i=0; i<anz; i++) {
				curpos=pos+2; // hinter Token "mal" springen und ausführen
				while(!tokenliste[curpos].getWert().equals("endewiederhole") && !fail) {
					executeToken();
				}
			}			
		} else if (akt.getWert().equals("solange")) { //Schleifentyp solange 
			curpos=pos+1; // Zur Bedingung springen und übrerprüfen
			controller.execute(MyKaController.RBefehl, new String[] {tokenliste[curpos].getWert()});
			while (result && !fail) { //Bedingung zutreffend
				curpos++; //erstes ausführbares Token nach der Bedingung
				//Schleifenblock ausführen
				while(!tokenliste[curpos].getWert().equals("endewiederhole") && !fail) {
					executeToken();
				}				
				curpos=pos+1; // Wieder zur Bedingung springen und erneut überprüfen
				controller.execute(MyKaController.RBefehl, new String[] {tokenliste[curpos].getWert()});
			}
			//Bedingung nicht mehr zutreffend aber curpos (aktuelle Position) ist noch am Anfang der Schleife
			vorlaufPassendes("endewiederhole");			
		} else { //impossible
			fehler("Schleife falsch!!! - eigentlich impossible nach Parsing");
		}
		depth--; //Schachtelungstiefe zurücksetzen
	}

	/**
	 * Diese Methode wird aufgerufen sobald eine Anweisung gestartet wird.
	 * Es arbeitet die in der Anweisung deklarierten Befehle ab. 
	 * Nachdem die Ausführung der Anweisung beendet ist, wird zur
	 * ursprünglichen Ausführung zurückgesprungen.
	 */
	private static void anweisung() {
		depth++; //Schachtelungstiefe um eins erhöhen und prüfen
		if (depth>MAX_REK_DEPTH) fehler("Maximale Rekursionstiefe erreicht!");
		if (fail) return;
		//curpos++; // hinter Token "mal" springen und ausführen
		while(!tokenliste[curpos].getWert().equals("endeAnweisung") && !fail) {
			executeToken();
		}
		depth--; //Schachtelungstiefe zurücksetzen
	}

	/**
	 * durchläuft die Tokenliste so lange bis das Token mit dem 
	 * gesuchten Wert gefunden ist. Beim Auftreten von weiteren 
	 * Verschachtelungen (z.B. wiederhole bzw. wenn) wird rekursiv
	 * das jeweils dazu passende Ende gesucht.
	 * @param string1 - zu suchender Wert
	 */
	private static void vorlaufPassendes(String string) {
		vorlaufPassendes(string,null);
	}
	private static void vorlaufPassendes(String string1, String oder) {
		depth++; //Schachtelungstiefe um eins erhöhen
		if (depth>MAX_REK_DEPTH) fehler("Maximale Rekursionstiefe erreicht!");
		if (fail) return;
		debug("vorlaufPassendes "+string1+","+oder);
		//solange keiner der beiden Werte gefunden wurde
		while (!tokenliste[curpos].getWert().equals(string1) && !tokenliste[curpos].getWert().equals(oder) ) {
			curpos++; //zum nächsten Token springen
			Token akt = tokenliste[curpos];
			if (akt.getTyp()==Token.T_Cont) { //es könnte eine neue Verschachtelung sein
				if (akt.getWert().equals("wenn")) { //neue Bedinung
					vorlaufPassendes("endewenn"); //dazu passendes Ende suchen
					curpos++; //dieses "endewenn" darf nicht doppelt gefunden werden
				} else if (akt.getWert().equals("wiederhole")) { //neue Schleife
					vorlaufPassendes("endewiederhole");	//dazu passendes Ende suchen				
					curpos++; //siehe oben - nicht doppelt finden
				}
			}
		}
		debug("vorlauf bis Token ("+curpos+"): "+tokenliste[curpos]);
		depth--; //Schachtelungstiefe verringern		
	}

	public static boolean getFail() {
		return fail;
	}

	/**
	 * Hier kann der Kontroller das Ergebnis der Prüfung einer
	 * Bedingung eintragen
	 * @param res
	 */
	public static void setResult(boolean res) {
		result=res;
	}
	
	private static void debug(String text) {
		if (debug) System.out.println("I:"+text);
	}
	
	/**
	 * Methode gibt einen Fehlertext aus (inkl. zugehoerigem
	 * Token wenn möglich) und setzt den Fehlerstatus
	 * @param text - Fehlertext
	 */
	private static void fehler(String text) {
		text+=" Pos:"+curpos;
		if (curpos<tokenliste.length) {
			text+=": "+tokenliste[curpos];
		}
		System.err.println(text);
		fail = true;
	}
	
	/**
	 * Hier kann die Wartezeit zwischen einzelnen Aktionen
	 * eingestellt werden
	 * @param ms
	 */
	public static void setWaitTime(int ms) {
		waitms = ms;
	}
	
	public static int getWaitTime() {
		return waitms;
	}

	/**
	 * Diese Methode macht das Abbrechen der Ausführung möglich
	 * falls sich z.B. der Roboter in einer Dauerschleife befindet, z.B. 
	 * wiederhole solange NichtIstWand LinksDrehen endewiederhole
	 */
	public static void stop() {
		fail = true; //Zum Abbrechen der Ausfürhung		
	}

}
