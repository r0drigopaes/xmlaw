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
 * file: Graph.java
 * @version     1.3, 30 July 2002
 * @author      Matt Perry
 */


package jsim.animator;

import java.lang.*;
//import java.lang.math.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/***********************************************************************************
 * This class defines a Graph to be used with the Simulation package of JSIM.  The 
 * Graph represents a skeleton of sources, sinks, and facilities along with interconnections
 * between them.
 */

 public class Graph {

  public LinkedList<AnimationEntity> nodes; 		//nodes of the graph
  public LinkedList<Edge> edges;		//edges of the graph

  public double step = 50.0;		//step along an edge for one move
  

 /***********************************************************************************
  * Constructor
  * @param nodes	LinkedList of nodes for the Graph
  * @param edges	LinkedList of edges for the Graph
  */

  public Graph (LinkedList<AnimationEntity> nodes, LinkedList<Edge> edges) {

	this.nodes = nodes;       
	this.edges = edges;
	
  }//COnstructor

 /**********************************************************************************
  * Sets the Edges of the Graph to connect the center of their respective endpoints
  * Also calls all other initialization methods for the Graph
  */

  public void setEdges () {
	double x1 = 0;
	double x2 = 0;
	double y1 = 0;
	double y2 = 0;
	Rectangle container1;
	Rectangle container2;
	AnimationEntity node1;
	AnimationEntity node2;

//This segment of code taken out for compatability with JSIM
//Simulation package is responsible for determining these specifications
/***********************************
	ListIterator it = edges.listIterator(); 

	while (it.hasNext()) {
		Edge e = (Edge)it.next();
		node1 = getNode (e.node1);
		node2 = getNode (e.node2);
		container1 = node1.shape.getBounds();
		container2 = node2.shape.getBounds();

		x1 = (container1.getX() + ((container1.getWidth())/2.0));
		y1 = (container1.getY() + ((container1.getHeight())/2.0));
		
		x2 = (container2.getX() + ((container2.getWidth())/2.0));
		y2 = (container2.getY() + ((container2.getHeight())/2.0));

		//System.out.println (x1 + " " + y1 + " " + x2 + " " +y2);

		e.line = new Line2D.Double (x1, y1, x2, y2);
		setSlope (e.id);
		setLength (e.id);
		setIncrements (e.id);
		setMid (e.id);
		setEdgeLabel (e.id);
	}//while
***************************/	
        ListIterator<AnimationEntity> it = nodes.listIterator();
        
        while (it.hasNext()) {
        	AnimationEntity ae = it.next();
        	setNodeLabel (ae.id);
        	setTokenLoc (ae.id);
        }//while
        
   }//setEdges()

/****************** Taken out for upgrade from line to curve ************************
 /********************************************************************************
  * Sets the slope of an Edge
  * @param id		id of the Edge
  *

  public void setSlope (int id) {
	double rise = 0;
	double run = 0;

	Edge e = getEdge(id);

	if (e != null) {
		rise = (e.line.getY2() - e.line.getY1());
		run = (e.line.getX2() - e.line.getX1());

		if (run == 0) {
			e.undefinedSlope = true;
			e.slope = 0.0;
		   }

		else {
			e.slope = (rise/run);
                   }
         }//if
   }//setSlope

 /********************************************************************************
  * Sets the length of an edge
  * @param id		id of Edge
  *

  public void setLength (int id) {

	Edge e = getEdge(id);

	if (e != null) {
		if (!e.undefinedSlope) {
			double xValue = (e.line.getX2() - e.line.getX1())*(e.line.getX2() - e.line.getX1());
			double yValue = (e.line.getY2() - e.line.getY1())*(e.line.getY2() - e.line.getY1());
			double length = Math.sqrt (xValue + yValue);
			e.length = length;
                   }
		else {
			double yValue = (e.line.getY2() - e.line.getY1())*(e.line.getY2() - e.line.getY1()); 
		 	e.length = Math.sqrt(yValue);
		     }
	    }//if
   }//setLength
   
 /********************************************************************************
  * Sets the midpoint for an edge
  * @param id		id of the edge
  *
  
  public void setMid (int id) {
  
  	Edge e = getEdge(id);

	if (e != null) {
		e.mid_x = (e.line.getX2() - e.line.getX1())/2;
		if (e.mid_x < 0) {
			e.mid_x = (-1)*e.mid_x;
		    }
		if (e.line.getX1() < e.line.getX2()) {
			e.mid_x = e.line.getX2() - e.mid_x;
		   }
		else {	
			e.mid_x = e.line.getX1() - e.mid_x;
		     }
		
		e.mid_y = (e.line.getY2() - e.line.getY1())/2;
		if (e.mid_y < 0) {
			e.mid_y = (-1)*e.mid_y;
		    }
		if (e.line.getY1() < e.line.getY2()) {
			e.mid_y = e.line.getY2() - e.mid_y;
		   }
		else {	
			e.mid_y = e.line.getY1() - e.mid_y;
		     }
           }//if
  }//setMid

***************************************/
  
 /********************************************************************************
  * Sets the label position for a node
  * @param id		id of node
  */
  
  public void setNodeLabel (int id) {
  
  	AnimationEntity ae = getNode (id);
  	
  	if (ae != null) {
  		Rectangle bounds = ae.shape.getBounds();
  		ae.label_x = bounds.getX() + 15;
  		ae.label_y = bounds.getY() - 15;
  		
  		ae.serviced_x = (bounds.getX() + ((bounds.getWidth())/2.0)) - 7;
		ae.serviced_y = (bounds.getY() + ((bounds.getHeight())/2.0)) + 2.5;
  		
         }
  }
  
 /********************************************************************************
  * Sets the label position for an edge
  * @param id 		id of edge
  */
  
  public void setEdgeLabel (int id) {
  
  	Edge e = getEdge(id);
  	
  	if (e != null) {
  		e.label_y = e.mid_y - 40;
  		e.label_x = e.mid_x - 30;
  	   }
  }//setEdgeLabel
  
 /********************************************************************************
  * Sets the token locations for a node
  */
  
  public void setTokenLoc (int id) {
  
  	AnimationEntity ae = getNode (id);
  	
  	if (ae != null) {
  		Rectangle bounds = ae.shape.getBounds();
  		ae.tokStart_x = (int)bounds.getX() + (int)bounds.getWidth() - (11);
  		ae.tokStart_y = (int)bounds.getY() + (5);
  	   }
  		
    }//setTokenLoc

 /********************************************************************************
  * Sets the increments for an edge
  * @param id		id of the edge
  */

  public void setIncrements (int id) {

	Edge e = getEdge(id);

	if (e != null) {
		if (!e.undefinedSlope) {
			double x_increment = Math.sqrt((step*step)/((e.slope*e.slope) + 1));
			double y_increment = x_increment*e.slope;
		   	e.x_increment = x_increment;
			e.y_increment = y_increment;
		   }//if
		else {
			e.x_increment = 0;
			e.y_increment = step;
		     }
	    }//if
   }//setIncrements

 /********************************************************************************
  * Returns the node with the given id
  * @param id		id of the node to remove
  */

  public AnimationEntity getNode (int id) {

	ListIterator<AnimationEntity> it = nodes.listIterator();
	AnimationEntity ae = null;

	while (it.hasNext()) {
		ae = it.next();
		if (ae.id == id) {
			return ae;
		   }
	}

	return null;
  }

 /********************************************************************************
  * Returns the edge with the given id
  * @param id		id of the node to remove
  */

  public Edge getEdge (int id) {

	ListIterator<Edge> it = edges.listIterator();
	Edge e = null;

	while (it.hasNext()) {
		e = (Edge)it.next();
		if (e.id == id) {
			return e;
		   }
	}

	return null;
  }

 /*********************************************************************************
  * Adds a new node to the Graph
  * @param ae		AnimationEntity to add as a node
  */

  public void addNode (AnimationEntity ae) {

	nodes.add(ae);
  }

 /*********************************************************************************
  * Adds a new edge to the Graph
  * @param e		Edge to add to the Graph
  */

  public void addEdge (Edge e) {

	edges.add(e);
  }

 /*********************************************************************************
  * Removes a node from the Graph
  * @param id		id of node to remove
  */

  public void removeNode (int id) {

	ListIterator<AnimationEntity> it = nodes.listIterator();
	AnimationEntity ae = null;

	while (it.hasNext()) {
		ae = it.next();
		if (ae.id == id) {
			nodes.remove (ae);
		   }
	}
   }

 /*********************************************************************************
  * Removes an edge from the Graph
  * @param id		id of edge to remove
  */

  public void removeEdge (int id) {

	ListIterator<Edge> it = edges.listIterator();
	Edge e = null;

	while (it.hasNext()) {
		e = it.next();
		if (e.id == id) {
			edges.remove (e);
		   }
	}
   }

 /**********************************************************************************
  * Main function for testing
  */

  public static void main (String [] args) {
  
 /**********************************
	AnimationEntity node1 = new AnimationEntity (0,
				new Rectangle2D.Double (0,0, 10, 10), 
				new AffineTransform(), Color.blue);

	AnimationEntity node2 = new AnimationEntity (1,
				new Rectangle2D.Double (10,10, 10, 10), 
				new AffineTransform(), Color.blue);

	Edge e = new Edge (0,0, 1);

	LinkedList nodes = new LinkedList();
	LinkedList edges = new LinkedList();

	nodes.add(node1);
	nodes.add(node2);
	edges.add(e);

	Graph g = new Graph (nodes, edges);
	g.setEdges();
	Edge test = g.getEdge (0);
	System.out.println (test.slope + "   " + test.length + "  " +test.x_increment+"  "+test.y_increment);
 
 ************************************/
  }//main


	


 }//Graph
	

		 		
