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
import java.util.Arrays;



/**
 * Diese Klasse stellt den Controller zwischen dem Lexer,Parser,Compiler,Iinterpreter und dem View
 * (grafische Oberfläche) dar
 * 
 * @author Peter Scholl (peter.scholl@aeg-online.de)
 *
 */
public class MyKaController {
	public static String curfilename = "";
	public static final int Schritt = 1;		//Roboter geht einen Schritt vorwärts
	public static final int LinksDrehen = 2;	//Roboter dreht sich nach links
	public static final int RechtsDrehen = 3;	//Roboter dreht sich nach rechts
	public static final int Hinlegen = 4;		//Roboter legt einen Ziegel ab
	public static final int Aufheben = 5;		//Roboter hebt einen Ziegel auf
	public static final int MarkeSetzen = 6;	//Roboter setzt eine Marke
	public static final int MarkeLoeschen = 7;	//Roboter entfernt eine Marke
	public static final int FileLesen = 10;		//Datei einlesen
	public static final int FileSpeichern = 11; //Datei speichern
	public static final int Lexen = 20; 		//Lexen
	public static final int Parsen = 21; 		//Parsen
	public static final int Execute = 22;		//Programm ausführen
	public static final int RBefehl = 30; 		//RoboterBefehl in args ausführen
	private int imagewidth,imageheight;
	private RobotArea robotArea = new RobotArea();
	private MyKaView view = null;
	private static Image imgZiegel;
	private static Image[] imgRobs = new Image[4];
	private boolean debug = true;
	private List<Token> curTokenList= null;
	private Token[] curTokenArray = null;
	private boolean enableInput = true;
	private static MyKaController controller = null;

	public static void main(String[] args) {
		MyKaController c = MyKaController.getController();
		MyKaView v = new MyKaView(c, "Parser und Lexer nach MyKa (inf-schule.de) V 0.1");
		c.view = v;
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
	private MyKaController() {
	}
	
	public void init() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		File aktfile = new File("./bilder/Ziegel.png");
		//file_ziegel = chooseFile(true);
		if (aktfile.exists()) {
			imgZiegel = kit.getImage(aktfile.getAbsolutePath());
			//System.out.println("Bild hoehe:"+imgZiegel.getHeight(imgobs));
		} else {
			System.out.println("File does not exist");
		}
		aktfile = new File("./bilder/robotN.png");
		imgRobs[RobotArea.DIR_NORTH] = kit.getImage(aktfile.getAbsolutePath()); 
		aktfile = new File("./bilder/robotW.png");
		imgRobs[RobotArea.DIR_WEST] = kit.getImage(aktfile.getAbsolutePath()); 
		aktfile = new File("./bilder/robotE.png");
		imgRobs[RobotArea.DIR_EAST] = kit.getImage(aktfile.getAbsolutePath()); 
		aktfile = new File("./bilder/robotS.png");
		imgRobs[RobotArea.DIR_SOUTH] = kit.getImage(aktfile.getAbsolutePath()); 
		//TODO: Bessere Lösung finden als diesen Kniff!!
		for (int i=0;i<4;i++) {
			robotArea.turnLeft();
			robotZeichnen();
			debug(""+robotArea);
		}
		robotArea.ablegen();
		robotZeichnen();
		robotArea.aufnehmen();
		robotArea.setMark(1, 1);
		robotZeichnen();
	}

