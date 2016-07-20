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

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;

import model.Shape;

/**
 * <p>Title: ScaleCommand</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Jean-Fran�ois Barras, �ric Paquette</p>
 * <p>Company: (�TS) - �cole de Technologie Sup�rieure</p>
 * <p>Created on: 2004-03-19</p>
 * @version $Revision: 1.2 $
 */
public class ScaleCommand extends AnchoredTransformationCommand {
	
	private MementoTracker mt = new MementoTracker();
	private List objects;
	private double sx;
	private double sy;
	
	/**
	 * @param sx the multiplier to the horizontal size
	 * @param sy the multiplier to the vertical size
	 * @param anchor one of the predefined positions for the anchor point
	 */
	public ScaleCommand(double sx, double sy, int anchor, List aObjects) {
		super(anchor);
		this.sx = sx;
		this.sy = sy;
		objects = aObjects;
	}
	
	/* (non-Javadoc)
	 * @see controller.Command#execute()
	 */
	public void execute() {
		System.out.println("command: scaling x by " + sx +
                           " and y by " + sy + " ; anchored on " + getAnchor() );

		Iterator iterateur = objects.iterator();
		Shape shape;
		
		while(iterateur.hasNext()){
			shape = (Shape)iterateur.next();
			AffineTransform t = shape.getAffineTransform();
			
			mt.addMememto(shape);
			t.translate(0 - this.getAnchorPoint(shape).getX(), 0 - this.getAnchorPoint(shape).getY());
			t.scale(sx, sy);
			t.translate(this.getAnchorPoint(shape).getX(), this.getAnchorPoint(shape).getY());
			shape.setAffineTransform(t);
			
			
//			int anchorPointX = (int)this.getAnchorPoint(shape).getX();
//			int anchorPointY = (int)this.getAnchorPoint(shape).getY();
//			
//			System.out.println("Point d'ancrage : x = " + anchorPointX + " y = " + anchorPointY);
//			
//			int distanceXInit = (int)( 0 - anchorPointX );
//			int distanceYInit = (int)( 0 - anchorPointY );
//			
//			System.out.println("Distance x : " + distanceXInit + ", Distance y : " + distanceYInit);
//			
//			AffineTransform t = shape.getAffineTransform();
//			AffineTransform t2 = new AffineTransform();
//			AffineTransform t3 = new AffineTransform();
//			AffineTransform t4 = new AffineTransform();
//			
//			t4.translate(distanceXInit, distanceYInit);
//			t3.scale(sx, sy);
//			t2.translate(0 - distanceXInit, 0 - distanceYInit);
//			
//			System.out.println("Distance x : " + (0 - distanceXInit) + ", Distance y : " + (0 - distanceYInit));
//			
//			t3.preConcatenate(t4);
//			t2.preConcatenate(t3);
//			t.preConcatenate(t2);
//			shape.setAffineTransform(t);
		}
	}

	/* (non-Javadoc)
	 * @see controller.Command#undo()
	 */
	public void undo() {
		mt.setBackMementos();
	}
}