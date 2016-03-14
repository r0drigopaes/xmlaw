/******************************************************************
 * @(#) Node.java     1.3
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
 * @author      John Miller, Greg Silver
 */


package jsim.util;

import java.awt.*;
import java.io.*;
import java.awt.geom.*;
import java.util.logging.Logger;

/******************************************************************
 * The Node class allows nodes to be created.  It is a data structure
 * for model elements and is designed to be extensible.
 */

public class Node
{
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
     * Size specifications
     */
    private static final int MAX_DEGREE   = 50;
    public  static final int TOK_RADIUS   =  4;
    public  static final int TOK_DIAMETER =  2 * TOK_RADIUS;
    public  static final int OUT_DIAMETER = TOK_DIAMETER + 3;

    /**
     * Node and edge numbers
     */
    public  static final int SERVER    = 0;   // services entity requests
    public  static final int FACILITY  = 1;   // services entity requests
                                              //   and has an embedded queue
    public  static final int SIGNAL    = 2;   // signals servers
    public  static final int SOURCE    = 3;   // produces entities
    public  static final int SINK      = 4;   // consumes entities
    public  static final int TRANSPORT = 5;   // edge connecting 2 nodes
                                              //   (must be last)
//  public  static final int LOADSOURCE= 6;   // temporary type for testing load
    public  static final int SPLIT     = 6;   // temporary type for testing splits
    public  static final int ANDJOIN   = 7;   // temporary type for testing joins
	
    public  static final int NUM_SERVICE_ORIENTED = 2;

    public  static final String [] TYPE_NAME = { "Server",
                                                 "Facility",
                                                 "Signal",
                                                 "Source",
                                                 "Sink",
                                                 "Transport",
//												 "LoadSource", /* temporary for testing loadsource */
                                                 "Split",      /* Temporary for testing splits */
                                                 "AndJoin" 

};

    public  static final String [] TYPE_NAME_CAP = { "SERVER",
                                                     "FACILITY",
                                                     "SIGNAL",
                                                     "SOURCE",
                                                     "SINK",
                                                     "TRANSPORT",
//					  						         "LOADSOURCE", /* temporary for testing loadsource */
                                                     "SPLIT",      /* Temporary for testing splits*/
                                                     "ANDJOIN" 
 };

    /**
     * Server
     */
    public  static final int [] X_SERVER = {0, 40, 40, 40,  0,  0};
    public  static final int [] Y_SERVER = {5,  0, 20, 40, 35, 20};
    public  static final int    N_SERVER = X_SERVER.length;
    public  static final Point  T_SERVER =
                     new Point (X_SERVER [2] - OUT_DIAMETER, Y_SERVER [0]);
 
    /**
     * Facility
     */
    public  static final int [] X_FACILITY = {0, 70, 110, 110, 110, 70,  0,  0};
    public  static final int [] Y_FACILITY = {5,  5,   0,  20,  40, 35, 35, 20};
    public  static final int    N_FACILITY = X_FACILITY.length;
    public  static final Point  T_FACILITY =
                     new Point (X_FACILITY [2] - OUT_DIAMETER, Y_FACILITY [0]);
    public  static final Point  Q_FACILITY =
                     new Point (X_FACILITY [1], Y_FACILITY [3]);

    /**
     * Signal
     */
    public  static final int [] X_SIGNAL = {0, 30, 30,  0};
    public  static final int [] Y_SIGNAL = {0,  0, 40, 40};
    public  static final int    N_SIGNAL = X_SIGNAL.length;
 
    /**
     * Source
     */
    public  static final int [] X_SOURCE = {0, 30, 40, 30,  0};
    public  static final int [] Y_SOURCE = {0,  0, 20, 40, 40};
    public  static final int    N_SOURCE = X_SOURCE.length;
 
    /**
     * Sink
     */
    public  static final int [] X_SINK = {0, 40, 40,  0, 10};
    public  static final int [] Y_SINK = {0,  0, 40, 40, 20};
    public  static final int    N_SINK = X_SINK.length;

    /**
     * Split
     */
    public  static final int WIDTH_SPLIT = 20;
    public  static final int HEIGHT_SPLIT = 10;


    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Tracing Messages
     */
	protected static Logger trc = Logger.getLogger(Node.class.getName() );

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /*********************** NODE STATE **************************/

    // --- Intrinsic Properties
    /**
     * Type of node (SERVER .. SINK)
     */
    public  int        nodeType;

    /**
     * Position of node (coordinates of top left)
     */
    public  Point      location;

