package myka;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Hilfsfunktionen {
	/**
	 * Zentriert das ├╝bergebene Fenster auf dem Bildschirm
	 * 
	 * @param fenster
	 */
	public static void fensterZentrieren(JFrame fenster) {

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

		int x = dimension.width;
		int y = dimension.height;

		int fensterX = (x - fenster.getWidth()) / 2;
		int fensterY = (y - fenster.getHeight()) / 2;

		fenster.setLocation(fensterX, fensterY);
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		@SuppressWarnings("rawtypes")
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}

	public static int sindSieSicher(JFrame root, String frage) {
		// Custom button text
		Object[] options = { "Ja", "Abbrechen" };
		int n = JOptionPane.showOptionDialog(root, frage, "Sind Sie sicher?", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		return n;
	}

	public static int[] stringArrayToIntArray(String[] args) {
		int[] ret = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			try {
				ret[i] = Integer.parseInt(args[i]);
			} catch (Exception e) {
				System.err.println("Could not parse to Int " + args[i] + "\n" + e.getMessage());
			}
		}
		return ret;
	}
	
	public static Token[] convertTokenListToArray(List<Token> tokenlist) {
		if (tokenlist==null) return null;
		tokenlist.toFirst();
		int count = 0;
		while (tokenlist.hasAccess()) {
			count++;
			tokenlist.next();
		}
		Token[] ret = new Token[count];
		tokenlist.toFirst();
		count = 0;
		while (tokenlist.hasAccess()) {
			ret[count++]=tokenlist.getContent();
			tokenlist.next();
		}
		return ret;		
	}
	
	public static String stringErfragen(String frage, String title, String vorgabe) {
		return (String) JOptionPane.showInputDialog(null,
                frage,title,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        vorgabe);
	}
	
	public static boolean isStringInArray(String text, String[] array) {
		for (int i=0; i<array.length; i++) {
			if (text.equals(array[i])) return true;
		}
		return false;
	}
}
