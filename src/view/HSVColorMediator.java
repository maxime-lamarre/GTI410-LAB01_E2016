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
	
	//Les trois sliders virtuels;
	ColorSlider hCS;
	ColorSlider sCS;
	ColorSlider vCS;
	
	//Valeurs RGB
	int red; //0-255
	int green; //0-255
	int blue; //0-255
	
	//Valeurs HSV
	double hue; //0-360
	double saturation; //0-1
	double value; //0-1
	
	//Valeurs pour les constantes
	private static final int H = 0;
	private static final int S = 1;
	private static final int V = 2;
	
	private static final int R = 0;
	private static final int G = 1;
	private static final int B = 2;
	
	//For testing only
	boolean verbose = true;
	boolean veryverbose = false;
	
	BufferedImage hueImage;
	BufferedImage saturationImage;
	BufferedImage valueImage;
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
		
		hueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		saturationImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		valueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeHueImage(red, green, blue);
		computeSaturationImage(red, green, blue);
		computeValueImage(red, green, blue); 	
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		
		double updatedValue;
		String slider = "";
		
		
		if(v != 0) {
			updatedValue = (double)v/255;
		}
		else {
			updatedValue = 0;
		}
		
		boolean updateHue = false;
		boolean updateSaturation = false;
		boolean updateValue = false;
		
		//if (s == hCS && updatedValue != hue)
		if (s == hCS){
			hue = updatedValue;
			updateSaturation = true;
			updateValue = true;
			
			slider = "hue";
		}
		//if (s == sCS && updatedValue != saturation)
		if (s == sCS) {
			saturation = updatedValue;
			updateHue = true;
			updateValue = true;
			
			slider = "saturation";
		}
		//if (s == vCS && updatedValue != value)
		if (s == vCS) {
			value = updatedValue;
			updateHue = true;
			updateSaturation = true;
			slider = "value";
		}
		
		double[] rgbfinal = convertHSV_to_RGB(hue,saturation,value);
		
		red = (int)rgbfinal[R];
		green = (int)rgbfinal[G];
		blue = (int)rgbfinal[B];
		
		if (updateHue) {
			computeHueImage(hue,saturation,value);
		}
		if (updateSaturation) {
			computeSaturationImage(hue,saturation,value);
		}
		if (updateValue) {
			computeValueImage(hue,saturation,value);
		}
				
		Pixel pixel = new Pixel(red,green,blue, 255);
		
		if(verbose){		
		System.out.println("The slider for " + slider + " has been updated.");	
		System.out.println("The new colors are : "
				+ "red -> " + red 
				+ ", green -> " + green 
				+ ", blue ->" +blue);
		System.out.println("The HSV color is now : "
				+ "hue -> " + hue 
				+ ", saturation -> " + saturation 
				+ ", value ->" +value);
		}
		System.out.println("=======================");
		
		result.setPixel(pixel);
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
		
		double[] hsv = convertRGB_to_HSV(red,green,blue);
		
		hCS.setValue((int)(hsv[H]*255));
		sCS.setValue((int)(hsv[S]*255));
		vCS.setValue((int)(hsv[V]*255));
		
		
		
		computeHueImage(hsv[H],hsv[S],hsv[V]);
		computeSaturationImage(hsv[H],hsv[S],hsv[V]);
		computeValueImage(hsv[H],hsv[S],hsv[V]);
		
		
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}
	
	public void computeHueImage(double var_hue, double var_saturation, double var_value) { 

		double currentVal;
		double[] temprgb;
		Pixel temp = new Pixel(red,green,blue,255);
		
		for (int i = 0; i<imagesWidth; ++i) {
			
			currentVal = ((double)i)/255;
			temprgb = convertHSV_to_RGB(currentVal, var_saturation, var_value);			
			
			if(veryverbose)	{System.out.println("Value for currentVal is now : " + currentVal);}
			if(veryverbose)	{System.out.println("Value for temprgb is now : " + temprgb[R] +','+ temprgb[G] +','+ temprgb[B]);}
			
			temp.setRed((int)temprgb[R]);
			temp.setGreen((int)temprgb[G]);
			temp.setBlue((int)temprgb[B]);
			
			int rgb = temp.getARGB();
			
			//p.setRed((int)(((double)i / (double)imagesWidth)*255.0)); 
			//int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}
		if (hCS != null) {
			hCS.update(hueImage);
		}
	}
	
	public void computeSaturationImage(double var_hue, double var_saturation, double var_value) {
		
		/*
		 * 1- Conversion RGB => HSV
		 * 2- Calcul modification du S (avec les données déjà présentes en H et V)
		 * 3- Pour chaque pixel, conversion HSV => RGB
		 */
		double currentVal;
		double[] temprgb;
		Pixel temp = new Pixel(red,green,blue,255);
		
		for (int i = 0; i<imagesWidth; ++i) {
			
			currentVal = ((double)i)/255;				
			temprgb = convertHSV_to_RGB(var_hue, currentVal, var_value);			
			
			if(veryverbose)	{System.out.println("Value for currentVal is now : " + currentVal);}
			if(veryverbose)	{System.out.println("Value for temprgb is now : " + temprgb[R] +','+ temprgb[G] +','+ temprgb[B]);}
			
			temp.setRed((int)temprgb[R]);
			temp.setGreen((int)temprgb[G]);
			temp.setBlue((int)temprgb[B]);
			
			int rgb = temp.getARGB();
			
			for (int j = 0; j<imagesHeight; ++j) {
				saturationImage.setRGB(i, j, rgb);
			}
		}
		if (sCS != null) {
			sCS.update(saturationImage);
		}
	}
	
	public void computeValueImage(double var_hue, double var_saturation, double var_value) {
		/*
		 * 1- Conversion RGB => HSV
		 * 2- Calcul modification du V (avec les données déjà présentes en H et S)
		 * 3- Pour chaque pixel, conversion HSV => RGB
		 */
		double currentVal;
		double[] temprgb;
		Pixel temp = new Pixel(red,green,blue,255);
		
		for (int i = 0; i<imagesWidth; ++i) {
			
			currentVal = ((double)i)/255;
			temprgb = convertHSV_to_RGB(var_hue, var_saturation, currentVal);
			
			if(veryverbose)	{System.out.println("Value for currentVal is now : " + currentVal);}
			if(veryverbose)	{System.out.println("Value for temprgb is now : " + temprgb[R] +','+ temprgb[G] +','+ temprgb[B]);}
			
			temp.setRed((int)temprgb[R]);
			temp.setGreen((int)temprgb[G]);
			temp.setBlue((int)temprgb[B]);
			
			int rgb = temp.getARGB();
			
			for (int j = 0; j<imagesHeight; ++j) {
				valueImage.setRGB(i, j, rgb);
			}
		}
		if (vCS != null) {
			vCS.update(valueImage);
		}
	}
	
	/**
	 * @return
	 */
	public BufferedImage getValueImage() {
		return valueImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getSaturationImage() {
		return saturationImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getHueImage() {
		return hueImage;
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

	private double setTripleMax(double a, double b, double c){
		return setDoubleMax(setDoubleMax(a,b),c);
	}
	
	private double setTripleMin(double a, double b, double c){
		return setDoubleMin(setDoubleMin(a,b),c);
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
	
	private double[] convertHSV_to_RGB(double h, double s, double v){
	
		/*
		 * This color conversion algorithm is taken in part from http://www.easyrgb.com/index.php?X=MATH&H=21#text21
		 * It has been adapted to java format for this method.
		 * 
		 */
		
			double r;
			double g;
			double b;
			
			double var_r; 
			double var_g;
			double var_b;
			
		if ( s == 0 )                       //HSV from 0 to 1
		{
		   r = v * 255;
		   g = v * 255;
		   b = v * 255;
		}
		else
		{
		   double var_h = h * 6;
		   if ( var_h == 6 ) var_h = 0;      //H must be < 1
		   double var_i = var_h;             //Or ... var_i = floor( var_h )
		   double var_1 = v * ( 1 - s );
		   double var_2 = v * ( 1 - s * ( var_h - var_i ) );
		   double var_3 = v * ( 1 - s * ( 1 - ( var_h - var_i ) ) );
	
		   if      ( var_i <= 0 ) { 
			   var_r = v;
			   var_g = var_3;
			   var_b = var_1;
			   }
		   else if ( var_i <= 1 ) { 
			   var_r = var_2;
			   var_g = v;
			   var_b = var_1;
			   }
		   else if ( var_i <= 2 ) { 
			   var_r = var_1;
			   var_g = v;
			   var_b = var_3;
			   }
		   else if ( var_i <= 3 ) { 
			   var_r = var_1;
			   var_g = var_2;
			   var_b = v;
			   }
		   else if ( var_i <= 4 ) { 
			   var_r = var_3;
			   var_g = var_1;
			   var_b = v;
			   }
		   else { 
			   var_r = v;
			   var_g = var_1;
			   var_b = var_2; 
			   }
	
		  r = var_r * 255;                  //RGB results from 0 to 255
		  g = var_g * 255;
		  b = var_b * 255;
		}
		double[] rgbcolor = {r,g,b};
		
		return rgbcolor;
	}
	
	private double[] convertRGB_to_HSV(double r, double g, double b){


		double var_R = ( r / 255 );                     //RGB from 0 to 255
		double var_G = ( g / 255 );
		double var_B = ( b / 255 );

		double var_Min = setTripleMin( var_R, var_G, var_B );    //Min. value of RGB
		double var_Max = setTripleMax( var_R, var_G, var_B );    //Max. value of RGB
		double del_Max = var_Max - var_Min;             //Delta RGB value

		double V = var_Max;
		double H = 0;
		double S = 0;

		if ( del_Max == 0 )                     //This is a gray, no chroma...
		{
			H = 0;                               //HSV results from 0 to 1
			S = 0;
		}
		else                                    //Chromatic data...
		{
			S = del_Max / var_Max;

			double del_R = ( ( ( var_Max - var_R ) / 6 ) + ( del_Max / 2 ) ) / del_Max;
			double del_G = ( ( ( var_Max - var_G ) / 6 ) + ( del_Max / 2 ) ) / del_Max;
			double del_B = ( ( ( var_Max - var_B ) / 6 ) + ( del_Max / 2 ) ) / del_Max;

			if      ( var_R == var_Max ) H = del_B - del_G;
			else if ( var_G == var_Max ) H = ( 1 / 3 ) + del_R - del_B;
			else if ( var_B == var_Max ) H = ( 2 / 3 ) + del_G - del_R;

			if ( H < 0 ) H += 1;
			if ( H > 1 ) H -= 1;
		}

		double[] hsvcolor = {H,S,V};

		return hsvcolor;
	}

	public int getHueFromRGB(int red, int green, int blue){
		double[] HSV = convertRGB_to_HSV((double)red,(double)green,(double)blue);
		
		return (int)HSV[0];
	}
	
	public int getSaturationFromRGB(int red, int green, int blue){
		double[] HSV = convertRGB_to_HSV((double)red,(double)green,(double)blue);
		
		return (int)HSV[1];
	}
	
	public int getValueFromRGB(int red, int green, int blue){
		double[] HSV = convertRGB_to_HSV((double)red,(double)green,(double)blue);
		
		return (int)HSV[2];
	}

}

