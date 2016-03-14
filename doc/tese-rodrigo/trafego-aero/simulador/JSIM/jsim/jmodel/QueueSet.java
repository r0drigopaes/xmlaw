/******************************************************************
 * @(#) QueueSet.java     1.3     98/4/16
 * 
 * Copyright (c) 2005, John Miller, Junxiu Tao
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
 * @author      John Miller, Junxiu Tao
 */

package jsim.jmodel;


/******************************************************************
 * The QueueSet class inserts and removes items in FIFO order, but
 * also provides an organaize method to eliminate duplicate items.
 */

public class QueueSet 
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Empty marker
     */
    private static final int  EMPTY = Integer.MIN_VALUE;

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Queue storage array
     */
    private final int []  queueArr;

    /**
     * Capacity of queue
     */
    private final int     capacity;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Start index of queue
     */
    private int  start;

    /**
     * End index of queue
     */
    private int  end;


    /**************************************************************
     * Constructor for QueueSet.
     * @param  size  queue capacity
     */
    public QueueSet (int size)
    {
        start = end = 0;
        capacity = size;
        queueArr = new int [capacity + 1];

     }; // QueueSet


     /**********************************************************
      * Is the Queue Empty?
      * @return  boolean  whether queue is empty
      */
     public boolean isEmpty ()
     {
         return start >= end;

     }; // isEmpty


     /***************************************************************
      * Is the Queue full?
      * @return  boolean  whether queue is full
      */
     public boolean isFull ()
     {
         return end >= capacity;

     }; // isFull


     /***************************************************************
      * Organize the queue to generate the correct order for Generator.
      * @throws  ArrayIndexOutOfBoundsException  out of bounds
      */
     public void organize () throws ArrayIndexOutOfBoundsException
     {
         boolean equal;
		 // Debug code
                 /*
		 System.out.println("Queue before organize");
		 for (int z = 0; z < queueArr.length; z++) 
		 {
			 System.out.print(queueArr [z] + "  ");
		 }
		 System.out.println();
                 */ 
		 // End debug code
         for (int i = end - 1; i > start; i--) {
             equal = false;
             if (queueArr [i] == EMPTY)  continue;

             for (int j = i - 1; j >= start; j--) {
                 if (queueArr [i] == queueArr [j]) {
                     queueArr [j] = EMPTY;
                     equal = true;
                 }; // if
             }; // for

             if (equal)  queueArr [i] = - queueArr [i] - 1;   // WHAT
         }; // for
		 // Debug code
                 /*
		 System.out.println("Queue middle of organize");
		 for (int z = 0; z < queueArr.length; z++) 
		 {
			 System.out.print(queueArr [z] + "  ");
		 }
		 System.out.println();
                 */
		 // End debug code
         int k = 0;
         for (int i = start; i < end; i++) {
             if (queueArr [i] != EMPTY) {
                queueArr [k++] = queueArr [i];
             }; // if
         }; // for

         end = k;

		 // Debug code
                 /*
		 System.out.println("Queue after organize");
		 for (int z = 0; z < queueArr.length; z++) 
		 {
			 System.out.print(queueArr [z] + "  ");
		 }
		 System.out.println();
                 */
		 // End debug code
      }; // organize


     /***************************************************************
      * Insert an item on the back of the queue.
      * @param   item                            item to be added
      * @throws  ArrayIndexOutOfBoundsException  out of bounds
      */
     public void enqueue (int item) throws ArrayIndexOutOfBoundsException
     {
         queueArr [end++] = item;

     }; // enqueue


     /***************************************************************
      * Remove the item at the front of the queue.
      * @return  int                             first item
      * @throws  ArrayIndexOutOfBoundsException  out of bounds
      */
     public int dequeue () throws ArrayIndexOutOfBoundsException
     {
         return (start < end) ? queueArr [start++] : -1;

     }; // dequeue


     /***************************************************************
      * Get the item at the front of the queue.
      * @return  int                             first item
      * @throws  ArrayIndexOutOfBoundsException  out of bounds
      */
     public int getFront () throws ArrayIndexOutOfBoundsException
     {
         return queueArr [start];

     }; // getFront


     /***************************************************************
      * Reset the Queue.
      */
     public void reset () 
     {
        start = end = 0;

     }; // reset


}; // class

