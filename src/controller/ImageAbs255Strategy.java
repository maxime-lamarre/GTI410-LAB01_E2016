package controller;

import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;

public class ImageAbs255Strategy extends ImageConversionStrategy {
	/**
	 * Converts an ImageDouble to an ImageX using a clamping strategy (0-255).
	 */
	
	double minRed = 0.0, minGreen = 0.0, minBlue = 0.0, minAlpha = 0.0;
	double maxRed = 255.0, maxGreen = 255.0, maxBlue = 255.0, maxAlpha = 255.0;
	
	public ImageX convert(ImageDouble image) {
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		
		minMaxColor(image);
		
		ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
		PixelDouble curPixelDouble = null;

		newImage.beginPixelUpdate();
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				
				newImage.setPixel(x, y, new Pixel((int)(abs255Red(curPixelDouble.getRed())),
												  (int)(abs255Green(curPixelDouble.getGreen())),
												  (int)(abs255Blue(curPixelDouble.getBlue())),
												  (int)(curPixelDouble.getAlpha())));
			}
		}
		newImage.endPixelUpdate();
		return newImage;
	}
	
	private void minMaxColor(ImageDouble image){
		PixelDouble tempoPixel = new PixelDouble();
		tempoPixel = image.getPixel(0, 0);
		minRed = maxRed = tempoPixel.getRed();
		minGreen = maxGreen = tempoPixel.getGreen();
		minBlue = maxBlue = tempoPixel.getBlue();
		minAlpha = maxAlpha = tempoPixel.getAlpha();		
		
		for (int h = 0; h < image.getImageHeight(); h++) {
			for (int w = 0; w < image.getImageWidth(); w++){
				tempoPixel = image.getPixel(w, h);
				if (Math.abs(tempoPixel.getRed()) < minRed) {minRed = Math.abs(tempoPixel.getRed());}
				if (Math.abs(tempoPixel.getRed()) > maxRed) {maxRed = Math.abs(tempoPixel.getRed());}
				if (Math.abs(tempoPixel.getGreen()) < minGreen) {minGreen = Math.abs(tempoPixel.getGreen());}
				if (Math.abs(tempoPixel.getGreen()) > maxGreen) {maxGreen = Math.abs(tempoPixel.getGreen());}
				if (Math.abs(tempoPixel.getBlue()) < minBlue) {minBlue = Math.abs(tempoPixel.getBlue());}
				if (Math.abs(tempoPixel.getBlue()) > maxBlue) {maxBlue = Math.abs(tempoPixel.getBlue());}
				if (Math.abs(tempoPixel.getAlpha()) < minAlpha) {minAlpha = Math.abs(tempoPixel.getAlpha());}
				if (Math.abs(tempoPixel.getAlpha()) > maxAlpha) {maxAlpha = Math.abs(tempoPixel.getAlpha());}
			}
		}
		System.out.println("Min Red:"+minRed+" Max Red: "+maxRed);
		System.out.println("Min Green:"+minGreen+" Max Green: "+maxGreen);
		System.out.println("Min Blue:"+minBlue+" Max Blue: "+maxBlue);
	}
	
	private double abs255Red(double value) {
		value = Math.abs(value);
		value = 255.0 * (value - minRed) / (maxRed - minRed);
		return value;
	}	
	
	private double abs255Green(double value) {
		value = Math.abs(value);
		value = 255.0 * (value - minGreen) / (maxGreen - minGreen);
		return value;
	}
	
	private double abs255Blue(double value) {
		value = Math.abs(value);
		value = 255.0 * (value - minBlue) / (maxBlue - minBlue);
		return value;
	}
	
	private double abs255Alpha(double value) {
		value = Math.abs(value);
		value = 255.0 * (value - minAlpha) / (maxBlue - minAlpha);
		return value;
	}
	
}