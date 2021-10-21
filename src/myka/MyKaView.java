package myka;

import java.awt.*;
import java.awt.Dialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * In dieser Klasse wird das GUI zur Verfügung gestellt
 * 
 * @author scholl@unterrichtsportal.org
 * @version 20.10.21
 */
public class MyKaView implements MouseListener, KeyListener {
	public static final int MODUS_NORMAL = 0;
	public static final int MODUS_ZIEGEL = 1;
	public static final int MODUS_MARKE = 2;

	private JFrame fenster;
	private JPanel center;
	private JTextArea textareaSRC;
	private JMenu hilfemenue, dateimenue, compmenue, robotermenue, einstellungmenue;
	private JMenuItem parserEintrag, interpreterEintrag;
	private RobotCanvas robotCanvas;
	private JLabel upperLabel, statusLabel;
	// private JList<String> fragenliste = new JList<String>(new String[] {});
	private MyKaController controller = null;
	private Font generalfont = new Font("Dialog", Font.BOLD, 16);
	private static final boolean debug = false;
	private final static String infotext = "Vorlage zum Erstellen eines Lexers und Parsers\nWeitere Infos im README.md";
	private int clickModus = MODUS_NORMAL;

	/**
	 * Constructor for objects of class GUI
	 */
	public MyKaView(MyKaController c, String title) {
		this.controller = c;
		c.setView(this);
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

		dateimenue = new JMenu("Datei"); // Datei-Menue
		menuezeile.add(dateimenue);
		JMenuItem oeffnenEintrag = new JMenuItem("Datei öffnen");
		oeffnenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srcDateiOeffnen();
			}
		});
		dateimenue.add(oeffnenEintrag);

		JMenuItem srcSpeichernEintrag = new JMenuItem("Datei speichern");
		srcSpeichernEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.FileSpeichern, new String[] { textareaSRC.getText() });
			}
		});
		dateimenue.add(srcSpeichernEintrag);
		
		dateimenue.addSeparator();
		JMenuItem weltOeffnenEintrag = new JMenuItem("Welt öffnen");
		weltOeffnenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.WeltLaden, null);
			}
		});
		dateimenue.add(weltOeffnenEintrag);

		JMenuItem weltSpeichernEintrag = new JMenuItem("Welt speichern");
		weltSpeichernEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.WeltSpeichern, null);
			}
		});
		dateimenue.add(weltSpeichernEintrag);
		
		dateimenue.addSeparator();

		JMenuItem beendenEintrag = new JMenuItem("Beenden");
		beendenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				beenden();
			}
		});
		dateimenue.add(beendenEintrag);

		compmenue = new JMenu("Compiler"); // Menue für die Verarbeitung
		menuezeile.add(compmenue);
		JMenuItem srcLexenEintrag = new JMenuItem("Lexer aufrufen");
		srcLexenEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Lexer aufrufen - Tokenliste generieren
				controller.execute(MyKaController.Lexen, new String[] { textareaSRC.getText() });
			}
		});
		compmenue.add(srcLexenEintrag);

		parserEintrag = new JMenuItem("Parser aufrufen");
		parserEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.Parsen, null);
			}
		});
		compmenue.add(parserEintrag);

		interpreterEintrag = new JMenuItem("Ausführen (e)");
		interpreterEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.Execute, null);
			}
		});
		compmenue.add(interpreterEintrag);
		setEnableParse(false);

		robotermenue = new JMenu("Roboter"); // Zugriff auf den Roboter-Menue
		menuezeile.add(robotermenue);
		JMenuItem gehe = new JMenuItem("Schritt (g)");
		gehe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.Schritt, null);
			}
		});
		robotermenue.add(gehe);
		JMenuItem turnLeft = new JMenuItem("linksDrehen (l)");
		turnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.LinksDrehen, null);
			}
		});
		robotermenue.add(turnLeft);
		JMenuItem turnRight = new JMenuItem("rechtsDrehen (r)");
		turnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.RechtsDrehen, null);
			}
		});
		robotermenue.add(turnRight);
		JMenuItem ablegen = new JMenuItem("hinlegen (h)");
		ablegen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.Hinlegen, null);
			}
		});
		robotermenue.add(ablegen);
		JMenuItem aufheben = new JMenuItem("aufheben (a)");
		aufheben.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.Aufheben, null);
			}
		});
		robotermenue.add(aufheben);
		JMenuItem markeSetzen = new JMenuItem("markeSetzen (m)");
		markeSetzen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.MarkeSetzen, null);
			}
		});
		robotermenue.add(markeSetzen);
		JMenuItem markeLoeschen = new JMenuItem("markeLoeschen (n)");
		markeLoeschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.MarkeLoeschen, null);
			}
		});
		robotermenue.add(markeLoeschen);
		JMenuItem info = new JMenuItem("aktueller Status");
		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showInfoBox(controller.getRobotArea().toString(), "Roboterstatus", 0);
				//System.out.println("Roboter-Status:\n" + controller.getRobotArea());
			}
		});
		robotermenue.add(info);

		einstellungmenue = new JMenu("Einstellungen"); // Einstellungen-Menue
		menuezeile.add(einstellungmenue);
		JMenuItem feldGroesseEintrag = new JMenuItem("Spielfeldgröße");
		feldGroesseEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Feldgröße einstellen
				String ans = Hilfsfunktionen.stringErfragen(
						"Gib die Größe des Spielfelds ein\n" + "(Länge,Breite,Höhe)", "Spielfeld erstellen", "5,5,6");
				if (ans != null) {
					String[] ansa = ans.split(",");
					controller.execute(MyKaController.SetWorld, ansa);
				}
			}
		});
		einstellungmenue.add(feldGroesseEintrag);

		JMenuItem mykaPositionEintrag = new JMenuItem("Roboterposition");
		mykaPositionEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Roboterposition festlegen
				String ans = Hilfsfunktionen.stringErfragen("Gib die Position des Roboters ein\n" + "(x,y)",
						"Roboter versetzen", "0,0");
				String[] ansa = ans.split(",");
				controller.execute(MyKaController.SetRobPos, ansa);
			}
		});
		einstellungmenue.add(mykaPositionEintrag);

		JMenuItem resetWorldEintrag = new JMenuItem("Welt zurücksetzen (z)");
		resetWorldEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.execute(MyKaController.ResetWorld, null);
			}
		});
		einstellungmenue.add(resetWorldEintrag);

		JMenuItem waitTimeEintrag = new JMenuItem("Wartezeit bei Ausführen");
		waitTimeEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ms = Interpreter.getWaitTime();
				String zeit = (String) JOptionPane.showInputDialog(null, "Gib die Wartezeit in ms ein!",
						"Einstellung Wartezeit", JOptionPane.PLAIN_MESSAGE, null, null, "" + ms);
				try {
					ms = Integer.parseInt(zeit);
				} catch (Exception ex) {
					debug("Keine Zahl für Wartezeit angegeben: " + zeit);
				}
				Interpreter.setWaitTime(ms);
			}
		});
		einstellungmenue.add(waitTimeEintrag);

		// TODO Hindernisse und Marken setzen

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

		hilfemenue = new JMenu("Hilfe"); // Hilfe-Menue
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
				String bsp = "{Beispielcode - dies ist ein Kommentar}\n"
						+ "    Schritt\n"
						+ "  LinksDrehen\n";
				textareaSRC.setText(bsp);
			}
		});
		hilfemenue.add(srcBeispielEintrag);

		JMenuItem stopEintrag = new JMenuItem("Ausführung Anhalten");
		stopEintrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Interpreter.stop();
			}
		});
		hilfemenue.add(stopEintrag);

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
		center = new JPanel(new GridLayout(1, 2));

		// Textarea für SRC-Code
		textareaSRC = new JTextArea();
		textareaSRC.setLineWrap(true);
		textareaSRC.setWrapStyleWord(false);
		textareaSRC.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				textChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				debug("removeUpdate fired!");
				textChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				debug("changedUpdate fired!");
				textChanged();
			}
		});
		center.add(new JScrollPane(textareaSRC));
		// center.add(textareaSRC);
		// Canvas für den Roboter
		robotCanvas = new RobotCanvas(controller);
		robotCanvas.addMouseListener(this);
		// robotCanvas.addMouseMotionListener(this);
		robotCanvas.addKeyListener(this);
		robotCanvas.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				debug("Resized" + center.getWidth() + "-" + center.getHeight());
				controller.robotZeichnen();
			}
		});

		center.add(robotCanvas);
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

	protected void textChanged() {
		// Mitteilen, dass neu geparst werden muss
		controller.textChanged();
	}

	/**
	 * 'Datei oeffnen'-Funktion: Öffnet einen Dateiauswahldialog zur Auswahl einer
	 * Logical-datei und zeigt dieses an.
	 */
	private void srcDateiOeffnen() {
		controller.execute(MyKaController.FileLesen, null);
		fenster.pack();
	}

	private void beenden() {
		// Abzuarbeitender Code, wenn auf beenden geclickt wurde
		System.out.println("Beenden!");
		System.exit(0);
	}

	private void infoAusgeben() {
		// Abzuarbeitender Code, wenn auf Info geclickt wurde
		this.showInfoBox(infotext, "Info", 0);
	}

	private void testfunktion() {
		System.out.println("Testfunktion ausführen - Ausführung abbrechen");
		Interpreter.stop();
		controller.sleep(1000);
	}

	// ******** Von außen aufzurufende Methoden ***********//

	public void setStatusLine(String text) {
		debug("Status schreiben: " + text);
		statusLabel.setText(text);
	}

	public void fillSRCArea(String inhalt) {
		textareaSRC.setText((inhalt));
	}

	public String getSRCAreaText() {
		return textareaSRC.getText();
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
		// BufferedImage img = null;
		// center.getBufferedImage().getGraphics().setFont(generalfont);
		BufferedImage img = robotCanvas.getBufferedImage();
		Graphics g = img.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		return img;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			debug("Double-clicked: " + e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// System.out.println("Pressed: " + e);
		if (e.isPopupTrigger() && e.getSource().equals(robotCanvas)) {
			// System.out.println("Pop-UP-Menu der Fragenliste öffnen! - Mouse pressed");
			this.doPopMenuCanvas(e);
		} else if (e.getSource().equals(robotCanvas)) {
			debug("Focus on robotCanvas");
			robotCanvas.requestFocus();
			if (clickModus == MODUS_ZIEGEL && e.getButton() == MouseEvent.BUTTON1) {
				controller.execute(MyKaController.EditWorld, new String[] { "Z+", "" + e.getX(), "" + e.getY() });
			} else if (clickModus == MODUS_ZIEGEL && e.getButton() == MouseEvent.BUTTON2) {
				controller.execute(MyKaController.EditWorld, new String[] { "Z-", "" + e.getX(), "" + e.getY() });
			} else if (clickModus == MODUS_MARKE) {
				controller.execute(MyKaController.EditWorld, new String[] { "M", "" + e.getX(), "" + e.getY() });
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// System.out.println("Released: " + e);
		if (e.isPopupTrigger() && e.getSource().equals(robotCanvas)) {
			// System.out.println("Pop-UP-Menu der Fragenliste öffnen! - Mouse released");
			this.doPopMenuCanvas(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource().equals(robotCanvas)) {
			debug("Key in Canvas gedrückt:" + e.getKeyCode());
			int code = e.getKeyCode();

			switch (code) {
			case 71: // g
				controller.execute(MyKaController.Schritt, null);
				break;
			case 76: // l
				controller.execute(MyKaController.LinksDrehen, null);
				break;
			case 82: // r
				controller.execute(MyKaController.RechtsDrehen, null);
				break;
			case 65: // a
				controller.execute(MyKaController.Aufheben, null);
				break;
			case 72: // h
				controller.execute(MyKaController.Hinlegen, null);
				break;
			case 77: // m
				controller.execute(MyKaController.MarkeSetzen, null);
				break;
			case 78: // n
				controller.execute(MyKaController.MarkeLoeschen, null);
				break;
			case 90: // z
				controller.execute(MyKaController.ResetWorld, null);
				break;
			case 69: // e
				controller.execute(MyKaController.Execute, null);
				break;
			case KeyEvent.VK_UP:
				// System.out.println("UP ");
				break;
			case KeyEvent.VK_DOWN:
				// System.out.println("DOWN ");
				break;
			case KeyEvent.VK_DELETE:
				// System.out.println("Del ");
				break;
			}
		}

	}

	private void doPopMenuCanvas(MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		debug("Mouse-Event: " + e);

		JMenuItem ziegelSetzen = new JMenuItem("Ziegel setzen");
		ziegelSetzen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				controller.execute(MyKaController.EditWorld, new String[] { "Z+", "" + e.getX(), "" + e.getY() });
			}
		});
		menu.add(ziegelSetzen);

		JMenuItem ziegelEntfernen = new JMenuItem("Ziegel entfernen");
		ziegelEntfernen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				controller.execute(MyKaController.EditWorld, new String[] { "Z-", "" + e.getX(), "" + e.getY() });
			}
		});
		menu.add(ziegelEntfernen);

		JMenuItem markierungAendern = new JMenuItem("Markierung ändern");
		markierungAendern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				controller.execute(MyKaController.EditWorld, new String[] { "M", "" + e.getX(), "" + e.getY() });
			}
		});
		menu.add(markierungAendern);
		menu.addSeparator();

		if (clickModus != MODUS_NORMAL) {
			JMenuItem modusNormal = new JMenuItem("Clickmodus normal");
			modusNormal.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					clickModus = MODUS_NORMAL;
				}
			});
			menu.add(modusNormal);
		}

		if (clickModus != MODUS_ZIEGEL) {
			JMenuItem modusZIEGEL = new JMenuItem("Clickmodus ZIEGEL");
			modusZIEGEL.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					clickModus = MODUS_ZIEGEL;
				}
			});
			menu.add(modusZIEGEL);
		}

		if (clickModus != MODUS_MARKE) {
			JMenuItem modusMARK = new JMenuItem("Clickmodus MARKE");
			modusMARK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					clickModus = MODUS_MARKE;
				}
			});
			menu.add(modusMARK);
		}

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	/**
	 * Erzeugt eine Infobox mit der Nachricht und dem Titel, die für timerms
	 * Millisekunden angezeigt wird, wenn timerms>0 ist
	 * 
	 * @param message Nachricht
	 * @param title   Title der Box
	 * @param timerms wenn größer als 0, verschwindet die Box nach dieser Zeit von
	 *                ms
	 */
	public void showInfoBox(String message, String title, int timerms) {
		JDialog d = createDialog(fenster, message, title, timerms);
		d.setLocationRelativeTo(fenster);
		// JFrame parent = fenster;
		// d.setLocation(parent.getX() + parent.getWidth()/2, parent.getY());
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

	public void setEnabledAll(boolean bool) {
		debug("setEnableAll to " + bool);
		compmenue.setEnabled(bool);
		robotermenue.setEnabled(bool);
		einstellungmenue.setEnabled(bool);
		for (int i = 0; i < dateimenue.getItemCount(); i++) {
			JMenuItem c = dateimenue.getItem(i);
			if (c != null && c.getText()!=null && !c.getText().equals("Beenden")) //seperators machen Stress
				c.setEnabled(bool);
		}
	}

	public void updateCanvas() {
		debug("updateCanvas!");
		robotCanvas.update();
		robotCanvas.repaint();
	}

	public void setEnableParse(boolean b) {
		parserEintrag.setEnabled(b);
		if (!b)
			setEnableExec(false);
	}

	public void setEnableExec(boolean b) {
		interpreterEintrag.setEnabled(true);
	}

}
