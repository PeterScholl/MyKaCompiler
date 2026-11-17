package myka;

import java.util.HashMap;

/**
 * Der Interpreter der Sprache MyKa.
 * Das Tokenarray, das vom Parser geprüft wurde, wird hier ausgeführt und steuert
 * den Roboter.
 *
 * Alles, was mit "TODO" markiert ist, ergänzt du: Lies zu jeder Methode zuerst
 * den Kommentar. Der Rest ist fertig.
 *
 * Diese Klasse ist statisch: Es wird nie ein Interpreter-Objekt erzeugt.
 *
 * So sprichst du mit dem Roboter: Befehle UND Bedingungen werden mit derselben
 * Zeile an den Controller geschickt:
 *
 *     controller.execute(MyKaController.RBefehl, new String[] { token.getWert() });
 *
 * Ein Befehl (z. B. Schritt) lässt den Roboter etwas tun. Bei einer Bedingung
 * (z. B. IstWand) schreibt der Controller die Antwort in das Attribut result.
 *
 * Die wichtigste Spielregel: curpos ist die Position im Tokenarray. Die Methoden
 * bed(), loop() und anweisung() lassen curpos auf dem LETZTEN Token ihrer
 * Struktur stehen (endewenn, endewiederhole bzw. endeAnweisung). Erst
 * executeToken() zählt danach um 1 weiter.
 */
public class Interpreter {
	// Die Schachtelungstiefe ist eigentlich nicht erforderlich, solange die Sprache keine
	// rekursiven Aufrufe verbietet. Sie bricht aber endlose Rekursion ab (Anweisung A A endeAnweisung).
	private static final int MAX_REK_DEPTH = 200; // Maximale Schachtelungstiefe
	private static int depth = 0; // aktuelle Schachtelungstiefe
	private static final boolean debug = false; // debug-Ausgaben ein- und ausschalten
	private static MyKaController controller = null;
	private static int curpos = 0; // aktuelle Position in der Tokenliste
	private static Token[] tokenliste = null; // Attributspeicher für die Tokenliste
	private static boolean fail = false; // Wenn ein Fehler eingetreten ist - bricht die Bearbeitung ab
	private static boolean result = true; // Ergebnis von Bedingungsanfragen - der Controller
											// schreibt nach der Anfrage das Ergebnis in dieses Attribut
	private static int waitms = 20; // Wartezeit nach jedem Befehl - damit man die Ausführung sehen kann
	private static HashMap<String, Integer> sprungmarken = new HashMap<String, Integer>(); // Name einer eigenen
																							// Anweisung -> Position
																							// ihres ersten Tokens

	private Interpreter() { // kein Objekt wird erzeugt - statische Klasse
	}

	/**
	 * Diese Methode startet die Ausführung der Tokenliste und setzt ggf. die
	 * (statischen) Attribute der Klasse auf die Ausgangswerte
	 *
	 * @param tokenliste - abzuarbeitende Tokenliste (hoffentlich vom Parser geprüft)
	 */
	public static void execute(Token[] tokenliste) {
		fail = false; // noch kein Fehler
		depth = 0; // Schachtelungstiefe auf 0 setzen
		curpos = 0; // Startposition der Tokenliste
		controller = MyKaController.getController(); // zum Aufruf von Befehlen und Abfrage von Bedingungen
		Interpreter.tokenliste = tokenliste; // übergebene Tokenliste im Attribut speichern
		sprungmarken.clear(); // alte Sprungmarken löschen
		while (curpos < tokenliste.length && !fail) { // solange kein Fehler und nicht am Ende
			executeToken(); // aktuelles Token ausführen
		}
		if (fail) {
			System.err.println("Fehler in der Ausführung!!! pos: " + curpos + " Token: "
					+ (curpos < tokenliste.length ? tokenliste[curpos] : "(hinter dem Programmende)"));
		}
	}

	/**
	 * Führt das aktuelle Token (an curpos) aus. Gehört es zu einer Schleife oder
	 * Bedingung, wird die entsprechende Methode aufgerufen (die ggf. rekursiv
	 * wieder executeToken() benutzt). Anschließend wird zum nächsten Token
	 * gesprungen.
	 */
	private static void executeToken() {
		if (fail)
			return; // Wenn Fehlerzustand - keine weitere Ausführung
		Token akt = tokenliste[curpos]; // aktuelles Token in Zwischenspeicher
		debug("ExecToken (Tiefe:" + depth + "): " + akt); // für debug-Ausgaben
		if (akt.getTyp() == Token.T_Move) { // ein ausführbares Token
			controller.execute(MyKaController.RBefehl, new String[] { akt.getWert() });
			warten(); // nach jeder Aktion kurz warten
		} else if (akt.getTyp() == Token.T_Bez) { // eigene Anweisung ausführen
			int pos = curpos; // Position merken
			Integer t = sprungmarken.get(akt.getWert());
			if (t == null) {
				fehler("Bezeichner existiert nicht!");
			} else {
				curpos = t;
				anweisung();
				curpos = pos; // und an alter Stelle weitermachen
			}
		} else if (akt.getTyp() == Token.T_Cont) { // Steuerungstoken verarbeiten
			if (akt.getWert().equals("wiederhole")) {
				curpos++;
				loop();
			} else if (akt.getWert().equals("wenn")) {
				curpos++;
				bed();
			} else if (akt.getWert().equals("Anweisung")) {
				curpos++;
				// Sprungmarke anlegen
				sprungmarken.put(tokenliste[curpos].getWert(), curpos + 1);
				vorlaufPassendes("endeAnweisung");
			} else { // unmöglich
				fehler("Fehler - Ausführen einer nicht ausführbaren Kontrollstruktur");
			}
		} else { // unmöglich
			fehler("Fehler - Ausführen eines nicht ausführbaren Tokens!!");
		}
		curpos++;
	}

