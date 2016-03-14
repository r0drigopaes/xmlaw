
/******************************************************************
 * @(#) ModelCanvas.java     1.1
 *
 * Copyright (c) 1998 John Miller, Rajesh Nair
 * All Right Reserved
 *-----------------------------------------------------------------
 * Permission to use, copy, modify and distribute this software and
 * its documentation without fee is hereby granted provided that
 * this copyright notice appears in all copies.
 * WE MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WE SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY ANY USER AS A RESULT OFUSING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *-----------------------------------------------------------------
 *
 * @version     1.1, 6 Nov 1998
 * @author      John Miller, Rajesh Nair
 */

package jsim.process;

import java.awt.*;
import java.io.*;
import java.util.*;

import jsim.coroutine.*;
import jsim.util.*;


/******************************************************************
 * The ModelCanvas class allows canvases embedded in model frames to
 * be created.  A ModelCanvas object is used for animating a model.
 */

public class ModelCanvas extends Canvas
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Environment for canvas, i.e., the containing model/frame.
     */
    private final Model  env;

    /**
     * Tracing Messages
     */
    private final Trace  trc;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * View of screen determined by scrollbars.
     */
    private Point       view;

    /**
     * Off screen image.
     */
    private Image       offscreenImg;

    /**
     * Off screen graphics buffer.
     */
    private Graphics2D  offscreenG;

    /**
     * Width of screen
     */
    private int         screenWidth;

    /**
     * Height of screen
     */
    private int         screenHeight;

    /******************** STATIC MODEL STATE *********************/
    /**
     * Model name.
     */
    private String   mName;

    /**
     * Number of nodes.
     */
    private int      numNodes;
 
    /**
     * Array of dynamic nodes.
     */
    private DynamicNode []  dynNode;

    /**
     * Array of nodes.
     */
    private Node []  node;


    /**************************************************************
     * Construct a model canvas.
     * @param  mName      name of model
     * @param  env        environment (containing model)
     * @param  dynNode    model's dynamic nodes
     */
    public ModelCanvas (String         mName,
                        Model          env,
                        DynamicNode [] dynNode)
    {
        super ();

        trc = new Trace ("ModelCanvas", mName);

        this.mName   = mName;
        this.env     = env;
        this.dynNode = dynNode;

        view = new Point (0, 0);
        offscreenImg = null;
        offscreenG   = null;
        screenWidth  = 0;
        screenHeight = 0;


        initNodes (dynNode);

        //load (mName + ".jm");

    }; // ModelCanvas


    /**************************************************************
     * Reset the model state.
     */
    public void reset ()
    {
        for (int i = 0; i < numNodes; i++) {
            node [i] = null;    // for memory reclamation
        }; // for
        numNodes = 0;
        Node.reset ();   // reset Node counters

    }; // reset


    /**************************************************************
     * Set the view of the canvas based on position of scrollbars.
     * @param  newView  point indicating view of screen
     */
    public void setView (Point newView)
    {
        view = newView;

    }; // setView


    /**************************************************************
     * Add a node to the set.  Must include all drawing information.
     * @param  nType     type of node (SERVER .. SINK)
     * @param  name      name of the node
     * @param  location  position of node
     * @param  outgoing  outgoing edges (transports)
     */
    private void addNode (Prop         props,
                          Transport [] outgoing,
                          int          j)
    {
        Node n = new Node (props);

        if (outgoing != null) {
            n.numOutEdges = outgoing.length;
            n.outEdge     = new QCurve [n.numOutEdges];
            for (int i = 0; i < n.numOutEdges; i++) {
                n.outEdge [i] = outgoing [i].getEdge ();
            }; // for
        } else {
            n.numOutEdges = 0;
            n.outEdge     = null;
        }; // if

        n.unpack ();

        node [j] = n;

    }; // addNode


    /**************************************************************
     * Initialize all the nodes to set up display.
     * @param  nodeArray  array of node extended properties
     */
    private void initNodes (DynamicNode [] dynNode)
    {
        numNodes = dynNode.length;
        node     = new Node [numNodes];

        for (int j = 0; j < numNodes; j++) {
            addNode (dynNode [j].getProps (), dynNode [j].getOutgoing (), j);
        }; // for

    }; // initNodes


    /**************************************************************
     * Load all the nodes from saveFile.
     * @param  saveFile  file storing serialization of model
     */
    public void load (String saveFile)
    {
        try {

            FileInputStream   fin = new FileInputStream (saveFile);
            ObjectInputStream in  = new ObjectInputStream (fin);

            mName    = (String) in.readObject ();
            numNodes = in.readInt ();

            trc.show ("load", "numNodes = " + numNodes);

            for (int i = 0; i < numNodes; i++) {
                Node n = new Node ();
                n.loadNode (in);
                node [i] = n;
            }; // for

            in.close ();

        } catch (IOException e) {
            trc.tell ("load", "unable to open file " + saveFile);
        } catch (ClassNotFoundException e) {
            trc.tell ("load", "unable to access .class file for " +
                       saveFile);
        }; // try

        repaint ();

    }; // load


    /*******************************************************************
     * Create an off-screen drawable image to be used for double buffering.
     */  
    private void makeOffscreen ()
    {
        screenWidth  = getSize ().width;
        screenHeight = getSize ().height;
        offscreenImg = createImage (screenWidth, screenHeight);
        offscreenG   = (Graphics2D) offscreenImg.getGraphics ();
        offscreenG.setFont (new Font ("TimesRoman", Font.BOLD, 16));

    }; // makeOffscreen


    /**************************************************************
     * Paint the screen.
     * @param  gg  graphics buffer
     */
    public void paint (Graphics gg)
    {
        if (offscreenImg == null) {
            makeOffscreen ();
        }; // if

        offscreenG.clearRect (0, 0, screenWidth, screenHeight);

        //trc.show ("paint", "view = ( " + view.x + " , " + view.y + " )",
        //           Coroutine.getTime ());

        /**********************************************************
         * Draw clock displaying number of seconds since start.
         */
        offscreenG.setColor (Node.LABEL_COLOR);
        int    dispTime = (int) (Coroutine.getTime () / 1000.0);
        String timeStr  = new String ("Time: " + dispTime);
        offscreenG.drawString (timeStr, screenWidth - 100, 25);

        /**********************************************************
         * Draw the current number of lost entities.
         */
        //int    lost    = env.getLostEntities ();
        int    lost    = SimObject.getLostEntities ();
        String lostStr = new String ("Lost:  " + lost);
        offscreenG.drawString (lostStr, screenWidth - 100, 50);

        /**********************************************************
         * Draw all of the nodes (STATIC PART).
         */
        for (int i = 0; i < numNodes; i++) {

            Node    n       = node [i];
            int     nType   = n.nodeType;
            Polygon poly    = n.polyg;
            Point   topLeft = new Point (poly.xpoints [0], poly.ypoints [0]);

            // Draw the node itself and its name
            offscreenG.setColor    (n.color);
            offscreenG.fillPolygon (poly);
            offscreenG.drawString (n.nodeName, topLeft.x,
                                   topLeft.y - Node.TOK_DIAMETER);

            // Draw the nodes's dynamic characteristics
            paintDynamic (n, i);

            // Draw the node's outgoing edges.
            //offscreenG.setColor
            //    (new Color (Color.white.getRGB () ^ BG_COLOR.getRGB ()));
            offscreenG.setColor (Node.TRANSPORT_COLOR);
            for (int j = 0; j < n.numOutEdges; j++) {
                QCurve e = n.outEdge [j];
                if (e != null) {
                    offscreenG.draw (e);
                }; // if
            }; // for

        }; // for

        /**********************************************************
         * Draw live entities moving through the graph (DYNAMIC PART)
         */
	synchronized (env.liveEntity) {
        for (Iterator it = env.liveEntity.listIterator (); it.hasNext (); ) {

            try {

                SimObject entity = (SimObject) it.next ();
                Point position   = entity.getIntPosition ();
                offscreenG.setColor (entity.eColor);
                offscreenG.fillOval (position.x, position.y,
                                     Node.TOK_DIAMETER, Node.TOK_DIAMETER);

            } catch (ConcurrentModificationException ex) {
                trc.tell ("paint", "env.liveEntity comodified -> skip");
                //break;
            }; // try

        }; // for
	}
        

        //trc.show ("paint", "view = ( " + view.x + " , " + view.y + " )",
        //           Coroutine.getTime ());
        gg.drawImage (offscreenImg, view.x, view.y, this);

    }; // paint


    /**************************************************************
     * Paint the dynamic portions of node n.
     * These include tokens, queue length indicators, entity counters
     * @param  n  current node
     * @param  i  index of current node
     */
    private void paintDynamic (Node n, int i)
    {
        DynamicNode dn      = dynNode [i];
        Point       token   = new Point ();
        int         qLength = 0;
        int         tCount  = 0;
        int         eCount  = 0;

        switch (n.nodeType) {

        case Node.SERVER:
            token.x = dn.props.location.x + Node.T_SERVER.x;
            token.y = dn.props.location.y + Node.T_SERVER.y;
            tCount  = ((Server) dn).tokenV.getNumTokens ();
            break;

        case Node.FACILITY:
            token.x = dn.props.location.x + Node.T_FACILITY.x;
            token.y = dn.props.location.y + Node.T_FACILITY.y;
            tCount  = ((Facility) dn).tokenV.getNumTokens ();
            qLength = ((Facility) dn).queueLength ();
            break;

        case Node.SOURCE:
            token.x = dn.props.location.x + Node.TOK_RADIUS;
            token.y = dn.props.location.y + 4 * Node.TOK_RADIUS;
            eCount  = ((Source) dn).getEntitiesCreated ();
            break;

        case Node.SINK:
            token.x = dn.props.location.x + 2 * Node.TOK_RADIUS;
            token.y = dn.props.location.y + 4 * Node.TOK_RADIUS;
            eCount  = ((Sink) dn).getEntitiesConsumed ();
            break;

        }; // switch

        /**********************************************************
         * Draw entity counters for sources and sinks
         */
        if (eCount > 0) {
            offscreenG.setColor (Node.LABEL_COLOR);
            String countStr = new String ("" + eCount);
            offscreenG.drawString (countStr, token.x, token.y);
        }; // if

        /**********************************************************
         * Draw tokens (service units)
         */
        offscreenG.setColor (Node.TOKEN_COLOR);
        for (int j = 0; j < tCount; j++) {
            offscreenG.fillOval (token.x, token.y, Node.TOK_DIAMETER,
                                                   Node.TOK_DIAMETER);
            token.y += Node.OUT_DIAMETER;
        }; // for

        /**********************************************************
         * Draw the occupied portion of queue
         */
        if (qLength > 0) {
            token.x = dn.props.location.x + Node.Q_FACILITY.x;
            token.y = dn.props.location.y + Node.Q_FACILITY.y;
            offscreenG.setColor (Node.QUEUE_COLOR);
            int width  = 3 * qLength;
            int xStart = token.x - width;
            offscreenG.fillRect (xStart, token.y - Node.TOK_DIAMETER,
                                 width,  2 * Node.TOK_DIAMETER);
        }; // if

    }; // paintDynamic


    /**************************************************************
     * Don't erase the screen.
     * @param  gg  graphics buffer
     */
    public void update (Graphics gg)
    {
        paint (gg);

    }; // update


}; // class

