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
		// TODO 1 - Ausführung starten
		//   - fail auf false, depth auf 0, curpos auf 0 setzen
		//   - controller = MyKaController.getController();
		//   - die übergebene Tokenliste im Attribut tokenliste speichern (Achtung: gleicher Name!)
		//   - sprungmarken.clear();
		//   - solange curpos noch im Array liegt und kein Fehler vorliegt: executeToken()
		//   - bei einem Fehler eine Meldung ausgeben (System.err.println) mit curpos und dem Token.
		//     Achtung: curpos kann dann schon hinter dem Array-Ende liegen.
	}

	/**
	 * Führt das aktuelle Token (an curpos) aus. Gehört es zu einer Schleife oder
	 * Bedingung, wird die entsprechende Methode aufgerufen (die ggf. rekursiv
	 * wieder executeToken() benutzt). Anschließend wird zum nächsten Token
	 * gesprungen.
	 */
	private static void executeToken() {
		// TODO 2 - ein Token ausführen
		//   - Gibt es schon einen Fehler (fail), tu nichts.
		//   - Befehl (T_Move): an den Controller schicken, danach warten() aufrufen.
		//   - Bezeichner (T_Bez): eigene Anweisung aufrufen. Position merken, in sprungmarken
		//     nachschlagen (gibt es den Namen nicht: fehler(...)), zur Position springen,
		//     anweisung() aufrufen und zur gemerkten Position zurückspringen.
		//   - Steuerwort (T_Cont): "wiederhole": curpos++ und loop();  "wenn": curpos++ und bed();
		//     "Anweisung" (Definition): curpos++ auf den Namen, in sprungmarken eintragen
		//     (Name -> Position des ersten Tokens NACH dem Namen), dann bis "endeAnweisung" vorlaufen.
		//   - Alles andere kann es nach dem Parser nicht geben: fehler(...)
		//   - Zum Schluss curpos++ (siehe Spielregel oben).
	}

	/**
	 * Diese Methode wird aufgerufen, sobald ein Token "wenn" erreicht wurde (curpos
	 * steht auf der Bedingung). Hier wird die Bedingung geprüft und der passende
	 * Teil ausgeführt. Danach steht curpos auf "endewenn".
	 */
	private static void bed() {
		// TODO 3 - wenn ... dann ... [sonst ...] endewenn
		//   - Schachtelungstiefe um 1 erhöhen; ist sie größer als MAX_REK_DEPTH: fehler(...).
		//   - Bedingung an den Controller schicken und result auswerten.
		//   - result ist true: hinter das "dann" springen und Token ausführen (executeToken()),
		//     bis "sonst" oder "endewenn" erreicht ist. Gab es einen sonst-Teil, überspringe ihn
		//     mit vorlaufPassendes("endewenn").
		//   - result ist false: den dann-Teil überspringen (vorlaufPassendes("sonst", "endewenn")).
		//     Steht curpos auf "sonst", dahinter alles bis "endewenn" ausführen.
		//   - Schachtelungstiefe wieder um 1 verringern.
	}

	/**
	 * Diese Methode wird aufgerufen, sobald ein Token "wiederhole" erreicht wurde
	 * (curpos steht auf der Zahl bzw. auf "solange"). Sie erkennt die Art der
	 * Schleife und arbeitet den Inhalt entsprechend oft ab. Danach steht curpos auf
	 * "endewiederhole".
	 */
	private static void loop() {
		// TODO 4 - wiederhole ... endewiederhole
		//   - Schachtelungstiefe wie in bed() erhöhen und prüfen.
		//   - Position des Schleifenanfangs merken (int pos = curpos;).
		//   - Zahl (T_Zahl): so oft wie nötig hinter das "mal" springen und Token ausführen
		//     (executeToken()), bis "endewiederhole" erreicht ist.
		//     Sonderfall 0 mal: Der Inhalt wird nie ausgeführt. curpos muss trotzdem auf
		//     "endewiederhole" stehen (vorlaufPassendes, Start z. B. beim "mal").
		//   - "solange": zur Bedingung springen und sie an den Controller schicken. Solange result
		//     true ist: den Inhalt ausführen, zurück zur Bedingung springen, erneut abfragen.
		//     Danach mit vorlaufPassendes("endewiederhole") hinter die Schleife.
		//   - Schachtelungstiefe wieder verringern.
	}

	/**
	 * Diese Methode wird aufgerufen, sobald eine eigene Anweisung gestartet wird
	 * (curpos steht auf dem ersten Token ihres Rumpfes). Sie arbeitet den Rumpf bis
	 * "endeAnweisung" ab. Danach steht curpos auf "endeAnweisung".
	 */
	private static void anweisung() {
		// TODO 5 - eine eigene Anweisung ausführen
		//   - Schachtelungstiefe erhöhen und prüfen (auch hier greift die Grenze: Rekursion!).
		//   - Solange das aktuelle Token nicht "endeAnweisung" ist und kein Fehler vorliegt: executeToken().
		//   - Schachtelungstiefe wieder verringern.
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
		// TODO 6 - bis zum passenden Ende springen
		//   - Schachtelungstiefe erhöhen und prüfen.
		//   - Solange das Token an curpos keiner der gesuchten Werte ist: curpos++.
		//     Ist das neue Token ein "wenn", suche rekursiv das dazu passende "endewenn"
		//     (vorlaufPassendes("endewenn")) und gehe dann um 1 weiter, sodass dieses endewenn
		//     nicht noch einmal gefunden wird. Entsprechend für "wiederhole" und "endewiederhole".
		//   - Schachtelungstiefe wieder verringern.
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
