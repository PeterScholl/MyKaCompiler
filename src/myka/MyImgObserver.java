package myka;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

public class MyImgObserver implements ImageObserver {
	private static MyKaController controller = null;

	public MyImgObserver(MyKaController c) {
		MyImgObserver.controller = c;
	}

	@Override
	public boolean imageUpdate(Image img, int flags, int x, int y, int width, int height) {
		System.out.println("ImageObserver called - width: " + this.WIDTH);
		if ((flags & HEIGHT) != 0)
			System.out.println("Image height = " + height);
		if ((flags & WIDTH) != 0)
			System.out.println("Image width = " + width);
		if ((flags & FRAMEBITS) != 0)
			System.out.println("Another frame finished.");
		if ((flags & SOMEBITS) != 0)
			System.out.println("Image section :" + new Rectangle(x, y, width, height));
		if ((flags & ALLBITS) != 0)
			System.out.println("Image finished!");
		if ((flags & ABORT) != 0)
			System.out.println("Image load aborted...");

		controller.updateView();
		
		return true;
		// return false; // No furhter information on changes
	}

}
