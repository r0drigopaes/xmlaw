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
 * @version     1.3, 28 Jan 2005
 * @author Matt Perry
 */

package jsim.animator;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import jsim.util.*;


/********************************************************************
 * This class is used as a container for a Shape in the Animator 
 * Package of the JSIM simulation package
 * It has data members of type Shape, AffineTransform, and Color
 */

 public class AnimationEntity {

   int id;			//id of this entity
   Shape shape;			//Shape of the Entity
   AffineTransform transform;	//AffineTransform object associated with the Entity
   Color color;			//Color of the entity
   String label;		//name of the node
   int numServiced;		//number of entities serviced
   double label_x, label_y;	//positions of the labels for the nodes
   double serviced_x, serviced_y;  //positions to display num serviced
   boolean queued;		//true if entity is waiting in a queue
   int numTokens = 0;		//number of server units at a particular node
   int tokStart_x;		//X-position of first token
   int tokStart_y;		//y-position of fisrt token
   boolean showNumServed;
   
   //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * Color scheme for JSIM (easy to change)
     * May also need to adjust colors for entities (see SimObject)
     */
    public  static final Color  BACK_COLOR      = DeColores.brightskyblue;
    public  static final Color  FORE_COLOR      = DeColores.red;
    public  static final Color  SERVER_COLOR    = DeColores.darkyellow;
    public  static final Color  FACILITY_COLOR  = DeColores.lemonlime;
    public  static final Color  SIGNAL_COLOR    = DeColores.orange;
    public  static final Color  SOURCE_COLOR    = DeColores.lightgreen;
    public  static final Color  SINK_COLOR      = DeColores.red;
    public  static final Color  TRANSPORT_COLOR = DeColores.black;
    public  static final Color  TOKEN_COLOR     = DeColores.blue;
    public  static final Color  QUEUE_COLOR     = DeColores.purple;
    public  static final Color  LABEL_COLOR     = DeColores.black;
    public  static final Color  SPLIT_COLOR     = DeColores.blue;
    public  static final Color  JOIN_COLOR      = DeColores.deeppink;
    
    /**
     * Node  numbers
     */
    public  static final int SERVER    = 0;   // services entity requests
    public  static final int FACILITY  = 1;   // services entity requests
                                              //   and has an embedded queue
    public  static final int SIGNAL    = 2;   // signals servers
    public  static final int SOURCE    = 3;   // produces entities
    public  static final int SINK      = 4;   // consumes entities
    public  static final int SPLIT     = 7;   // causes an AND split
    public  static final int JOIN      = 8;   // causes and AND join   
  
    /**
     * x and y values for predefined Shapes in JSIM
     */ 

    /**
     * Server
     */
    public  static final int [] X_SERVER = {0, 40, 40, 40,  0,  0};
    public  static final int [] Y_SERVER = {5,  0, 20, 40, 35, 20};
    public  static final int SERVER_PTS = 6;
    
    /**
     * Facility
     */
    public  static final int [] X_FACILITY = {0, 70, 110, 110, 110, 70,  0,  0};
    public  static final int [] Y_FACILITY = {5,  5,   0,  20,  40, 35, 35, 20};
    public  static final int FACILITY_PTS = 8;
    
    /**
     * Signal
     */
    public  static final int [] X_SIGNAL = {0, 30, 30,  0};
    public  static final int [] Y_SIGNAL = {0,  0, 40, 40};
    public  static final int SIGNAL_PTS = 4;
    
    /**
     * Source
     */
    public  static final int [] X_SOURCE = {0, 30, 40, 30,  0};
    public  static final int [] Y_SOURCE = {0,  0, 20, 40, 40};
    public  static final int SOURCE_PTS = 5;
    
    /**
     * Sink
     */
    public  static final int [] X_SINK = {0, 40, 40,  0, 10};
    public  static final int [] Y_SINK = {0,  0, 40, 40, 20};
    public  static final int SINK_PTS = 5;
    

 /*******************************************************************
  * Constructor
  * @param id		The id of the new entity
  * @param shape	The shape of the new entity
  * @param transfrom	AffineTransform object associated with the entity
  * @param color	Color associated with the entity
  */

  public AnimationEntity (int id, Shape shape, AffineTransform transform, Color color) {
	
	this.id = id;
	this.shape = shape;
	this.transform = transform;
	this.color = color;
	this.label = "";
	this.numServiced = 0;
	this.queued = false;
  }//constructor
  
 /*******************************************************************
  * Constructor for Graph node
  * @param id		The id of the new entity
  * @param shape	The shape of the new entity
  * @param transfrom	AffineTransform object associated with the entity
  * @param color	Color associated with the entity
  * @param label	Label for the particular node
  */

  public AnimationEntity (int id, Shape shape, AffineTransform transform, Color color, String label) {
	
	this.id = id;
	this.shape = shape;
	this.transform = transform;
	this.color = color;
	this.label = label;
	this.numServiced = 0;
	this.queued = false;
  }//constructor
  
  /*******************************************************************
  * Constructor intended to be called by a derived JSIM example
  * When building initital graph
  * @param id		The id of the new Node
  * @param type 		Predefined type of Node (Server etc.)
  * @param name		Name for the Node
  * @param color		Color associated with the Node
  * @param x_position	x-coordinate for the Node
  * @param y_position	y-coordinate for the Node
  * @param numTokens	number of service tokens at the node
  */

  public AnimationEntity (int id, int type, String name, Point position, int numTokens) {
	
	this.id = id;
	this.label = name;
	this.numServiced = 0;
	this.transform = null;
	this.queued = false;
	this.numTokens = numTokens;
	this.showNumServed = true;

	/*******************
       * Build the Node
	 */
	  if (type == 0) 
	  {
		  this.shape = new Polygon (move (X_SERVER, position.getX()), move (Y_SERVER, position.getY()), SERVER_PTS);
		  this.color = SERVER_COLOR;
	  }	
	  else if (type == 1) 
	  {
		  this.shape = new Polygon (move (X_FACILITY, position.getX()), move (Y_FACILITY, position.getY()), FACILITY_PTS);
		  this.color = FACILITY_COLOR;
	  }
	  else if (type == 2) 
	  {
		  this.shape = new Polygon (move (X_SIGNAL, position.getX()), move (Y_SIGNAL, position.getY()), SIGNAL_PTS);
		  this.color = SIGNAL_COLOR;
	  }
	  else if (type == 3) 
	  { /* temporary 'type == 6' added for testing load */
		  this.shape = new Polygon (move (X_SOURCE, position.getX()), move (Y_SOURCE, position.getY()), SOURCE_PTS);
		  this.color = SOURCE_COLOR;
	  }
	  else if (type == 4) 
	  {
		  this.shape = new Polygon (move (X_SINK, position.getX()), move (Y_SINK, position.getY()), SINK_PTS);
		  this.color = SINK_COLOR;
	  }
	  else if (type == 6) 
	  {
		  this.shape = new Ellipse2D.Double (position.getX(), position.getY(), 20, 10);
		  this.color = SPLIT_COLOR;
		  this.showNumServed = false;
	  }
	  else if  (type == 7) 
	  {
		  this.shape = new Ellipse2D.Double (position.getX(), position.getY(), 20, 10);
		  this.color = JOIN_COLOR;
		  this.showNumServed = false;
	  }
		  //Default
	  else 
	  {
		  this.shape = new Rectangle2D.Double (0, 0, 50, 50);
		  this.color = Color.blue;
	  }
	 
  }//constructor
  
 /********************************************************************
  * Moves the x or y coordinates of a shape
  * @param coordinates 	coordinates to move
  * @param length 	integer value of the move
  * @return int[] array representing moved coordinates
  */
  
  public int[] move (int [] coordinates, double length) {
  	
  	int[] newCoordinates = new int[coordinates.length];
  
  	for (int i = 0; i < coordinates.length; i++) {
  		newCoordinates[i] = coordinates[i] + (int)length;
  	    };
  	    
  	return newCoordinates;
  }//move
  
 /*******************************************************************
  * Adds num to the value of numServiced
  * @param num		the number to add
  */
  public void adjustNumServiced (int num) {
  
  	numServiced = numServiced + num;
  } 


 }//AnimatorEntity
