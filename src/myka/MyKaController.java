package myka;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
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
	private MyKaView view = null;

	public static void main(String[] args) {
		MyKaController c = new MyKaController();
		MyKaView v = new MyKaView(c, "Parser und Lexer nach MyKa (inf-schule.de) V 0.1");
		c.view = v;
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
	}

	public void writeStatus(String text) {
		view.setStatusLine(text);
	}
	
	public void robotZeichnen() {
		// hier soll die aktuelle Position des Roboters gezeichnet werden
		if (view == null) {
			System.err.println("Kein Canvas zum Zeichnen!");
			return;
		}
		BufferedImage img = view.getBufferedImage();

		Graphics g = img.getGraphics();
		if (img.getWidth() != imagewidth || img.getHeight() != imageheight) {
			imagewidth = img.getWidth();
			imageheight = img.getHeight();
			//TODO updateImgValues(); //Werte fuer den Roboter aktualisieren
		}
		g.setColor(Color.blue);
		g.drawLine(0,0,	imagewidth,imageheight);
		view.updateCanvas();
	}


}
