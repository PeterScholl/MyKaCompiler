package myka;

public class Executor extends Thread{
	private Token[] tokenliste= null;

	public Executor(Token[] tokenliste) {
		this.tokenliste = tokenliste;
	}
	
	public void run() {
		Interpreter.execute(tokenliste);
		MyKaController c = MyKaController.getController();
		if (Interpreter.getFail()) {
			c.writeStatus("Executing...Failed!!!");
		} else {
			c.writeStatus("Executing...Done!");
		}
		c.enableInput();
	}

}
