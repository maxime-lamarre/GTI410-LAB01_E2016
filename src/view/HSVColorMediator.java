/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider hCS;
	ColorSlider sCS;
	ColorSlider vCS;
	int red;
	int green;
	int blue;
	BufferedImage redImage;
	BufferedImage greenImage;
	BufferedImage blueImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		this.red = result.getPixel().getRed();
		this.green = result.getPixel().getGreen();
		this.blue = result.getPixel().getBlue();
		this.result = result;
		result.addObserver(this);
		
		redImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		greenImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		blueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeHueImage(red, green, blue);
		computeSaturationImage(red, green, blue);
		computeValueImage(red, green, blue); 	
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateRed = false;
		boolean updateGreen = false;
		boolean updateBlue = false;
		if (s == hCS && v != red) {
			red = v;
			updateGreen = true;
			updateBlue = true;
		}
		if (s == sCS && v != green) {
			green = v;
			updateRed = true;
			updateBlue = true;
		}
		if (s == vCS && v != blue) {
			blue = v;
			updateRed = true;
			updateGreen = true;
		}
		if (updateRed) {
			computeHueImage(red, green, blue);
		}
		if (updateGreen) {
			computeSaturationImage(red, green, blue);
		}
		if (updateBlue) {
			computeValueImage(red, green, blue);
		}
		
		Pixel pixel = new Pixel(red, green, blue, 255);
		result.setPixel(pixel);
	}
	
	public void computeHueImage(int red, int green, int blue) { 
		/*
		 *R' = R/255
		 *G' = G/255
		 *B' = B/255
		 *
		 *Cmax = max(R',G',B')
		 *Cmin = min(R',G',B')
		 *
		 *delta = Cmax-Cmin
		 *
		 *Si Cmax = R' :
		 *H = 60*(((G'-B')/delta)mod(6))
		 *
		 *Si Cmax = G' :
		 *H = 60*(((B'-R')/delta) +2)
		 *
		 *Si Cmax = B' :
		 *H = 60*(((R'-G')/delta) +4)
		 * 
		 * Ref : http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
		 * 
		 */ 
		
		double hue;
		
		double rPrime = red / 255;
		double gPrime = green / 255;
		double bPrime = blue / 255;
		
		double cmax = setTripleMax(rPrime,gPrime,bPrime);
		double cmin = setTripleMin(rPrime,gPrime,bPrime);
		double delta = cmax - cmin;
		
		if(cmax == rPrime){
			hue = 60*(((gPrime-bPrime) / delta) %6); 
		}
		else if(cmax == gPrime){
			hue = 60*(((bPrime-rPrime) / delta) +2);
		}
		else {
			hue = 60*(((rPrime-gPrime) / delta) +4);
		}
//		
//		Pixel p = new Pixel(red, green, blue, 255); 
//		for (int i = 0; i<imagesWidth; ++i) {
//			p.setRed((int)(((double)i / (double)imagesWidth)*255.0)); 
//			int rgb = p.getARGB();
//			for (int j = 0; j<imagesHeight; ++j) {
//				redImage.setRGB(i, j, rgb);
//			}
//		}
		if (hCS != null) {
			hCS.update(redImage);
		}
	}
	
	public void computeSaturationImage(int red, int green, int blue) {
		/*
		 *R' = R/255
		 *G' = G/255
		 *B' = B/255
		 *
		 *Cmax = max(R',G',B')
		 *Cmin = min(R',G',B')
		 *
		 *delta = Cmax-Cmin
		 *
		 * Si Cmax = 0 :
		 * S = 0
		 * 
		 * Si Cmax !=0 :
		 * S = delta/Cmax
		 * 
		 * http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
		 */
		
		Pixel p = new Pixel(red, green, blue, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setGreen((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				greenImage.setRGB(i, j, rgb);
			}
		}
		if (sCS != null) {
			sCS.update(greenImage);
		}
	}
	
	public void computeValueImage(int red, int green, int blue) {
		/*
		 *R' = R/255
		 *G' = G/255
		 *B' = B/255
		 *
		 *Cmax = max(R',G',B')
		 *
		 * V = Cmax
		 * 
		 * http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
		 */
		
		Pixel p = new Pixel(red, green, blue, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setBlue((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				blueImage.setRGB(i, j, rgb);
			}
		}
		if (vCS != null) {
			vCS.update(blueImage);
		}
	}
	
	/**
	 * @return
	 */
	public BufferedImage getBlueImage() {
		return blueImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getGreenImage() {
		return greenImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getRedImage() {
		return redImage;
	}

	/**
	 * @param slider
	 */
	public void setHCS(ColorSlider slider) {
		hCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setSCS(ColorSlider slider) {
		sCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setVCS(ColorSlider slider) {
		vCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @return
	 */
	public double getBlue() {
		return blue;
	}

	/**
	 * @return
	 */
	public double getGreen() {
		return green;
	}

	/**
	 * @return
	 */
	public double getRed() {
		return red;
	}


	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = new Pixel(red, green, blue, 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		red = result.getPixel().getRed();
		green = result.getPixel().getGreen();
		blue = result.getPixel().getBlue();
		
		hCS.setValue(red);
		sCS.setValue(green);
		vCS.setValue(blue);
		computeHueImage(red, green, blue);
		computeSaturationImage(red, green, blue);
		computeValueImage(red, green, blue);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}
	
	private double setTripleMax(double a, double b, double c){
		return setDoubleMax(setDoubleMax(a,b),c);
	}
	
	private double setTripleMin(double a, double b, double c){
		return setDoubleMax(setDoubleMax(a,b),c);
	}
	
	private double setDoubleMax(double a, double b){
		
		double high = a;
		if (b>a){
			high = b;
		}		
		return high;
	}
	
	private double setDoubleMin(double a, double b){
		
		double low = a;
		if (b<a){
			low = b;
		}		
		return low;
	}

}

