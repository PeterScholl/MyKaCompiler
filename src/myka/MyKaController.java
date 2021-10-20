package myka;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * Diese Klasse stellt den Controller zwischen dem
 * Lexer,Parser,Compiler,Iinterpreter und dem View (grafische Oberfläche) dar
 * 
 * @author Peter Scholl (peter.scholl@aeg-online.de)
 *
 */
public class MyKaController {
	public static String curfilename = "";
	public static final int Schritt = 1; // Roboter geht einen Schritt vorwärts
	public static final int LinksDrehen = 2; // Roboter dreht sich nach links
	public static final int RechtsDrehen = 3; // Roboter dreht sich nach rechts
	public static final int Hinlegen = 4; // Roboter legt einen Ziegel ab
	public static final int Aufheben = 5; // Roboter hebt einen Ziegel auf
	public static final int MarkeSetzen = 6; // Roboter setzt eine Marke
	public static final int MarkeLoeschen = 7; // Roboter entfernt eine Marke
	public static final int FileLesen = 10; // Datei einlesen
	public static final int FileSpeichern = 11; // Datei speichern
	public static final int SetWorld = 12; // Welt ändern! länge,breite,hoehe
	public static final int SetRobPos = 13; // Position des Roboters verändern
	public static final int ResetWorld = 14; //Welt auf Ausgangsposition setzen
	public static final int EditWorld = 15; //z.B. Ziegel setzen, Marke setzen usw.
	public static final int Lexen = 20; // Lexen
	public static final int Parsen = 21; // Parsen
	public static final int Execute = 22; // Programm ausführen
	public static final int RBefehl = 30; // RoboterBefehl in args ausführen
	private static final boolean debug = false;
	private int imagewidth, imageheight;
	private RobotArea robotArea = new RobotArea();
	private MyKaView view = null;
	private static Image imgZiegel;
	private static Image[] imgRobs = new Image[4];
	private boolean lexed = false, parsed = false;
	private List<Token> curTokenList = null;
	private Token[] curTokenArray = null;
	private boolean enableInput = true;
	private int ursprung_x,ursprung_y; //Pixelkoordinaten des Koordinatenursprungs
	private static MyKaController controller = null;

	public static void main(String[] args) {
		MyKaController c = MyKaController.getController();
		c.view = new MyKaView(c, "Parser und Lexer nach MyKa (inf-schule.de) V 0.1");
		c.init();
		c.robotZeichnen();
	}

	public static MyKaController getController() {
		if (controller == null) {
			controller = new MyKaController();
		}
		return controller;
	}

	/**
	 * 
	 */
	private MyKaController() { //Singleton - darum private
	}

	public void init() {
		try {
			InputStream y = getClass().getResourceAsStream("img/Ziegel.png");
			imgZiegel = ImageIO.read(y);
			y = getClass().getResourceAsStream("img/robotN.png");
			imgRobs[RobotArea.DIR_NORTH] = ImageIO.read(y);
			y = getClass().getResourceAsStream("img/robotS.png");
			imgRobs[RobotArea.DIR_SOUTH] = ImageIO.read(y);
			y = getClass().getResourceAsStream("img/robotE.png");
			imgRobs[RobotArea.DIR_EAST] = ImageIO.read(y);
			y = getClass().getResourceAsStream("img/robotW.png");
			imgRobs[RobotArea.DIR_WEST] = ImageIO.read(y);
		} catch (Exception e) {
			System.err.println("Could not load Images!!");
			System.exit(-1);
		}
		//Hier war ein kleiner Trick der die vier Roboterpositionen schon mal gezeichnet hat
		//weil das mit den Bilderladen nicht auf anhieb funktioniert hat....
	}

