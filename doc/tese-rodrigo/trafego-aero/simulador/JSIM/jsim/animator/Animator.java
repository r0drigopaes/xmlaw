/******************************************************************
 * Copyright (c) 2005, John Miller
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   
 * 1. Redistributions of source code must retain the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer. 
 * 2. Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials
 *    provided with the distribution. 
 * 3. Neither the name of the University of Georgia nor the names
 *    of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * file: Animator.java
 * @version     1.3, 28 Jan 2005
 * @author      Matt Perry
 */

package jsim.animator;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public interface Animator {
      
       /**************************************************************
        * Adds a Shape at given location
        * Shapes: Line2D, QuadCurve2D, CubicCurve2D, Rectangle2D, RoundRectangle2D,
        * 	  Arc2D, Ellipse2D
  	* @param shapeID	id for the new Shape
	* @param shape		Shape of the object
  	* @param l		Label for the created Shape
  	* @param x		x-coordinate of the location
  	* @param y		y-coordinate of the location
  	* @param w		width of the shape
  	* @param h		height of the shape
  	* @param arcW		arc width for curves
  	* @param arcH		arc height for curves
  	* @param x2		double needed for some shapes
  	* @param y2		double needed for some shapes
  	* @param type		type needed for certian shapes
  	* @param time		Simulation time
        */
	public void create (int shapeID, String shape, Label l, double x, double y, double w, double h, 
		     double arcW, double arcH, double x2, double y2, int type, double time);
	
       /****************************************************************
        * Destroys a Shape based on ShapeID
        * @param shapeID	id of the Shape to destroy
        * @param time		SImulation time
        */	
	public void destroy (int shapeID, double time);
	
       /*****************************************************************
        * Translates a Shape to a new (x,y) location
        * @param shapeID	id of Shape to translate
        * @param tx		new x-coordinate
        * @param ty		new y-coordinate
        * @param time		Simulation time
        */	
	public void translate (int shapeID, double tx, double ty, double time);
	
       /******************************************************************
        * Rotates a shape
        * @param shapeID	id of Shape to tanslate
        * @param theta		angle of rotation
        * @param x		new x-coordinate
        * @param y		new y-coordinate
        * @param time		Simulation time
        */	
	public void rotate (int shapeID, double theta, double x, double y, double time);
	
       /*******************************************************************
        * Scales a Shape
        * @param shapeID	id of Shape to scale
        * @param sx		scaled x-coordinate
        * @param sy		scaled y-coordiante
	* @param time		Simulation time
        */
	public void scale (int shapeID, double sx, double sy, double time);
	
       /******************************************************************
        * Shears a Shape
        * @param shapeID	id of Shape to shear
        * @param shx		scaled x-coordinate
        * @param shy		scaled y-cooordinate
        * @param time		Simulation time
        */	
	public void shear (int shapeID, double shx, double shy, double time);
	
       /***************************************************************
        * Paint Method for solid color, uses 1 Color argument
        * @param shapeID	id for Shape to paint
        * @param c		Color for the Shape
        * @param time		Simulation time
        */	
	public void setPaint (int shapeID, Color c, double time);
	
       /***************************************************************
        * Paint Method for gradient color, uses 2 Color arguments
        * @param shapeID	id for SHape to paint
        * @param c1		1st Color for Shape
        * @param c2		2nd Color for Shape
        * @param time		Simulation time
        */
	public void setPaint (int shapeID, Color c1, Color c2, double time);
	
  }//Animatior Interface
	
	
	
	
  
