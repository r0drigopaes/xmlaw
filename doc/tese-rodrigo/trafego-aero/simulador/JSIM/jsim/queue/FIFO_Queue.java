/******************************************************************
 * @(#) FIFO_Queue.java           1.3     96/06/20
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
 * @author      John Miller, Zhiwei Zhang
 */



package jsim.queue;


/********************************************************************
 * FIFO_Queue class implements the First_In_First_Out queue. The 
 * implementation is based on the Pascal implementation in Data 
 * Structures by Rick Decker.
 *
 * @version   1.0
 * @author    John Miller, Zhiwei Zhang
 */

public class FIFO_Queue extends Queue
{ 
    /***************************************************************
     * Tail (last node) of queue
     */
    private Q_Node tail;


    /***************************************************************
     * Constructs an empty FIFO queue with unlimited capacity.
     */
    public FIFO_Queue ()
    {
        tail = null;

    }; // FIFO_Queue


    /***************************************************************
     * Constructs an empty FIFO queue with limited capacity.
     * @param   capacity   the maximum number of items queue can hold.
     */
    public FIFO_Queue (int capacity)
    {
        tail = null;
        this.capacity = capacity;

    }; // FIFO_Queue


    /***************************************************************
     * Insert an element into the queue.
     * @param   item                new item to be inserted
     * @return  Q_Node              node holding the new item
     * @throws  FullQueueException  if queue is full
     */
    public Q_Node addAt (Object item) throws FullQueueException
    {
        if (size >= capacity) {
            throw new FullQueueException ("The queue is full");
        }; // if        

        Q_Node newNode = new Q_Node (item);    // new node at back

        if (tail != null) {
            tail.right = newNode;
        } else {
            root = newNode;
        }; // if

        size++;
        return tail = newNode;

    }; // addAt


    /***************************************************************
     * Remove the node containing the minimum/first element from the queue.
     */
    protected void removeMin ()
    {
        if (root == tail) {
            tail = null;
        }; // if

        root = root.right;
 
    }; // removeMin


    /***************************************************************
     * Clear all elements from the queue.
     */
    public void clear ()
    {
        root = tail = null;

    }; // clear


}; // class