    /**
     * Color of node
     */
    public  Color      color;

    // --- Adjustable Properties
    /**
     * Name of node
     */
    public  String     nodeName;

    /**
     * Initial number of tokens (service units)
     */
    public  String     numTokens;

    /**
     * Probability distribution
     */
    public  String     distribution;

    /**
     * Scale parameter (e.g., mean)
     */
    public  String     alpha;

    /**
     * Shape parameter (e.g., variance)
     */
    public  String     beta;

    /**
     * Random number stream
     */
    public  String     stream;


	/**
	 * Probability distribution for cost
	 */
	public  String     costDistribution;

	/**
	 * Scale parameter for cost
	 */
	public  String     costAlpha;

	/**
	 * Shape parameter (e.g., variance) for cost
	 */
	public  String     costBeta;

	/**
	 * Random number stream for cost
	 */
	public  String     costStream;


    /**
     * Type of queue (one of FIFO, LIFO, Priority, Temporal)
     * FIX: also used to indicate Server controlled by Signal
     */
    public  String     queueType;

    /*-----------------------------------------------------------*/
    // --- Outgoing Edges (Transports)
    /**
     * Number of outgoing edges
     */
    public  int        numOutEdges;

    /**
     * Array of outgoing edges
     */
    public  QCurve []  outEdge   = new QCurve [MAX_DEGREE];

    /**
     * Probability of taking each outgoing edge
     */
    public  String []  outCondition   = new String [MAX_DEGREE];

    // --- Incoming Edges (Transports)  [not needed for animation]
    /**
     * Number of incoming edges
     */
    public  int        numInEdges;

	/**
	 * Cost of using the resource represented by the Node.
	 */
	// public	String	   cost;

    /**
     * Array of incoming edges
     */
    public  QCurve []  inEdge    = new QCurve [MAX_DEGREE];

    /*************************************************************/

    // --- Derived quantity
    /**
     * Complete coordinates of node 
     */
    public  Polygon     polyg;

    /**
     * Split or Join node object
     */
    public  Ellipse2D.Double oval;

    // --- Counters used to generate names
    /**
     * Counter for servers
     */
    private static int serverCount   = 0;

    /**
     * Counter for facilities
     */
    private static int facilityCount = 0;

    /**
     * Counter for signals
     */
    private static int signalCount   = 0;

    /**
     * Counter for sources
     */
    private static int sourceCount   = 0;

    /**
     * Counter for sinks
     */
    private static int sinkCount     = 0;

    /**
     * Counter for splits
     */
    private static int splitCount    = 0;

    /**
     * Counter for joins
     */
    private static int joinCount    = 0;


    /******************************************************************
     * Construct an empty node (e.g., before loading).
     * @param  props  common node properties
     */
    public Node ()
    {
    }; // Node


    /******************************************************************
     * Construct a node based on properties.
     */
    public Node (Prop props)
    {
        nodeType     = props.nType;
        nodeName     = props.nName;
        numTokens    = String.valueOf (props.nTokens);
        location     = props.location;

        distribution = props.timeDist.getClass ().getName ();

        Double [] distParams = props.timeDist.getParameters ();
        int       numParams  = distParams.length;

		Trace.setLogConfig ( trc );

        switch (numParams) {

        case 1: alpha  = "NA";
                beta   = "NA";
                stream = distParams [0].toString ();
                break;

        case 2: alpha  = distParams [0].toString ();
                beta   = "NA";
                stream = distParams [1].toString ();
                break;

        case 3: alpha  = distParams [0].toString ();
                beta   = distParams [1].toString ();
                stream = distParams [2].toString ();
                break;

        default:
				trc.warning ( "incorrect number of parameter for distribution" );
                // trc.tell ("Node", "incorrect number of parameter for distribution");

        }; // switch

		costDistribution = props.costDist.getClass ().getName ();

		Double [] costDistParams = props.costDist.getParameters ();
		int       costNumParams  = costDistParams.length;

		switch (costNumParams) 
		{

			case 1: costAlpha  = "NA";
					costBeta   = "NA";
					costStream = costDistParams [0].toString ();
					break;

			case 2: costAlpha  = costDistParams [0].toString ();
					costBeta   = "NA";
					costStream = costDistParams [1].toString ();
					break;

			case 3: costAlpha  = costDistParams [0].toString ();
					costBeta   = costDistParams [1].toString ();
					costStream = costDistParams [2].toString ();
					break;

			default:
				trc.warning ( "incorrect number of parameter for distribution" );
				// trc.tell ("Node", "incorrect number of parameter for distribution");

		}; // switch
        
        queueType    = "?";

        numOutEdges   = 0;
        numInEdges    = 0;
 
        unpack ();    // expand based on nodeType and location
		
    }; // Node


