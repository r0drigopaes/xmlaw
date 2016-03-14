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
 * file: AnimatorImp.java
 * @version 1.3, 22 July 2002
 * @Author John Miller and Matt Perry
 */

package jsim.animator;

import java.lang.*;
import java.util.*;
import java.util.logging.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

import jsim.util.*;
import jsim.process.*;


/*********************************************************************
 * This class implements the Animation front end for the JSIM Simulation
 * package.  It uses a shared data Structure to receive AnimatorMessages
 * from the Simulator and then repaints the Animation to reflect these
 * changes.
 */

public class AnimationImp extends Thread implements Animator {

  private HashMap<Integer, AnimationEntity> entitySet;		//set of all Items to be painted in the Display
  private Graph initialGraph;		//initial Graph for the SImulation
  private AnimationQueue messageQueue;  //Shared data structure for communication with Simulation
  private double time;			//Simulation time
  private int numLost = 0;		//number of Lost entities
  private AffineTransform defaultTransform;//AffineTransform Object for drawing the time and Graph
  private boolean start;		//Boolean to control start of Animation
  private boolean end;			//Boolean to control end of Animation
  private AnimationFrame af;		//AnimationFrame for this animation

  private int DELAY = 200;			//delay between instructions in ms
  private int CHANGE = 65;			//change amount when adjusting delay
  private Color  BACK_COLOR = Color.white;      //Background color for animation
  private int TOK_DIAMETER = 8;		//diameter of a token for a facility
  private int TOK_OFFSET = 11;		//vertical distance between tokens
  private Model env = null;		//reference to creating Model
  
  protected static Logger trc = Logger.getLogger(AnimationImp.class.getName() );
  // private final Trace trace = new Trace ("AnimationImp", "AnimationImp");  //trace for showing information
  									   //to user

 /******************************************************************
  * Constructor Initializes all dataMembers
  * @param g		initial Graph for animation
  * @param q		Animation queue for communication with Simulator
  */
  public AnimationImp (Graph g, AnimationQueue q) {
	
	this.initialGraph = g;
	this.messageQueue = q;
	entitySet = new HashMap<Integer, AnimationEntity> ();
	defaultTransform = new AffineTransform();
	start = false;
	end = false;
	Trace.setLogConfig ( trc );

  }//Constructor 
  
 /******************************************************************
  * Constructor Initializes all dataMembers
  * @param g		initial Graph for animation
  * @param q		Animation queue for communication with Simulator
  * @param env		creating Model object
  */
  public AnimationImp (Graph g, AnimationQueue q, Model env) {
	
	this.initialGraph = g;
	this.messageQueue = q;
	entitySet = new HashMap<Integer, AnimationEntity> ();
	defaultTransform = new AffineTransform();
	start = false;
	end = false;
	this.env = env;
	Trace.setLogConfig ( trc );
    
  }//Constructor 




 /**************************************************************
  * Adds a Shape at given location
  * Shapes: Line2D, QuadCurve2D, CubicCurve2D, Rectangle2D, RoundRectangle2D,
  * 	    Arc2D, Ellipse2D
  * Qparam shapeID	id for the new Shape
  * @param shape	Shape of the object
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
		     double arcW, double arcH, double x2, double y2, int type, double time) {
	Shape newShape = null;

	if (shape.toUpperCase().equals ("ARC2D")) {
		newShape = new Arc2D.Double (x, y, w, h, arcW, arcH, type);
   	    }
	else if (shape.toUpperCase().equals ("LINE2D")) {
		newShape = new Line2D.Double (x, y, w, h);
	    }
	else if (shape.toUpperCase().equals ("QUADCURVE2D")) {
		newShape = new QuadCurve2D.Double (x, y, w, h, arcW, arcH);
	    }
	else if (shape.toUpperCase().equals ("CUBICCURVE2D")) {
		newShape = new CubicCurve2D.Double (x, y, w, h, arcW, arcH, x2, y2);
	    }
	else if (shape.toUpperCase().equals ("RECTANGLE2D")) {
		newShape = new Rectangle2D.Double (x, y, w, h);
	    }
	else if (shape.toUpperCase().equals ("ROUNDRECTANGLE2D")) {
		newShape = new RoundRectangle2D.Double (x, y, w, h, arcW, arcH);
 	    }
	else if (shape.toUpperCase().equals ("ELLIPSE2D")) {
		newShape = new Ellipse2D.Double (x, y, w, h);
	    }

	if (newShape != null) {
		AnimationEntity ae = new AnimationEntity (shapeID, newShape, new AffineTransform(), Color.black);
		Integer id = new Integer(shapeID);
		entitySet.put (new Integer(id.hashCode()), ae);
	   }

	this.time = time;

  }//create()
	
 /****************************************************************
  * Destroys a Shape based on ShapeID
  * @param shapeID	id of the Shape to destroy
  * @param time		SImulation time
  */	
  public void destroy (int shapeID, double time) {
	
	entitySet.remove (new Integer(shapeID));
	this.time = time;
  }//destroy()
  
