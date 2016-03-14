/******************************************************************
 * @(#) NetCanvas.java     1.3     98/4/16
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
 * @version     1.3, 28 Jan 2005
 * @author      John Miller, Junxiu Tao, Greg Silver
 */

package jsim.jmodel;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.logging.*;

import jsim.util.*;


/******************************************************************
 * The NetCanvas class allows canvases to be created for drawing
 * simulation models.
 */

public class NetCanvas extends JPanel
                       implements MouseListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private static final int  MAX_NODES  = 100;
    private static final int  MAX_CLICKS = 3;

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Frame containing canvas.
     */
    private final Frame  inFrame;

    /**
     * Tracing messages.
     */
	private static Logger trc;
    // private final Trace  trc;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * View of screen.
     */
    private Point       view;

    /**
     * Image for use in double-buffering.
     */
    private Image       offscreenImg;

    /**
     * Graphics buffer for use in double-buffering.
     */
    private Graphics2D  offscreenG;

    /**
     * Type of action.
     */
    private int         actionType;

    /**
     * Name of model.
     */
    private String      modelName;

    /*********************** MODEL STATE *************************/
    /**
     * Number of nodes.
     */
    private int      numNodes;

    /**
     * Array of nodes.
     */
    private Node []  node;
 
    /******************* SAVE BETWEEN EVENTS *********************/
    /**
     * Node being moved.
     */
    private Node           moveNode;

    /**
     * Node number being moved.
     */
    private int            nMoveNode;

    /**
     * This node's original location.
     */
    private Point2D.Float  from;
 
    /**
     * Points defining an edge.
     */
    private Point2D.Float  edgePt [];

    /**
     * Number of mouse clicks in current action.
     */
    private int            clickCount;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 6323259977400321451L; 

    /*******************************************************************
     * Constructs a design network canvas.
     * @param  inFrame  containing frame
     */  
    public NetCanvas (JFrame inFrame)
    {
        super ();

        modelName = new String ("JModel");
		trc = Logger.getLogger(NetCanvas.class.getName() );
		Trace.setLogConfig ( trc );
        // trc       = new Trace  ("NetCanvas", modelName);

        this.inFrame = inFrame;
        edgePt       = new Point2D.Float [MAX_CLICKS];
        clickCount   = 0;
        view         = new Point (0, 0);
        actionType   = 0;

        numNodes     = 0;
        node         = new Node [MAX_NODES];

        // Put it in a scroll pane
        JScrollPane canvasPane = new JScrollPane ( 
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        canvasPane.setPreferredSize (new Dimension (600, 550));

        canvasPane.setViewportBorder (
                    BorderFactory.createLineBorder (Node.LABEL_COLOR));

        // Set the layout for the canvas pane
        setLayout (new BoxLayout (this, BoxLayout.X_AXIS));

        // Add a mouse listener to the canvas
        addMouseListener (this);

        // Add the canvas pane to the canvas
        // put this back in, GS. add (canvasPane);

        setBorder (BorderFactory.createEmptyBorder (5, 5, 5, 5));

    }; // NetCanvas


    /*******************************************************************
     * Set the model name.
     * @param  modelName  name of model
     */  
    public void setModelName (String modelName)
    {   
        this.modelName = modelName;
		trc.info ( "set modelName to " + this.modelName );
        // trc.show ("setModelName", "set modelName to " + this.modelName);

    }; // setModelName


    /*******************************************************************
     * Reset the model state.
     */  
    public void reset ()
    {
        for (int i = 0; i < numNodes; i++) {
            node [i] = null;                 // Release the memory of node i 
        }; // for

        numNodes = 0;

        Node.reset ();                       // Reset the static Node counters

        repaint ();

    }; // reset


    /*******************************************************************
     * Save all the nodes.
     * @param  saveFile  name of file the model is stored in
     */ 
    public void save (String saveFile)
    { 
		trc.info ( "model " + modelName + " in " + saveFile );
        // trc.show ("save", "model " + modelName + " in " + saveFile);
        try {
 
            FileOutputStream   fout = new FileOutputStream (NetFrame.currDir + NetFrame.SLASH + saveFile);
            ObjectOutputStream out  = new ObjectOutputStream (fout);
 
            out.writeInt (Node.getFacilityCount ());
            out.writeInt (Node.getServerCount ());
            out.writeInt (Node.getSourceCount ());
            out.writeInt (Node.getSinkCount ());
            out.writeInt (numNodes);
 
            for (int i = 0; i < numNodes; i++) {
                node [ i ].saveNode ( out );
            }; // for
 
            fout.close ();
 
        } catch (IOException e) {
        }; // try
 
    }; // save


    /*******************************************************************
     * Load all the nodes.
     * @param  loadFile  name of file the model is stored in
     */  
    public void load (String loadFile)
    {
        try {

            FileInputStream fin  = new FileInputStream (NetFrame.currDir + NetFrame.SLASH + loadFile);
            ObjectInputStream in = new ObjectInputStream (fin);

            Node.setFacilityCount (in.readInt ());
            Node.setServerCount   (in.readInt ());
            Node.setSourceCount   (in.readInt ());
            Node.setSinkCount     (in.readInt ());

            numNodes  = in.readInt ();

			trc.info ( "numNodes = " + numNodes );
            // trc.show ("load", "numNodes = " + numNodes);

            for (int i = 0; i < numNodes; i++) {
                node [i] = new Node ();
                node [i].loadNode (in);
		    }; // for

            in.close ();

        } catch (IOException e) {
			trc.warning ( "unable to open file " + loadFile + " " + e.getMessage ());
            // trc.tell ("load", "unable to open file " + loadFile +
            //           e.getMessage ());
        } catch (ClassNotFoundException e) {
			trc.warning ( "unable to access .class file for " + loadFile +
							e.getMessage ());
            // trc.tell ("load", "unable to access .class file for " + loadFile +
            //           e.getMessage ());
        }; // try

        repaint ();
 
    }; // load


    /**************************************************************
     * Don't erase the screen.
     * @param  gg  graphics buffer
     */ 
    public void update (Graphics gg)
    {  
        //paint (gg);
 
    }; // update
 

    /*******************************************************************
     * Paint the screen.
     * @param  gg  graphics buffer
     */  
    public void paint (Graphics gg)
    {
        offscreenImg = createImage (getSize ().width, getSize ().height);
        offscreenG   = (Graphics2D) offscreenImg.getGraphics ();

        //offscreenG.drawImage(background,0,0,this);

        /***************************************************************
         * Draw all of the nodes.
         */
        for (int i = 0; i < numNodes; i++) {

            Node              n       = node [i];
            int               nType   = n.nodeType;
            Point2D.Float     topLeft;
            Polygon           poly = null;
            Ellipse2D.Double  ovl  = null;
            if (n.nodeType < n.SPLIT) {
                 poly    = n.polyg;
                 topLeft = new Point2D.Float (poly.xpoints [0],
                                                            poly.ypoints [0]);
            }
            else {
                 ovl   = n.oval;
                 topLeft = new Point2D.Float ((float)ovl.getX(), (float)ovl.getY());
            }
            //Ellipse2D.Float token = new Ellipse2D.Float
            //         (poly.xpoints [1] - Node.OUT_DIAMETER,
            //          poly.ypoints [0], Node.TOK_DIAMETER, Node.TOK_DIAMETER);
 
            /***********************************************************
             * Draw the node itself
             */
                offscreenG.setColor (n.color);
                if (n.nodeType < n.SPLIT) {
                    offscreenG.fill (poly);
                }
                else {
                    offscreenG.fill (ovl);
                }               

            /***********************************************************
             * Draw the node's name
             */
            offscreenG.drawString (n.nodeName, (float) topLeft.x,
                                   topLeft.y - Node.TOK_DIAMETER);

            /***********************************************************
             * Draw the nodes's tokens (only for SERVER and FACILITY).
             */
            if (nType == Node.FACILITY || nType == Node.SERVER) {
                // Token definition
                int             x      = poly.xpoints[2] - Node.OUT_DIAMETER;
                int             y      = poly.ypoints[0];
                int             width  = Node.TOK_DIAMETER;
                int             height = Node.TOK_DIAMETER;
                Ellipse2D.Float token  = new Ellipse2D.Float (x, y, width, height);

                offscreenG.setColor (Node.TOKEN_COLOR);
                int count = Integer.parseInt (n.numTokens);
                for (int j = 0; j < count; j++) {
                    offscreenG.fill (token);
                    token.y += Node.OUT_DIAMETER;
                }; // for
            }; // if
 
            /***********************************************************
             * Draw the node's outgoing edges.
             */
            offscreenG.setColor (Node.TRANSPORT_COLOR);
            for (int j = 0; j < n.numOutEdges; j++) {
                QCurve e = n.outEdge [j];
                if (e != null) {
                    offscreenG.draw (e);
                    int midX = (int) ((e.x1 + e.ctrlx + e.x2) / 3.0);
                    int midY = (int) ((e.y1 + e.ctrly + e.y2) / 3.0);
					String trunc;
					if (nType == Node.SPLIT)
					{
						trunc = new String (" ");
					}
					else 
					{
						trunc = new String (n.outCondition [j] + " ");
					}
					if (trunc.length () < 4)
					{
						offscreenG.drawString (trunc, midX, midY - 6);                 
					}
					else
					{
						offscreenG.drawString (trunc.substring (0, 4), midX, midY - 6);                 

					} // if
                }; // if
            }; // for
        }; // for 
 
        gg.drawImage (offscreenImg, view.x, view.y, this);

    }; // paint
 
 
    /*******************************************************************
     * Set the action type.
     * @param  actionType  type of design action
     */ 
    public void setActionType (int actionType)
    {
        this.actionType = actionType;
		trc.info ( "set actionType to " + this.actionType );
        // trc.show ("setActionType", "set actionType to " + this.actionType);
 
    }; // setActionType
 
 
    /*******************************************************************
     * Add a new node to graph.  FIX
     * @param  location  location of node
     */ 
    public void addNode (Point2D.Float location)
    {
        Point nodeLoc = new Point ( (int) location.getX (), (int) location.getY () ); 
        node [numNodes++] = new Node ( actionType, nodeLoc );
 
    }; // addNode
 
 
    /*******************************************************************
     * Compute the distance (norm-1) between points p and q.
     * @param   p      first point
     * @param   q      second point
     * @return  float  distance between points
     */ 
    private float distance (Point2D.Float p, Point2D.Float q)
    {
        return Math.abs (p.x - q.x) + Math.abs (p.y - q.y);
 
    }; // distance


    /*******************************************************************
     * Adjust point p's location to correspond to the closest distinguished
     * point in the containing polygon (node).
     * @param   p     point to be adjusted
     * @return  Node  containing node
     */ 
    public Node adjust (Point2D.Float p)
    { 
        float         minDist = Float.MAX_VALUE;
        Point2D.Float saveQ   = null;
        int           saveI   =   -1;
 
        for (int i = 0; i < numNodes; i++) {
	    if (node [i].nodeType <= node [i].TRANSPORT) {
                Polygon poly = node [i].polyg;
                if (poly.contains (p)) {
                    for (int j = 0; j < poly.npoints; j++) {
                        Point2D.Float q =
                            new Point2D.Float (poly.xpoints [j], poly.ypoints [j]);
                        float dist = distance (p, q);
                        if (dist < minDist) {
                            minDist = dist;
                            saveQ   = q;
                            saveI   = i;
                        }; // if
                    }; // for
                 }; // if
	     } // if
	     else {
		 Ellipse2D.Double ovl = node [i].oval;
		 if (ovl.contains(p.getX(), p.getY())) {
		     saveQ = p;
		     saveI = i;
		 } // if
	     } // else
         }; // for
         
         if (saveQ != null) {
             p.x = saveQ.x;
             p.y = saveQ.y;
             return node [saveI];
         } else {
			 trc.warning ( "mouse must be in a node" );
             // trc.tell ("adjust", "mouse must be in a node");
             return null;
         } //
         
    } // adjust
 
 
    /*******************************************************************
     * Which node is point p contained in.
     * @param   p    reference point
     * @return  int  index of containing node
     */ 
    private int nodeAtN (Point2D.Float p)
    { 
        for (int i = 0; i < numNodes; i++) {
            Node n = node [i];
	    if (n.nodeType <= n.TRANSPORT) {
		if (n.polyg.contains (p)) {
                    return i;
            	}; // if
	    } 
	    else {
		if (n.oval.contains (p.getX(), p.getY())) {
		    return i;
		} // if
	    } // if
        }; // for
        return -1;
 
    }; // nodeAtN
 

    /*******************************************************************
     * Which node is point p contained in.
     * @param   p     reference point
     * @return  Node  containing node
     */ 
    private Node nodeAt (Point2D.Float p)
    { 
        for (int i = 0; i < numNodes; i++) {
            Node n = node [i];
	    if (n.nodeType <= n.TRANSPORT) {	
            	if (n.polyg.contains (p)) {
                    return n;
            	} // if 
            }
	    else {
		if (n.oval.contains (p.getX(), p.getY())) {
		    return n;
		} // if
	    } // if
        }; // for
        return null;
 
    }; // nodeAt
 
	/*******************************************************************
	 * Which transport is point p contained in.
	 * @param   p     reference point
	 * @return Transport containing node
	 */ 
	private QCurve qcurveAt (Point2D.Float p)
	{ 
		Point2D.Double tempP = new Point2D.Double ((double) p.getX (), (double) p.getY ());

		for (int i = 0; i < numNodes; i++) 
		{
			Node n = node [i];
			for (int j = 0; j < n.numOutEdges; j++)
			{	trc.info ("############# " + n.outEdge [j].contains (p));
				if (n.outEdge [j].contains (p))
				{
					trc.info ("############# contained in qcurve = true");
					return n.outEdge [j];
				}
				else 
				{
					
					if (n.outEdge [j].onQCurve (tempP))
					{
						return n.outEdge [j];
					}
				} // if

			} // for
		}; // for
		
		return null;
 
	}; // QCurveAt
 

    /*******************************************************************
     * Delete node n's incoming edges.
     * @param  n  reference node
     */ 
    public void deleteInEdges (Node n)
    {
        // delete outEdges same of the deleted node
        for (int j = 0; j < numNodes; j++) {
            if (node [j] != n) {
 
                Node m = node [j];
                int matches = 0;        // matches for node m
                for (int k = 0; k < m.numOutEdges; k++) {
                    for (int l = 0; l < n.numInEdges; l++) {
                        if (m.outEdge [k] == n.inEdge [l]) {
                            matches++;
                            m.outEdge [k] = null;
                        }; // if
                    }; // for
                }; // for
 
                if (matches > 0) {
                    m.numOutEdges -= matches;
                    for (int k = 0; k < m.numOutEdges; k++) {
                        int skipped = 0;
                        for (int i = k; m.outEdge [i] == null; i++) {
                            skipped++;           // skip nulls
                        }; // for
                        if (skipped > 0) {
                            m.outEdge [k]           = m.outEdge [k + skipped];
							m.outEdge [k].setEdgeIndex (k);
                            m.outEdge [k + skipped] = null;
                        }; // if
                    }; // for
                    setProbability (m);    // reset m's probabilities
                }; // if
 
            }; // if
        }; // for
 
        // delete inEdges same of the deleted node
        for (int j = 0; j < numNodes; j++) {
            if (node [j] != n) {
 
                Node m = node [j];
                int matches = 0;        // matches for node m
                for (int k = 0; k < m.numInEdges; k++) {
                    for (int l = 0; l < n.numOutEdges; l++) {
                        if (m.inEdge [k] == n.outEdge [l]) {
                            matches++;
                            m.inEdge [k] = null;
                        }; // if
                    }; // for
                }; // for
 
                if (matches > 0) {
                    m.numInEdges -= matches;
                    for (int k = 0; k < m.numInEdges; k++) {
                        int skipped = 0;
                        for (int i = k; m.inEdge [i] == null; i++) {
                            skipped++;           // skip nulls
                        }; // for
                        if (skipped > 0) {
                            m.inEdge [k]           = m.inEdge [k + skipped];
                            m.inEdge [k + skipped] = null;
                        }; // if
                    }; // for
 
                    setProbability (m);    // reset m's probabilities
                }; // if
 
            }; // if
        }; // for
 
    }; // deleteInEdges
 

    /*******************************************************************
     * Delete node n's incoming edges.
     * @param  p  reference curve
     */ 
    public void deleteInEdge (QCurve p)
    { 
        // delete inEdges same of the deleted edge
        for (int j = 0; j < numNodes; j++) {
                Node m = node [j];
                int matches = 0;        // matches for node m
                for (int k = 0; k < m.numInEdges; k++) {
                    if (m.inEdge [k] == p) {
                        m.inEdge [k] = null;
                        matches ++;
                    }; // if
                }; // for
 
                if (matches > 0) {
                    m.numInEdges -= matches;
                    for (int k = 0; k < m.numInEdges; k++) {
                        int skipped = 0;
                        for (int i = k; m.inEdge [i] == null; i++) {
                            skipped++;           // skip nulls
                        }; // for
                        if (skipped > 0) {
                            m.inEdge [k] = m.inEdge [k + skipped];
                            m.inEdge [k + skipped] = null;
                        }; // if
                    }; // for
                    setProbability (m);    // reset m's probabilities
                }; // if
        }; // for
 
    }; // deleteInEdge

 
    /*******************************************************************
     * Delete the node at location p.
     * @param  p  reference point
     */ 
    public void deleteNode (Point2D.Float p)
    { 
        boolean found = false;
	Shape shape;
        for (int i = 0; i < numNodes; i++) {
	    if (node [i].nodeType <= node [i].TRANSPORT) {
		shape = node [i].polyg;
	    } 
	    else {
		shape = node [i].oval;
	    } // if
            if ( ! found && shape.contains (p)) {
                found = true;
                deleteInEdges (node [i]);
            } else if (found) {
                node [i - 1] = node [i];     // fill deleted node
            }; // if
        }; // for
 
        if (found) {
            node [numNodes--] = null;
        } else {
            for (int i = 0; i < numNodes; i++) {
                for (int k = 0; k < node [i].numOutEdges; k++) {
                    if ( ! found && node [i].outEdge[k].contains (p)) {
                        found = true;
                        deleteInEdge (node [i].outEdge [k]);
                    } else if (found) {
                        // fill deleted node
                        node [i].outEdge [k - 1] = node [i].outEdge [k];
                    }; // if
                }; //for
                if (found) {
                    node[i].outEdge[node [i].numOutEdges--] = null;
                    setProbability (node [i]);    // reset m's probabilities
                    break;
                }; // if
            }; // for
 
        }; // if
 
    }; // deleteNode
 

    /*******************************************************************
     * Move node "moveNode" and its parts (tokens and incident edges).
     * @param  delta  movement vector
     */ 
    public void moveNodeAndParts (Point2D.Float delta)
    { 
        int i,num=-1;
        // move outgoing edges
		for (i = 0; i < moveNode.numOutEdges; i++)
		{
			// Move the starting point of the connecting edge
			if (moveNode.nodeType <= moveNode.TRANSPORT)
			{
				moveNode.outEdge [i].setFirst (delta);
			}
			else
				moveNode.outEdge [i].setFirstFixed 
					(new Point2D.Float(delta.x + from.x + (float) moveNode.oval.getWidth(), 
					delta.y + from.y + (float) (moveNode.oval.getHeight() * .5)));
			
			moveNode.outEdge [i].ReconstructQCurve ( moveNode.outEdge [i].getX1 (), 
													 moveNode.outEdge [i].getY1 (),
													 moveNode.outEdge [i].getCtrlX (),
													 moveNode.outEdge [i].getCtrlY (), 
													 moveNode.outEdge [i].getX2 (),
													 moveNode.outEdge [i].getY2 () );

		} // for

        // move incoming edges
		for (i = 0; i < moveNode.numInEdges; i++)
		{
			// Move the ending point of the connecting edge
			if (moveNode.nodeType <= moveNode.TRANSPORT) 		
			{
				moveNode.inEdge [i].setLast (delta);
			}
			else
				moveNode.inEdge [i].setLastFixed 
					(new Point2D.Float(delta.x + from.x, 
					delta.y + from.y + (float) (moveNode.oval.getHeight() * .5)));	

			moveNode.inEdge [i].ReconstructQCurve ( moveNode.inEdge [i].getX1 (), 
												    moveNode.inEdge [i].getY1 (),
													moveNode.inEdge [i].getCtrlX (),
													moveNode.inEdge [i].getCtrlY (), 
													moveNode.inEdge [i].getX2 (),
													moveNode.inEdge [i].getY2 () );
 
		} // for

        // move the node
	if (moveNode.nodeType <= moveNode.TRANSPORT) {
            moveNode.polyg.translate ( (int) delta.x, (int) delta.y);
            moveNode.location.x += delta.x;
            moveNode.location.y += delta.y;
	} // if
	else {
	    moveNode.oval.setFrame(delta.x + from.x, delta.y + from.y, 
		moveNode.oval.getWidth(), moveNode.oval.getHeight());
	    moveNode.location.x = (int) (from.x + delta.x);
        moveNode.location.y = (int) (from.y + delta.y);
	} // else
 
    }; // moveNodeAndParts
 
 
    /*******************************************************************
     * Set the probability of node n's outgoing edges.  Give them all
     * equal probability (may be changed by a dialog with designer).
     * @param  n  reference node
     */ 
    public void setProbability (Node n)
    { 
        String probability = String.valueOf (1.0 / n.numOutEdges);
        for (int i = 0; i < n.numOutEdges; i++) {
            n.outCondition [i] = probability;
			n.outEdge [i].setOutCondType (0);
        }; // for
 
    }; // setProbability
 
 
    /*******************************************************************
     * Pop up a dialog box allowing the designer to adjust a node's
     * parameters.
     * @param  location  location of reference node
     */ 
    public void adjustParameters (Point2D.Float location)
    { 
        Node onNode = nodeAt (location);
        if (onNode != null) {
            NodeDialog nDialog = new NodeDialog (inFrame, onNode);
            // nDialog.show (); the show method was deprecated as of jdk 1.5
			nDialog.setVisible(true);
        } else {
			QCurve onQCurve = qcurveAt (location);
			if (onQCurve != null)
			{
				TransportDialog tDialog = new TransportDialog (inFrame, onQCurve);
				tDialog.setVisible (true);
			}
			else 
			{
				trc.warning ( "must select a node or transport" );
				// trc.tell ("adjustParameters", "must select a node");
			} // if
        }; // if
 
        //repaint ();
 
    }; // adjustParameters
 

    /*******************************************************************
     * Add an edge.  This requires three points: start, control and end.
     * @param  location  location of reference node
     */ 
    public void addEdge (Point2D.Float location)
    { 
        Node    startNode = null;
        Node    endNode = null;
        QCurve  newEdge = null;
        int     index;
        boolean done = false;
 
        edgePt [clickCount] = location;
 
        if (clickCount == 0) {
 
            if ((index = nodeAtN (location)) < 0) {
				trc.warning ( "first click must be in a node" );
                // trc.tell ("addEdge", "first click must be in a node");
                clickCount = 0;
            } else {
                clickCount++;
            }; // if

        } else if (clickCount == MAX_CLICKS - 2) {

            if ((index = nodeAtN (location)) >= 0) {
                endNode   = adjust (edgePt [MAX_CLICKS - 2]);
                done = true;
            } else {
                clickCount++;
            }; // if
 
        } else if (clickCount == MAX_CLICKS - 1) {
 
            if ((index = nodeAtN (location)) >= 0) {
                endNode = adjust (edgePt [MAX_CLICKS - 1]);
                done    = true;
            } else {
				trc.warning ( "last click must be in a node" );
                // trc.tell ("addEdge", "last click must be in a node");
                clickCount = 0;
            }; // if

        } else {
			trc.warning ( "too many clicks" );
            // trc.tell ("addEdge", "too many clicks");
            clickCount = 0;
        }; // if
 
        if (done) {
            startNode = adjust (edgePt [0]);
            if (startNode == endNode) {
				trc.warning ( "start and end nodes must be different" );
                // trc.tell ("addEdge", "start and end nodes must be different");
                clickCount = 0;
            } else {
                if (clickCount == MAX_CLICKS - 2) {
                    newEdge = new QCurve (edgePt [0].x, edgePt [0].y,    // start
                                          edgePt [1].x, edgePt [1].y);   // end
                } else {
                    newEdge = new QCurve (edgePt [0].x, edgePt [0].y,    // start
                                          edgePt [1].x, edgePt [1].y,    // control
                                          edgePt [2].x, edgePt [2].y);   // end
                }; // if
				newEdge.setStartNode (startNode);
				newEdge.setEdgeIndex (startNode.numOutEdges);
                startNode.outEdge [startNode.numOutEdges++] = newEdge;
                endNode.inEdge    [endNode.numInEdges++]    = newEdge;
                setProbability (startNode);          // on all outgoing edges
                clickCount = 0;
            }; // if
        }; // if
 
            //for ( int i = 0; i < MAX_CLICKS; i++ )
            // Zero the edgePt array
              //edgePt[ i ] = 0;
  
    }; // addEdge
 

    /*******************************************************************
     * Handle mouse clicked event.
     * @param  evt  mouse clicked event
     */ 
    public void mouseClicked (MouseEvent evt)
    { 
        Point2D.Float location = new Point2D.Float (evt.getX (), evt.getY ()); 
        switch (actionType) {
 
        case Node.SERVER:
        case Node.FACILITY:
        case Node.SIGNAL:
        case Node.SOURCE:
        case Node.SINK:
        case Node.SPLIT:
        case Node.ANDJOIN:
            addNode (location);
            break;
 
        case Node.TRANSPORT:
            addEdge (location);
            break;
 
        case NetFrame.MOVE:
            // move item -- use mouse pressed & released
            break;
 
        case NetFrame.DELETE:
            deleteNode (location);
            break;
 
        case NetFrame.UPDATE:
            adjustParameters (location);
            break;
 
        case NetFrame.GENERATE:
			trc.info ( "generate code for " + modelName ); 
            // trc.show ("mouseClicked", "generate code for " + modelName); 
            Generator.emitCode (modelName, node, numNodes);
            break;
 
        default:
			trc.warning ( "illegal action " + actionType );
	     // trc.tell ("mouseClicked", "illegal action " + actionType);
        }; // switch
 
        repaint ();
 
    }; // mouseClicked
 

    /*******************************************************************
     * Handle mouse pressed event.
     * @param  evt  mouse pressed event
     */ 
    public void mousePressed (MouseEvent evt)
    { 
        int x = evt.getX ();
        int y = evt.getY ();
 
        if (actionType == NetFrame.MOVE) {
            from      = new Point2D.Float (x, y);
            nMoveNode = nodeAtN (from);
            moveNode  = nodeAt (from);
        }; // if
 
    }; // mousePressed
 

    /*******************************************************************
     * Handle mouse released event.
     * @param  evt  mouse released event
     */ 
    public void mouseReleased (MouseEvent evt)
    { 
        int x = evt.getX ();
        int y = evt.getY ();
 
        if (actionType == NetFrame.MOVE) {
            Point2D.Float delta = new Point2D.Float (x - from.x, y - from.y);
            moveNodeAndParts (delta);
        }; // if
 
        repaint ();
 
    }; // mouseReleased
 
 
    /*******************************************************************
     * The final 2 MouseListener event handlers are not implemented.
     */ 
    public void mouseEntered (MouseEvent evt) {};
      
    public void mouseExited (MouseEvent evt) {};
     

}; // class

