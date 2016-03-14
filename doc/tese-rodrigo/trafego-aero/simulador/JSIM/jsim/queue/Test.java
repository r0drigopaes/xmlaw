/******************************************************************
 * @(#) Test.java           1.3     99/1/14
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
 * @author      John Miller
 */
package jsim.queue;

import java.util.*;


/************************************************************************
 * Test class is used to test temporal and priority queues.
 *
 * @version   1.0    99/1/14
 * @author    John Miller, Xuewei Xiang
 */

public class Test
{
    /********************************************************************
     * The main function performs several adds followed by removes.
     * @param  args  arguments
     */
    public static void main (String [] args)
    {
        String        item;
        //PriorityQueue q = new PriorityQueue ();
        TemporalQueue q = new TemporalQueue ();
 
        //q.add ("Item a", 1);
        //q.add ("Item b", 5);
        //q.add ("Item c", 7);
        //q.add ("Item d", 2);
        //q.add ("Item e", 3);
        //q.add ("Item f", 0);
        //q.add ("Item g", 6);
        //q.add ("Item h", 4);
        q.addAt ("Item a", 8.0, 1);
        q.addAt ("Item b", 5.0, 5);
        q.addAt ("Item c", 6.0, 7);
        q.addAt ("Item d", 2.0, 2);
        q.addAt ("Item e", 3.0, 3);
        q.addAt ("Item f", 0.0, 0);
        q.addAt ("Item g", 6.0, 6);
        q.addAt ("Item h", 4.0, 4);
        q.printQueue ();

        try {
            while (true) {
                item = (String) (q.remove ().getItem ());
                System.out.println ("remove " + item);
                q.printQueue ();
            } // while 
        } catch (NoSuchElementException e) {
            System.out.println ("The queue is now empty! " + e);
        }; // try

    }; // main


}; // class

