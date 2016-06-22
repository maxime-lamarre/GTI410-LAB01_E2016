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

package controller;
import model.*;

import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Stack;

/**
 * <p>Title: ImageLineFiller</p>
 * <p>Description: Image transformer that inverts the row color</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barre-Brisebois, Eric Paquette</p>
 * <p>Company: ETS - Ecole de Technologie Superieure</p>
 * @author unascribed
 * @version $Revision: 1.13 $
 */
public class ImageLineFillerNew extends ImageLineFiller {
	private ImageX currentImage;
	private Pixel fillColor = new Pixel(0xFF00FFFF);
	private Pixel borderColor = new Pixel(0xFFFFFF00);
	private Pixel clickedColor = new Pixel(0xFFFFFFFF);
	private boolean floodFill = true;
	private int hueThreshold = 1;
	private int saturationThreshold = 2;
	private int valueThreshold = 3;
	private int redThreshold = 0;
	private int greenThreshold = 0;
	private int blueThreshold = 0;
	
	private int stackValue = 0;
	
	/**
	 * Creates an ImageLineFiller with default parameters.
	 * Default pixel change color is black.
	 */
	public ImageLineFillerNew() {
	}
	
	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_FLOODER; } 
	
	protected boolean mouseClicked(MouseEvent e){
		List intersectedObjects = Selector.getDocumentObjectsAtLocation(e.getPoint());
		if (!intersectedObjects.isEmpty()) {
			Shape shape = (Shape)intersectedObjects.get(0);
			if (shape instanceof ImageX) {
				currentImage = (ImageX)shape;

				Point pt = e.getPoint();
				Point ptTransformed = new Point();
				try {
					shape.inverseTransformPoint(pt, ptTransformed);
				} catch (NoninvertibleTransformException e1) {
					e1.printStackTrace();
					return false;
				}
				ptTransformed.translate(-currentImage.getPosition().x, -currentImage.getPosition().y);
				if (0 <= ptTransformed.x && ptTransformed.x < currentImage.getImageWidth() &&
				    0 <= ptTransformed.y && ptTransformed.y < currentImage.getImageHeight()) {
					currentImage.beginPixelUpdate();
					if (floodFill) newFloodFill(ptTransformed);  
					else newBorderFill(ptTransformed);		
					currentImage.endPixelUpdate();											 	
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Horizontal line fill with specified color
	 */
	private void horizontalLineFill(Point ptClicked) {
		Stack<Point> stack = new Stack<Point>();
		stack.push(ptClicked);
		while (!stack.empty()) {
			Point current = (Point)stack.pop();
			if (0 <= current.x && current.x < currentImage.getImageWidth() &&
				!currentImage.getPixel(current.x, current.y).equals(fillColor)) {
				currentImage.setPixel(current.x, current.y, fillColor);
				
				// Next points to fill.
				Point nextLeft = new Point(current.x-1, current.y);
				Point nextRight = new Point(current.x+1, current.y);
				stack.push(nextLeft);
				stack.push(nextRight);
			}
		}

		// TODO EP In this method, we are creating many new Point instances. 
		//      We could try to reuse as many as possible to be more efficient.
		// TODO EP In this method, we could be creating many Point instances. 
		//      At some point we can run out of memory. We could create a new point
		//      class that uses shorts to cut the memory use.
		// TODO EP In this method, we could test if a pixel needs to be filled before
		//      adding it to the stack (to reduce memory needs and increase efficiency).
	}
	
	public void newBorderFill(Point myPoint){
	    
		//Inspire par : http://stackoverflow.com/questions/23031087/stack-overflow-error-when-filling-a-shape-with-boundary-fill-algorithm
		
		//Il faut creer un stack de point parce que si on ne fait que faire des appels recursifs, on overflow
		//a chaque fois des qu'il y a trop de pixels. Le probleme arrive autour d'une profondeur de 3000 appels.
		Stack<Point> points = new Stack<>();
		
		//On ajoute le premier point a la pile.
	    points.add(myPoint);
	    
	    System.out.println("Filling points with boundaryFill, get rdy for awesome.");
	    System.out.println("On imprime avec 8 directions");

	    //On va sortir les points un a un de la pile puis en prendre les coordonnees.
	    //Ensuite, on verifie qu'il repond aux conditions (le pixel est dans l'image) et
	    // on verifie sa couleur par rapport aux couleurs de contour et de remplissage.
	    
	    //Une fois le point colorie, on ajoute creee des nouveaux points avec chacunes des directions et on les
	    //ajoute a la pile.
	    
	    //A la prochaine iteration, on sort le prochain point et on repete l'operation.
	    while(!points.isEmpty()) {
	        Point currentPoint = points.pop();
	        int x = currentPoint.x;
	        int y = currentPoint.y;

	        //System.out.println("Sur le pixel (" + x + ',' + y + ')');
	        
	        if(0 <= x && x < currentImage.getImageWidth() && 
	        		0 <= y && y < currentImage.getImageHeight() &&
	        		!colorThreshold (currentImage.getPixel(x, y), borderColor) && 
	        		!colorThreshold (currentImage.getPixel(x, y), fillColor)){
	            
	        	//On colorie le pixel actuel puisqu'il repond aux conditions.
	        	currentImage.setPixel(x, y, fillColor);

	            points.push(new Point(x+1, y));
	            points.push(new Point(x+1,y+1));
	            points.push(new Point(x, y+1));
	            points.push(new Point(x-1,y+1));
	            points.push(new Point(x-1, y));
	            points.push(new Point(x-1,y-1));
	            points.push(new Point(x, y-1));
	            points.push(new Point(x+1,y-1));
	        }
	    }
	}
	
	
	public void newFloodFill(Point ptClicked){
		
		Stack<Point> points = new Stack<>();
		
		Pixel p = currentImage.getPixel(ptClicked.x, ptClicked.y);
		clickedColor.setARGB(p.getARGB());
		
		//On ajoute le premier point a la pile.
	    points.add(ptClicked);
	    
	    System.out.println("Filling points with floodFill, get rdy for awesome.");
	    System.out.println("On imprime avec 8 directions");

	    while(!points.isEmpty()) {
	        Point currentPoint = points.pop();
	        int x = currentPoint.x;
	        int y = currentPoint.y;

	        //System.out.println("Sur le pixel (" + x + ',' + y + ')');
	        
	        if(0 <= x && x < currentImage.getImageWidth() && 
	        		0 <= y && y < currentImage.getImageHeight() &&
	        				colorThreshold (currentImage.getPixel(x, y), clickedColor) && 
	    	        		!colorThreshold (currentImage.getPixel(x, y), fillColor)){
	            
	        	//On colorie le pixel actuel puisqu'il repond aux conditions.
	        	currentImage.setPixel(x, y, fillColor);

	            points.push(new Point(x+1, y));
	            points.push(new Point(x+1,y+1));
	            points.push(new Point(x, y+1));
	            points.push(new Point(x-1,y+1));
	            points.push(new Point(x-1, y));
	            points.push(new Point(x-1,y-1));
	            points.push(new Point(x, y-1));
	            points.push(new Point(x+1,y-1));
	        }
	    }
		
	}
	
	/**
	 * @return
	 */
	public Pixel getBorderColor() {
		return borderColor;
	}

	/**
	 * @return
	 */
	public Pixel getFillColor() {
		return fillColor;
	}

	/**
	 * @param pixel
	 */
	public void setBorderColor(Pixel pixel) {
		borderColor = pixel;
		System.out.println("new border color");
	}

	/**
	 * @param pixel
	 */
	public void setFillColor(Pixel pixel) {
		fillColor = pixel;
		System.out.println("new fill color");
	}
	/**
	 * @return true if the filling algorithm is set to Flood Fill, false if it is set to Boundary Fill.
	 */
	public boolean isFloodFill() {
		return floodFill;
	}

	/**
	 * @param b set to true to enable Flood Fill and to false to enable Boundary Fill.
	 */
	public void setFloodFill(boolean b) {
		floodFill = b;
		if (floodFill) {
			System.out.println("now doing Flood Fill");
		} else {
			System.out.println("now doing Boundary Fill");
		}
	}

	/**
	 * @return
	 */
	public int getHueThreshold() {
		return hueThreshold;
	}

	/**
	 * @return
	 */
	public int getSaturationThreshold() {
		return saturationThreshold;
	}

	/**
	 * @return
	 */
	public int getValueThreshold() {
		return valueThreshold;
	}

	/**
	 * @param i
	 */
	public void setHueThreshold(int i) {
		hueThreshold = i;
		System.out.println("new Hue Threshold " + i);
		convertHSV_to_RGB(hueThreshold/360.0, saturationThreshold/255.0, valueThreshold/255.0);
	}

	/**
	 * @param i
	 */
	public void setSaturationThreshold(int i) {
		saturationThreshold = i;
		System.out.println("new Saturation Threshold " + i);
		convertHSV_to_RGB(hueThreshold/360.0, saturationThreshold/255.0, valueThreshold/255.0);
	}

	/**
	 * @param i
	 */
	public void setValueThreshold(int i) {
		valueThreshold = i;
		System.out.println("new Value Threshold " + i);
		convertHSV_to_RGB(hueThreshold/360.0, saturationThreshold/255.0, valueThreshold/255.0);
	}
	
	private void convertHSV_to_RGB(double h, double s, double v){

		/*
		 * This color conversion algorithm is taken in part from http://www.easyrgb.com/index.php?X=MATH&H=21#text21
		 * and http://www.rapidtables.com/convert/color/hsv-to-rgb.htm
		 * It has been adapted to java format for this method.
		 */

		double hdeg = h * 360; //conversion de 0-1 a 0-360	

		Double r;
		Double g;
		Double b;

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
		
		redThreshold = r.intValue();
		greenThreshold = g.intValue();
		blueThreshold = b.intValue();
		
		System.out.println("R: "+redThreshold+" V: "+greenThreshold+" B: "+blueThreshold);

	}
	
	private boolean colorThreshold (Pixel pXY, Pixel pColor) {
		
		if ((pXY.getRed() >= (pColor.getRed() - redThreshold)) && (pXY.getRed() <= (pColor.getRed() + redThreshold))) {
			if ((pXY.getGreen() >= (pColor.getGreen() - greenThreshold)) && (pXY.getGreen() <= (pColor.getGreen() + greenThreshold))) {
				if ((pXY.getBlue() >= (pColor.getBlue() - blueThreshold)) && (pXY.getBlue() <= (pColor.getBlue() + blueThreshold))) {
					return true;
				}
			}
		}
		
		return false;
	}

}