    /******************************************************************
     * Construct and initialize a node using defaults.
     * @param  nodeType  type of node
     * @param  location  location (top left) of node
     */
    public Node (int nodeType, Point location)
    {
        this.nodeType = nodeType;
        this.location = location;

        numOutEdges   = 0;
        numInEdges    = 0;
		// cost	      = "0";

        unpack ();    // expand based on nodeType and location

        /******************************************************************
         * Assign default values to adjustable parameters.
         */
        switch (nodeType) {

        case SERVER:
            nodeName     = new String ("Server" + serverCount++);
            numTokens    = "1";
            distribution = "Uniform";
            alpha        = "2000.0";
            beta         = "1000.0";
            stream       = "0";
			costDistribution = "Uniform";
			costAlpha    = "0.0";
			costBeta     = "0.0";
			costStream       = "0";
            queueType    = "NA";
            break;

        case FACILITY:
            nodeName     = new String ("Facility" + facilityCount++);
            numTokens    = "1";
            distribution = "Uniform";
            alpha        = "2000.0";
            beta         = "1000.0";
            stream       = "0";
			costDistribution = "Uniform";
			costAlpha    = "0.0";
			costBeta     = "0.0";
			costStream   = "0";
			queueType	 = "LinkedList<Coroutine>";
//			queueType    = "FIFO";
            break;

        case SIGNAL:
            nodeName     = new String ("Signal" + serverCount++);
            numTokens    = "10";
            distribution = "Uniform";
            alpha        = "2000.0";
            beta         = "1000.0";
            stream       = "0";
			costDistribution = "Uniform";
			costAlpha    = "0.0";
			costBeta     = "0.0";
			costStream   = "0";
            queueType    = "Facility0";              // server to control
            break;

        case SOURCE:
            nodeName     = new String ("Source" + sourceCount++);
            numTokens    = "500";
            distribution = "Uniform";
            alpha        = "2000.0";
            beta         = "1000.0";
            stream       = "0";
			costDistribution = "Uniform";
			costAlpha    = "0.0";
			costBeta     = "0.0";
			costStream   = "0";
            queueType    = "NA";
            break;

	// Temporary code to test load source
	/*
        case LOADSOURCE:
            nodeName     = new String ("Source" + sourceCount++);
            numTokens    = "500";
            distribution = "Uniform";
            alpha        = "2000.0";
            beta         = "1000.0";
            stream       = "0";
			costDistribution = "Uniform";
			costAlpha    = "0.0";
			costBeta     = "0.0";
			costStream   = "0";
            queueType    = "NA";
            break;
	*/	
        case SINK:
            nodeName     = new String ("Sink" + sinkCount++);
            numTokens    = "0";
            distribution = "Bernoulli";
            alpha        = "0.1";
            beta         = "0";
            stream       = "0";
			costDistribution = "Uniform";
			costAlpha    = "0.0";
			costBeta     = "0.0";
			costStream   = "0";
            queueType    = "NA";
            break;

        case SPLIT:
            nodeName     = new String ("Split" + splitCount++);
            numTokens    = "0";
            distribution = "Uniform";
            alpha        = "2000.0";
            beta         = "1000.0";
            stream       = "0";
			costDistribution = "Uniform";
			costAlpha    = "0.0";
			costBeta     = "0.0";
			costStream   = "0";
            queueType    = "NA";
            break;

        case ANDJOIN:
            nodeName     = new String ("Join" + joinCount++);
            numTokens    = "0";
            distribution = "Uniform";
            alpha        = "2000.0";
            beta         = "1000.0";
            stream       = "0";
			costDistribution = "Uniform";
			costAlpha    = "0.0";
			costBeta     = "0.0";
			costStream   = "0";
            queueType    = "NA";
            break;

        default:
			trc.warning ( "illegal node type " + nodeType );
            // trc.tell ("Node", "illegal node type " + nodeType);
            System.exit (-1);

        }; // switch

    }; // Node


