package myka;

public class Executor extends Thread {
	private static Executor s = null; // singleton
	private Token[] tokenliste = null;
	private boolean running = false;

	private Executor(Token[] tokenliste) {
		this.tokenliste = tokenliste;
	}

	public static Executor getInstance(Token[] tokenliste) {
		if (s == null) {
			s = new Executor(tokenliste);
		} else if (!s.running) {
			s.tokenliste = tokenliste;
		}
		return s;
	}

	public void run() {
		//System.out.println("Starting thread");
		if (!running) {
			running = true;
			MyKaController c = MyKaController.getController();
			boolean ok = false;
			try {
				Interpreter.execute(tokenliste);
				ok = !Interpreter.getFail();
			} catch (RuntimeException e) { // z.B. Fehler im (Schüler-)Interpreter
				e.printStackTrace();
			} finally {
				// Oberfläche immer wieder freigeben, auch nach einer Exception
				c.writeStatus(ok ? "Executing...Done!" : "Executing...Failed!!!");
				c.enableInput();
				running = false;
				s = null;
			}
		}
		s = null;
	}

}
