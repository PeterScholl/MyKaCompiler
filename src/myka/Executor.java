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
			Interpreter.execute(tokenliste);
			MyKaController c = MyKaController.getController();
			if (Interpreter.getFail()) {
				c.writeStatus("Executing...Failed!!!");
			} else {
				c.writeStatus("Executing...Done!");
			}
			c.enableInput();
			running = false;
		}
		s = null;
	}

}
