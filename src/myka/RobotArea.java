package myka;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Objects of this class describe the environment in which the
 * robot moves
 * @author peter
 *
 */
/**
 * @author peter
 *
 */
public class RobotArea {
	public static final int DIR_NORTH = 0;
	public static final int DIR_SOUTH = 2;
	public static final int DIR_EAST = 1;
	public static final int DIR_WEST = 3;
	public static final int RET_SUCCESS = 0; // Erfolg
	public static final int RET_ERROR = -1; // allgemeiner Fehler
	public static final int RET_ToHIGH = -2; // Kann Hoehe nicht ueberwinden
	public static final int RET_BORDER = -3; // Rand erreicht
	public static final int RET_OutOfBounds = -4; // Zugriff ausserhalb der Genzen
	public static final int RET_MaxReached = -5; // Maximum an Ziegeln erreicht
	public static final int RET_MinReached = -6; // Maximum an Ziegeln erreicht
	public static final int RET_AlreadyMarked = -7; // Marke breits gesetzt
	public static final int RET_AlreadyUnmarked = -8; // Marke war nicht gesetzt
	public static final String[] fehlertext = new String[] { "Erfolg", "Fehler",
			"Fehler: Zu Hoch - Schritt nicht moeglich", "Fehler: Rand erreicht", "Fehler: Ausserhalb des Bereichs",
			"Fehler: Maximum erreicht", "Fehler: Minimum erreicht", "Fehler: Das Feld ist schon markiert",
			"Fehler: Das Feld hat keine Markierung" };
	private int width, length, height;
	private static final int MAX_WIDTH = 40, MAX_LENGTH = 40, MAX_HEIGHT = 14;
	private int rob_x, rob_y, rob_x_start, rob_y_start;
	private int dir_start = DIR_SOUTH;
	private int dir;
	private int[][] ziegel; // Wie viele Ziegel liegen an welcher Position [width][length]
	private boolean[][] marked; // Marke setzen
	private String resetString = "#RobotArea width length height rob_x rob_y dir\n"
			+ "5 10 6 0 0 2\n"
			+ "#Ziegel\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "#Markierungen\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "0 0 0 0 0\n"
			+ "";

	/**
	 * 
	 */
	public RobotArea() {
		// Standardbereich mit Weite 5, Laenge 10 und Hoehe 6
		// Roboter in der hinteren Ecke
		this(5, 10, 6, 0, 0);
	}

	/**
	 * @param width  Weite des Feldes (links-rechts)
	 * @param length Laenge des Feldes (vorne-hinten)
	 * @param height Maximale Hoehe des Feldes
	 * @param rob_x  x-Position des Roboters (links-rechts)
	 * @param rob_y  y-Position
	 */
	public RobotArea(int width, int length, int height, int rob_x, int rob_y) {
		if (width < 2 || width > MAX_WIDTH)
			width = 5;
		if (length < 2 || length > MAX_LENGTH)
			length = 10;
		if (height < 0 || height > MAX_HEIGHT)
			height = 6;

		this.width = width;
		this.length = length;
		this.height = height;
		if (!isInside(rob_x, rob_y)) {
			rob_x = 0;
			rob_y = 0;
		}
		this.rob_x_start = rob_x;
		this.rob_y_start = rob_y;
		reset();
	}