 /*****************************************************************
  * Increments the number of lost entities
  */
  public void incrementLost() {
  	
  	numLost = numLost + 1;
  }
  	
 /*****************************************************************
  * Translates a Shape to a new (x,y) location
  * @param shapeID	id of Shape to translate
  * @param tx		new x-coordinate
  * @param ty		new y-coordinate
  * @param time		Simulation time
  */	
  public void translate (int shapeID, double tx, double ty, double time) {

	Integer id = new Integer (shapeID);
	AnimationEntity ae = entitySet.get (new Integer(id.hashCode()));

	ae.transform.translate (tx, ty);

	this.time = time;

  }//translate()
	
 /******************************************************************
  * Rotates a shape
  * @param shapeID	id of Shape to tanslate
  * @param theta	angle of rotation
  * @param x		new x-coordinate
  * @param y		new y-coordinate
  * @param time		Simulation time
  */	
  public void rotate (int shapeID, double theta, double x, double y, double time) {
	
	Integer id = new Integer (shapeID);
	AnimationEntity ae = entitySet.get (new Integer(id.hashCode()));

	ae.transform.rotate (theta, x, y);
	
	this.time = time;
  }//rotate()
	
 /*******************************************************************
  * Scales a Shape
  * @param shapeID	id of Shape to scale
  * @param sx		scaled x-coordinate
  * @param sy		scaled y-coordiante
  * @param time		simulation time
  */
  public void scale (int shapeID, double sx, double sy, double time) {
	
	Integer id = new Integer (shapeID);
	AnimationEntity ae = entitySet.get (new Integer(id.hashCode()));

	ae.transform.scale (sx, sy);	

	this.time = time;
  }//scale()
	
 /******************************************************************
  * Shears a Shape
  * @param shapeID	id of Shape to shear
  * @param shx		scaled x-coordinate
  * @param shy		scaled y-cooordinate
  * @param time		Simulation time
  */	
  public void shear (int shapeID, double shx, double shy, double time) {

	Integer id = new Integer (shapeID);
	AnimationEntity ae = entitySet.get (new Integer(id.hashCode()));

	ae.transform.shear (shx, shy);

	this.time = time;
  }//shear()
	
 /***************************************************************
  * Paint Method for solid color, uses 1 Color argument
  * @param shapeID	id for Shape to paint
  * @param c		Color for the Shape
  * @param time		Simulation time
  */	
  public void setPaint (int shapeID, Color c, double time) {
	
	Integer id = new Integer (shapeID);
	AnimationEntity ae = entitySet.get (new Integer(id.hashCode()));

	ae.color = c;
	
	this.time = time;
  }//setPaint()
	
 /***************************************************************
  * Paint Method for gradient color, uses 2 Color arguments
  * @param shapeID	id for SHape to paint
  * @param c1		1st Color for Shape
  * @param c2		2nd Color for Shape
  * @param time		Simulation time
  */
  public void setPaint (int shapeID, Color c1, Color c2, double time) {

	//to be determined	

	this.time = time;
  }//setPaint()