	public void execute(int command, String[] args) {
		if (!enableInput) {
			writeStatus("Input currently disabled!!");
			return;
		}
		writeStatus(" ");
		int result = 0; //Für die Rückmeldungen von Befehlen 
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
			writeStatus("Lexing result: "+Lexer.getStatus());
			curTokenArray = Hilfsfunktionen.convertTokenListToArray(curTokenList);
			break;
		case Parsen:
			Parser.parse(curTokenArray);
			writeStatus("Parsing result: "+Parser.getFehlerText());
			break;
		case Execute:
			writeStatus("Executing...");
			view.setEnabledAll(false);
			Executor ex = new Executor(curTokenArray);
			ex.start();
			debug("Thread running?!");
			writeStatus("Executing...running!");
			//Input is enabled after execution is done
			break;
		case RBefehl:
			if (args!=null && args.length>0) {
				String befehl = args[0];
				switch(befehl) {
				case "Schritt":
					execute(Schritt,null);
					break;
				case "LinksDrehen":
					execute(LinksDrehen,null);
					break;
				case "RechtsDrehen":
					execute(RechtsDrehen,null);
					break;
				case "Hinlegen":
					execute(Hinlegen,null);
					break;
				case "Aufheben":
					execute(Aufheben,null);
					break;
				case "MarkeSetzen":
					execute(MarkeSetzen,null);
					break;
				case "MarkeLöschen":
					execute(MarkeLoeschen,null);
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
					writeStatus("Unbekannter Roboterbefehl gesendet:"+args[0]);
				}
			}
			break;
		default:
			System.err.println("No valid command: " + command + " with args " + Arrays.deepToString(args));
		}
		updateView();
	}

	public void updateView() {
		//TODO: Ansicht auffrischen - was muss gemacht werden?
		view.updateCanvas();
	}

	public void writeStatus(String text) {
		view.setStatusLine(text);
	}
	
	public boolean robotZeichnen() {
		// hier soll die aktuelle Situation der RobotArea gezeichnet werden
		if (view == null) {
			System.err.println("Kein Canvas zum Zeichnen!");
			return false;
		}
		BufferedImage img = view.getBufferedImage();

		Graphics g = img.getGraphics();
		if (img.getWidth() != imagewidth || img.getHeight() != imageheight) {
			imagewidth = img.getWidth();
			imageheight = img.getHeight();
			//TODO updateImgValues(); //Werte fuer den Roboter aktualisieren
		}
		g.setColor(Color.black);
		g.drawLine(0,0,	0,imageheight); //Rechte begrenzungslinie zur TextArea
		//Position der hinteren, linken, unteren Ecke berechnen
		int w = robotArea.getWidth();
		int l = robotArea.getLength();
		int h = robotArea.getHeight();
		int mitte_canvas_x = imagewidth/2;
		int mitte_canvas_y = imageheight/2;
		int ursprung_x = mitte_canvas_x-(30*robotArea.getWidth()-15*robotArea.getLength())/2;
		int ursprung_y = mitte_canvas_y-(15*robotArea.getLength()-15*robotArea.getHeight())/2;
		ursprung_x = (ursprung_x<15*robotArea.getLength()+20)?15*robotArea.getLength()+20:ursprung_x;
		ursprung_y = (ursprung_y<15*robotArea.getHeight()+60)?15*robotArea.getHeight()+60:ursprung_y;
		//Boden zeichnen
		g.setColor(Color.blue);
		for (int i=0; i<=l; i++) { //y-Schritte - Linien in Richtung y-Achse li-re
			g.drawLine(ursprung_x-i*15, ursprung_y+i*15, ursprung_x-i*15+30*w,ursprung_y+i*15);
		}
		for (int i=0; i<=w; i++) { //x-Schritte - Linien in Richtung x-Achse vo-hi
			g.drawLine(ursprung_x+i*30, ursprung_y, ursprung_x+i*30-15*l,ursprung_y+l*15);
		}
		//Rueckwand und Seitenwand
		Graphics2D g2d = (Graphics2D) g;
		Stroke orig = g2d.getStroke();
		Stroke dotted = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {4,2,8,2}, 0);
		g2d.setStroke(dotted);
		for (int i=0; i<=w; i++) { //x-Schritte - Linien in Richtung z-Achse 
			g2d.drawLine(ursprung_x+i*30, ursprung_y, ursprung_x+i*30,ursprung_y-h*15);
		}
		for (int i=0; i<=l; i++) { //x-Schritte - Linien in Richtung z-Achse
			g2d.drawLine(ursprung_x-i*15, ursprung_y+i*15, ursprung_x-i*15,ursprung_y+i*15-h*15);
		}
		//obere  Kanten
			g2d.drawLine(ursprung_x, ursprung_y-h*15, ursprung_x+w*30,ursprung_y-h*15);
			g2d.drawLine(ursprung_x, ursprung_y-h*15, ursprung_x-l*15,ursprung_y-h*15+l*15);
		g2d.setStroke(orig);
		
		//Ziegel und Roboter zeichnen
		int[] r_pos = robotArea.getRobotPos();
		for (int i=0; i<l; i++) { //Reihen durchgehen
			for (int j=0; j<w; j++) { //Spalten durchgehen
				int anz_z = robotArea.getZiegelCount(j, i);
				int akt_h = 0;
				while (anz_z > 0) {
					g.drawImage(imgZiegel, ursprung_x+j*30-15-i*15,ursprung_y+i*15-15-akt_h,null);
					akt_h+=15;
					anz_z--;
				}
				if (robotArea.getMark(j, i)) {
					//gelbe Markierung zeichnen
					Polygon p = new Polygon();
					p.addPoint(ursprung_x+j*30-i*15,ursprung_y+i*15-akt_h);
					p.addPoint(ursprung_x+j*30+30-i*15,ursprung_y+i*15-akt_h);
					p.addPoint(ursprung_x+j*30+15-i*15,ursprung_y+i*15+15-akt_h);
					p.addPoint(ursprung_x+j*30-15-i*15,ursprung_y+i*15+15-akt_h);
					g.setColor(Color.yellow);
					g.fillPolygon(p);
				}
				if (r_pos[0]==j && r_pos[1]==i) { // Roboter zeichnen
					debug("Roboter bei "+j+"-"+i);
					g.drawImage(imgRobs[robotArea.getDir()], ursprung_x+j*30-15-i*15,ursprung_y+i*15-15-akt_h-45,null);
				}
			}
		}
		g.setColor(Color.black);
		g.drawLine(1,0,	1,imageheight); //Rechte begrenzungslinie zur TextArea
		
		view.updateCanvas();
		return true;
	}
	
	
	private void debug(String text) {
		if (debug) {
			System.out.println("C:"+text);
		}
	}

	public RobotArea getRobotArea() {
		return robotArea;
	}

	public void enableInput() {
		enableInput=true;
		view.setEnabledAll(true);		
	}


}
