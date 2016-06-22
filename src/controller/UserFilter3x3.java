package controller;

import model.ImageDouble;
import model.ImageX;
import model.PixelDouble;

public class UserFilter3x3 extends Filter {	
	private double filterMatrix[][] = null;
	
	/**
	 * Default constructor.
	 * @param paddingStrategy PaddingStrategy used 
	 * @param conversionStrategy ImageConversionStrategy used
	 */
	public UserFilter3x3(PaddingStrategy paddingStrategy, 
						 ImageConversionStrategy conversionStrategy) {
		super(paddingStrategy, conversionStrategy);	
		filterMatrix = new double[3][3];

	}
	
	/**
	 * Filters an ImageX and returns a ImageDouble.
	 */
	public ImageDouble filterToImageDouble(ImageX image) {
		return filter(conversionStrategy.convert(image));
	}
	
	/**
	 * Filters an ImageDouble and returns a ImageDouble.
	 */	
	public ImageDouble filterToImageDouble(ImageDouble image) {
		return filter(image);
	}
	
	/**
	 * Filters an ImageX and returns an ImageX.
	 */	
	public ImageX filterToImageX(ImageX image) {
		ImageDouble filtered = filter(conversionStrategy.convert(image)); 
		return conversionStrategy.convert(filtered);
	}
	
	/**
	 * Filters an ImageDouble and returns a ImageX.
	 */	
	public ImageX filterToImageX(ImageDouble image) {
		ImageDouble filtered = filter(image); 
		return conversionStrategy.convert(filtered);		
	}
	
	/*
	 * Filter Implementation 
	 */
	private ImageDouble filter(ImageDouble image) {
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		System.out.println(image.getImageHeight()+" "+image.getImageWidth());
		ImageDouble newImage = new ImageDouble(imageWidth, imageHeight);
		PixelDouble newPixel = null;
	
		double resultRed = 0.0; 
		double resultGreen = 0.0; 
		double resultBlue = 0.0; 
		PixelDouble p;

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				newPixel = new PixelDouble();
			
				//*******************************
				// Convolution
				for (int i = 0; i <= 2; i++) {
					for (int j = 0; j <= 2; j++) {
						p = getPaddingStrategy().pixelAt(image,x+(i-1),y+(j-1));
						resultRed   += filterMatrix[i][j] * p.getRed();
						resultGreen += filterMatrix[i][j] * p.getGreen();
						resultBlue  += filterMatrix[i][j] * p.getBlue();
					}
				}
				
				newPixel.setRed(resultRed);
				newPixel.setGreen(resultGreen);
				newPixel.setBlue(resultBlue);
				
				resultRed = 0.0;
				resultGreen = 0.0;
				resultBlue = 0.0;
							
				//*******************************
				// Alpha - Untouched in this filter
				newPixel.setAlpha(getPaddingStrategy().pixelAt(image, x,y).getAlpha());
							 
				//*******************************
				// Done
				newImage.setPixel(x, y, newPixel);
			}
		}
		
		return newImage;
	}
	
	public void setMatrix(int i, int j, double value){
		filterMatrix[i][j] = value;
		System.out.println("["+i+"]"+"["+j+"]"+value);
	}
}