 /****************************************************************
  * Moves an entity from a starting node to an ending node along
  * a given edge
  * @param shapeID	id for Shape to move
  * @param edgeID	id for edge to move along
  * @param endNodeID	id of end node 
  * @param time		Simulation time
  */
  public void move (int shapeID, int edgeID, int endNodeID, double time) {

	Integer id = new Integer (shapeID);
	AnimationEntity ae = entitySet.get (new Integer(id.hashCode()));

        Edge e = initialGraph.getEdge (edgeID);

	int numMoves = (int)(e.length/initialGraph.step);

	if (e.node2 == endNodeID) {
		setToLocation (shapeID, e.line.getX1(), e.line.getY1());

		af.repaint();
		try {
			sleep (DELAY);
		} catch (Exception ex) {ex.printStackTrace();}

		for (int i = 0; i < numMoves; i++) {
			translate (shapeID, e.x_increment, e.y_increment, time);
			af.repaint();
			try {
				sleep (DELAY);
			} catch (Exception ex) {ex.printStackTrace();}
			
                };
		setToLocation (shapeID, e.line.getX2(), e.line.getY2());
		af.repaint();
            }//if
	else {
		setToLocation (shapeID, e.line.getX2(), e.line.getY2());

		af.repaint();
		try {
			sleep (DELAY);
		} catch (Exception ex) {ex.printStackTrace();}

		for (int i = 0; i < numMoves; i++) {
			translate (shapeID, (-1*e.x_increment), (-1*e.y_increment), time);
			af.repaint();
			try {
				sleep (DELAY);
			} catch (Exception ex) {ex.printStackTrace();}
			
                };
		setToLocation (shapeID, e.line.getX1(), e.line.getY1());
		af.repaint();
	    }//else
  }

 /*****************************************************************
  * Moves an entity to the specified location
  * @param shapeID		id of shape to move
  * @param x_location		new x-coordinate
  * @param y_location		new y-coordinate
  */
  public void setToLocation (int shapeID, double x_location, double y_location) {
	
	Integer id = new Integer (shapeID);
	AnimationEntity ae = entitySet.get (new Integer(id.hashCode()));

	ae.transform = new AffineTransform(defaultTransform);
	
	Rectangle bounds = ae.shape.getBounds();
	double current_x = bounds.getX() + (bounds.getWidth()/2);
	double current_y = bounds.getY() + (bounds.getHeight()/2);

	double x_move = x_location - current_x;
	double y_move = y_location - current_y;

	ae.transform.translate (x_move, y_move);

  }	
  

 /*****************************************************************
  * Begins the Animation by setting start to true
  */

  public void beginSim() {

	start = true;
  }//beginSim()

 /******************************************************************
  * Ends the Animation by setting end to true
  */

  public void endSim() {

	end = true;
  }//endSim()

 /******************************************************************
  * Controls the speed of the Animation
  * @param value the value used to adjust speed
  */

  public void changeSpeed (int value) {

	if (!((value < 0) && (DELAY < CHANGE))) {
		DELAY = DELAY + (value*CHANGE);
        }
  
  }//changeSpeed
  
 /******************************************************************
  * Enqueues an entity
  * @param shapeID	id of the entity
  * @param time		time in simulation
  */
  
  public void enqueueEntity (int shapeID, double time) {
  
  	Integer id = new Integer (shapeID);
	AnimationEntity ae = entitySet.get (new Integer(id.hashCode()));

	ae.queued = true;
	
	this.time = time;
  }//enqueueEntity
  
 /******************************************************************
  * Dequeues an entity
  * @param shapeID	id of the entity
  * @param time		time in simulation
  */
  
