/******************************************************************
 * @(#) PQ_Node.java           1.3     96/06/20
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
 * PQ_Node class stores data and children for the PriorityQueue.
 *
 * @version    1.0    96/06/20
 * @author    Zhiwei Zhang
 */

public class PQ_Node extends Q_Node
{
    /**
     * Priority of item (low = 0, higher > 0)
     */
    int      priority;

    /**
     * Left child node
     */
    PQ_Node  left;
    

    /*****************************************************************
     * Constructor to initialize node.
     */
    PQ_Node ()
    {
        super ();
        priority = 0;
        left = null;

    }; // PQ_Node


    /*****************************************************************
     * Constructs a node for a priority queue.
     * @param item        the data item 
     * @param priority    priority level (low = 0, higher > 0)
     */
    PQ_Node (Object item, int priority)
    {
        super (item);
        this.priority = priority;
        left = null;

    }; // PQ_Node

    
    /*****************************************************************
     * Compare the priority of two nodes.  If this.priority > that.priority
     * return 1, 0 if they are equal, and -1 otherwise.
     * @param  that   the other node to compare with this
     * @return int    as 1 (greater), 0 (equal) , or -1 (less)
     */
    int compare (PQ_Node that)
    {
        if      (priority >  that.priority) return 1;
        else if (priority == that.priority) return 0;
        else    return -1;

    }; // compare


}; // class

