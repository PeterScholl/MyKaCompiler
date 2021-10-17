package myka;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * In der Klasse GUI wird das Logical dargestellt
 * 
 * @author scholl@unterrichtsportal.org
 * @version 16.05.2016
 */
public class MyKaView implements MouseListener, KeyListener {
	//public static final int PANEL_XMLtemplate = 2;
	//public static final int PANEL_Questions = 1;
	//public static final int PANEL_Database = 3;
	//public static final int PANEL_MultiChoice = 4;

	private JFrame fenster;
	private JPanel center;
	private JTextArea textareaSRC;
	private JLabel upperLabel, statusLabel;
	private JList<String> fragenliste = new JList<String>(new String[] {});
	private Controller controller = null;
	private Font generalfont = new Font("Dialog", Font.BOLD, 16);
	private boolean debug=true;
	private final static String infotext = "Vorlage zum Erstellen eines Lexers und Parsers";

	/**
	 * Constructor for objects of class GUI
	 */
	public MyKaView(Controller c, String title) {
		this.controller = c;
		fensterErzeugen(title);
	}

	public void fensterErzeugen(String title) {
		Hilfsfunktionen.setUIFont(new javax.swing.plaf.FontUIResource(generalfont));
		if (title == null)
			title = "Fenster";
		fenster = new JFrame(title);
		fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Menü erzeugen
		JMenuBar menuezeile = new JMenuBar();
		fenster.setJMenuBar(menuezeile);

		JMenu dateimenue = new JMenu("Datei"); // Datei-Menue
		menuezeile.add(dateimenue);
		JMenuItem oeffnenEintrag = new JMenuItem("Programmdatei öffnen");
		oeffnenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srcDateiOeffnen();
			}
		});
		dateimenue.add(oeffnenEintrag);

		JMenuItem srcSpeichernEintrag = new JMenuItem("Source-Datei speichern");
		srcSpeichernEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//controller.execute(Controller.SRC_speichern, null);
			}
		});
		dateimenue.add(srcSpeichernEintrag);

		JMenuItem beendenEintrag = new JMenuItem("Beenden");
		beendenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				beenden();
			}
		});
		dateimenue.add(beendenEintrag);

		JMenu compmenue = new JMenu("Compiler"); // Menue für die Verarbeitung
		menuezeile.add(compmenue);
		JMenuItem srcLexenEintrag = new JMenuItem("Lexer");
		srcLexenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: Lexer aufrufen - Tokenliste generieren
			}
		});
		compmenue.add(srcLexenEintrag);

		JMenuItem parserEintrag = new JMenuItem("Parser aufrufen");
		parserEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: Parsen - code prüfen
			}
		});
		compmenue.add(parserEintrag);

		JMenuItem interpreterEintrag = new JMenuItem("Interpreter");
		interpreterEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Interpreter aufrufen
			}
		});
		compmenue.add(interpreterEintrag);

		JMenu einstellungmenue = new JMenu("Einstellungen"); // Einstellungen-Menue
		menuezeile.add(einstellungmenue);
		JMenuItem feldGroesseEintrag = new JMenuItem("Spielfeldgröße");
		feldGroesseEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Feldgröße einstellen
			}
		});
		einstellungmenue.add(feldGroesseEintrag);

		JMenuItem mykaPositionEintrag = new JMenuItem("Roboterposition");
		mykaPositionEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: Roboterposition festlegen
			}
		});
		einstellungmenue.add(mykaPositionEintrag);
		
		//TODO Hindernisse und Marken setzen

		einstellungmenue.addSeparator();

		JMenuItem schriftGroesserEintrag = new JMenuItem("Schrift +");
		schriftGroesserEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				increaseFontSize(fenster, 1);
				;
			}
		});
		einstellungmenue.add(schriftGroesserEintrag);

		JMenuItem schriftKleinerEintrag = new JMenuItem("Schrift -");
		schriftKleinerEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				increaseFontSize(fenster, -1);
				;
			}
		});
		einstellungmenue.add(schriftKleinerEintrag);
		
		JMenu hilfemenue = new JMenu("Hilfe"); // Hilfe-Menue
		menuezeile.add(hilfemenue);
		JMenuItem infoEintrag = new JMenuItem("Info");
		infoEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoAusgeben();
			}
		});
		hilfemenue.add(infoEintrag);

		JMenuItem srcBeispielEintrag = new JMenuItem("Beispielcode einfügen");
		srcBeispielEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO Beispiel erstellen
			}
		});
		hilfemenue.add(srcBeispielEintrag);

		JMenuItem testEintrag = new JMenuItem("Testfunktion");
		testEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				testfunktion();
			}
		});
		hilfemenue.add(testEintrag);

		Container contentPane = fenster.getContentPane();

		contentPane.setLayout(new BorderLayout());

		upperLabel = new JLabel("Infobereich oben");
		contentPane.add(upperLabel, BorderLayout.NORTH);

		// Kern des Fensters ist ein Bereich für den Code und ein Canvas für den Roboter
		center = new JPanel(new BorderLayout());

		// Textarea für SRC-Code
		textareaSRC = new JTextArea();
		textareaSRC.setLineWrap(true);
		textareaSRC.setWrapStyleWord(false);
		new JScrollPane(textareaSRC);
		center.add(textareaSRC, BorderLayout.CENTER);
		contentPane.add(center, BorderLayout.CENTER);


		statusLabel = new JLabel("Ich bin das Status-Label");
		contentPane.add(statusLabel, BorderLayout.SOUTH);

		// Hilfsfunktionen.fensterZentrieren(fenster);
		fenster.setLocation(200, 200);
		fenster.setPreferredSize(new Dimension(1200, 600));
		increaseFontSize(fenster, 0); // Alle Komponenten auf den gleichen Font setzen
		fenster.pack();
		fenster.setVisible(true);

	}

	/**
	 * 'Datei oeffnen'-Funktion: Öffnet einen Dateiauswahldialog zur Auswahl einer
	 * Logical-datei und zeigt dieses an.
	 */
	private void srcDateiOeffnen() {
		controller.execute(Controller.SRC_lesen, null);
		fenster.pack();
	}

	private void beenden() {
		// Abzuarbeitender Code, wenn auf beenden geclickt wurde
		System.out.println("Beenden!");
		System.exit(0);
	}

	private void infoAusgeben() {
		// Abzuarbeitender Code, wenn auf Info geclickt wurde
		System.out.println("Info!");
		this.showInfoBox(infotext, "Info", 0);
	}

	private void testfunktion() {
		System.out.println("Testfunktion ausführen - aktuell keine");
	}

	// ******** Von außen aufzurufende Methoden ***********//

	public void setStatusLine(String text) {
		statusLabel.setText(text);
	}

	public void fillSRCArea(String inhalt) {
		textareaSRC.setText((inhalt));
	}


	public void increaseFontSize(Container parent, int inc) {
		generalfont = generalfont.deriveFont((float) (1.0 * generalfont.getSize() + (1.0 * inc)));
		Hilfsfunktionen.setUIFont(new javax.swing.plaf.FontUIResource(generalfont));
		increaseFontSizeRek(parent, inc);
	}

	public void increaseFontSizeRek(Container parent, int inc) {
		if (parent instanceof JMenu) {
			int icount = ((JMenu) parent).getItemCount();
			// System.out.println("JMenu found - Anz Component: "+icount);
			for (int i = 0; i < icount; i++)
				if (((JMenu) parent).getItem(i) != null)
					((JMenu) parent).getItem(i).setFont(generalfont);
		} else {
			for (Component c : parent.getComponents()) {
				// System.out.println(c.toString());
				Font font = c.getFont();
				// System.out.println("Font: " + font);
				if (font != null) {
					c.setFont(generalfont);
				}

				if (c instanceof Container)
					increaseFontSizeRek((Container) c, inc);
			}
		}
	}

	public BufferedImage getBufferedImage() {
		BufferedImage img = null;
		// center.getBufferedImage().getGraphics().setFont(generalfont);
		/*
		BufferedImage img = center.getBufferedImage();
		Graphics g = img.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		if (bgImage != null) {
			debug("Background-Image erstellen - Kartenhoehe" + bgImage.getHeight(center) + " weite:"
					+ bgImage.getWidth(center));
			g.drawImage(bgImage, 10, 10, img.getWidth() - 20, img.getHeight() - 20, center);
		}
		*/
		return img;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getSource() instanceof JList) {
			@SuppressWarnings("unchecked")
			JList<String> theList = (JList<String>) e.getSource();
			int index = theList.locationToIndex(e.getPoint());
			//controller.execute(Controller.Question_anzeigen, new String[] { "" + index });
			if (index >= 0) {
				Object o = theList.getModel().getElementAt(index);
				System.out.println("Double-clicked on: " + o.toString());
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//System.out.println("Pressed: " + e);
		if (e.isPopupTrigger() && e.getSource().equals(fragenliste)) {
			//System.out.println("Pop-UP-Menu der Fragenliste öffnen! - Mouse pressed");
			this.doPopMenuFragenliste(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//System.out.println("Released: " + e);
		if (e.isPopupTrigger() && e.getSource().equals(fragenliste)) {
			//System.out.println("Pop-UP-Menu der Fragenliste öffnen! - Mouse released");
			this.doPopMenuFragenliste(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(fragenliste)) {
			System.out.println("Key in Liste gedrückt - bei Element " + fragenliste.getSelectedIndex());
			int code = e.getKeyCode();

			switch (code) {
			case KeyEvent.VK_UP:
				System.out.println("UP " + fragenliste.getSelectedIndex());
				break;
			case KeyEvent.VK_DOWN:
				System.out.println("DOWN " + fragenliste.getSelectedIndex());
				break;
			case KeyEvent.VK_DELETE:
				System.out.println("Del " + fragenliste.getSelectedValue());
				System.out.println("Delete Indices: " + Arrays.toString(fragenliste.getSelectedIndices()));
				String frage = "Sind Sie sicher, dass Sie die gewählten " + fragenliste.getSelectedIndices().length
						+ " Elemente löschen wollen?";
				if (JOptionPane.YES_OPTION == Hilfsfunktionen.sindSieSicher(fenster, frage)) {
					String[] args = new String[fragenliste.getSelectedIndices().length];
					for (int i = 0; i < args.length; i++)
						args[i] = "" + fragenliste.getSelectedIndices()[i];
					//controller.execute(Controller.Delete_Questions, args);
				} else {
					this.setStatusLine("Löschen abgebrochen");
				}
			}
		}

	}

	private void doPopMenuFragenliste(MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		
		JMenuItem questionToXMLEintrag = new JMenuItem("Frage nach XML");
		questionToXMLEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int index = fragenliste.locationToIndex(e.getPoint());				
				//controller.execute(Controller.QuestionToXML, new String[] {""+index});
			}
		});
		menu.add(questionToXMLEintrag);

		JMenuItem quizToXMLEintrag = new JMenuItem("Quiz nach XML");
		quizToXMLEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				//controller.execute(Controller.QuizToXML, null);
			}
		});
		menu.add(quizToXMLEintrag);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	/**
	 * Erzeugt eine Infobox mit der Nachricht und dem Titel, die für timerms Millisekunden
	 * angezeigt wird, wenn timerms>0 ist
	 * @param message Nachricht
	 * @param title Title der Box
	 * @param timerms wenn größer als 0, verschwindet die Box nach dieser Zeit von ms
	 */
	public void showInfoBox(String message, String title, int timerms) {
		JDialog d = createDialog(fenster, message, title, timerms);
		d.setLocationRelativeTo(fenster);
		//JFrame parent = fenster;
		//d.setLocation(parent.getX() + parent.getWidth()/2, parent.getY());
		debug("Setting Dialog visible");
		long time = System.nanoTime();
		d.setVisible(true);
		debug("Dialog - back form being visible " + (System.nanoTime() - time) + "ns");
	}

	private static JDialog createDialog(final JFrame frame, String message, String title, int timerms) {
		final JDialog modelDialog = new JDialog(frame, title, Dialog.ModalityType.DOCUMENT_MODAL);
		modelDialog.setBounds(132, 132, 500, 200);
		modelDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Container dialogContainer = modelDialog.getContentPane();
		dialogContainer.setLayout(new BorderLayout());
		// Textarea mit dem Text erzeugen
		JTextArea textarea = new JTextArea(message);
		// textarea.setFont(View.generalfont);
		textarea.setFont(new Font("monospaced", frame.getFont().getStyle(), frame.getFont().getSize()));
		textarea.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
		textarea.setEditable(false);
		textarea.setWrapStyleWord(true);
		textarea.setLineWrap(true);
		dialogContainer.add(textarea, BorderLayout.CENTER);
		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout());
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modelDialog.setVisible(false);
			}
		});
		modelDialog.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				okButton.requestFocusInWindow();
			}
		});
		if (timerms > 0) {
			Timer timer = new Timer(timerms, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					modelDialog.setVisible(false);
					modelDialog.dispose();
				}
			});
			timer.setRepeats(false);
			timer.start();
		}

		panel1.add(okButton);
		dialogContainer.add(panel1, BorderLayout.SOUTH);
		return modelDialog;
	}
	private void debug(String text) {
		if (debug)
			System.out.println("V:" + text);
	}

}
