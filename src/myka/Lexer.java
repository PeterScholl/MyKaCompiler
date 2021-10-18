package myka;

public class Lexer {
	private static final int Z_Trenner = 0; // Zustand Trenner
	private static final int Z_Alpha = 1; //Wort wird gebildet
	private static final int Z_Zahl = 2; // Zahl wird gebildet
	private static final int Z_Sink = 3; // Fehlersenke
	private static final int STAT_GOOD = 0;
	private static final int ERR_charunknown = -1;
	private static final int ERR_number_in_word = -2;
	private static final int ERR_char_in_number = -3;
	private static final int ERR_terminal_unknown = -4;
	private static int status = STAT_GOOD;
	private static int zustand = Z_Trenner;
	private static int tokentyp = 0;
	private static String terminalstring = "";
	private static final boolean debug = false;

	public Lexer() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main (String[] args) {
		//Testfunktion
		System.out.println("Zifferntest: "+isZiffer('5')+" a:"+isZiffer('a'));
		System.out.println("Alphatest: 5:"+isAlpha('5')+" a:"+isAlpha('a')+" R:"+isAlpha('R'));
		System.out.println("Trennertest: 5:"+isTrenner('5')+" \\n:"+isTrenner('\n')+" ' ':"+isTrenner(' '));
		String test = "Schritt     DreheLinks\n234";
		List<Token> temp = lex(test);
		temp.toFirst();
		while (temp.hasAccess()) {
			System.out.println(temp.getContent());
			temp.next();
		}
	}
	
	public static List<Token> lex (String text) {
		List<Token> tokenlist = new List<Token>();
		//Leerzeichen anhaengen um alle Terminal richtig zu interpretieren
		text += " ";
		zustand = 0;
		status = STAT_GOOD;
		int pos = 0;
		while (status == STAT_GOOD && pos<text.length()) { // go on lexing
			if (lexstep(text.charAt(pos++))) { //token muss gebildet werden
				//Token erzeugen und an die Liste anhaengen
				Token t = generateTokenFromString(terminalstring);
				debug("Token generiert: "+t);
				terminalstring =""; //String zuruecksetzen
				tokenlist.append(t);
			}
		}
		if (status!=STAT_GOOD) return null;
		return tokenlist;		
	}
	
	private static Token generateTokenFromString(String text) {
		debug("in generateTokenFromString: "+text);
		if (tokentyp == Z_Alpha) {
			String[] conditions = new String[] {"IstWand","NichtIstWand","IstZiegel","NichtIstZiegel"};
			String[] moves = new String[] {"Schritt", "DreheLinks", "DreheRechts", "Hinlegen", "Aufheben", "MarkeSetzen","MarkeLöschen" };
			String[] control = new String[] {"wiederhole","mal","endewiederhole","wenn","dann","sonst","endewenn","Beenden"};
			for (int i=0; i<moves.length; i++) {
				debug("Vergleiche mit "+moves[i]);
				if (text.equals(moves[i])) {
					return new Token(Token.T_Move,text);
				}
			}
			for (int i=0; i<conditions.length; i++) {
				if (text.equals(conditions[i])) {
					return new Token(Token.T_Cond,text);
				}
			}
			for (int i=0; i<control.length; i++) {
				if (text.equals(control[i])) {
					return new Token(Token.T_Cont,text);
				}
			}
			status = ERR_terminal_unknown;
			return null;
		} else if (tokentyp == Z_Zahl) {
			return new Token(Token.T_Zahl , text);			
		}
		return null;
	}

	/**
	 * Der Text wird zeichenweise gelext. Solange kein Fehler 
	 * auftritt wird der Wordstring mit den Zeichen ergaenzt, die keien Leerzeichen sind 
	 * @param c zu lexendes Zeichen
	 * @return true wenn ein Token erzeugt werden muss
	 */
	private static boolean lexstep(char c) {
		debug("Lexing char: "+c+" Zustand: "+zustand);
		switch (zustand) {
		case Z_Trenner: // Noch kein neues Terminal begonnen
			if (isZiffer(c)) {
				zustand = Z_Zahl;
				terminalstring = ""+c;
			} else if (isAlpha(c)) {
				zustand = Z_Alpha;
				terminalstring = ""+c;
			} else if (isTrenner(c)) {
			} else {
				status = ERR_charunknown;
				zustand = Z_Sink;
			}
			return false;
		case Z_Zahl: // Zahl wird gelesen
			if (isZiffer(c)) {
				terminalstring += c;
			} else if (isAlpha(c)) {
				status = ERR_char_in_number;
				zustand = Z_Sink;
			} else if (isTrenner(c)) {
				zustand = Z_Trenner;
				tokentyp = Z_Zahl;
				return true; //Token bilden
			} else {
				status = ERR_charunknown;
				zustand = Z_Sink;
			}
			return false;
		case Z_Alpha: // Wort wird gelesen
			if (isAlpha(c)) {
				terminalstring += c;
			} else if (isZiffer(c)) {
				status = ERR_number_in_word;
				zustand = Z_Sink;
			} else if (isTrenner(c)) {
				zustand = Z_Trenner;
				tokentyp = Z_Alpha;
				return true; //Token muss gebildet werden
			} else {
				status = ERR_charunknown;
				zustand = Z_Sink;
			}
			return false;
		default: // Unbekannter Zustand -> Fehlersenke
			zustand = Z_Sink;			
		}
		return false;
	}
	
	public static boolean isZiffer(char c) {
		return (c>='0' && c<='9');
	}
	public static boolean isAlpha(char c) {
		return (c>='a' && c<='z') || (c>='A' && c<='Z');
	}
	public static boolean isTrenner(char c) {
		return (c==' ' || c=='\n' || c=='\t');
	}
	
	public static void debug(String text) {
		if (debug) {
			System.out.println(text);
		}
	}

	public static String getStatus() {
		switch(status) {
		case STAT_GOOD:
			return "Lexing Fehlerfrei";
		case ERR_char_in_number:
			return "Lexing fehlerhaft: Zeichen in einer Zahl";
		case ERR_charunknown:
			return "Lexing fehlerhaft: Unbekanntes Zeichen";
		case ERR_number_in_word:
			return "Lexing fehlerhaft: Ziffer innerhalb eines Wortes";
		case ERR_terminal_unknown:
			return "Lexing fehlerhaft: Unbekanntes Schluesselwort (Terminal)";
		}
		return null;
	}

}
