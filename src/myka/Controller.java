package myka;

import java.io.File;
import java.util.Arrays;

/**
 * Diese Klasse stellt den Controller zwischen dem Lexer,Parser,Compiler,Iinterpreter und dem View
 * (grafische Oberfläche) dar
 * 
 * @author Peter Scholl (peter.scholl@aeg-online.de)
 *
 */
public class Controller {
	public static String curfilename = "";
	public static final int SRC_lesen = 1; // SRC-Datei-Textdatei lesen
	private View view = null;

	public static void main(String[] args) {
		Controller c = new Controller();
		View v = new View(c, "Parser und Lexer nach MyKa (inf-schule.de) V 0.1");
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

}