  public void dequeueEntity (int shapeID, double time) {
  
  	Integer id = new Integer (shapeID);
	AnimationEntity ae = entitySet.get (new Integer(id.hashCode()));

	ae.queued = false;
	
	this.time = time;
  }//enqueueEntity
  
 /******************************************************************
  * Gets an instruction from the shared queue and performs the 
  * instruction
  */

  public void executeInstruction () {

	//System.out.println("here");

	AnimationMessage newMessage = messageQueue.dequeue();
	trc.info ( "executeInstruction " + newMessage.toString());
	// trace.show("executeInstruction", newMessage.toString());
	Object [] parameters = newMessage.getParameters();
	int id = newMessage.getId();
	double time = newMessage.getTime();
	String operation = newMessage.getOperation().toUpperCase();

	if (operation.equals ("CREATE")) {
		create (id, (String)parameters[0], (Label)parameters[1],
			((Double)parameters[2]).doubleValue(),
			((Double)parameters[3]).doubleValue(),
			((Double)parameters[4]).doubleValue(),
			((Double)parameters[5]).doubleValue(),
			((Double)parameters[6]).doubleValue(),
			((Double)parameters[7]).doubleValue(),
			((Double)parameters[8]).doubleValue(),
			((Double)parameters[9]).doubleValue(),
			((Integer)parameters[10]).intValue(),
			time );
		if (parameters.length == 12) {
			setPaint (id, (Color)parameters[11], time);
		   }
		else {
			setPaint (id, (Color)parameters[11], (Color)parameters[12], time);
		   }	
		
	   }
	else if (operation.equals ("DESTROY")) {
		destroy (id, time);
	   }
	else if (operation.equals ("TRANSLATE")) {
		translate (id, ((Double)parameters[0]).doubleValue(),
				((Double)parameters[1]).doubleValue(), time);
	   }
	else if (operation.equals ("ROTATE")) {
		rotate (id, ((Double)parameters[0]).doubleValue(),
			((Double)parameters[1]).doubleValue(), 
			((Double)parameters[2]).doubleValue(), time); 
	   }
	else if (operation.equals ("SCALE")) {
		scale (id, ((Double)parameters[0]).doubleValue(), 
			((Double)parameters[1]).doubleValue(), time);
	   }
	else if (operation.equals ("SHEAR")) {
		shear (id, ((Double)parameters[0]).doubleValue(), 
			((Double)parameters[1]).doubleValue(), time);
	   }
	else if (operation.equals ("SETPAINT")) {
		if (parameters.length == 1) {
			setPaint (id, (Color)parameters[0], time);
		   }
		else {
			setPaint (id, (Color)parameters[0], (Color)parameters[1], time);
		   }
	   }
	else if (operation.equals ("MOVE")) {
		move (id, ((Integer)parameters[0]).intValue(),
		      ((Integer)parameters[1]).intValue(), time);
           }
        else if (operation.equals ("ADJUSTSERVICED")) {
        	AnimationEntity ae = initialGraph.getNode(id);
        	ae.adjustNumServiced (((Integer)parameters[0]).intValue());
        	this.time = time;
             }
    	else if (operation.equals ("ADJUSTTOKENS")) {
        	AnimationEntity ae = initialGraph.getNode(id);
        	int value = ae.numTokens;
        	ae.numTokens = ae.numTokens + ((Integer)parameters[0]).intValue();
        	if (value == ae.numTokens) {
        		System.out.println ("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        	   }
        	this.time = time;
             }
        else if (operation.equals ("ENQUEUE")) {
        	enqueueEntity (id, time);
             }
        else if (operation.equals ("DEQUEUE")) {
        	dequeueEntity (id, time);
             }
        else if (operation.equals ("INCLOST")) {
        	incrementLost();
             }
	else {
		trc.info ( "executeInstruction ERROR");
        // trace.show("executeInstruction", "ERROR");
	}			
  }//executeInstruction


 /*******************************************************************
  * run()
  * Responsible for the main control loop of the Animation
  * Creates the Frame and Panel, then sleeps and repaints until
  * the animation is through
  */

  public void run() {
  
	af = new AnimationFrame(this);
	AnimationPanel ap = new AnimationPanel();
	ap.setBackground (BACK_COLOR);
	af.getContentPane().add(ap);

	//initialGraph.setEdges();

	af.setVisible (true);
	af.repaint();
	
	while (!start) {
		try {
			sleep (250);
		} catch (Exception ex) {ex.printStackTrace();}
	}//while

	while (!end) {
		executeInstruction();
		
		try {
			sleep (DELAY);
		} catch (Exception ex) {ex.printStackTrace();}
		af.repaint();
	}//while

  }//run

  /*******************************************************************
   * Frame class for holding the components of the Animation
   */
   public class AnimationFrame extends JFrame implements AdjustmentListener,
   							 WindowListener {

    private AnimationImp ai = null;

    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Maximum number of nodes in simulation
     */
    //        static final int      MAX_NODES  = 100;

    /**
     * Top x coordinate for frame
     */
    private static final int      F_TOP_X    =  20;

    /**
     * Top y coordinate for frame
     */
    private static final int      F_TOP_Y    =  40;

    /**
     * Width of frame
     */
    private static final int      F_WIDTH    = 980;

    /**
     * Height of frame
     */
    private static final int      F_HEIGHT   = 600;

    /**
     * Increment for scrollbar
     */
    private static final int      F_BAR_INCR =   5;

    /**
     * Time for display thread to sleep
     * Too small => monopolize CPU (FIX)
     * Too big   => jumpy animation  
     */
    private static final int      TIME_DELAY =  100;

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Horizontal scrollbar.
     */
    private final Scrollbar    horbar;

    /**
     * Vertical scrollbar.
     */
    private final Scrollbar    verbar;

	/**
	* Unique identifier of the serialized object
	*/
	private static final long serialVersionUID = 4374612948306420199L;

    /******************************************************************
     * Constructor for AnimationFrame
     * @param ai	creating AnimationImp instance
     */
     public AnimationFrame (AnimationImp ai) {

	this.ai = ai;

        /*************************************************************
         * Menu bar at the top of frame.
         */
         MenuBar menuBar      = new MenuBar ();          // top menu bar
         Menu    jsimControls = new AnimationMenu (this.ai, this.ai.env);    // JSIM control menu
         menuBar.add (jsimControls);

         /*************************************************************
          * Scrollbars at right and bottom.
          */
         horbar = new Scrollbar (Scrollbar.HORIZONTAL, 0, 100, 0, F_WIDTH);
         verbar = new Scrollbar (Scrollbar.VERTICAL,   0, 100, 0, F_HEIGHT);
 
         horbar.setUnitIncrement  (F_BAR_INCR);
         verbar.setUnitIncrement  (F_BAR_INCR);
         horbar.setBlockIncrement (5 * F_BAR_INCR);
         verbar.setBlockIncrement (5 * F_BAR_INCR);
         //horbar.addAdjustmentListener (this);
         //verbar.addAdjustmentListener (this);

         /*************************************************************
          * Initialize properties of model frame.
          */
         setMenuBar    (menuBar);
         setForeground (Color.black);
         setBackground (Color.white);
         setSize       (F_WIDTH, F_HEIGHT);
         setLocation   (F_TOP_X, F_TOP_Y);

         //setPrimaryStat ();   // FIX - later

	//show();
      
    }//constructor
    
  /*****************************************************************
   * Handle event by adjusting the view of the design canvas based
   * on position of scrollbars.
   * @param  evt  scrollbar adjustment event
   */  
   public void adjustmentValueChanged (AdjustmentEvent evt)
    {
        //canvas.setView (new java.awt.Point (horbar.getValue (), verbar.getValue ()));
 
       // canvas.repaint ();
 
    } // adjustmentValueChanged
 
 
  /*****************************************************************
   * Handle window closing event by exiting/disposing of window.
   * Use of exit () under the BeanBox will cause the whole BeanBox
   * to exit.
   * @param  evt  window closing event
   */  
   public void windowClosing (WindowEvent evt)
    {
        //System.exit (0);
        dispose (); 
 
    } // windowClosing
    
   /*****************************************************************
    * The rest of the WindowEvent handlers are not implemented.
    */  
    public void windowClosed (WindowEvent evt) {};
 
    public void windowDeiconified (WindowEvent evt) {};
 
    public void windowIconified (WindowEvent evt) {};
 
    public void windowActivated (WindowEvent evt) {};

    public void windowDeactivated (WindowEvent evt) {};

    public void windowOpened (WindowEvent evt) {};
 

  }//Class AnimationFrame

 /***********************************************************************
  * Panel Class for holding Animation components
  */
  public class AnimationPanel extends JPanel {

   /**
	* Unique identifier of the serialized object
	*/
    private static final long serialVersionUID = 7658473847211209083L;

   /***********************************************************************
    * Paints the Animation
    * @param gr		Graphics object used to paint the Animation
    */
    public void paintComponent (Graphics gr) {
	super.paintComponent(gr);
	Graphics2D g2d = (Graphics2D)gr;
	Font myFont = new Font("SansSarif", Font.BOLD, 14);
	g2d.setFont (myFont);	

	/*********************************************
         * Paint the Graph
         */
	g2d.setTransform (defaultTransform);
	g2d.setColor (Color.black);

	ListIterator it = initialGraph.edges.listIterator();
	Edge e = null;
	while (it.hasNext()) {
		e = (Edge)it.next();
		g2d.draw (e.line);
		//g2d.drawString (e.label, (float)e.label_x, (float)e.label_y);
              }

	it = initialGraph.nodes.listIterator();
	AnimationEntity ae = null;
	while (it.hasNext()) {
		ae = (AnimationEntity)it.next();
		g2d.setColor (ae.color);
		g2d.fill (ae.shape);
              }
              
         it = initialGraph.nodes.listIterator();         
         while (it.hasNext()) {
         	ae = (AnimationEntity)it.next();
		g2d.setColor (ae.color);
         	g2d.drawString (ae.label, (float)ae.label_x, (float)ae.label_y);
         	g2d.setColor (Color.black);
         	String servicedString = new String (""+ae.numServiced);
                if (ae.showNumServed)
         	   g2d.drawString (servicedString, (float)ae.serviced_x, (float)ae.serviced_y);
         }
         
         it = initialGraph.nodes.listIterator();         
         while (it.hasNext()) {
         	ae = (AnimationEntity)it.next();
		g2d.setColor (ae.TOKEN_COLOR);
		
		int y_loc = 0;
		
		for (int i = 0; i < ae.numTokens; i++) {
			y_loc = ae.tokStart_y;
			y_loc = y_loc + (i*TOK_OFFSET); 
	 		g2d.fillOval (ae.tokStart_x, y_loc, TOK_DIAMETER, TOK_DIAMETER);
	 	   };
         }
         

	/**********************************************
         * Paint all other entities
         */
	Object o = null;
	
	Iterator it2 = entitySet.values().iterator();

	try {
		while (it2.hasNext()) {
			o = it2.next();
			ae = (AnimationEntity)o;
			if (!ae.queued) {
				g2d.setColor(ae.color);
				g2d.setTransform(ae.transform);
				g2d.fill(ae.shape);
			   }//if
		}
	} catch (Exception ex) { System.out.println(o.getClass().toString());} 
	
	g2d.setTransform (defaultTransform);	
	g2d.setColor (DeColores.black);
	String theTime = "Time: " + ((int)time/1000);
	String theLost = "Lost: " + numLost;
	g2d.drawString (theTime, 10, 15);
	g2d.drawString (theLost, 12, 35);
    }//paintComponenet

  }//AnimationPanel



 /***********************************************************************
  * Main Function used for testing purposes
  */

  public static void main (String[] args) {

  }


 

}//AnimatorImp
	
