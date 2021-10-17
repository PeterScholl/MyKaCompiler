package myka;

import java.awt.Image;
import java.awt.image.ImageObserver;

public class MyImgObserver implements ImageObserver {
	private static MyKaController controller = null;

	public MyImgObserver(MyKaController c) {
		MyImgObserver.controller = c;
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		System.out.println("ImageObserver called");
		if (controller!=null) controller.robotZeichnen();
		return true;
	}

}