	public void execute(int command, String[] args) {
		if (!enableInput) {
			writeStatus("Input currently disabled!!");
			return;
		}
		writeStatus(" ");
		int result = 0; // Für die Rückmeldungen von Befehlen
		switch (command) {
		case FileLesen:
			File file = Dateiaktionen.chooseFileToRead();
			curfilename = (file != null ? file.getAbsolutePath() : "");
			if (file != null) {
				String inhaltMC = Dateiaktionen.liesTextDatei(file);
				view.fillSRCArea(inhaltMC);
			}
			break;
		case FileSpeichern:
			Dateiaktionen.writeStringToFile(args[0]);
			break;
		case SetWorld:
			int[] v = new int[3];
			if (args.length == 3) {
				try {
					v[0] = Integer.parseInt(args[0]);
					v[1] = Integer.parseInt(args[1]);
					v[2] = Integer.parseInt(args[2]);
					robotArea = new RobotArea(v[0], v[1], v[2], 0, 0);
					robotZeichnen();
				} catch (Exception ex) {
					writeStatus("Keine drei Zahlen für Spielfeldgröße!");
				}
			} else {
				writeStatus("Keine drei zahlen für Spielfeldgröße!");
			}

			break;
		case SetRobPos:
			try {
				if (robotArea.setRobotPos(Integer.parseInt(args[0]),
						Integer.parseInt(args[1])) == RobotArea.RET_SUCCESS) {
					robotZeichnen();
				} else {
					writeStatus("Roboterposition ungueltig: " + args);
				}
			} catch (Exception ex) {
				writeStatus("Roboterposition ungueltig: " + args);
			}
			break;
		case ResetWorld:
			robotArea.reset();
			robotZeichnen();
			break;
		case EditWorld:
			editWorld(args);
			break;
		case Schritt:
			result = robotArea.forward();
			robotZeichnen();
			writeStatus(RobotArea.fehlertext[-result]);
			break;
		case LinksDrehen:
			result = robotArea.turnLeft();
			robotZeichnen();
			writeStatus(RobotArea.fehlertext[-result]);
			break;
		case RechtsDrehen:
			result = robotArea.turnRight();
			robotZeichnen();
			writeStatus(RobotArea.fehlertext[-result]);
			break;
		case Aufheben:
			result = robotArea.aufnehmen();
			robotZeichnen();
			writeStatus(RobotArea.fehlertext[-result]);
			break;
		case Hinlegen:
			result = robotArea.ablegen();
			robotZeichnen();
			writeStatus(RobotArea.fehlertext[-result]);
			break;
		case MarkeSetzen:
			result = robotArea.mark();
			robotZeichnen();
			writeStatus(RobotArea.fehlertext[-result]);
			break;
		case MarkeLoeschen:
			result = robotArea.unmark();
			robotZeichnen();
			writeStatus(RobotArea.fehlertext[-result]);
			break;
		case Lexen:
			curTokenList = Lexer.lex(args[0]);
			writeStatus("Lexing result: " + Lexer.getStatusText());
			if (Lexer.getStatus() == 0) {
				lexed = true;
				view.setEnableParse(true);
				parsed = false;
			}
			curTokenArray = Hilfsfunktionen.convertTokenListToArray(curTokenList);
			break;
		case Parsen:
			if (lexed) {
				Parser.parse(curTokenArray);
				writeStatus("Parsing result: " + Parser.getFehlerText());
				if (Parser.getFehlerText().equals("Success!!")) {
					parsed = true;
					view.setEnableExec(true);
				}
			} else {
				writeStatus("Lexing first...");
				sleep(400); // 400ms Pause
				execute(Lexen, new String[] { view.getSRCAreaText() });
				if (lexed) {
					execute(Parsen, null);
				} else {
					writeStatus("Parsing not passible ... Lexing failed");
				}
			}
			break;
		case Execute:
			if (!parsed) {
				writeStatus("Parsing first");
				sleep(100);
				execute(Parsen, null);
			}
			if (parsed) {
				writeStatus("Executing...");
				view.setEnabledAll(false);
				Executor ex = Executor.getInstance(curTokenArray);
				try {
					ex.start();
					writeStatus("Executing...running!");
				} catch (Exception ex1) {
					writeStatus("Starting tooo fast!!");
				}
				// Input is re-enabled after execution is done
			} else {
				writeStatus("Execution without parsing not passible!");
			}
			break;
		case RBefehl:
			if (args != null && args.length > 0) {
				String befehl = args[0];
				switch (befehl) {
				case "Schritt":
					execute(Schritt, null);
					break;
				case "LinksDrehen":
					execute(LinksDrehen, null);
					break;
				case "RechtsDrehen":
					execute(RechtsDrehen, null);
					break;
				case "Hinlegen":
					execute(Hinlegen, null);
					break;
				case "Aufheben":
					execute(Aufheben, null);
					break;
				case "MarkeSetzen":
					execute(MarkeSetzen, null);
					break;
				case "MarkeLöschen":
					execute(MarkeLoeschen, null);
					break;
				case "IstWand":
					Interpreter.setResult(robotArea.istWand());
					break;
				case "NichtIstWand":
					Interpreter.setResult(!robotArea.istWand());
					break;
				case "IstMarke":
					Interpreter.setResult(robotArea.istMarke());
					break;
				case "NichtIstMarke":
					Interpreter.setResult(!robotArea.istMarke());
					break;
				case "IstZiegel":
					Interpreter.setResult(robotArea.istZiegel());
					break;
				case "NichtIstZiegel":
					Interpreter.setResult(!robotArea.istZiegel());
					break;
				default:
					writeStatus("Unbekannter Roboterbefehl gesendet:" + args[0]);
				}
			}
			break;
		default:
			System.err.println("No valid command: " + command + " with args " + Arrays.deepToString(args));
		}
		updateView();
	}

