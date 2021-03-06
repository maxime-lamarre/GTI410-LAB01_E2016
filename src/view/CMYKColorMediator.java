package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

public class CMYKColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider cyanCS;
	ColorSlider magentaCS;
	ColorSlider yellowCS;
	ColorSlider blackCS;
	// int red;
	// int green;
	// int blue;
	// int black;
	Double cyan = 0.0;
	Double magenta = 0.0;
	Double yellow = 0.0;
	Double black = 0.0;
	Double redD, greenD, blueD, blackD, tempoCouleur;
	BufferedImage cyanImage;
	BufferedImage magentaImage;
	BufferedImage yellowImage;
	BufferedImage blackImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	CMYKColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		//this.red = result.getPixel().getRed();
		//this.green = result.getPixel().getGreen();
		//this.blue = result.getPixel().getBlue();
		//this.black = 0;
		this.result = result;
		result.addObserver(this);
		
		cyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		blackImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		//computeCyanImage(1.0, 0.0, 0.0, 0.0);
		//computeMagentaImage(0.0, 1.0, 0.0, 0.0);
		//computeYellowImage(0.0, 0.0, 1.0, 0.0); 
		//computeBlackImage(0.0, 0.0, 0.0, 0.0);
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		Double color;
		boolean updateCyan = false;
		boolean updateMagenta = false;
		boolean updateYellow = false;
		boolean updateBlack = false;
		
		int red, green, blue, tempo;
		
		black = minValueDouble(cyan, magenta, yellow);
		
		red = (int) (255.0 * (1.0-cyan) * (1.0-black));
		green = (int) (255.0 * (1.0-magenta) * (1.0-black));
		blue = (int) (255.0 * (1.0-yellow) * (1.0-black));
		
		color = (v * 1.0 / 255.0);
		
		if (s == cyanCS && color != cyan) {
			cyan = color;
			updateMagenta = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == magentaCS && color != magenta) {
			magenta = color;
			updateCyan = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == yellowCS && color != yellow) {
			yellow = color;
			updateCyan = true;
			updateMagenta = true;
			updateBlack = true;
		}
		if (s == blackCS && color != black) {
			
			if (black < color) {
				if (black < 1.0) black = black + (1/255.0);
				if (cyan +(1.0/255.0) >= 1.0) cyan = 1.0;
				else cyan = cyan + (1.0/255.0);
				if (magenta+(1.0/255.0) >= 1.0) magenta = 1.0;
				else magenta = magenta + (1/255.0);
				if (yellow+(1.0/255.0) >= 1.0) yellow = 1.0;
				else yellow = yellow + (1.0/255.0);			
			}
			
			if (black >= color) {
				if (black > 0.0) black = black - (1.0/255.0);
				if (cyan-(1.0/255.0) <= 0.0) cyan = 0.0;
				else cyan = cyan - (1.0/255.0);
				if (magenta-(1.0/255.0) <= 0.0) magenta = 0.0;
				else magenta = magenta - (1.0/255.0);
				if (yellow-(1.0/255.0) <= 0.0) yellow = 0.0;
				else yellow = yellow - (1.0/255.0);				
			}
		
			updateCyan = true;
			updateYellow = true;
			updateMagenta = true;
			updateBlack = true;
					
			red = (int) (255.0 * (1.0-cyan) * (1.0-black));
			green = (int) (255.0 * (1.0-magenta) * (1.0-black));
			blue = (int) (255.0 * (1.0-yellow) * (1.0-black));
						
			cyanCS.setValue(255-red);
			cyanCS.setArrowPosition(255-red);
			
			magentaCS.setValue(255-green);
			magentaCS.setArrowPosition(255-green);
			
			yellowCS.setValue(255-blue);
			yellowCS.setArrowPosition(255-blue);
		}
		if (updateCyan) {
			computeCyanImage(cyan, magenta, yellow, black);
		}
		if (updateMagenta) {
			computeMagentaImage(cyan, magenta, yellow, black);
		}
		if (updateYellow) {
			computeYellowImage(cyan, magenta, yellow, black);
		}
		
		black = minValueDouble(cyan, magenta, yellow);
		tempo = (int) (black * 255.0);
		
		blackCS.setValue(tempo);
		blackCS.setArrowPosition(tempo);
		
		red = (int) (255.0 * (1.0-cyan) * (1.0-black));
		green = (int) (255.0 * (1.0-magenta) * (1.0-black));
		blue = (int) (255.0 * (1.0-yellow) * (1.0-black));
		
		
		Pixel pixel = new Pixel(red, green, blue, 255);
		result.setPixel(pixel);
	}
	
	private int maxValue(int red, int green, int blue) {

		if ((red >= green) && (red >= blue)) return red;
		if ((green >= red) && (green >= blue)) return green;
		if ((blue >= red) && (blue >= green)) return blue;
		
		return 0;
	}
	
	private double minValueDouble(double cyan, double magenta, double yellow) {
		Double black = 0.0;

		if ((cyan <= magenta) && (cyan <= yellow)) black = 0.0 + cyan;
		if ((magenta <= cyan) && (magenta <= yellow)) black = 0.0 + magenta;
		if ((yellow <= cyan) && (yellow <= magenta)) black = 0.0 + yellow;
		
		if (black <= 0.0) black = 0.001;
		
		return black;
	}
	
	public void computeBlackImage(Double cyanC, Double magentaC, Double yellowC, Double blackC) { 
		int red, green, blue, tempo;
		
		red = (int) (255.0 * (1.0-cyanC) * (1.0-blackC));
		green = (int) (255.0 * (1.0-magentaC) * (1.0-blackC));
		blue = (int) (255.0 * (1.0-yellowC) * (1.0-blackC));
		
		Pixel p = new Pixel(red, green, blue, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			tempo = (int)(((double)i / (double)imagesWidth)*255.0);
			p.setRed(255-tempo); 
			p.setGreen(255-tempo);
			p.setBlue(255-tempo);
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				blackImage.setRGB(i, j, rgb);
			}
		}
		if (blackCS != null) {
			blackCS.update(blackImage);
			tempo = (int) (minValueDouble(cyanC, magentaC, yellowC) * 255.0);
			blackCS.setValue(tempo);
			blackCS.setArrowPosition(tempo);
		}
	}
	
	public void computeCyanImage(Double cyanC, Double magentaC, Double yellowC, Double blackC) {
		int red, green, blue, tempo=0;
		
		red = (int) (255.0 * (1.0-cyanC) * (1.0-blackC));
		green = (int) (255.0 * (1.0-magentaC) * (1.0-blackC));
		blue = (int) (255.0 * (1.0-yellowC) * (1.0-blackC));

		Pixel p = new Pixel(red, green, blue, 255);

		for (int i = 0; i<imagesWidth; ++i) {
			tempo = (int)(((double)i / (double)imagesWidth)*255.0);
			p.setRed(255-tempo); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				cyanImage.setRGB(i, j, rgb);
			}
		}
		if (cyanCS != null) {
			cyanCS.update(cyanImage);
			cyanCS.setValue(255-red);
			cyanCS.setArrowPosition(255-red);
		}
		
	}
	
	public void computeMagentaImage (Double cyanC, Double magentaC, Double yellowC, Double blackC) {
		int red, green, blue, tempo=0;
		
		red = (int) (255.0 * (1.0-cyanC) * (1.0-blackC));
		green = (int) (255.0 * (1.0-magentaC) * (1.0-blackC));
		blue = (int) (255.0 * (1.0-yellowC) * (1.0-blackC));
	
		Pixel p = new Pixel(red, green, blue, 255);
		
		for (int i = 0; i<imagesWidth; ++i) {
			tempo = (int)(((double)i / (double)imagesWidth)*255.0);
			p.setGreen(255-tempo); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				magentaImage.setRGB(i, j, rgb);
			}
		}
		if (magentaCS != null) {
			magentaCS.update(magentaImage);
			magentaCS.setValue(255-green);
			magentaCS.setArrowPosition(255-green);
		}
		
	}
	
	public void computeYellowImage (Double cyanC, Double magentaC, Double yellowC, Double blackC) { 
		int red, green, blue, tempo=0;
		
		red = (int) (255.0 * (1.0-cyanC) * (1.0-blackC));
		green = (int) (255.0 * (1.0-magentaC) * (1.0-blackC));
		blue = (int) (255.0 * (1.0-yellowC) * (1.0-blackC));
	
		Pixel p = new Pixel(red, green, blue, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			tempo = (int)(((double)i / (double)imagesWidth)*255.0);
			p.setBlue(255-tempo); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				yellowImage.setRGB(i, j, rgb);
			}
		}
		if (yellowCS != null) {
			yellowCS.update(yellowImage);
			yellowCS.setValue(255-blue);
			yellowCS.setArrowPosition(255-blue);
		}
		
	}
	
	/**
	 * @return
	 */
	public BufferedImage getYellowImage() {
		return yellowImage;
	}
	
	/**
	 * @return
	 */
	public BufferedImage getBlackImage() {
		return blackImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getMagentaImage() {
		return magentaImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getCyanImage() {
		return cyanImage;
	}

	/**
	 * @param slider
	 */
	public void setCyanCS(ColorSlider slider) {
		cyanCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setMagentaCS(ColorSlider slider) {
		magentaCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setYellowCS(ColorSlider slider) {
		yellowCS = slider;
		slider.addObserver(this);
	}
	
	/**
	 * @param slider
	 */
	public void setBlackCS(ColorSlider slider) {
		blackCS = slider;
		slider.addObserver(this);
	}
	
	/**
	 * @return
	 */
	public double getCyan() {
		return cyan;
	}

	/**
	 * @return
	 */
	public double getMagenta() {
		return magenta;
	}

	/**
	 * @return
	 */
	public double getYellow() {
		return yellow;
	}
	
	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		
		int red, green, blue, blackI;
		
		red = (int) (255.0 * (1.0-cyan) * (1.0-black));
		green = (int) (255.0 * (1.0-magenta) * (1.0-black));
		blue = (int) (255.0 * (1.0-yellow) * (1.0-black));
		
		Pixel currentColor = new Pixel(red, green, blue, 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		red = result.getPixel().getRed();
		green = result.getPixel().getGreen();
		blue = result.getPixel().getBlue();
		
		redD = (double) (red / 255.0);
		greenD = (double) (green / 255.0);
		blueD = (double) (blue / 255.0);
		
		black = minValueDouble(cyan, magenta, yellow);
		
		cyan = (1.0 - redD - black) / (1.0 - black);
		magenta = (1.0 - greenD - black) / (1.0 - black);
		yellow = (1.0 - blueD - black) / (1.0 - black);
		
		blackI = (int) (black * 255.0);
		
		if (black > 0.0) {
			tempoCouleur = black * 255.0;
			
			blackCS.setValue(tempoCouleur.intValue());
			blackCS.setArrowPosition(blackI);
			greenD = 255.0 * (1.0 - magenta) * (1.0 - black);
			green = greenD.intValue();

		}
		
		cyanCS.setValue(red);
		cyanCS.setArrowPosition(red);
		magentaCS.setValue(green);
		magentaCS.setArrowPosition(green);
		yellowCS.setValue(blue);
		yellowCS.setArrowPosition(blue);
		black = minValueDouble(cyan, magenta, yellow);
		
		computeCyanImage(cyan, magenta, yellow, black);
		computeMagentaImage(cyan, magenta, yellow, black);
		computeYellowImage(cyan, magenta, yellow, black);
		computeBlackImage(cyan, magenta, yellow, black);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}
	
	public void setCyan(Double cyan) {
		this.cyan = 0.0 + cyan;
	}
	
	public void setMangenta(Double magenta) {
		this.magenta = 0.0 + magenta;
	}
	
	public void setYellow(Double yellow) {
		this.yellow = 0.0 + yellow;
	}
	
	public void setBlack(Double black) {
		this.black = 0.0 + black;
	}

}