/******************************************************************
 * @(#) TQ_Node.java           1.3     96/07/16
 * 
 * Copyright (c) 2005, John Miller, Rajesh S. Nair, Zhiwei Zhang.
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
 * @author      John Miller, Rajesh S. Nair, Zhiwei Zhang.
 */

package jsim.queue;

/*********************************************************************
 * Temporal_Node class stores data and children for the TemporalQueue.
 *
 * @version    1.0    96/07/16
 * @author    Zhiwei Zhang, John Miller
 */

public class TQ_Node extends PQ_Node
{
    /**
     * Node timestam
     */
    protected double time;
    

    /*****************************************************************
     * Constructor to initialize node.
     */
    TQ_Node ()
    {
        super ();
        time = 0.0;

    }; // TQ_Node
    

    /*****************************************************************
     * Constructor to set data.
     * @param  item      new item to be inserted
     * @param  time      the activation time for the new item
     * @param  priority  the priority of the new item
     */
    TQ_Node (Object item, double time, int priority)
    {
        super (item, priority);
        this.time = time;

    }; // TQ_Node


    /*****************************************************************
     * Compare the priority of two nodes.  If this.time > that.time or
     * this.time == that.time and this.priority > that.priority
     * return 1, 0 if they are equal, and -1 otherwise.
     * @param   that  the other node to compare with this
     * @return  int   as 1 (greater), 0 (equal), -1 (less)
     */
    int compare (TQ_Node that)
    {
       // System.out.println ("TQ_Node compare");
        if (time > that.time || time == that.time && priority > that.priority) {
            return  1;   // this > that

        } else if (time == that.time && priority == that.priority) {
            return  0;   // this == that

        } else {
            return -1;   // this < that
        } // if

    }; // compare


    /*****************************************************************
     * Get the node's timestamp.
     * @return  double  node's timestamp
     */
    public double getTime ()
    {
        return time;

    }; // getTime


}; // class

