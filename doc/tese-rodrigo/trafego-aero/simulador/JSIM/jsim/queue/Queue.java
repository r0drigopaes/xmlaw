/******************************************************************
 * @(#) Queue.java           1.3     96/12/20
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

import java.util.*;


/***********************************************************************
 * Class Queue is an abstract class which pure virtual functions must be 
 * implemented in subclasses.
 *
 * @version   1.0    96/07/10
 * @author    Zhiwei Zhang, John Miller
 */

public abstract class Queue implements List
{
    /***************************************************************
     * Root (front) of queue
     */
    protected Q_Node root     = null;

    /***************************************************************
     * Current number of elements
     */
    protected int    size     = 0;

    /***************************************************************
     * Maximum number of elements allowed
     */
    protected int    capacity = (int) 1E31 - 1;


    /***************************************************************
     * Return whether the queue is empty (true for empty, false for nonempty).
     * @return  boolean  whether queue is empty
     */
    public boolean isEmpty ()
    {
        return size == 0;

    }; // isEmpty


    /***************************************************************
     * Return whether the queue is full (true for full, false for not full).
     * @return  boolean  whether queue is full
     */
    public boolean isFull ()
    {
        return size >= capacity;

    }; // isFull


    /***************************************************************
     * Return the number of elements in the queue.
     * @return  int  queue length
     */
    public int size ()
    {
        return size;

    }; // size


    /***************************************************************
     * Return the first element in the queue without removing it.
     * @return  Q_Node                  node containing first item
     * @throws  NoSuchElementException  if the queue is empty.
     */
    public Q_Node first () throws NoSuchElementException
    {
        if (root == null) {
           throw new NoSuchElementException ();
        }; // if

        return root;

    }; // first


    /***************************************************************
     * Cancel (remove) node from the queue.
     * @param  node  node to be cancelled
     */
    public void cancel (Q_Node node)
    {
        node.present = false;

    }; // cancel 


    /***************************************************************
     * Remove and return the first element in the queue.
     * If first node has been cancelled (not present), continue to remove.
     * @return  Q_Node                  first node
     * @throws  NoSuchElementException  if the queue is empty.
     */
    public Q_Node remove () throws NoSuchElementException
    {
        Q_Node first = null;

        for (boolean done = false; ! done; ) { 
 
            if (root == null)  {
                throw new NoSuchElementException ();
            }; // if 
    
            first = root;             // node containing min item
            done  = first.present;    // node has not been cancelled
            removeMin ();             // bypass min node
            size--;                   // shrink tree  
 
        }; // for 
 
        return first;                 // return node with minimum item;
 
    }; // remove


    /***************************************************************
     * Remove the node containing the minimum/first element from the queue.
     * Method for compliance with List interface.
     * @param  index  list position (must be 0)
     */
    public Object remove (int index)
    {
        if (index != 0) {
            return null;
        }; // if

        return remove ();

    }; // remove


    /***************************************************************
     * Remove the node containing the minimum/first element from the queue.
     */
    protected void removeMin ()
    {
        root = root.right;
 
    }; // removeMin


    /***************************************************************
     * Remove all elements from the queue.
     */
    public void clear ()
    { 
        root = null;
 
    }; // clear


    /***************************************************************
     * Insert an element into the queue.
     * Throws FullQueueException if * the size of the queue is out of capacity.
     * @param   item                new item to be added
     * @return  boolean             whether insertion suceeded
     */  
    public boolean add (Object item)
    {
        return addAt (item) != null;

    }; // add


    /***************************************************************
     * Insert an element into the queue and return holding node.
     * Throws FullQueueException if * the size of the queue is out of capacity.
     * @param   item                new item to be added
     * @return  Q_Node              node holding the new item
     * @throws  FullQueueException  if the queue is full.
     */
    public abstract Q_Node addAt (Object item) throws FullQueueException;


    /***************************************************************
     * Methods in List interface that are not implemented.
     */
    public boolean contains (Object o) { throw new UnsupportedOperationException (); };
    public boolean containsAll (Collection c) { throw new UnsupportedOperationException (); };
    public boolean addAll (Collection c) { throw new UnsupportedOperationException (); };
    public boolean addAll (int index, Collection c) { throw new UnsupportedOperationException (); };
    public boolean remove (Object o) { throw new UnsupportedOperationException (); };
    public boolean removeAll (Collection c) { throw new UnsupportedOperationException (); };
    public boolean retainAll (Collection c) { throw new UnsupportedOperationException (); };
    public int indexOf (Object o) { throw new UnsupportedOperationException (); };
    public int lastIndexOf (Object o) { throw new UnsupportedOperationException (); };
    public Iterator iterator () { throw new UnsupportedOperationException (); };
    public List subList (int fromIndex, int toIndex) { throw new UnsupportedOperationException (); };
    public ListIterator listIterator () { throw new UnsupportedOperationException (); };
    public ListIterator listIterator (int index) { throw new UnsupportedOperationException (); };
    public Object get (int index) { throw new UnsupportedOperationException (); };
    public Object set (int index, Object element) { throw new UnsupportedOperationException (); };
    public Object [] toArray () { throw new UnsupportedOperationException (); };
    public Object [] toArray (Object [] a) { throw new UnsupportedOperationException (); };
    public void add (int index, Object element) { throw new UnsupportedOperationException (); };


}; // class

