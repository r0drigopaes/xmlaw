
/******************************************************************
 * @(#) DynamicNode.java     1.1
 *
 * Copyright (c) 1998 John Miller
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
 * @version     1.1, 28 Oct 1998
 * @author      John Miller
 */

package jsim.process;

import java.awt.*;
import java.util.*;
import java.util.logging.Logger;

import jsim.coroutine.*;
import jsim.statistic.*;
import jsim.util.*;
import jsim.variate.*;


/******************************************************************
 * The DynamicNode abstract class allows functional node classes
 * to be derived from it (e.g., Source , Server).  It generalizes
 * the common features of all functional nodes.  It also has methods
 * to collect statistics.
 */

public abstract class DynamicNode
{
    //////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\
    /**
     * Duration statistics collected by this node (sample or batch).
     */
    protected final BatchStat  duration;

    /**
     * Occupancy statistics collected by this node.
     */
    protected final TimeStat   occupancy;

	/**
	 * Cost statistics collected by this node (sample or batch).
	 */
	protected final BatchStat  costs;

    /**
     * Tracing messages
     */
    //protected final Trace      trc;
	protected static Logger trc;

    /////////////////////////  Variables \\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Common node properties.
     */
    protected Prop props;

    /**
     * Outgoing edges.
     */
    protected Transport [] outgoing;


    /**************************************************************
     * Construct a dynamic node from a (static) node.
     * @param  props     properties of node
     * @param  outgoing  outgoing transports (edges)
     */
    public DynamicNode (Prop          props,
                        Transport []  outgoing)
    {
        this.props    = props;
        this.outgoing = outgoing;

        duration  = new BatchStat (props.nName + " (dur)");
        occupancy = new TimeStat  (props.nName + " (occ)");
		costs  = new BatchStat (props.nName + " (cost)");

		trc = Logger.getLogger(DynamicNode.class.getName() + "." + 
						Node.TYPE_NAME [props.nType] + "." + props.nName);
		Trace.setLogConfig ( trc );
        // trc = new Trace (Node.TYPE_NAME [props.nType], props.nName);
        
/*        trc.show ("DynamicNode", "create node of type " + props.nType + " at " +
                  " ( " + props.location.x + " , " + props.location.y + " ) ",
                  Coroutine.getTime ());
*/
    }; // DynamicNode


    /**************************************************************
     * Get the properties of this dynamic node.
     * @return  Prop  node properties
     */
    public Transport [] getOutgoing ()
    {
        return outgoing;

    }; // getOutgoing


    /**************************************************************
     * Get the properties of this dynamic node.
     * @return  Prop  node properties
     */
    public Prop getProps ()
    {
        return props;

    }; // getProps


    /**************************************************************
     * Reset the adjutable properties of this dynamic node.
     * @param  property  node properties
     */
    public void setProps (Prop props)
    {
        this.props = props;

    }; // setProps


    /**************************************************************
     * Set the primary statistic to analyze for the stopping rule.
     * @param  statNodeName  the primary statistic
     */
    public void setPrimary (boolean value)
    {
        duration.setPrimary ( value );
		costs.setPrimary ( value );

    }; // setPrimaryStat


    /**************************************************************
     * Get both duration and occupancy statistics for this node.
     * @return  Statistic []  statistics for this node
     */
    public Statistic [] getNodeStats ()
    {
        Statistic [] statArray = {duration, occupancy, costs};
        return statArray;

    }; // getNodeStats


    /**********************************************************
     * Set the batch size and number of batches for all the
     * models batch statistics.
     * @param  bSize   size of batch
     * @param  nBatch  number of batches
     */
    public void setBatchProperties (int bSize, int nBatch)
    {
        duration.setBatchProperties (bSize, nBatch);
		costs.setBatchProperties (bSize, nBatch);

    }; // setBatchProperties


}; // class

