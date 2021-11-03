package myka;

/**
 * Sprache erkennen, die in der Tokenliste genutzt wird 
 * START: DEK (für Deklarationen) 
 * DEK -> Anweisung {Bez} BLOCK endeAnweisung DEK | BLOCK 
 * BLOCK -> leer | ANW BLOCK 
 * ANW -> MOVE | wiederhole ZAHL mal BLOCK endewiederhole |
 *    wiederhole solange BED BLOCK endewiederhole | wenn BED dann BLOCK endewenn |
 *    wenn BED dann BLOCK sonst BLOCK endewenn | {Bez} 
 * BED -> IstWand | NichtIstWand | ... 
 * MOVE -> Schritt | ...
 * 
 * Das müsste ohne Kellerspeicher möglich sein
 * 
 * @author peter
 * 
 * TODO: Bei irreparablem Fehler einen fail-state setzen - Rückgabewert reicht nicht
 *
 */
public class Parser2 {
	private static String fehlertext = "";
	private static final boolean debug = true;
	private static int curpos = 0; // Position im Tokenarray an der man sich befindet
	private static Token[] tokenArray = null;

	private Parser2() {
		// Vom Parser wird kein Objekt erzeugt - statische Klasse
	}

	public static boolean parse(Token[] tokenliste) {
		fehlertext = "Success!!";
		curpos = 0;
		tokenArray = tokenliste;
		if (tokenArray == null)
			return true;
		if (pruefeDEK()) {
			debug("true?!");
			return true;
		} else {
			fehlertext += aktToken().toString();
			debug("Neuer Fehlertext nach Prüfung: "+fehlertext);
			return false;
		}
	}

	private static boolean pruefeDEK() {
		debug("In pruefeDEK an Positon:" + curpos);
		if (hasAccess() && tokenArray[curpos].getTyp() == Token.T_Cont
				&& tokenArray[curpos].getWert().equals("Anweisung")) { // Anweisung prüfen
			curpos++;
			if (!hasAccess() || tokenArray[curpos].getTyp() != Token.T_Bez) {
				fehlertext = "FEHLER: Kein Bezeichner für die Anweisung!";
				return false;
			}
			curpos++;
			if (!pruefeBLOCK()) {
				return false;
			} else {
				if (!hasAccess() || tokenArray[curpos].getTyp() != Token.T_Cont
						|| !tokenArray[curpos].getWert().equals("endeAnweisung")) {
					fehlertext = "Fehler: Anweisungsrestellung nicht korrekt beendet!";
					return false;
				}
				curpos++;
				return pruefeDEK();
			}
		} else {
			if (pruefeBLOCK()) {
				if (hasAccess()) {
					//In der Prüfung war ein Fehler! 
					return false;
				}
				return true; // Programm endet nach Anweisungsblock
			}
		}
		fehlertext = "Fehler: Unerwarteter Fall in pruefeDEK - wird eig. nicht erreicht!";
		return false; 
	}

	private static boolean pruefeBLOCK() {
		debug("In pruefeBLOCK an pos: "+curpos);
		if (hasAccess() && pruefeANW()) {
			return pruefeBLOCK(); //Evtl. geht es nach der Anweisung noch weiter
		} else {
			//LEERER BLOCK
			debug("In pruefeBLOCK - leerer Block");
			return true;
		}
	}

