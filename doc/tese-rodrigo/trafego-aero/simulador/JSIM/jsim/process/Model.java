/******************************************************************
 * @(#) Model.java     1.3
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
 * @author      John Miller, Matt Perry, Greg Silver
 */

package jsim.process;

import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import java.util.logging.Logger;

import jsim.coroutine.*;
import jsim.statistic.*;
import jsim.util.*;
import jsim.jmessage.*;
import jsim.animator.*;

/******************************************************************
 * The Model class allows application specific simulation
 * models to be derived from it.  It generalizes all models.
 * Simulation models run in their own frame and multiple frames
 * (models) can be active simultaneously.
 * 
 */

public class Model extends Frame
                   implements Runnable,
                              WindowListener,
                              AdjustmentListener
{
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

    /**
     * Debug flag
     */
    private static final boolean  DEBUG      = true;  

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * 
     */
    private final String       mName;

    /**
     * 
     */
    //private final ModelBean    mBean;

    /**
     * Model animation canvas.
     */
    //private ModelCanvas  canvas;

    /**
     * Horizontal scrollbar.
     */
    private final Scrollbar    horbar;

    /**
     * Vertical scrollbar.
     */
    private final Scrollbar    verbar;

    /**
     * Thread to display animation of simulation.
     */
    private final Thread       displayThread;

    /**
     * Tracing messages
     */
    //private final Trace        trc;
	private static Logger trc;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * JSIM system property: use_xml
     */
    private boolean            use_xml = false;

    /**
     * JSIM system property: generate_xml_files
     */
    private boolean            generate_xml_files = false;

    /**
     * Maximum time until sources are shut down (for safety).
     */
    protected double  stopTime;

    /**
     * Start simulation flag.
     */
    private boolean   startSim;

    /**
     * Stop simulation flag.
     */
    private boolean   stopSim;

    /**
     * Primary statistic to observe for stopping rule.
     */
    private String    primaryStat = "None";

    /******************** DYNAMIC MODEL STATE ********************/
    /**
     * Actual number of nodes
     */
    private int  numNodes = 0;

    /**
     * Put every node in set as constructed (see DynamicNode)
     */
    DynamicNode []  dynNode;

    /**
     * Put every live entity in set
     */
    final LinkedList<Coroutine>  liveEntity = new LinkedList<Coroutine> ();

    /**
     * Put nodes waiting on an AND join in map
     */
    HashMap<Integer, Coroutine> joinEntity = new HashMap<Integer, Coroutine> ();

    /**
     * Put nodes waiting that are lost in map
     */
    HashMap<Integer, Coroutine> lostJoinEntity = new HashMap<Integer, Coroutine> ();

	/**
	 * Unique identifier of the serialized object
	 */
	private static final long serialVersionUID = 8574837282718746123L;


    /*****************************************************************
     * Construct a JSIM simulation model to run in an embedded frame.
     * The model will use a virtual time clock at full speed.
     * @param  mlName     name of the simulation model
     * @param  mBean      bean holding the model
     */
    public Model (String     mName, Prop[] pList, Transport[] tList, AnimationQueue sharedQueue)      //ModelBean  mBean)
    {
        this (mName, 0.0);  //mBean, 0.0);
		trc = Logger.getLogger(Model.class.getName());
		Trace.setLogConfig ( trc );
		trc.info("constructor started " + Coroutine.getTime ());

	//////////////////// Create and Start an AnimationImp instance \\\\\\\\\\\\\\\\\\\\\\\\
	
	Prop currProp = null;    //Prop instance during each iteration
	Transport currTrans = null;	//Transport instance during each iteration
	QuadCurve2D.Double currEdge = null;	//edge for each iteration
	LinkedList<AnimationEntity> nodes = new LinkedList<AnimationEntity> ();	//nodes of the graph
	LinkedList<Edge> edges = new LinkedList<Edge> ();	//edges of the graph
	
	
	for (int i = 0; i < pList.length; i++) {
		currProp = pList[i];
		if (currProp.nType == Node.SERVER || currProp.nType == Node.FACILITY) {
			nodes.add (new AnimationEntity (i, currProp.nType, currProp.nName, currProp.location, currProp.nTokens));
		    }
		    
		 else {
		 	nodes.add (new AnimationEntity (i, currProp.nType, currProp.nName, currProp.location, 0));
		    }
	    };//end for
	    
	for (int i = 0; i < tList.length; i++) {
		currTrans = tList[i];	
		
		currEdge = currTrans.getEdge();
		Point2D.Double point1 = (Point2D.Double)currEdge.getP1();
		Point2D.Double point2 = (Point2D.Double)currEdge.getP2();
		edges.add (new Edge (point1.getX(), point1.getY(), 
		           currEdge.getCtrlX(), currEdge.getCtrlY(), point2.getX(), 
		           point2.getY()));
	    
	    }; //end for
	    
	/**
         * Create the Graph and set the Edges
	 */
	Graph g = new Graph (nodes, edges);
	g.setEdges();
		
	/**
	 * Start the Animator
	 */
	AnimationImp a = new AnimationImp (g, sharedQueue, this);
	a.start();

    }; // Model


    /*****************************************************************
     * Construct a JSIM simulation model to run in an embedded frame.
     * The model will use a virtual time clock at full speed, unless
     * slowness is positive (> 0); then the model's execution will be
     * slowed down for better animation.
     * @param  mName      name of the simulation model
     * @param  mBean      bean holding the model
     * @param  slowness   factor to slow down animation
     */
    public Model (String     mName,
                  double     slowness)    //ModelBean  mBean,
    {
        super (mName);
		
	    trc = Logger.getLogger(Model.class.getName());
		Trace.setLogConfig ( trc );
		trc.info("constructor started " + Coroutine.getTime ());
        //trc.show ("Model", "constructor started", Coroutine.getTime ());

	String jsimprop = System.getProperty ("jsim.use_xml", "false");
	if (jsimprop.toLowerCase ().equals ("true")) {
	    use_xml = true;
	} // if	

	jsimprop = System.getProperty ("jsim.generate_xml_files", "false");
	if (jsimprop.toLowerCase ().equals ("true")) {
            generate_xml_files = true;
        } // if

	trc.info("jsim system property: use_xml = " + use_xml);
	trc.info("jsim system property: generate_xml_files = " +
				generate_xml_files);
	// trc.show ("Model", "jsim system property: use_xml = " + use_xml);
	// trc.show ("Model", "jsim system property: generate_xml_files = " +
	//			generate_xml_files);

        this.mName = mName;
//	this.mBean = mBean;
//        this.mBean = null;           // FIX:  = mBean;

       // if (slowness > 0.0)  Coroutine.changeSpeed (slowness);

        stopTime = 600000000.0;      // run for at most 10000 minutes
        startSim = false;
        stopSim  = false;

        /*************************************************************
         * Menu bar at the top of frame.
         */
        MenuBar menuBar      = new MenuBar ();          // top menu bar
        Menu    jsimControls = new ModelMenu (this);    // JSIM control menu
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
        horbar.addAdjustmentListener (this);
        verbar.addAdjustmentListener (this);

        /*************************************************************
         * Initialize properties of model frame.
         */
        setMenuBar    (menuBar);
        setForeground (Node.FORE_COLOR);
        setBackground (Node.BACK_COLOR);
        setSize       (F_WIDTH, F_HEIGHT);
        setLocation   (F_TOP_X, F_TOP_Y);

        setPrimaryStat ();   // FIX - later

        /*************************************************************
         * Initialize the display thread.  Start this thread by calling
         * the start method from derived class (e.g., Bank).
         */
        displayThread = new Thread (this, "display");

    }; // Model


    /*****************************************************************
     * Finish initializing the mode.  Create a canvas to diplay animation
     * of model in the center.  Put scrollbars to East and South.
     * @param  dynNodes  nodes to display on canvas
     */
    public void initModel (DynamicNode [] dynNode)
    {
        numNodes     = dynNode.length;
        this.dynNode = dynNode;

		trc.info("create model canvas with " + numNodes + " nodes");
        //trc.show ("Model", "create model canvas with " + numNodes + " nodes");

        //canvas = new ModelCanvas (mName, this, dynNode);

        /*************************************************************
         * Layout components.
         */
        //add ("Center", canvas);
        add ("East",   verbar);
        add ("South",  horbar);

        addWindowListener (this);
		trc.info("construction completed " + Coroutine.getTime ());
        //trc.show ("initModel", "construction completed", Coroutine.getTime ());

    }; // initModel


    /******************************************************************
     * Get the primary statistic for the stopping rule.
     * @return  the primary statistic
     */   
    public String getPrimaryStat () 
    { 
        return primaryStat;
 
    }; // getPrimaryStat


    /******************************************************************
     * Set the primary statistic to analyze for the stopping rule.
     * @param  statNodeName  the primary statistic
     */  
    public void setPrimaryStat (String statNodeName)
    {
        this.primaryStat = primaryStat;
        for (int i = 0; i < numNodes; i++) {
            DynamicNode dn = dynNode [i];
            if (dn.props.nName.equals (statNodeName)) {
                dn.setPrimary (true);
                break;
            }; // if
        }; // for

    }; // setPrimaryStat


    /******************************************************************
     * Set the primary statistic to analyze for the stopping rule.
     * @param  statNodeName  the primary statistic
     */  
    public void setPrimaryStat ()
    {
        for (int i = 0; i < numNodes; i++) {
            DynamicNode dn = dynNode [i];
            if (dn.props.nType == Node.SINK) {
                primaryStat = dn.props.nName;
                dn.setPrimary (true);
		trc.info("primaryStat = " + primaryStat);
		//trc.show("setPrimaryStat", "primaryStat = " + primaryStat);
                break;
            }; // if
        }; // for
 
    }; // setPrimaryStat


    /******************************************************************
     * Set the batch size and number of batches for all batch statistics.
     * This is done on a node by node basis (where Facility is a special
     * case).
     * @param  bSize   batch size
     * @param  nNatch  number of batches
     */
    public void setBatchProperties (int bSize, int nBatch)
    {
		trc.info(" bSize  = " + bSize +	" nBatch = " + nBatch);
		//trc.show ("setBatchProperties", " bSize  = " + bSize +
        //                                " nBatch = " + nBatch);

        for (int i = 0; i < numNodes; i++) {

            DynamicNode n = dynNode [i];

            if (n instanceof Facility) {
                ((Facility) n).setBatchProperties (bSize, nBatch);
            } else {
                n.setBatchProperties (bSize, nBatch);
            }; // if

        }; // for

    }; // setBatchProperties


    /*****************************************************************
     * Start the display thread.
     */
    public void start ()
    {
		trc.info("start display thread: startSim = " + startSim);
        //trc.show ("start", "start display thread: startSim = " + startSim);
        //setVisible (true);
        displayThread.start ();

    }; // start


    /*****************************************************************
     * Set the flag to start the simulation.
     */  
    public void beginSim ()
    {
        startSim = true; 
		trc.info("startSim = " + startSim);
        // trc.show ("beginSim", "startSim = " + startSim);
 
    }; // beginSim 

 
    /*****************************************************************
     * Set the flag to end the simulation.
     */  
    public void endSim ()
    {
        stopSim = true;  
		trc.info("stopSim = " + stopSim);
        // trc.show ("endSim", "stopSim = " + stopSim);
 
    }; // endSim


    /*****************************************************************
     * Is the simulation over?
     * @return  boolean  whether simulation is over
     */
    public boolean isDone ()
    {
        for (int i = 0; i < numNodes; i++) {

            DynamicNode n = dynNode [i];

            if (n instanceof Source) {
                if ( ! ((Source) n).getStopped ()) {
                    return false;
                }; // if
            }; // if

        }; // for
	

        return liveEntity.isEmpty ();

     }; // isDone


    /*****************************************************************
     * Run the display thread.  The actual display is done by the
     * ModelCanvas.
     */
    public void run ()
    {
		trc.info("start of run method: startSim = " + startSim);
		trc.info("wait until run is selected " + Coroutine.getTime ());
		// trc.show ("run", "start of run method: startSim = " + startSim);
        // trc.show ("run", "wait until run is selected", Coroutine.getTime ());

        /*************************************************************
         * Wait until run is selected from the JSIM menu.
         */
        while ( ! startSim) {
            try {
            displayThread.sleep (TIME_DELAY);	
            } catch (InterruptedException ex) {
            }; // try
        }; // while

		trc.info("start all sources and signals " + Coroutine.getTime ());
        //trc.show ("run", "start all sources and signals", Coroutine.getTime ());

        /*************************************************************
         * Start all the source and signal nodes.
         */
        DynamicNode dn;
        for (int i = 0; i < numNodes; i++) {
            dn = dynNode [i];
            if (dn instanceof Source) {
//		trc.show ("run", "liveEntity: " + liveEntity);
//		trc.show ("run", "liveEntity has length: " + liveEntity.size ());
                ((Source) dn).start (liveEntity, this);
            } else if (dn instanceof Signal) {
                ((Signal) dn).start ();
            }; // if
        }; // for

		trc.info("start simulation " + Coroutine.getTime ());
        // trc.show ("run", "start simulation", Coroutine.getTime ());
        //clock.handleEvents (displayThread);

        Coroutine.start ();

        try {

            /*********************************************************
             * Sleep and repaint the canvas until the simulation is over.
             */
            while ( ! isDone () && ! stopSim) {

                //trc.show ("run", "repaint the animation canvas", Coroutine.getTime ());
                //canvas.repaint ();
                try {
                    displayThread.sleep (TIME_DELAY);	
                } catch (InterruptedException ex) {
					trc.warning("display thread's sleep interrupted " + ex);
                    // trc.tell ("run", "display thread's sleep interrupted " + ex);
                }; // try

                //displayThread.yield ();    // for NT (d95-dkr@nada.kth.se)

            }; // while
          

        } catch (StopException se) {
			trc.info("caught StopException " + Coroutine.getTime ());
            // trc.show ("run", "caught StopException", Coroutine.getTime ());
        }; // try

        try {
           // canvas.repaint();
            displayThread.sleep (50 * TIME_DELAY);
            
        } catch (InterruptedException ex) {
			trc.warning("display thread's sleep interrupted " + ex);
            // trc.tell ("run", "display thread's sleep interrupted " + ex);
        }; // try
	
		trc.info(" isDone  = " + isDone () + " stopSim = " + stopSim);
        // trc.show ("run", " isDone  = " + isDone () +
        //                 " stopSim = " + stopSim);

        boolean complete = liveEntity.isEmpty ();
		trc.info("liveEntity.isEmpty () = " + complete + " @ " + 
					Coroutine.getTime ());
        // trc.show ("run", "liveEntity.isEmpty () = " + complete + " @ " +
        //                  Coroutine.getTime ());

		/* ********** Call debug method **************/
		Coroutine.DebugPQ ();
		/* ********** End of call to degug method */

        if ( ! complete) {
            for (Iterator it = liveEntity.listIterator (); it.hasNext (); ) {
                SimObject entity = (SimObject) it.next ();
				trc.info( "entity = " + entity.name );
                // trc.show ("run", "entity = " + entity.name);
            }; // for
        }; // if

        /*************************************************************
         * Shut down the simulation (signals must be stopped).
         * If at the user's requests, sources must also be stopped.
         */
        for (int i = 0; i < numNodes; i++) {
            dn = dynNode [i];
            if (stopSim && dn instanceof Source) {
                ((Source) dn).stop ();
            } else if (dn instanceof Signal) {
                ((Signal) dn).stop ();
            }; // if
        }; // for

//        canvas.reset ();
        setVisible (false);
        dispose ();

/*****************************
        // prepare model report event
	JsimEvent   evt;
	FinalReport fr = prepareReport ();
        String      xmlStr = null;
	if (use_xml) {
            try {
                Vector data = new Vector ();  data.add (fr);
                if (generate_xml_files) {
		    String SLASH = System.getProperty ("file.separator");
	            String fname = System.getProperty ("user.home");
	            fname += SLASH + "JSIM" + SLASH + "jsim" + SLASH 
				+ "jmessage" + SLASH + "finalReport.xml";
                    xmlStr = XMLSerializer.serialize (data, fname);
                } else {
	            xmlStr = XMLSerializer.serialize (data);
                }; // if
		evt = new JsimEvent (this, EventMap.REPORT_EVT, xmlStr);
            } catch (Exception e) {
                System.out.println ("run: " + e.getMessage ());
                e.printStackTrace ();
                return;
            }
        } else {
	    evt = new JsimEvent (this, EventMap.REPORT_EVT, fr);
	}; // if
***********************************/	
/***************************
        if (mBean != null) {

            /*********************************************************
             * Send statistical results for the entire run to model agent.
             * Include all stat variables, not just primary stat.
             *
	    mBean.fireJsimEvent (evt);
            trc.show ("run", "beans based model shutdown");

        } else {
****************************/
/***************************
            /*********************************************************
             * Display statistical results as a table in a window.
             *
            String winTitle = new String ("Statistical Results Window");
	    StatFrame statTable = null;
	    if (use_xml) {
	        statTable = new StatFrame (winTitle, xmlStr);
	    } else {
                statTable = new StatFrame (winTitle, fr);
	    }; // if
            statTable.showWin ();
            trc.show ("run", "applet based model shutdown");

        //}; // if
**************************/
    }; // run

    /*********************************************************
    * Display statistical results as a table in a window.
    */
    public void showStats ()
    {
        // prepare model report event
		JsimEvent   evt;
		FinalReport fr = prepareReport ();    
      	String winTitle = new String ("Statistical Results Window");
		StatFrame statTable = null;
		evt = new JsimEvent (this, EventMap.REPORT_EVT, fr);
        statTable = new StatFrame (winTitle, fr);
        statTable.showWin ();
		trc.info( "Displaying statistics window." );
        
		// prepare report for total cost
		//CostReport cr = prepareCostReport ();
		//String costWinTitle = new String ("Cost Statistical Results");
		//CostStatFrame costStatTable = null;
		//costStatTable = new CostStatFrame (costWinTitle, cr);
		//costStatTable.showWin ();
		trc.info ( "Displaying total cost statistics window.");
            
     } // showStats            
        
    /*****************************************************************
     * Prepare a report to ModelAgent. The report will consist of
     * information about the model, its input node properties, and
     * its output statistcal data.
     * @param FinalReport	The final report to be sent to the model agent
     */
    public FinalReport prepareReport ()
    {
       String actionType = "Final";
       String modelName = this.getTitle ();
       String [] statLabel = Statistic.LABEL;
       Vector [] timeStat = new Vector [numNodes];
       Vector [] occuStat = new Vector [numNodes];
	   Vector [] costStat = new Vector [numNodes];
       Vector [] timeObsStat = new Vector [numNodes];
       Vector [] occuObsStat = new Vector [numNodes];
       Vector [] costObsStat = new Vector [numNodes];

       for (int i = 0; i < numNodes; i++) {
    
          Statistic [] stat = dynNode [i].getNodeStats ();
          timeStat [i] = stat [0].getStats ();
          occuStat [i] = stat [1].getStats ();
          costStat [i] = stat [2].getStats ();
	

          if (stat [0] instanceof BatchStat) {
              timeObsStat [i] = ((BatchStat) stat [0]).getObsStats ();
          } else {
	      timeObsStat [i] = null;
	  }; // if

          if (stat [1] instanceof BatchStat) {
              occuObsStat [i] = ((BatchStat) stat [1]).getObsStats ();
          } else {
	      occuObsStat [i] = null;
          }; // if
  
		   if (stat [2] instanceof BatchStat) 
		   {
			   costObsStat [i] = ((BatchStat) stat [2]).getObsStats ();
		   } 
		   else 
		   {
			   costObsStat [i] = null;
		   }; // if
       } // for
     
       // get batch stat data for the replication agent
       double [] statData = null;
       Statistic [] temp = dynNode [0].getNodeStats ();
       if (temp [0] instanceof BatchStat) {
	    statData = ((BatchStat) temp [0]).getBatchData ();
       } else {
		   trc.info( "stat is instance of " + temp [0].getClass () );
	       // trc.show ("prepareReport", "stat is instance of " + 
		   //				temp [0].getClass ());
       }; // if

       FinalReport mr = new FinalReport (actionType, modelName,
                        numNodes, statLabel, timeStat,
                        occuStat, costStat, timeObsStat, occuObsStat, 
						costObsStat, statData);
       return mr;

    }; // prepareReport

	/*****************************************************************
	 * Prepare a report to ModelAgent. The report will consist of
	 * cost information about the model, its input node properties, and
	 * its output cost data.
	 * @param CostReport	The cost report to be sent to the model agent
	 */
	/*
	public CostReport prepareCostReport ()
	{
		String actionType = "Final";
		String modelName = this.getTitle ();
		String [] statLabel = Statistic.LABEL;
		Vector [] costStat = new Vector [1];
		Vector [] costObsStat = new Vector [1];

		Statistic [] stat = SimObject .getModelStats ();
		costStat [0] = stat [0].getStats ();
	
		if (stat [0] instanceof BatchStat) 
			{
				costObsStat [0] = ((BatchStat) stat [0]).getObsStats ();
			} 
			else 
			{
				costObsStat [0] = null;
			}; // if
     
		// get batch stat data for the replication agent
		double [] statData = null;
		Statistic [] temp = SimObject .getModelStats ();
		if (temp [0] instanceof BatchStat) 
		{
			statData = ((BatchStat) temp [0]).getBatchData ();
		} 
		else 
		{
			trc.info( "stat is instance of " + temp [0].getClass () );
			// trc.show ("prepareReport", "stat is instance of " + 
			//				temp [0].getClass ());
		}; // if

		CostReport mr = new CostReport (actionType, modelName,
			1, statLabel, costStat, costObsStat, statData);
		return mr;

	}; // costReport
	*/

    /*****************************************************************
     * Collect data from all the nodes (except Source FIX) in model.
     * @return  Vector  collected statistical data for model
     */
    public Vector collectStatData ()
    {
        Vector<Object> statDataVector = new Vector<Object> ();
        Statistic [] nStats;

        /*************************************************************
         * Pack vector with (a) stat report type.
         */
        statDataVector.add ("Final"); 

        for (int i = 0; i < numNodes; i++) {

            DynamicNode n = dynNode [i];

            /*********************************************************
             * Pack vector with (c) stats for node n.
             */
			trc.info ( "$$$$$$$$$$$$$$$$$$ Get node stats for " + n.getProps ().nName ); 
            nStats = n.getNodeStats ();
            for (int j = 0; j < nStats.length; j++) {
                statDataVector.add (nStats [j]); 
            }; // for

        }; // for

        return statDataVector;

    }; // collectStatData


    /*****************************************************************
     * Handle event by adjusting the view of the design canvas based
     * on position of scrollbars.
     * @param  evt  scrollbar adjustment event
     */  
    public void adjustmentValueChanged (AdjustmentEvent evt)
    {
//        canvas.setView (new java.awt.Point (horbar.getValue (), verbar.getValue ()));
 
//        canvas.repaint ();
 
    }; // adjustmentValueChanged
 
 
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
 
    }; // windowClosing
 
 
    /*****************************************************************
     * The rest of the WindowEvent handlers are not implemented.
     */  
    public void windowClosed (WindowEvent evt) {};
 
    public void windowDeiconified (WindowEvent evt) {};
 
    public void windowIconified (WindowEvent evt) {};
 
    public void windowActivated (WindowEvent evt) {};

    public void windowDeactivated (WindowEvent evt) {};
 
    public void windowOpened (WindowEvent evt) {};


   /**********************************************************
    * Trigger a model report event (call mBean's fire).
    * Send an interim report on primary stat.
    */
    public void triggerModelReportEvent (StatException ex)
    {
		trc.info( "on StatException" );
        // trc.show ("triggerModelReportEvent", "on StatException");

	JsimEvent evt;

	/* if (use_xml) {
	    InterimReport imr = new InterimReport
                                 ("Interim", ex.getData ());
            Vector data = new Vector ();  data.add (imr);
            String xmlStr;
            try {
		if (generate_xml_files) {
		    String SLASH = System.getProperty ("file.separator");
		    String fname = System.getProperty ("user.home");
		    fname += SLASH + "JSIM" + SLASH + "jsim" + SLASH 
                                + "jmessage" + SLASH + "interimReport.xml";
                    xmlStr = XMLSerializer.serialize (data, fname);
		} else {
		    xmlStr = XMLSerializer.serialize (data);
		} // if
		evt = new JsimEvent (this, EventMap.REPORT_EVT, xmlStr);
            } catch (Exception e) {
                System.out.println ("jsim.process.Model: " + e.getMessage ());
                e.printStackTrace ();
                return ;
            }
        } else { */
            Vector<Object> statData = new Vector<Object> ();
            statData.add ("Interim");
            statData.add (ex.getData ());
	    evt = new JsimEvent (this, EventMap.REPORT_EVT, statData);
	/* }; */ // if

       // mBean.fireJsimEvent (evt);

    }; // triggerModelReportEvent

 
    /**************************************************************
     * Method to inject an entity in every source node of the model.
     */
    public void injectAnEntity ()
    { 
        /*************************************************************
         * Start all the source and signal nodes.
         */         
        DynamicNode dn;
        for (int i = 0; i < numNodes; i++) {
            dn = dynNode [i];
            if (dn instanceof Source) {
                ((Source) dn).start (liveEntity, this);
            } else if (dn instanceof Signal) {
                ((Signal) dn).start ();
            }; // if
        }; // for

/*        for (int i = 0; i < numNodes; i++) {
 
            DynamicNode n = dynNode [i];
 
            if (n instanceof Source) {
                ((Source) n).startEntity ();
            }; // if
 
        }; // for
*/ 
    }; // injectAnEntity

    /**
     * Return hash map contains entities waiting on AND join
     */

    public HashMap<Integer, Coroutine> getJoinEntity () 
    {
       return joinEntity;

    } // getJoinEntity

    /**
     * Return hash map containing entities that are lost
     */

    public HashMap<Integer, Coroutine> getLostJoinEntity () 
    {
       return lostJoinEntity;

    } // getLostJoinEntity

}; // class

