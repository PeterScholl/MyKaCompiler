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
	public static final int SRC_lesen = 1; // Multiple-Choice-Textdatei lesen
	public static final int SRC_speichern = 2; // XML-Datei speichern
	public static final int Quiz_loeschen = 3; // Quiz leeren bzw. loeschen
	public static final int XMLTemplate_lesen = 4; // Eine XML-Schablone (Textdatei) einlesen
	public static final int Datensatz_lesen = 5; // Eine csv-Datenbank lesen
	public static final int Testfunktion = 6; // Eine Testfunktion, die man ggf. Nutzen kann
	public static final int XMLToQuiz = 7; // XMLzuQuiz konvertieren
	public static final int XMLToQuizDS = 8; // XMLzuQuiz konvertieren
	public static final int Question_anzeigen = 9; // Frage anzeigen
	public static final int Delete_Questions = 10; // Fragen die als String[] übergeben wurden werden gelöscht
	public static final int MCToQuiz = 11; // MultipleChoice to Quiz konvertieren
	public static final int MCBeispielAusgeben = 12; // Der MultipleChoiceView wird mit einer Vorlage befüllt
	public static final int QuestionToXML = 13; // Frage in XML darstellen
	public static final int QuizToXML = 14; // quiz in XML darstellen
	public static final int QuizToMC = 15; // quiz als Multiple-Choice-Datei darstellen
	private View view = null;

	public static void main(String[] args) {
		Controller c = new Controller();
		View v = new View(c, "XML-Questiongenerator for moodle V 0.1");
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
