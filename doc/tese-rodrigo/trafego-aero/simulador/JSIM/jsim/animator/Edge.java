/******************************************************************
 * file: Edge.java
 * 
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
 * @version 1.3, 30 July 2002
 * @Author Matt Perry
 */

package jsim.animator;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/***********************************************************************************
 * This class defines an edge between two nodes in a Graph.  It includes 2 endpoints
 * represented by the ids of the respective nodes in the graph
 */

 public class Edge {

  public int id;		//id of this edge
  public int node1;		//start node id
  public int node2;		//end node id
  public QuadCurve2D.Double line; 	//line representing the edge on the Animation
  public double slope;		//slope of this line
  public double length;		//length of this line
  public boolean undefinedSlope;//true if slope is undefined (i.e. vertical line)
  public double x_increment;    //x increment for moving the entity along the line
  public double y_increment;  	//y increment for moving the entity along the line
  public double label_x, label_y;  //positions for the label for this edge
  public double mid_x, mid_y;	//midPoint of this edge
  public String label;		//label for this edge
  
/************* Removed for upgrade from line to curve *****************************
  /********************************************************************************
   * Constructor
   * @param id		id of new Edge
   * @param node1	id of start node
   * @param node2	id of end node
   *
   public Edge (int id, int node1, int node2) {

	this.id = id;
	this.node1 = node1;
	this.node2 = node2;
	this.line = new Line2D.Double();
	slope = 0.0;
	length = 0.0;
        x_increment = 0.0;
	y_increment = 0.0;
	undefinedSlope = false;
	label_x = 0.0;
	label_y = 0.0;
	mid_x = 0.0;
	mid_y = 0.0;
   }
   
  /********************************************************************************
   * Constructor for labeled edge
   * @param id		id of new Edge
   * @param node1	id of start node
   * @param node2	id of end node
   * @param label	label for this edge
   *
   public Edge (int id, int node1, int node2, String label) {

	this.id = id;
	this.node1 = node1;
	this.node2 = node2;
	this.line = new Line2D.Double();
	slope = 0.0;
	length = 0.0;
        x_increment = 0.0;
	y_increment = 0.0;
	undefinedSlope = false;
	label_x = 0.0;
	label_y = 0.0;
	mid_x = 0.0;
	mid_y = 0.0;
	this.label = label;
   } 
   
******************/   
  /********************************************************************************
   * Added constructor for compatability with JSIM
   * Uses (x,y) coordinates instead of node ids
   * @param x1		x-coordinate of first endpoint
   * @param y1		y-coordinate of first endpoint
   * @param curve1	x-value for curve
   * @param curve2	y-value for curve
   * @param x2		x-coordinate for second endpoint
   * @param y2		y-coordinate for second endpoint
   */
   public Edge (double x1, double y1, double curve1, double curve2, double x2, double y2) {

	this.id = id;
	this.line = new QuadCurve2D.Double(x1, y1, curve1, curve2, x2, y2);
	slope = 0.0;
	length = 0.0;
      x_increment = 0.0;
	y_increment = 0.0;
	undefinedSlope = false;
	label_x = 0.0;
	label_y = 0.0;
	mid_x = 0.0;
	mid_y = 0.0;
   } 
 }//Edge
   
