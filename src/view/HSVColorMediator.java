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
	boolean verboseUpdates = false;
	boolean verbose = false;
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
		
		double[] hsv = convertRGB_to_HSV(red,green,blue);
		
		hue = hsv[H];
		saturation = hsv[S];
		value = hsv[V];
		
		this.result = result;
		result.addObserver(this);

		hueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		saturationImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		valueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeHueImage(hsv[H],hsv[S],hsv[V]);
		computeSaturationImage(hsv[H],hsv[S],hsv[V]);
		computeValueImage(hsv[H],hsv[S],hsv[V]); 	
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
		if (s == hCS && updatedValue != hue){
			hue = updatedValue;
			//updateHue = true;
			updateSaturation = true;
			updateValue =true;

			slider = "hue";
		}
		//if (s == sCS && updatedValue != saturation)
		if (s == sCS && updatedValue != saturation) {
			saturation = updatedValue;
			updateHue = true;
			//updateSaturation = true;
			updateValue =true;

			slider = "saturation";
		}
		//if (s == vCS && updatedValue != value)
		if (s == vCS && updatedValue != value) {
			value = updatedValue;
			updateHue = true;
			updateSaturation = true;
			//updateValue =true;
			
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
			System.out.println("=======================");
		}
		

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
		
		hue = hsv[H];
		saturation = hsv[S];
		value = hsv[V];

		if(verboseUpdates){
			System.out.println("Color (RGB) is (" + red + ','+ green + ','+ blue + ')');
			System.out.println("Color (HSV) is (" + hsv[H] + ','+ hsv[S] + ','+ hsv[V] + ')');
		}

		hCS.setValue((int)(hue*imagesWidth));
		hCS.setArrowPosition((int)(hue*imagesWidth));
		computeHueImage(hue,saturation,value);

		sCS.setValue((int)(saturation*imagesWidth));
		sCS.setArrowPosition((int)(saturation*imagesWidth));
		computeSaturationImage(hue,saturation,value);

		vCS.setValue((int)(value*imagesWidth));
		vCS.setArrowPosition((int)(value*imagesWidth));
		computeValueImage(hue,saturation,value);



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

			currentVal = (((double)i)/imagesWidth); //Pour espacer le spectrum de Hue sur le largeur de l'image.
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
		 * and http://www.rapidtables.com/convert/color/hsv-to-rgb.htm
		 * It has been adapted to java format for this method.
		 */

		double hdeg = h * 360; //conversion de 0-1 a 0-360	

		double r;
		double g;
		double b;

		double var_r = 0; 
		double var_g = 0;
		double var_b = 0;

		if ( s == 0 )                       //HSV from 0 to 1
		{
			r = v * 255;
			g = v * 255;
			b = v * 255;
		}
		else
		{
			if ( hdeg >= 360 ) hdeg = 0;

			double C = v * s;
			double X = C * (1-Math.abs(((hdeg / 60)%2)-1));
			double m = v - C;

			if(hdeg >= 0 && hdeg <60){
				var_r = C;
				var_g = X;
				var_b = 0;
			}
			else if(hdeg >= 60 && hdeg <120){
				var_r = X;
				var_g = C;
				var_b = 0;
			}
			else if(hdeg >= 120 && hdeg <180){
				var_r = 0;
				var_g = C;
				var_b = X;
			}
			else if(hdeg >= 180 && hdeg <240){
				var_r = 0;
				var_g = X;
				var_b = C;
			}
			else if(hdeg >= 240 && hdeg <300){
				var_r = X;
				var_g = 0;
				var_b = C;
			}
			else if(hdeg >= 300 && hdeg <360){
				var_r = C;
				var_g = 0;
				var_b = X;
			}

			r = (var_r + m) * 255;
			g = (var_g + m) * 255;
			b = (var_b + m) * 255;

		}
		double[] rgbcolor = {r,g,b};

		return rgbcolor;
	}

	private double[] convertRGB_to_HSV(double r, double g, double b){

		//Color calculations are taken from the Web.
		//See here for more info : http://www.rapidtables.com/convert/color/rgb-to-hsv.htm

		double var_R = ( r / 255 );                     //RGB from 0 to 255
		double var_G = ( g / 255 );
		double var_B = ( b / 255 );
		
		if(verboseUpdates){
			System.out.println("Prime values are (" + var_R + ','+ var_G + ','+ var_B + ')' );
		}

		double var_Min = setTripleMin( var_R, var_G, var_B );    //Min. value of RGB
		double var_Max = setTripleMax( var_R, var_G, var_B );    //Max. value of RGB
		double delta_Max = var_Max - var_Min;             //Delta RGB value

		if(verboseUpdates){
			System.out.println("Delta values are (min/max/delta) : (" + var_Min + ','+ var_Max + ','+ delta_Max + ')' );
		}
		
		double V = var_Max;
		double H;
		double S;

		if ( delta_Max == 0 )                     //This is a gray, no chroma...
		{
			H = 0;                               //HSV results from 0 to 1
			S = 0;
		}
		else                                    //Chromatic data...
		{
			//Hue calculation
			if(delta_Max == var_R){
				H = 60 * (((var_G - var_B)/delta_Max)%6);
			}
			else if(delta_Max == var_G){
				H = 60 * (((var_B - var_R)/delta_Max) + 2);
			}
			else { //indique que le delta_Max est var_B
				H = 60 * (((var_R - var_G)/delta_Max) + 4);
			}
			
			//Saturation calculation
			//No if here since S is dependant only on whether delta_Max 
			//is 0 or not and the 0 case is covered above.
			S = (delta_Max / var_Max);
		}

		H = Math.abs(H/360);

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

