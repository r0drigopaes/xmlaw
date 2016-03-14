/******************************************************************
 * @(#) Q_Node.java           1.3     96/06/20
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


/********************************************************************
 * Class Q_Node deriving from Object implements a node to be
 * inserted into a queue.
 *
 * @version    1.0
 * @author    Zhiwei Zhang, John Miller
 */

public class Q_Node
{
    /**
     * Item containing data
     */
    Object  item    = null;

    /**
     * Item cancelled from queue (lazy delete)
     */
    boolean present = true;

    /**
     * Next node (right child)
     */
    Q_Node  right   = null;


    /****************************************************************
     * Constructs an empty queue node.
     */
    Q_Node ()
    {
    }; // Q_Node


    /****************************************************************
     * Constructs an unlinked queue node.
     * @param  item    item for the queue node
     */
    Q_Node (Object item)
    {
        this.item = item;

    }; // Q_Node


    /****************************************************************
     * Constructs an queue node.
     * @param  item    item for the queue node
     * @param  right   next node for the queue node
     */
    Q_Node (Object item, Q_Node right)
    {
        this.item  = item;
        this.right = right;

    }; // Q_Node


    /****************************************************************
     * Get the item out of the node.
     * @return  Object  item in node
     */
    public Object getItem ()
    {
        return item;

    }; // getItem


}; // class