    /******************************************************************
     * Unpack the node: polyg and color are derivable from nodeType and
     * location.
     */
	public void unpack ()
    {
        switch (nodeType) {

        case SERVER:
            polyg = new Polygon (X_SERVER, Y_SERVER, N_SERVER);
            color = SERVER_COLOR;
            polyg.translate (location.x, location.y);
            break;

        case FACILITY:
            polyg = new Polygon (X_FACILITY, Y_FACILITY, N_FACILITY);
            color = FACILITY_COLOR;
            polyg.translate (location.x, location.y);
            break;

        case SIGNAL:
            polyg = new Polygon (X_SIGNAL, Y_SIGNAL, N_SIGNAL);
            color = SIGNAL_COLOR;
            polyg.translate (location.x, location.y);
            break;

        case SOURCE:
            polyg = new Polygon (X_SOURCE, Y_SOURCE, N_SOURCE);
            color = SOURCE_COLOR;
            polyg.translate (location.x, location.y);
            break;

        case SINK:
            polyg = new Polygon (X_SINK, Y_SINK, N_SINK);
            color = SINK_COLOR;
            polyg.translate (location.x, location.y);
            break;
        
        case SPLIT:
            oval = new Ellipse2D.Double (location.x, location.y, 20, 10);  
	    color = SPLIT_COLOR;          
            break;

        case ANDJOIN:
            oval = new Ellipse2D.Double (location.x, location.y, 20, 10);            
	    color = JOIN_COLOR;
            break;
		
        default:
			trc.warning ( "illegal node type " + nodeType );
            // trc.tell ("unpack", "illegal node type " + nodeType);
            System.exit (-1);

        }; // switch


    }; // unpack

    /******************************************************************
     * Reset the counters.
     */
    public static void reset ()
    {
        serverCount   = 0;
        facilityCount = 0;
        signalCount   = 0;
        sourceCount   = 0;
        sinkCount     = 0;

    }; // reset


    /******************************************************************
     * Set methods for the counters (e.g., setServerCount for servers).
     */
    public static void setServerCount   (int c) { serverCount   = c; };
    public static void setFacilityCount (int c) { facilityCount = c; };
    public static void setSignalCount   (int c) { signalCount   = c; };
    public static void setSourceCount   (int c) { sourceCount   = c; };
    public static void setSinkCount     (int c) { sinkCount     = c; };


    /******************************************************************
     * Get methods for the counters (e.g., getServerCount for servers).
     */
    public static int  getServerCount   () { return serverCount;   };
    public static int  getFacilityCount () { return facilityCount; };
    public static int  getSignalCount   () { return signalCount;   };
    public static int  getSourceCount   () { return sourceCount;   };
    public static int  getSinkCount     () { return sinkCount;     };


    /******************************************************************
     * Save this node's state.
     * @param   out          output stream
     * @throws  IOException  can't write output
     */
    public void saveNode (ObjectOutputStream out) throws IOException
    {
        // Intrinsic Properties
        out.writeInt    (nodeType);
        out.writeObject (location);
        out.writeObject (color);
 
        // Adjustable Properties
        out.writeObject (nodeName);
        out.writeObject (numTokens);        // run-time adjustable
        out.writeObject (distribution);     // run-time adjustable
        out.writeObject (alpha);            // run-time adjustable
        out.writeObject (beta);             // run-time adjustable
        out.writeObject (stream);           // run-time adjustable
		out.writeObject (costDistribution);     // run-time adjustable
		out.writeObject (costAlpha);            // run-time adjustable
		out.writeObject (costBeta);             // run-time adjustable
		out.writeObject (costStream);           // run-time adjustable
        out.writeObject (queueType);
		// out.writeObject (cost);				// run-time adjustable

        /****New Code****/
        // Outgoing Edges
		
        out.writeInt (numOutEdges);
        for (int i = 0; i < numOutEdges; i++) {
            out.writeDouble ( (double) outEdge [i].getFirst ().getX () );
            out.writeDouble ( (double) outEdge [i].getFirst ().getY () );
            out.writeDouble ( (double) outEdge [i].getControl ().getX () );
            out.writeDouble ( (double) outEdge [i].getControl ().getY () );
            out.writeDouble ( (double) outEdge [i].getLast ().getX () );
            out.writeDouble ( (double) outEdge [i].getLast ().getY () );
			Node tempN = outEdge [i].getStartNode ();
			outEdge [i].setStartNode (null);
			out.writeObject (outEdge [i]);
			outEdge [i].setStartNode (tempN);
        }; // for

        for (int i = 0; i < numOutEdges; i++)  out.writeObject (outCondition [i]);
 
        // Incoming Edges
        out.writeInt (numInEdges);

        for (int i = 0; i < numInEdges; i++) {
			out.writeDouble ( (double) inEdge [i].getFirst ().getX () );
            out.writeDouble ( (double) inEdge [i].getFirst ().getY () );
            out.writeDouble ( (double) inEdge [i].getControl ().getX () );
            out.writeDouble ( (double) inEdge [i].getControl ().getY () );
            out.writeDouble ( (double) inEdge [i].getLast ().getX () );
            out.writeDouble ( (double) inEdge [i].getLast ().getY () );

			try
			{
				out.writeObject (inEdge [i]);
			}
			catch (Exception e) 
			{
				trc.warning ( "InEdge write problem " + e );
			}
	
        }; // for
		
        /****Old Code****/
        // Outgoing Edges
        // out.writeInt    (numOutEdges);
        // for (int i = 0; i < numOutEdges; i++)  out.writeObject (outEdge [i]);
        // for (int i = 0; i < numOutEdges; i++)  out.writeObject (outCondition [i]);

        // Incoming Edges
        // out.writeInt    (numInEdges);
        // for (int i = 0; i < numInEdges; i++)   out.writeObject (inEdge [i]);
 
        out.flush ();

    }; // saveNode


