package myka;

public class Token {
	public static final int T_Zahl = 1;
	public static final int T_Cond = 2;
	public static final int T_Move = 3;
	public static final int T_Cont = 4; //Control Token
	private static final int T_Unknown = 0; //unbekannter typ ;-)
	
	private int typ = T_Unknown;
	private String wert = "";
	private int value = 0;
	private int whileTokenNr = 0; //Tokennummer, die das While enthält
	private int endwhileTokenNr = 0; //Tokennummer, mit zugehoerigen endwhile
	private int dannTokenNr = 0; //Tokennummer, die das dann enthaelt
	private int sonstTokenNr = -1; //Tokennummer, die das sonst enthaelt
	private int ifTokenNr = 0; //Tokennummer, die das wenn enthaelt
	private int endifTokenNr = 0; //Tokennummer, die das endewenn enthaelt

	public Token(int typ, String wert) {
		this.typ=typ;
		this.wert = wert;
		if (typ==T_Zahl) {
			value = Integer.parseInt(wert);
		}
	}

	public int getTyp() {
		return typ;
	}

	public void setTyp(int typ) {
		this.typ = typ;
	}

	public String getWert() {
		return wert;
	}

	public void setWert(String wert) {
		this.wert = wert;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getWhileTokenNr() {
		return whileTokenNr;
	}

	public void setWhileTokenNr(int whileTokenNr) {
		this.whileTokenNr = whileTokenNr;
	}

	public int getEndwhileTokenNr() {
		return endwhileTokenNr;
	}

	public void setEndwhileTokenNr(int endwhileTokenNr) {
		this.endwhileTokenNr = endwhileTokenNr;
	}

	public int getDannTokenNr() {
		return dannTokenNr;
	}

	public void setDannTokenNr(int dannTokenNr) {
		this.dannTokenNr = dannTokenNr;
	}

	public int getSonstTokenNr() {
		return sonstTokenNr;
	}

	public void setSonstTokenNr(int sonstTokenNr) {
		this.sonstTokenNr = sonstTokenNr;
	}

	public int getIfTokenNr() {
		return ifTokenNr;
	}

	public void setIfTokenNr(int ifTokenNr) {
		this.ifTokenNr = ifTokenNr;
	}

	public int getEndifTokenNr() {
		return endifTokenNr;
	}

	public void setEndifTokenNr(int endifTokenNr) {
		this.endifTokenNr = endifTokenNr;
	}

	@Override
	public String toString() {
		return "Token [typ=" + typ + ", wert=" + wert + ", value=" + value + "]";
	}
	
	

}
