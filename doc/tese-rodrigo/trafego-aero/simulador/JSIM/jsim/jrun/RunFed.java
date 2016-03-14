
/******************************************************************
 * @(#) JsimEvent.java   1.3
 *
 * Copyright (c) 2000 John Miller
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
 * @version 1.3 (5 December 2000)
 * @author  Xueqin Huang, John Miller
 */

package jsim.jrun;

import java.util.*;

import jsim.jmessage.*;
import jsim.util.*;

/***************************************************************
 * This class does the actual linking among the jsim beans and agents.
 */
public class RunFed
{
    /***************************************************************
     * Constructs a model federation based on a graph specification.
     * @param  graph	A graph that specifies the relationship/events 
     *			between the model beans and agents.
     */
    public static boolean runfed (String [][] graph)
    {
	Trace           trc = new Trace ("RunFed", "");
	Hashtable       ht = new Hashtable ();
	String []       source;
	String []       target;		// for each source
	JsimBean []     sBean;
	JsimListener    l = null;
	Class           c;
	boolean         foundAgent = false;
	
	/**
	 * Loads the graph into a hash table
	 */
	source = new String [graph.length];
	for (int i = 0; i < graph.length; i++) {
		source [i] = graph [i][0];
		trc.show ("runfed", "source [" + i + "] is " + source [i]);
		target = new String [graph [i].length - 1];
		for (int j = 1; j < graph [i].length; j++) {
			target [j - 1] = graph [i][j];
			trc.show ("runfed", "target [" + i + "][" + 
						+ (j - 1) + "] is "
						+ target [j - 1]);
		}; // for
		ht.put (source [i], target);
	}; // for

	/**
	 * Links the beans and agents
	 */
	sBean = new JsimBean [source.length];
	try {
	    for (int i = 0; i < source.length; i++) {
		c = Class.forName (getClassName (source [i]));
		sBean [i] = (JsimBean) c.newInstance ();
	    }; // for

	    for (int i = 0; i < source.length; i++) {
		target = (String []) ht.get (source [i]);
		for (int j = 0; j < target.length; j++) {
		   boolean match = false;
		   for (int k = 0; !match && k < source.length; k++) {
		      if (target [j].equals (source [k])) {
			  l = (JsimListener) sBean [k];
			  match = true;
		      }; // if
		   }; // for
		   if (!match) {
			c = Class.forName (getClassName ((String) target [j]));
			l = (JsimListener) c.newInstance ();
		   }; // if
		   sBean [i].addJsimListener (l);
		}; // for
	    }; // for
	} catch (Exception e) {
		e.printStackTrace ();
		return false;
	}; // try

	/**
	 * Tries scenario agent first
	 */
	for (int i = 0; i < source.length; i++) {  
		if (sBean [i] instanceof ScenarioAgent) {
			foundAgent = true;
			ScenarioAgent sa = (ScenarioAgent) sBean [i];
			sa.actionPerformed (null);
			return true;
		}; // if
	}; // for

	/**
	 * Then tries model agent
	 */
	if (!foundAgent)  {   
		for (int i = 0; i < source.length; i++) {
			if (sBean [i] instanceof ModelAgent) {
				foundAgent = true;
				ModelAgent ma = (ModelAgent) sBean [i];
				ma.fireInquireEvent ();
			}; // if
		}; // for
	}; // if

	if (!foundAgent) {
		trc.show ("runfed", "No agents are found, model will " +
					"be run in standalone mode");
		return false;
	} else {
		trc.show ("runfed", "Starting model federation with agents.");
		return true;
	} // if

    }; // runfed

    /***************************************************************
     * Returns the class name of the bean or agent.
     * @param  label	A node label in the graph that represents the
     *			federation. The label usually consists of the
     *			name of the JsimBean class and a digit representing
     *			the instance ID of that class.
     */
    private static String getClassName (String label)
    {
	char c;
	for (int i = 0; i < label.length (); i++) {
	     c = label.charAt (i);
	     if (Character.isDigit (c)) {
		return label.substring (0, i);
	     }; // if
	}; // for

	return label;

    }; // getClassName

    /***************************************************************
     * Returns the instance ID of a JsimBean class represented by a
     * node label in the federation graph.
     * @param  label	The node label in the federation graph.
     */
    private static int getInstanceID (String label)
    {
	char c;
	for (int i = 0; i < label.length (); i++) {
	     c = label.charAt (i);
	     if (Character.isDigit (c)) {
		String id = label.substring (i, label.length ());
		return Integer.parseInt (id);
	     }; // if
	}; // for

	return 0;

    }; // getInstanceID

    /***************************************************************
     * This method exists for testing purposes only.
     */
    public static void main (String [] args)
    {
	System.out.println (getClassName ("jsim.jrun.BatchMeansAgent103"));
	System.out.println (getInstanceID ("jsim.jrun.BatchMeansAgent103"));

    }; // main

}; // class RunFed