	private static boolean pruefeANW() {
		if (!hasAccess()) {
			fehlertext="Fehler: Anweisung erwartet!";
			return false; 
		}
		//ab hier ist klar, dass es ein Token gibt
		Token akt = tokenArray[curpos]; //aktuelles Token speichern
		debug("in pruefeANW mit Token: "+akt);
		if (akt.getTyp()==Token.T_Cont) { //wiederhole oder wenn
			debug("in pruefeANW - Control-Token");
			if (akt.getWert().equals("wiederhole")) { // schleife!!
				curpos++;
				if (!hasAccess()) {
					fehlertext="Schleife endet nach wiederhole!!";
					return false;
				}
				akt = tokenArray[curpos];
				if (akt.getTyp()==Token.T_Zahl) { // SChleife mit Anzahl
					curpos++;
					if (!hasAccess() || tokenArray[curpos].getTyp()!=Token.T_Cont || !tokenArray[curpos].getWert().equals("mal")) {
						fehlertext = "FEHLER: wiederhole Anz mal ohne mal!";
						return false;
					}
					curpos++;
					if (!pruefeBLOCK()) {
						return false; //Fehler schon im Block
					}
					if (!hasAccess() || tokenArray[curpos].getTyp()!=Token.T_Cont || !tokenArray[curpos].getWert().equals("endewiederhole")) {
						fehlertext = "FEHLER: Schleife endet ohne endewiederhole!";
						return false;
					}
					curpos++;
					return true;					
				} else if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("solange")) { // solange Schleife
					curpos++;
					if (!hasAccess() || tokenArray[curpos].getTyp()!=Token.T_Cond) {
						fehlertext = "FEHLER: wiederhole solange ohne Bed!";
						return false;
					}
					curpos++;
					if (!pruefeBLOCK()) {
						return false; //Fehler schon im Block
					}
					if (!hasAccess() || tokenArray[curpos].getTyp()!=Token.T_Cont || !tokenArray[curpos].getWert().equals("endewiederhole")) {
						fehlertext = "FEHLER: wiederhole Anz mal ohne mal!";
						return false;
					}
					curpos++;
					return true;										
				} else {
					fehlertext="FEHLER: Schleife nicht korreket erstellt";
					return false;
				}
			} else if (akt.getWert().equals("wenn")) { //Bedingung
				curpos++;
				if (!hasAccess()) {
					fehlertext="Wenn-Verzweigung endet nach wenn!!";
					return false;
				}
				if (tokenArray[curpos++].getTyp()!=Token.T_Cond) {
					fehlertext="Wenn-Verzweigung ohne Bedingung";
					return false;
				}
				if (hasAccess() && tokenArray[curpos].getTyp()==Token.T_Cont && tokenArray[curpos].getWert().equals("dann")) {
					//dann - Zweig
					debug("dann gefunden");
					curpos++; //Nach dann springen
					if (pruefeBLOCK()) {
						debug("dann-Block beendet");
						// Nach dem Block muss endeWenn oder sonst kommen
						if (!hasAccess()) {
							fehlertext = "Wenn-Verzweigung endet ohne endewenn!";
							return false;
						}
						akt = tokenArray[curpos++];
						if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("sonst")) {
							debug("sonst-Block folgt an pos: "+curpos);
							if (!pruefeBLOCK()) {
								debug("Fehler im sonst-Block");
								return false; //Fehler im Block
							}
							debug("sonst-Block beendet an pos:"+curpos);
						}
						if (!hasAccess()) {
							fehlertext="Bedinung endet ohne endewenn!";
							return false;
						}
						akt = tokenArray[curpos++];
						if (akt.getTyp()==Token.T_Cont && akt.getWert().equals("endewenn")) {
							return true;
						}
						fehlertext="Bedingung endet ohne endeWenn";
						debug(fehlertext);
						return false;
					} else {
						return false; // War schon Fehler in prüfe Block
					}
				} else {
					fehlertext="Fehler: Wenn-Verzweigung ohne dann!";
					return false;
				}
			} else { //Control-Token ohne Einleitungsfunktion
				return false; //Keine Anweisung
			}
			
		} else if (akt.getTyp()==Token.T_Bez) {
			//eigene Anweisung wird aufgerufen - i.o.
			curpos++;
			return true;
		} else if (akt.getTyp()==Token.T_Move) {
			// auszuführender Befehl
			curpos++;
			return true;
		} else {
			fehlertext="Fehler: Anweisung erwartet - unsinniges Token: "+akt;
			return false;
		}
	}

	public static String getFehlerText() {
		return fehlertext;
	}

	private static void debug(String text) {
		if (debug)
			System.out.println("P:" + text);
	}

	private static boolean hasAccess() {
		return curpos < tokenArray.length;
	}
	
	private static Token aktToken() {
		if (hasAccess()) {
			return tokenArray[curpos];
		}
		return null;
	}

}
