package myka;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Dateiaktionen {
	private static String workingDirectory = System.getProperty("user.dir");
	
	public static String liesTextDatei(File file) {
		try {
			FileReader fr = null;
			fr = new FileReader(file.getAbsolutePath());

			BufferedReader reader = new BufferedReader(fr);

			// ArrayList<String> inhalt = new ArrayList<String>();
			StringBuffer inhalt = new StringBuffer();

			String line = reader.readLine();
			while (line != null) {
				// System.out.println("> "+line);
				// inhalt.add(line);
				inhalt.append(line + "\n");
				line = reader.readLine();
			}

			reader.close();
			return inhalt.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<String[]> liesDatensatz(String input) {
		try {
			InputStream targetStream = new ByteArrayInputStream(input.getBytes());
			InputStreamReader in = new InputStreamReader(targetStream);
			BufferedReader reader = new BufferedReader(in);
			int size = -1;

			ArrayList<String[]> inhalt = new ArrayList<String[]>();

			String line = reader.readLine();
			boolean topline = true; // needed if the first line contains comments
			while (line != null) {
				// System.out.println("> "+line);
				if (!topline || !line.startsWith("#")) {
					topline = false;
					String[] linesplit = line.split(";");
					if (size >= 0 && size != linesplit.length) {
						reader.close();
						throw new Exception("Anzahl Einträge sollte " + size + " sein: " + line);
					}
					size = linesplit.length;
					inhalt.add(linesplit);
				}
				line = reader.readLine();
			}

			reader.close();
			return inhalt;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;

	}

	public static ArrayList<String[]> liesDatensatz(File file) {
		try {
			FileReader fr = null;
			fr = new FileReader(file.getAbsolutePath());

			BufferedReader reader = new BufferedReader(fr);
			int size = -1;

			ArrayList<String[]> inhalt = new ArrayList<String[]>();

			String line = reader.readLine();
			while (line != null) {
				// System.out.println("> "+line);
				String[] linesplit = line.split(";");
				if (size >= 0 && size != linesplit.length) {
					reader.close();
					throw new Exception("Anzahl Einträge sollte " + size + " sein: " + line);
				}
				size = linesplit.length;
				inhalt.add(linesplit);
				line = reader.readLine();
			}

			reader.close();
			return inhalt;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	public static File chooseFileToRead() {
		// System.out.println("Working Directory: " + System.getProperty("user.dir"));
		// System.out.println("\n| Datei einlesen |\n");

		// JFileChooser-Objekt erstellen
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(workingDirectory));
		// Dialog zum Oeffnen von Dateien anzeigen
		int rueckgabeWert = chooser.showOpenDialog(null);

		/* Abfrage, ob auf "Öffnen" geklickt wurde */
		if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
			// Ausgabe der ausgewaehlten Datei
			// System.out.println("Die zu öffnende Datei ist: " +
			// chooser.getSelectedFile().getName());
			workingDirectory=chooser.getSelectedFile().getAbsolutePath();
		} else {
			System.out.println("Programm beendet - keine Datei gewählt");
			return null;
		}
		return chooser.getSelectedFile();
	}

	public static String liesTextDatei(File file, Charset charset) {
		try {
			FileInputStream is = new FileInputStream(file.getAbsolutePath());
			InputStreamReader isr = new InputStreamReader(is, charset);
			BufferedReader reader = new BufferedReader(isr);

			// ArrayList<String> inhalt = new ArrayList<String>();
			StringBuffer inhalt = new StringBuffer();

			String line = reader.readLine();
			while (line != null) {
				// System.out.println("> "+line);
				// inhalt.add(line);
				inhalt.append(line + "\n");
				line = reader.readLine();
			}

			reader.close();
			return inhalt.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void readTextInAllAvailableCharsetsAndPrint() {
		File file = chooseFileToRead();
		try {
			Map<String, Charset> map = Charset.availableCharsets();
			for (String name : map.keySet()) {
				System.out.println(name);
				System.out.println(liesTextDatei(file, map.get(name)));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void writeStringToFile(String text) {
		System.out.println("Working Directory: " + System.getProperty("user.dir"));
		
		// JFileChooser-Objekt erstellen
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		//System.out.println("Current filename: "+Controller.curfilename);
		//String fname = Controller.curfilename.replaceAll(".[a-z]*$", "");
		String fname = "robot.txt";
		chooser.setSelectedFile(new File("./"+fname));
		//Welche Erweiterungen sollen angezeigt werden
		//chooser.setFileFilter(new FileNameExtensionFilter("moodle-xml-Files (.xml)","xml"));
		// Dialog zur Auswahl von Dateien anzeigen
		JFrame jf = new JFrame( "Dialog" ); // added
        jf.setAlwaysOnTop( true ); // added
		int rueckgabeWert = chooser.showSaveDialog(jf);
		jf.dispose();
		/* Abfrage, ob auf "Speichern" geklickt wurde */
		if (rueckgabeWert != JFileChooser.APPROVE_OPTION) {
			System.out.println("Auswahl beendet - keine Datei gewählt");
			return;
		}
		//Wenn Datei schon existiert
		if (chooser.getSelectedFile().exists()) {
		    int response = JOptionPane.showConfirmDialog(null, //
		            "Do you want to replace the existing file?", //
		            "Confirm", JOptionPane.YES_NO_OPTION, //
		            JOptionPane.QUESTION_MESSAGE);
		    if (response != JOptionPane.YES_OPTION) {
		        return;
		    } 
		}
		try {

			//FileWriter fw = null;
			//fw = new FileWriter(chooser.getSelectedFile().getAbsolutePath());
			FileOutputStream fw = new FileOutputStream(chooser.getSelectedFile().getAbsolutePath());
			OutputStreamWriter writer = new OutputStreamWriter(fw, "UTF-8");

			//BufferedWriter writer = new BufferedWriter(fw);
			PrintWriter pwriter = new PrintWriter(writer);
			
			pwriter.println(text);

	
			pwriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