    /******************************************************************
     * Load this node's state.
     * @param   in           input stream
     * @throws  IOException  can't read input
     * @throws  IOException  can't find class
     */  
    public void loadNode (ObjectInputStream in) throws IOException,
                                                       ClassNotFoundException
    {
        // Intrinsic Properties
        nodeType     = in.readInt ();

        location     = (Point)  in.readObject ();
        color        = (Color)  in.readObject ();

        // Adjustable Properties
        nodeName     = (String) in.readObject ();
        numTokens    = (String) in.readObject ();
        distribution = (String) in.readObject ();
        alpha        = (String) in.readObject ();
        beta         = (String) in.readObject ();
        stream       = (String) in.readObject ();
		costDistribution = (String) in.readObject ();
		costAlpha        = (String) in.readObject ();
		costBeta         = (String) in.readObject ();
		costStream       = (String) in.readObject ();
        queueType    = (String) in.readObject ();
		// cost		 = (String) in.readObject ();
	

        /****New Code****/
        // Outgoing Edges
		
        numOutEdges  = in.readInt ();
        for (int i = 0; i < numOutEdges; i++) {
            double x1    = in.readDouble ();
            double y1    = in.readDouble ();
            double ctrlx = in.readDouble ();
            double ctrly = in.readDouble ();
            double x2    = in.readDouble ();
            double y2    = in.readDouble ();
		
			outEdge [i] = (QCurve) in.readObject ();
			outEdge [i].setStartNode (this);
		
			outEdge [i].ReconstructQCurve ( x1, y1, ctrlx, ctrly, x2, y2 );
			
            // outEdge [i]  = new QCurve ( x1, y1, ctrlx, ctrly, x2, y2 );
        }; // for
		
	
        for (int i = 0; i < numOutEdges; i++)  outCondition [i] = (String) in.readObject ();

        // Incoming Edges
        numInEdges = in.readInt ();
		
        for (int i = 0; i < numInEdges; i++) {
			
            double x1    = in.readDouble ();
            double y1    = in.readDouble ();
            double ctrlx = in.readDouble ();
            double ctrly = in.readDouble ();
            double x2    = in.readDouble ();
            double y2    = in.readDouble ();
	
			inEdge [i]  = (QCurve) in.readObject ();
		
			inEdge [i].ReconstructQCurve ( x1, y1, ctrlx, ctrly, x2, y2 );
	
            // inEdge [i]   = new QCurve ( x1, y1, ctrlx, ctrly, x2, y2 );
        }; // for
		
        /****Old Code****/
        // Outgoing Edges
        // numOutEdges  = in.readInt ();
        // for (int i = 0; i < numOutEdges; i++)  outEdge [i] = (QCurve) in.readObject ();
        // for (int i = 0; i < numOutEdges; i++)  outCondition [i] = (String) in.readObject ();

		// -------- Debug outedge read -----------------			 
		//for (int i = 0; i < numOutEdges; i++) 
		//{
		//	System.out.println ( "******* Out Edge Read: " + outEdge [i] );
		//	System.out.println ( "******* Coordinates: " + outEdge [i].getFirst () );
		//}
        
		// Incoming Edges
        // numInEdges = in.readInt ();
        // for (int i = 0; i < numInEdges; i++)   inEdge [i]  = (QCurve) in.readObject ();
		
        unpack ();
		
    }; // loadNode


}; // class

