package myka;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFileChooser;

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

}
