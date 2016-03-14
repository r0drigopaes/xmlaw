/******************************************************************
 * @(#) Transport.java     1.3
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

package jsim.process;

import java.awt.*;
import java.awt.geom.*;
import jsim.util.*;
import jsim.variate.*;
import java.util.logging.Logger;

/******************************************************************
 * The Transport class implements the connecting links between the nodes.
 * Any two nodes can be connected only by a Transport.
 * The transport can be a straight line (determined by 2 points) or
 * a quad curve (determined by 3 points).
 */

public class Transport
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Default step time for entities moving on transport.
     */
    protected static final double  DEFAULT_STEP_TIME = 100.0;

    /**
     * Default step size for entities moving on transport.
     */
    protected static final double  DEFAULT_STEP_SIZE =  20.0;

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * The quad curve representing the transport edge.
     */
    protected final QCurve          edge;

    /**
     * The first point of the edge.
     */
    protected final Point2D.Double  start;

    /**
     * The last point of the edge.
     */
    protected final Point2D.Double  end;

    /**
     * Tracing messages.
     */
	protected static Logger trc = Logger.getLogger(Transport.class.getName());
    // protected static final Trace trc = new Trace ("Transport");

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * The time between steps.
     */
    protected double          stepTime = DEFAULT_STEP_TIME;

    /**
     * The size of each step.
     */
    protected double          stepSize = DEFAULT_STEP_SIZE;

 
    protected double          totalTime;

    /*************************************************************
     * Construct a transport as a straight line (degenerate quad curve).
     * @param  x1     x coordinate of start point
     * @param  y1     y coordinate of start point
     * @param  x2     x coordinate of end point
     * @param  y2     y coordinate of end point
     */
    public Transport (double x1, double y1,         // start point
                      double x2, double y2,         // end point
		      Variate timeDist)             // distribution for move time 
    {
        edge  = new QCurve (x1, y1, x2, y2);

        start = new Point2D.Double (x1 - Node.TOK_RADIUS,
                                    y1 - Node.TOK_RADIUS);

        end   = new Point2D.Double (x2 - Node.TOK_RADIUS,
                                    y2 - Node.TOK_RADIUS);

        if ( timeDist != null )        
            totalTime = timeDist.gen ();

        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        stepTime = totalTime / (dist / stepSize);
		Trace.setLogConfig ( trc );
    }; // Transport


    /*************************************************************
     * Construct a transport as a quad curve.
     * @param  x1     x coordinate of start point
     * @param  y1     y coordinate of start point
     * @param  ctrlx  x coordinate of control point
     * @param  ctrly  y coordinate of control point
     * @param  x2     x coordinate of end point
     * @param  y2     y coordinate of end point
     */
    public Transport (double x1, double y1,         // start point
                      double ctrlx, double ctrly,   // control point
                      double x2, double y2,         // end point
		      Variate timeDist)             // distribution for move time 

    {
        edge  = new QCurve (x1, y1, ctrlx, ctrly, x2, y2);

        start = new Point2D.Double (x1 - Node.TOK_RADIUS,
                                    y1 - Node.TOK_RADIUS);

        end   = new Point2D.Double (x2 - Node.TOK_RADIUS,
                                    y2 - Node.TOK_RADIUS);

        if ( timeDist != null )
            totalTime = timeDist.gen ();
		Trace.setLogConfig ( trc );

    }; // Transport


    /**************************************************************
     * Get the edge for the transport.
     * @return  QCurve  the quad curve for the transport
     */
    QCurve getEdge ()
    {
        return edge;

    }; // getEdge
        

    /**************************************************************
     * Place entity at start of transport.
     * @param  entity  SimObject/entity joining the transport
     */
    public void join (SimObject entity)
    {
        trc.info ( entity.name + " at " + start);
		// trc.show ("join", entity.name + " at " + start);
        entity.setPosition (start);

    }; // join


    /**************************************************************
     * This method implements the logic for a SimObject stepping through 
     * the transport.  It changes the position co-ordinates of the SimObject
     * as required.
     * @param   entity   SimObject/entity stepping through the transport
     * @return  boolean  whether entity is still moving on transport
     */
    public boolean move (SimObject entity)
    {
        Point2D.Double currPos = entity.getPosition ();
        Point2D.Double nextPos = edge.next (currPos, stepSize);

        if (nextPos != null) {
            nextPos.x -= Node.TOK_RADIUS;
            nextPos.y -= Node.TOK_RADIUS;
            entity.setPosition (nextPos);
            System.out.println ();                   // JDK 1.3 bug workaround
			trc.info ( entity.name + " to " + nextPos );
            // trc.show ("move", entity.name + " to " + nextPos);
            entity._sleep (stepTime);
            return true;
        }; // if

		trc.info ( entity.name + " reached end of transport" );
        // trc.show ("move", entity.name + " reached end of transport");
        return false;

    }; // move


    /**************************************************************
     * Adjust the speed of the transport by reseting the stepTime
     * and/or stepSize.
     * @param stepTime  new step time (ignored if not positive)
     * @param stepSize  new step size (ignored if not positive)
     */
    public void adjustSpeed (double stepTime, double stepSize)
    {
        if (stepTime > 0.0)  this.stepTime = stepTime;
        if (stepSize > 0.0)  this.stepSize = stepSize;

    }; // adjustSpeed


}; // class