	/**
	 * Diese Methode wird aufgerufen, sobald ein Token "wenn" erreicht wurde (curpos
	 * steht auf der Bedingung). Hier wird die Bedingung geprüft und der passende
	 * Teil ausgeführt. Danach steht curpos auf "endewenn".
	 */
	private static void bed() {
		depth++; // Schachtelungstiefe um eins erhöhen
		if (depth > MAX_REK_DEPTH)
			fehler("Maximale Rekursionstiefe erreicht!");
		if (fail)
			return;
		// Bedingung prüfen
		controller.execute(MyKaController.RBefehl, new String[] { tokenliste[curpos].getWert() });
		debug("Bedingung " + tokenliste[curpos].getWert() + " ist " + result);
		if (result) { // Bedingung ist wahr
			curpos += 2; // hinter das dann springen
			Token akt = tokenliste[curpos];
			// solange das Ende des Bedingungsblocks nicht erreicht ist...
			while (!akt.getWert().equals("endewenn") && !akt.getWert().equals("sonst") && !fail) {
				executeToken(); // ausführen
				akt = tokenliste[curpos]; // akt auf neues Token nach Ausführung setzen
			}
			// Wenn es einen sonst-Block gibt - diesen überspringen
			if (akt.getWert().equals("sonst"))
				vorlaufPassendes("endewenn");
		} else { // Bedingung ist falsch
			// dann-Teil überspringen
			vorlaufPassendes("sonst", "endewenn");
			if (tokenliste[curpos].getWert().equals("sonst")) { // ausführen
				curpos++; // hinter das sonst springen
				while (!tokenliste[curpos].getWert().equals("endewenn") && !fail) {
					executeToken();
				}
			}
		}
		depth--; // Schachtelungstiefe wieder reduzieren
	}

	/**
	 * Diese Methode wird aufgerufen, sobald ein Token "wiederhole" erreicht wurde
	 * (curpos steht auf der Zahl bzw. auf "solange"). Sie erkennt die Art der
	 * Schleife und arbeitet den Inhalt entsprechend oft ab. Danach steht curpos auf
	 * "endewiederhole".
	 */
	private static void loop() {
		depth++; // Schachtelungstiefe um eins erhöhen und prüfen
		if (depth > MAX_REK_DEPTH)
			fehler("Maximale Rekursionstiefe erreicht!");
		if (fail)
			return;
		int pos = curpos; // aktuelle Position für Loop speichern
		// Typ der Schleife bestimmen
		Token akt = tokenliste[curpos];
		if (akt.getTyp() == Token.T_Zahl) { // Schleifentyp n-mal
			int anz = akt.getValue(); // Anzahl der Durchläufe
			for (int i = 0; i < anz; i++) {
				curpos = pos + 2; // hinter das Token "mal" springen und ausführen
				while (!tokenliste[curpos].getWert().equals("endewiederhole") && !fail) {
					executeToken();
				}
			}
			if (anz <= 0) { // Schleife wird nie ausgeführt: bis zum passenden Ende springen
				curpos = pos + 1;
				vorlaufPassendes("endewiederhole");
			}
		} else if (akt.getWert().equals("solange")) { // Schleifentyp solange
			curpos = pos + 1; // zur Bedingung springen und überprüfen
			controller.execute(MyKaController.RBefehl, new String[] { tokenliste[curpos].getWert() });
			while (result && !fail) { // Bedingung trifft zu
				curpos++; // erstes ausführbares Token nach der Bedingung
				// Schleifenblock ausführen
				while (!tokenliste[curpos].getWert().equals("endewiederhole") && !fail) {
					executeToken();
				}
				curpos = pos + 1; // wieder zur Bedingung springen und erneut überprüfen
				controller.execute(MyKaController.RBefehl, new String[] { tokenliste[curpos].getWert() });
			}
			// Bedingung trifft nicht mehr zu, aber curpos steht noch am Anfang der Schleife
			vorlaufPassendes("endewiederhole");
		} else { // unmöglich
			fehler("Schleife falsch!!! - eigentlich unmöglich nach dem Parsing");
		}
		depth--; // Schachtelungstiefe zurücksetzen
	}