	public void reset() {
		if (resetString.length() == 0) {
			this.ziegel = new int[width][length];
			this.marked = new boolean[width][length];
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < length; j++) {
					ziegel[i][j] = 0;
					marked[i][j] = false;
				}
			}
			rob_x = rob_x_start;
			rob_y = rob_y_start;
			this.dir = dir_start;
		} else {
			fromString(resetString);
		}
	}

	public void fromString(String a) {
		InputStream astream = new ByteArrayInputStream(a.getBytes());
		InputStreamReader ir = new InputStreamReader(astream);
		BufferedReader r = new BufferedReader(ir);
		try {
			String zeile = r.readLine();
			while (zeile != null && zeile.startsWith("#") ) { zeile=r.readLine(); } // Kommentare ??brespringen
			String[] daten = zeile.split(" ");
			if (daten.length != 6) {
				throw (new Exception("falsches Format"));
			}
			this.width = Integer.parseInt(daten[0]);
			this.length = Integer.parseInt(daten[1]);
			this.height = Integer.parseInt(daten[2]);
			this.rob_x_start = Integer.parseInt(daten[3]);
			this.rob_y_start = Integer.parseInt(daten[4]);
			this.dir = Integer.parseInt(daten[5]);
			rob_x = rob_x_start;
			rob_y = rob_y_start;
			//Ziegel einlesen
			ziegel = new int[width][length];
			zeile = r.readLine();
			while (zeile != null && zeile.startsWith("#") ) { zeile=r.readLine(); } // Kommentare ??brespringen
			for (int i = 0; i < ziegel[0].length; i++) {
				String[] v = zeile.split(" ");
				for (int j = 0; j < ziegel.length; j++) {
					ziegel[j][i]=Integer.parseInt(v[j]);
				}
				zeile = r.readLine(); // n??chste Zeile
			}
			//Markierung einlesen
			marked = new boolean[width][length];
			while (zeile != null && zeile.startsWith("#") ) { zeile=r.readLine(); } // Kommentare ??brespringen
			for (int i = 0; i < marked[0].length; i++) {
				String[] v = zeile.split(" ");
				for (int j = 0; j < marked.length; j++) {
					marked[j][i] = !v[j].equals("0");
				}
				zeile = r.readLine(); // n??chste Zeile
				resetString = a;
			}
		} catch (IOException e) {
			System.err.println("Fehler beim erstellen der Welt!");
			//e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Falsches Format!!!");
			reset();
			//e.printStackTrace();
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	/**
	 * liefert die Anzahl der Ziegel an Position x,y zurueck - bei Fehler -1
	 * 
	 * @param x Position x-Koordinate
	 * @param y y-Koordinate
	 * @return anzahl der Ziegel sonst -1
	 */
	public int getZiegelCount(int x, int y) {
		if (!isInside(x, y))
			return RET_OutOfBounds;
		return ziegel[x][y];
	}

	public int putZiegel(int x, int y) {
		if (!isInside(x, y))
			return RET_OutOfBounds;
		if (ziegel[x][y] >= height)
			return RET_MaxReached;
		ziegel[x][y]++;
		return RET_SUCCESS;
	}

	public int takeZiegel(int x, int y) {
		if (!isInside(x, y))
			return RET_OutOfBounds;
		if (ziegel[x][y] <= 0)
			return RET_MinReached;
		ziegel[x][y]--;
		return RET_SUCCESS;
	}

	public int setMark(int x, int y) {
		if (!isInside(x, y))
			return RET_OutOfBounds;
		if (marked[x][y])
			return RET_AlreadyMarked;
		marked[x][y] = true;
		return RET_SUCCESS;
	}

	public int removeMark(int x, int y) {
		if (!isInside(x, y))
			return RET_OutOfBounds;
		if (!marked[x][y])
			return RET_AlreadyUnmarked;
		marked[x][y] = false;
		return RET_SUCCESS;
	}

	public boolean getMark(int x, int y) {
		if (!isInside(x, y))
			return false;
		return marked[x][y];
	}

	public int[] getRobotPos() {
		return new int[] { rob_x, rob_y };
	}

	/**
	 * setzt die Position des Roboters falls diese gueltig ist
	 * 
	 * @param x
	 * @param y
	 */
	public int setRobotPos(int x, int y) {
		if (!isInside(x, y))
			return RET_ERROR;
		rob_x = x;
		rob_y = y;
		return RET_SUCCESS;
	}

	// *********Befehle an den Roboter *******

	public int turnLeft() {
		dir = (dir + 3) % 4;
		return RET_SUCCESS;
	}

	public int turnRight() {
		dir = (dir + 1) % 4;
		return RET_SUCCESS;
	}

	public int forward() {
		int[] n = nextFeld();
		if (!isInside(n[0], n[1]))
			return RET_BORDER;
		// Auf Hoehenunterschied pruefen
		int diff = getZiegelCount(rob_x, rob_y) - getZiegelCount(n[0], n[1]);
		if (diff < -1 || diff > 1)
			return RET_ToHIGH;
		return setRobotPos(n[0], n[1]);
	}

	/**
	 * legt einen Ziegel auf dem naechsten Feld ab
	 * 
	 * @return Fehlerwert
	 */
	public int ablegen() {
		int[] n = nextFeld();
		if (!isInside(n[0], n[1]))
			return RET_OutOfBounds;
		return putZiegel(n[0], n[1]);
	}

	/**
	 * hebt einen Ziegel von dem naechsten Feld auf
	 * 
	 * @return Fehlerwert
	 */
	public int aufnehmen() {
		int[] n = nextFeld();
		if (!isInside(n[0], n[1]))
			return RET_OutOfBounds;
		return takeZiegel(n[0], n[1]);
	}

	/**
	 * Markiere das Feld auf dem der Roboter steht
	 * 
	 * @return SUCCESS oder Fehler
	 */
	public int mark() {
		return setMark(rob_x, rob_y);
	}

	/**
	 * Entferne Markierung von dem Feld auf dem der Roboter steht
	 * 
	 * @return SUCCESS oder Fehler
	 */
	public int unmark() {
		return removeMark(rob_x, rob_y);
	}

	/**
	 * Ist das Feld markiert, auf dem der Roboter steht
	 * 
	 * @return true or false
	 */
	public boolean istMarke() {
		return getMark(rob_x, rob_y);
	}

	/**
	 * prueft, ob der Roboter vor einer Wand steht
	 * 
	 * @return true or false
	 */
	public boolean istWand() {
		int[] n = nextFeld();
		return !isInside(n[0], n[1]);
	}

	/**
	 * prueft, ob vor dem Roboter mind. ein Ziegel liegt
	 * 
	 * @return true or false
	 */
	public boolean istZiegel() {
		int[] n = nextFeld();
		return isInside(n[0], n[1]) && ziegel[n[0]][n[1]] > 0;
	}

	// *** ENDE Befehle an den Roboter **************************

	// *** BEGINN Hilfsfunktionen *******************************

	/**
	 * Prueft ob die Position (x,y) im Feld liegt
	 * 
	 * @param x
	 * @param y
	 * @return true or false
	 */
	private boolean isInside(int x, int y) {
		return !(x < 0 || x >= width || y < 0 || y >= length);
	}

	/**
	 * gibt das naechste Feld auf das der Roboter schaut die Koordinaten koennen
	 * ungueltig, also ausserhalb des Feldes sein
	 * 
	 * @return int[] mit den Koordinaten des naechsten Feldes
	 */
	private int[] nextFeld() {
		switch (dir) {
		case DIR_NORTH:
			return new int[] { rob_x, rob_y - 1 };
		case DIR_SOUTH:
			return new int[] { rob_x, rob_y + 1 };
		case DIR_EAST:
			return new int[] { rob_x + 1, rob_y };
		case DIR_WEST:
			return new int[] { rob_x - 1, rob_y };
		default:
			return new int[] { -1, -1 };
		}
	}

	public void testFunktion() {
		System.out.println("" + ablegen());
		System.out.println("" + forward());
		System.out.println("" + ablegen());
		System.out.println("" + turnLeft());
		System.out.println("" + forward());
		System.out.println(this);
	}

	@Override
	public String toString() {
		String out = "#RobotArea width length height rob_x rob_y dir\n";
		out += "" + width + " " + length + " " + height + " " + rob_x + " " + rob_y + " " + dir + "\n";
		out += "#Ziegel\n";
		for (int i = 0; i < ziegel[0].length; i++) {
			for (int j = 0; j < ziegel.length; j++) {
				out += "" + ziegel[j][i] + " ";
			}
			out += "\n";
		}
		out += "#Markierungen\n";
		for (int i = 0; i < marked[0].length; i++) {
			for (int j = 0; j < marked.length; j++) {
				out += "" + (marked[j][i] ? "1" : "0") + " ";
			}
			out += "\n";
		}
		return out;
	}
}
