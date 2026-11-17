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

	// TODO 1 - Zustände (Aufgabe 5.1)
	//   Lege Konstanten für die Zustände DEINES Kellerautomaten aus Aufgabe 4 an
	//   (wie im Lexer, z. B.  private static final int Z_Block = 0;).
	//   Du brauchst auch einen Zustand für die Fehlersenke.

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
		// TODO 2 - der Kellerautomat (Aufgabe 5.2 bis 5.6)
		//   a) Lege einen Keller an:  Stack<Character> keller = new Stack<Character>();
		//      und lege zuerst das Zeichen für den leeren Keller darauf.
		//   b) Lege eine Variable für den aktuellen Zustand an (Startzustand: normaler Zustand).
		//   c) Gehe in einer Schleife alle Token durch (solange der Zustand nicht die Fehlersenke ist).
		//      Ein  switch (zustand)  entscheidet, was mit dem aktuellen Token geschieht.
		//      Innerhalb eines Falls prüfst du die Art des Tokens (t.getTyp(), t.getWert())
		//      und, wo nötig, das oberste Kellerzeichen (keller.top()).
		//   d) Gerät der Automat in die Fehlersenke: setze fehlertext auf eine Meldung, die die
		//      Art des Fehlers UND das Token nennt, und gib false zurück.
		//   e) Sind alle Token gelesen, prüfe, ob der Automat akzeptieren darf:
		//      normaler Zustand UND nur der Kellerboden im Keller. Sonst fehlt irgendwo ein
		//      Endwort. Auch dafür gibt es eine passende Fehlermeldung.
		//   Tipp: Programmiere erst Befehle und Schleifen, teste, dann wenn, dann Anweisung.
		fehlertext = "Der Parser ist noch nicht implementiert (TODO in Parser.java)";
		return false; // TODO: ersetzen
	}

	// TODO 3 - Fehler melden (Aufgabe 5.4)
	//   Optional: Eine kleine Hilfsmethode, die den fehlertext setzt und den Zustand der
	//   Fehlersenke zurückgibt, spart Schreibarbeit, z. B.
	//      private static int fehler(String text, Token t)

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