	/**
	 * Diese Methode wird aufgerufen, sobald eine eigene Anweisung gestartet wird
	 * (curpos steht auf dem ersten Token ihres Rumpfes). Sie arbeitet den Rumpf bis
	 * "endeAnweisung" ab. Danach steht curpos auf "endeAnweisung".
	 */
	private static void anweisung() {
		depth++; // Schachtelungstiefe um eins erhöhen und prüfen
		if (depth > MAX_REK_DEPTH)
			fehler("Maximale Rekursionstiefe erreicht!");
		if (fail)
			return;
		while (!tokenliste[curpos].getWert().equals("endeAnweisung") && !fail) {
			executeToken();
		}
		depth--; // Schachtelungstiefe zurücksetzen
	}

	/**
	 * Durchläuft die Tokenliste ab curpos so lange, bis ein Token mit dem gesuchten
	 * Wert gefunden ist. Treten dabei weitere Verschachtelungen auf (wenn bzw.
	 * wiederhole), wird für sie das jeweils passende Ende übersprungen.
	 *
	 * @param string1 - zu suchender Wert
	 */
	private static void vorlaufPassendes(String string1) {
		vorlaufPassendes(string1, null);
	}

	/**
	 * Wie oben, aber es wird bis zu einem von ZWEI möglichen Werten gesucht
	 * (z. B. "sonst" oder "endewenn").
	 *
	 * Achtung: Das Token an curpos selbst wird nicht darauf geprüft, ob es eine
	 * neue Verschachtelung öffnet. Rufe die Methode deshalb auf einem Token auf,
	 * das keine öffnet (z. B. auf einer Bedingung, "mal" oder einem Namen).
	 */
	private static void vorlaufPassendes(String string1, String oder) {
		depth++; // Schachtelungstiefe um eins erhöhen
		if (depth > MAX_REK_DEPTH)
			fehler("Maximale Rekursionstiefe erreicht!");
		if (fail)
			return;
		debug("vorlaufPassendes " + string1 + "," + oder);
		// solange keiner der beiden Werte gefunden wurde
		while (!tokenliste[curpos].getWert().equals(string1) && !tokenliste[curpos].getWert().equals(oder)) {
			curpos++; // zum nächsten Token springen
			Token akt = tokenliste[curpos];
			if (akt.getTyp() == Token.T_Cont) { // es könnte eine neue Verschachtelung sein
				if (akt.getWert().equals("wenn")) { // neue Bedingung
					vorlaufPassendes("endewenn"); // dazu passendes Ende suchen
					curpos++; // dieses "endewenn" darf nicht doppelt gefunden werden
				} else if (akt.getWert().equals("wiederhole")) { // neue Schleife
					vorlaufPassendes("endewiederhole"); // dazu passendes Ende suchen
					curpos++; // siehe oben - nicht doppelt finden
				}
			}
		}
		debug("vorlauf bis Token (" + curpos + "): " + tokenliste[curpos]);
		depth--; // Schachtelungstiefe verringern
	}

	// ===== Ab hier ist alles fertig =======================================

	/** Wartet nach einem Befehl kurz (waitms), damit man die Ausführung verfolgen kann. */
	private static void warten() {
		try {
			Thread.sleep(waitms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean getFail() {
		return fail;
	}

	/**
	 * Hier trägt der Controller das Ergebnis der Prüfung einer Bedingung ein.
	 *
	 * @param res das Ergebnis
	 */
	public static void setResult(boolean res) {
		result = res;
	}

	private static void debug(String text) {
		if (debug)
			System.out.println("I:" + text);
	}

	/**
	 * Gibt einen Fehlertext aus (inkl. zugehörigem Token, wenn möglich) und setzt
	 * den Fehlerstatus.
	 *
	 * @param text - Fehlertext
	 */
	private static void fehler(String text) {
		text += " Pos:" + curpos;
		if (curpos < tokenliste.length) {
			text += ": " + tokenliste[curpos];
		}
		System.err.println(text);
		fail = true;
	}

	/**
	 * Hier kann die Wartezeit zwischen einzelnen Aktionen eingestellt werden.
	 *
	 * @param ms Millisekunden
	 */
	public static void setWaitTime(int ms) {
		waitms = ms;
	}

	public static int getWaitTime() {
		return waitms;
	}

	/**
	 * Macht das Abbrechen der Ausführung möglich, falls sich der Roboter z. B. in
	 * einer Endlosschleife befindet, etwa: wiederhole solange NichtIstWand
	 * LinksDrehen endewiederhole
	 */
	public static void stop() {
		fail = true; // zum Abbrechen der Ausführung
	}

}
