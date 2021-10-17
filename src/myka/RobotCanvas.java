package myka;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.swing.JPanel;

/**
 * This class simply represents a drawing canvas for the robot
 * it contains a foreground and background image. The background image
 * is used to draw the "next" position and will afterwards be changed to
 * the foreground
 * The actual drawing of the position is done in the controller
 * @author peter
 *
 */
public class RobotCanvas extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage foreground, background;
	private int width, height;

	public RobotCanvas(Controller c) {
		super();
		init_components();
	}

	public void init_components() {
		int width = getWidth();
		int height = getHeight();
		foreground = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		background = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		Graphics g = foreground.getGraphics();
		g.setColor(Color.black);
		g.drawOval(0, 0, width, height); //TODO: what is this for
		foreground = deepCopy(background);
	}

	public void clearBGScreen() {
		//System.out.println("New Backgroundscreen generated");
		background = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		width = this.getWidth();
		height = this.getHeight();
		Graphics g = background.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(foreground, 0, 0, this);
	}

	private static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	
	public void update() {
		foreground = deepCopy(background);
	}

	public BufferedImage getBufferedImage() {
		if (this.getWidth() != width || this.getHeight() != height) clearBGScreen();
		return background;
	}
}
