package myka;

import java.util.Arrays;

/**
 * Objects of this class describe the environment in which the
 * robot moves
 * @author peter
 *
 */
public class RobotArea {
	private static final int DIR_NORTH = 0;
	private static final int DIR_SOUTH = 2;
	private static final int DIR_EAST = 1;
	private static final int DIR_WEST = 3;
	private static final int RET_SUCCESS = 0; //Erfolg
	private static final int RET_ERROR = -1; //allgemeiner Fehler
	private static final int RET_ToHIGH = -2; //Kann Hoehe nicht ueberwinden
	private static final int RET_BORDER = -3; //Rand erreicht
	private static final int RET_OutOfBounds = -4; //Zugriff ausserhalb der Genzen
	private static final int RET_MaxReached = -5; //Maximum an Ziegeln erreicht
	private static final int RET_MinReached = -6; //Maximum an Ziegeln erreicht
	private int width,length,height;
	private int rob_x,rob_y;
	private int dir;
	private int[][] ziegel; //Wie viele Ziegel liegen an welcher Position [width][length]
	

	/**
	 * 
	 */
	public RobotArea() {
		//Standardbereich mit Weite 5, Laenge 10 und Hoehe 6
		//Roboter in der hinteren Ecke
		this(5,10,6,0,0);		
	}


	/**
	 * @param width Weite des Feldes (links-rechts)
	 * @param length Laenge des Feldes (vorne-hinten)
	 * @param height Maximale Hoehe des Feldes
	 * @param rob_x x-Position des Roboters (links-rechts)
	 * @param rob_y y-Position
	 */
	public RobotArea(int width, int length, int height, int rob_x, int rob_y) {
		this.width = width;
		this.length = length;
		this.height = height;
		this.rob_x = rob_x;
		this.rob_y = rob_y;
		this.ziegel = new int[width][height];
		this.dir=DIR_SOUTH;
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
	 * @param x Position x-Koordinate
	 * @param y y-Koordinate
	 * @return anzahl der Ziegel sonst -1
	 */
	public int getZiegelCount(int x, int y) {
		if (!isInside(x, y)) return RET_OutOfBounds;
		return ziegel[x][y];		
	}
	
	public int putZiegel(int x, int y) {
		if (!isInside(x, y)) return RET_OutOfBounds;
		if (ziegel[x][y]>=height) return RET_MaxReached;
		ziegel[x][y]++;
		return RET_SUCCESS;		
	}

	public int takeZiegel(int x, int y) {
		if (!isInside(x, y)) return RET_OutOfBounds;
		if (ziegel[x][y]<=0) return RET_MinReached;
		ziegel[x][y]--;
		return RET_SUCCESS;		
	}
	
	public int[] getRobotPos() {
		return new int[] {rob_x, rob_y};
	}
	
	/**
	 * setzt die Position des Roboters falls diese gueltig ist
	 * @param x
	 * @param y
	 */
	public int setRobotPos(int x, int y) {
		if (x<0 || x>=width || y<0 || y>=length) return RET_ERROR;
		rob_x=x;
		rob_y=y;
		return RET_SUCCESS;
	}
	
	//*********Befehle an den Roboter *******
	 
	public int turnLeft() {
		dir = (dir+1)%4;
		return RET_SUCCESS;
	}
	
	public int turnRight() {
		dir = (dir+3)%4;
		return RET_SUCCESS;
	}
	
	public int forward() {
		//TODO: Auf Hoehenunterschied pruefen
		switch(dir) {
		case DIR_NORTH:
			return setRobotPos(rob_x-1, rob_y);
		case DIR_SOUTH:
			return setRobotPos(rob_x+1, rob_y);
		case DIR_EAST:
			return setRobotPos(rob_x, rob_y+1);
		case DIR_WEST:
			return setRobotPos(rob_x, rob_y-1);
		default:
			return RET_ERROR;
		}
	}
	
	/**
	 * legt einen Ziegel auf dem naechsten Feld ab
	 * @return Fehlerwert
	 */
	public int ablegen() {
		int[] n = nextFeld();
		if (!isInside(n[0],n[1])) return RET_OutOfBounds;
		return putZiegel(n[0], n[1]);		
	}

	/**
	 * hebt einen Ziegel von dem naechsten Feld auf
	 * @return Fehlerwert
	 */
	public int aufnehmen() {
		int[] n = nextFeld();
		if (!isInside(n[0],n[1])) return RET_OutOfBounds;
		return takeZiegel(n[0], n[1]);		
	}
	
	//*** ENDE Befehle an den Roboter **************************

	//*** BEGINN Hilfsfunktionen *******************************
	
	/**
	 * Prueft ob die Position (x,y) im Feld liegt
	 * @param x
	 * @param y
	 * @return true or false
	 */
	private boolean isInside(int x, int y) {
		return !(x<0 || x>=width || y<0 || y>=length);
	}
	
	/**
	 * gibt das naechste Feld auf das der Roboter schaut
	 * die Koordinaten koennen ungueltig, also ausserhalb
	 * des Feldes sein
	 * @return int[] mit den Koordinaten des naechsten Feldes
	 */
	private int[] nextFeld() {
		switch(dir) {
		case DIR_NORTH:
			return new int[] {rob_x-1, rob_y};
		case DIR_SOUTH:
			return new int[] {rob_x+1, rob_y};
		case DIR_EAST:
			return new int[] {rob_x, rob_y+1};
		case DIR_WEST:
			return new int[] {rob_x, rob_y-1};
		default:
			return new int[] {-1, -1};
		}
	}
	
	public void testFunktion() {
		System.out.println(""+ablegen());
		System.out.println(""+forward());
		System.out.println(""+ablegen());
		System.out.println(""+turnLeft());
		System.out.println(""+forward());
		System.out.println(this);
	}


	@Override
	public String toString() {
		return "RobotArea [width=" + width + ", length=" + length + ", height=" + height + ", rob_x=" + rob_x
				+ ", rob_y=" + rob_y + ", dir=" + dir + ", ziegel=" + Arrays.toString(ziegel) + "]";
	}
	

}
