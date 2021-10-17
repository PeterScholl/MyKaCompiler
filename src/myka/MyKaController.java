package myka;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
	public static final int SRC_lesen = 1; // SRC-Datei-Textdatei lesen
	private int imagewidth,imageheight;
	private RobotArea robotArea = new RobotArea();
	private MyKaView view = null;
	private static Image imgZiegel;
	private static Image[] imgRobs = new Image[4];
	private boolean debug = true;
	private MyImgObserver imgobs = new MyImgObserver(this);

	public static void main(String[] args) {
		MyKaController c = new MyKaController();
		MyKaView v = new MyKaView(c, "Parser und Lexer nach MyKa (inf-schule.de) V 0.1");
		c.view = v;
		c.init();
		c.robotZeichnen();
	}

	/**
	 * 
	 */
	public MyKaController() {
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
		//for (int i=0; i<4; i++) kit.prepareImage(imgRobs[i], -1, -1, imgobs);
		//TODO: Bessere Lösung finden als diesen Kniff!!
		for (int i=0;i<4;i++) {
			robotArea.turnLeft();
			robotZeichnen();
			debug(""+robotArea);
		}
		robotArea.ablegen();
		robotZeichnen();
		robotArea.aufnehmen();
		robotZeichnen();
	}

	public void execute(int command, String[] args) {
		switch (command) {
		case SRC_lesen:
			File file = Dateiaktionen.chooseFileToRead();
			curfilename = (file != null ? file.getAbsolutePath() : "");
			if (file != null) {
				//TODO File einlesen
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
				if (r_pos[0]==j && r_pos[1]==i) { // Roboter zeichnen
					debug("Roboter bei "+j+"-"+i);
					g.drawImage(imgRobs[robotArea.getDir()], ursprung_x+j*30-15-i*15,ursprung_y+i*15-15-akt_h-45,view.robotCanvas);
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


}