	public void updateView() {
		view.updateCanvas();
	}

	public void writeStatus(String text) {
		view.setStatusLine(text);
	}

	public boolean robotZeichnen() {
		// hier soll die aktuelle Situation der RobotArea gezeichnet werden
		if (view == null) {
			System.err.println("In MyKaController - robotZeichnen - Kein Canvas zum Zeichnen!");
			return false;
		}
		BufferedImage img = view.getBufferedImage();

		Graphics g = img.getGraphics();
		if (img.getWidth() != imagewidth || img.getHeight() != imageheight) {
			imagewidth = img.getWidth();
			imageheight = img.getHeight();
		}
		g.setColor(Color.black);
		g.drawLine(0, 0, 0, imageheight); // Rechte begrenzungslinie zur TextArea
		// Position der hinteren, linken, unteren Ecke berechnen
		int w = robotArea.getWidth();
		int l = robotArea.getLength();
		int h = robotArea.getHeight();
		int mitte_canvas_x = imagewidth / 2;
		int mitte_canvas_y = imageheight / 2;
		ursprung_x = mitte_canvas_x - (30 * robotArea.getWidth() - 15 * robotArea.getLength()) / 2;
		ursprung_y = mitte_canvas_y - (15 * robotArea.getLength() - 15 * robotArea.getHeight()) / 2;
		ursprung_x = (ursprung_x < 15 * robotArea.getLength() + 20) ? 15 * robotArea.getLength() + 20 : ursprung_x;
		ursprung_y = (ursprung_y < 15 * robotArea.getHeight() + 60) ? 15 * robotArea.getHeight() + 60 : ursprung_y;
		// Boden zeichnen
		g.setColor(Color.blue);
		for (int i = 0; i <= l; i++) { // y-Schritte - Linien in Richtung y-Achse li-re
			g.drawLine(ursprung_x - i * 15, ursprung_y + i * 15, ursprung_x - i * 15 + 30 * w, ursprung_y + i * 15);
		}
		for (int i = 0; i <= w; i++) { // x-Schritte - Linien in Richtung x-Achse vo-hi
			g.drawLine(ursprung_x + i * 30, ursprung_y, ursprung_x + i * 30 - 15 * l, ursprung_y + l * 15);
		}
		// Rueckwand und Seitenwand
		Graphics2D g2d = (Graphics2D) g;
		Stroke orig = g2d.getStroke();
		Stroke dotted = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 4, 2, 8, 2 },
				0);
		g2d.setStroke(dotted);
		for (int i = 0; i <= w; i++) { // x-Schritte - Linien in Richtung z-Achse
			g2d.drawLine(ursprung_x + i * 30, ursprung_y, ursprung_x + i * 30, ursprung_y - h * 15);
		}
		for (int i = 0; i <= l; i++) { // x-Schritte - Linien in Richtung z-Achse
			g2d.drawLine(ursprung_x - i * 15, ursprung_y + i * 15, ursprung_x - i * 15, ursprung_y + i * 15 - h * 15);
		}
		// obere Kanten
		g2d.drawLine(ursprung_x, ursprung_y - h * 15, ursprung_x + w * 30, ursprung_y - h * 15);
		g2d.drawLine(ursprung_x, ursprung_y - h * 15, ursprung_x - l * 15, ursprung_y - h * 15 + l * 15);
		g2d.setStroke(orig);

		// Ziegel und Roboter zeichnen
		int[] r_pos = robotArea.getRobotPos();
		for (int i = 0; i < l; i++) { // Reihen durchgehen
			for (int j = 0; j < w; j++) { // Spalten durchgehen
				int anz_z = robotArea.getZiegelCount(j, i);
				int akt_h = 0;
				while (anz_z > 0) {
					g.drawImage(imgZiegel, ursprung_x + j * 30 - 15 - i * 15, ursprung_y + i * 15 - 15 - akt_h, null);
					akt_h += 15;
					anz_z--;
				}
				if (robotArea.getMark(j, i)) {
					// gelbe Markierung zeichnen
					Polygon p = new Polygon();
					p.addPoint(ursprung_x + j * 30 - i * 15, ursprung_y + i * 15 - akt_h);
					p.addPoint(ursprung_x + j * 30 + 30 - i * 15, ursprung_y + i * 15 - akt_h);
					p.addPoint(ursprung_x + j * 30 + 15 - i * 15, ursprung_y + i * 15 + 15 - akt_h);
					p.addPoint(ursprung_x + j * 30 - 15 - i * 15, ursprung_y + i * 15 + 15 - akt_h);
					g.setColor(Color.yellow);
					g.fillPolygon(p);
				}
				if (r_pos[0] == j && r_pos[1] == i) { // Roboter zeichnen
					debug("Roboter bei " + j + "-" + i);
					g.drawImage(imgRobs[robotArea.getDir()], ursprung_x + j * 30 - 15 - i * 15,
							ursprung_y + i * 15 - 15 - akt_h - 45, null);
				}
			}
		}
		g.setColor(Color.black);
		g.drawLine(1, 0, 1, imageheight); // Rechte begrenzungslinie zur TextArea

		view.updateCanvas();
		return true;
	}

	private void debug(String text) {
		if (debug) {
			System.out.println("C:" + text);
		}
	}

	public RobotArea getRobotArea() {
		return robotArea;
	}

	public void enableInput() {
		enableInput = true;
		view.setEnabledAll(true);
	}

	public void textChanged() {
		lexed = false;
		view.setEnableParse(false);
		parsed = false;
	}

	public void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			debug("sleep failed ?!");
		}
	}

	public void setView(MyKaView myKaView) {
		view=myKaView;		
	}
	
	private void editWorld(String[] args) {
		if (args.length!=3) return; //Parameter stimmen nicht
		try {
			int pix_x = Integer.parseInt(args[1]);
			int pix_y = Integer.parseInt(args[2]);
			//Feld ausrechnen
			int spalte = (pix_y - ursprung_y)/15;
			int zeile = (pix_x - ursprung_x+spalte*15)/30;
			debug("editWorld "+args[0]+"pix x,y: "+pix_x+","+pix_y+" zeile: "+zeile+" spalte:"+spalte);
			if (args[0].equals("Z+")) {
				robotArea.putZiegel(zeile, spalte);
			} else if (args[0].equals("Z-")) {
				robotArea.takeZiegel(zeile, spalte);
			} else if (args[0].equals("M")) {
				if (robotArea.getMark(zeile, spalte)) {
					robotArea.removeMark(zeile, spalte);
				} else {
					robotArea.setMark(zeile, spalte);
				}
			}
			robotZeichnen();			
		} catch (Exception e) {
			return; //Konvertierung nicht möglich
		}
	}


}
