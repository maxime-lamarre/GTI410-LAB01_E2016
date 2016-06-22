package controller;

import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;

public class PaddingMirrorStrategy extends PaddingStrategy {
	/**
	 * Returns and validates the Pixel at the specified coordinate.
	 * If the Pixel is invalid, a new black (0,0,0,0) Pixel is returned.
	 * @param image source Image
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the validated Pixel value at the specified coordinates 
	 */
	public Pixel pixelAt(ImageX image, int x, int y) {
		if ((x > 0) && (x < image.getImageWidth()) &&
			(y > 0) && (y < image.getImageHeight())) {
			return image.getPixel(x, y);
		} else {
			if ((x >= 0) && (x < image.getImageWidth()) && (y >= image.getImageHeight())) 
				return image.getPixel(x, image.getImageHeight()-1);   	//Bordure haut
			if ((x >= 0) && (x < image.getImageWidth()) && (y < 0)) 
				return image.getPixel(x, 0);   	//Bordure bas
			if ((x < 0) && (y >= 0) && (y < image.getImageHeight())) 
				return image.getPixel(0, y);	//Bordure gauche
			if ((x > image.getImageWidth()) && (y >= 0)  && (y < image.getImageHeight())) 
				return image.getPixel(0, y);	//Bordure droite
			if ((x < 0) && (y >= image.getImageHeight())) 
				return image.getPixel(0, image.getImageHeight()-1);	//Coins haut gauche
			if ((x >= image.getImageWidth()) && (y >= image.getImageHeight())) 
				return image.getPixel(image.getImageWidth()-1, image.getImageHeight()-1);	//Coins haut droite
			if ((x < 0) && (y < 0)) 
				return image.getPixel(0, 0);	//Coins bas gauche
			if ((x >= image.getImageWidth()) && (y < 0)) 
				return image.getPixel(image.getImageWidth()-1, 0);	//Coins bas droite
		}
		return image.getPixel(x, y);
	}

	/**
	 * Returns and validates the PixelDouble at the specified coordinate.
	 * Original Pixel is converted to PixelDouble.
	 * If the Pixel is invalid, a new black (0,0,0,0) PixelDouble is returned.
	 * @param image source ImageDouble
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the validated PixelDouble value at the specified coordinates
	 */	
	public PixelDouble pixelAt(ImageDouble image, int x, int y) {
		if ((x >= 0) && (x < image.getImageWidth()) &&
			(y >= 0) && (y < image.getImageHeight())) {
			return image.getPixel(x, y);
		} else {
			if ((x >= 0) && (x < image.getImageWidth()) && (y >= image.getImageHeight())) 
				return image.getPixel(x, image.getImageHeight()-1);   	//Bordure haut
			if ((x >= 0) && (x < image.getImageWidth()) && (y < 0)) 
				return image.getPixel(x, 0);   	//Bordure bas
			if ((x < 0) && (y >= 0) && (y < image.getImageHeight())) 
				return image.getPixel(0, y);	//Bordure gauche
			if ((x >= image.getImageWidth()) && (y >= 0)  && (y < image.getImageHeight())) 
				return image.getPixel(0, y);	//Bordure droite
			if ((x < 0) && (y >= image.getImageHeight())) 
				return image.getPixel(0, image.getImageHeight()-1);	//Coins haut gauche
			if ((x >= image.getImageWidth()) && (y >= image.getImageHeight())) 
				return image.getPixel(image.getImageWidth()-1, image.getImageHeight()-1);	//Coins haut droite
			if ((x < 0) && (y < 0)) 
				return image.getPixel(0, 0);	//Coins bas gauche
			if ((x >= image.getImageWidth()) && (y < 0)) 
				return image.getPixel(image.getImageWidth()-1, 0);	//Coins bas droite
		}
		
		return image.getPixel(x, y);
	}
}
