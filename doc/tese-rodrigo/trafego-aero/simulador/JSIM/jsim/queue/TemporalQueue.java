/******************************************************************
 * @(#) TemporalQueue.java           1.3     96/07/16
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


/************************************************************************
 * TemporalQueue class maintains a priority queue using self-adjusting 
 * binary tree (splay tree). The code is based on Daniel Sleator's C 
 * implementation of splay tree data structure
 * (http://gs213.sp.cs.cmu.edu/prog/splay/). 
 * The data structure is first proposed by Sleator and Tarjan in their
 * article "Self-adjusting Binary Search Tree", JACM, 32(3):652-686, 1985.
 *
 * @version   1.0    96/07/16
 * @author    John Miller, Zhiwei Zhang
 */


public class TemporalQueue extends PriorityQueue
{

    /****************************************************************
     * Constructs an empty temporal queue with unlimited capacity.
     */
    public TemporalQueue ()
    {
        super ();

    }; // TemporalQueue


    /****************************************************************
     * Constructs an empty temporal queue with limited capacity.
     * @param  capacity   the maximum number of items queue can hold
     */
    public TemporalQueue (int capacity)
    {
        super (capacity);

    }; // TemporalQueue


    /*****************************************************************
     * Insert item into the queue with default priority.
     * @param   item                new item to be inserted
     * @return  Q_Node              node holding the new item
     * @throws  FullQueueException  if the queue is full
     */
    public Q_Node addAt (Object item, double time) throws FullQueueException
    {
        return addAt (item, time, Thread.NORM_PRIORITY);

    }; // add


    /****************************************************************
     * Insert an item, its activation time and priority into the temporal queue.
     * @param   item                new item to be inserted
     * @param   time                the activation time of the new item
     * @param   priority            the priority associated with the new item
     * @return  Q_Node              node holding the new item
     * @throws  FullQueueException  if the queue is full
     */
    public Q_Node addAt (Object item, double time, int priority)
                         throws FullQueueException
    {
        if (size >= capacity) {
            throw new FullQueueException ("The queue is full");
        }; // if

        TQ_Node newNode = new TQ_Node (item, time, priority);
        if (root != null) {

            splayForInsert (newNode);

            /*********************************************************
             * case: newNode < root
             * make old root the right child of newNode
             */
            if (newNode.compare ((TQ_Node) root) < 0) {
                newNode.left  = root.left;
                newNode.right = root;
                root.left     = null;
 
            /*********************************************************
             * case: newNode >= root
             * make old root the left child of newNode
             */
            } else {
                newNode.left  = root;
                newNode.right = root.right;
                root.right    = null;
            }; // if

        }; // if

        size++;
        return root = newNode;            

    }; // add


    /*****************************************************************
     * Splay the tree until newNode can be inserted either between
     * lChild and root, or root and rChild.
     * @param  newNode    node to be inserted
     */
    private void splayForInsert (TQ_Node newNode)
    { 
        TQ_Node lChild;       // left child of root
        TQ_Node rChild;       // right child of root
            
        for ( ; ; ) {
 
            /*********************************************************
             * case: newNode <= root
             * want lChild == null or
             *      lChild <= newNode and lChild.right == null
             */
            if (newNode.compare ((TQ_Node) root) < 0) {
              
                if ((lChild = (TQ_Node) root.left) == null) break;
                if (newNode.compare (lChild) < 0) {
                    root = (TQ_Node) splayLeft (root, lChild);
                } else {
                    if (lChild.right == null) break;
                    lChild = (TQ_Node) splayRight (lChild, (TQ_Node) lChild.right);
                }; // if
 
            /*********************************************************
             * case: newNode >= root
             * want root <= newNode <= rChild and rChild.left == null
             */
            } else if (newNode.compare (root) >= 0) {
              
                if ((rChild = (TQ_Node) root.right) == null) break;
                if (newNode.compare (rChild) > 0) {
                    root = (TQ_Node) splayRight (root, rChild);
                } else {
                    if (rChild.left == null) break;
                    lChild = (TQ_Node) splayLeft (rChild, rChild.left);
                }; // if
 
            }; // if
 
        }; // for
 
    }; // splayForInsert


}; // class

