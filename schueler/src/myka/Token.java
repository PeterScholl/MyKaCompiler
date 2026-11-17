package myka;

public class Token {
	public static final int T_Zahl = 1;
	public static final int T_Cond = 2;
	public static final int T_Move = 3;
	public static final int T_Cont = 4; //Control Token
	public static final int T_Bez = 5; //Bezeichner
	private static final int T_Unknown = 0; //unbekannter typ ;-)
	
	private int typ = T_Unknown;
	private String wert = "";
	private int value = 0;
	private int zeile,pos; // Zeile und Position in der Zeile im Quelltext

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

	
	public int getZeile() {
		return zeile;
	}

	public void setZeile(int zeile) {
		this.zeile = zeile;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	@Override
	public String toString() {
		return "Token [typ=" + typ + ", wert=" + wert + ", value=" + value + ", zeile=" + zeile + ", pos=" + pos + "]";
	}

	
	

}
