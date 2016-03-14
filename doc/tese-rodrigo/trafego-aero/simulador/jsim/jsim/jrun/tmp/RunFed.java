package jsim.jrun;

import java.util.*;

import jsim.jmessage.*;
import jsim.util.*;

public class RunFed
{
    public static boolean runfed ()
    {
/*	Trace           trc = new Trace ("RunFed", "RunFed");
	Hashtable       ht = new Hashtable ();
	String []       source;
	String []       target;		// for each source
	JsimBean []     jsimBean;
	JsimListener    l;
	Class           c;
	boolean         foundAgent = false;

	source = new String [Fed.GRAPH.length];
	for (int i = 0; i < Fed.GRAPH.length; i++) {
		source [i] = Fed.GRAPH [i][0];
		target = new String [Fed.GRAPH [i].length - 1];
		for (int j = 1; j < Fed.GRAPH [i].length; j++) {
			target [j - 1] = Fed.GRAPH [i][j];
		}; // for
		ht.put (source [i], target);
	}; // for

	jsimBean = new JsimBean [source.length];
	try {
	    for (int i = 0; i < source.length; i++) {
		c = Class.forName ((String) source [i]);
		jsimBean [i] = (JsimBean) c.newInstance ();
		target = (String []) ht.get (source [i]);
		for (int j = 0; j < target.length; j++) {
			c = Class.forName ((String) target [j]);
			l = (JsimListener) c.newInstance ();
			jsimBean [i].addJsimListener (l);
		}; // for
	    }; // for
	} catch (Exception e) {
		e.printStackTrace ();
		return false;
	}; // try

	// try scenario agent first
	for (int i = 0; i < source.length; i++) {  
		if (jsimBean [i] instanceof ScenarioAgent) {
			foundAgent = true;
			ScenarioAgent sa = (ScenarioAgent) jsimBean [i];
			sa.actionPerformed (null);
			return true;
		}; // if
	}; // for

	// then try model agent
	if (!foundAgent)  {   
		for (int i = 0; i < source.length; i++) {
			if (jsimBean [i] instanceof ScenarioAgent) {
				foundAgent = true;
				ModelAgent ma = (ModelAgent) jsimBean [i];
				ma.fireInquireEvent ();
			}; // if
		}; // for
	}; // if

	if (!foundAgent) {
		trc.show ("runfed", "No agents are found, model will be run in standalone mode");
		return false;
	}; // if

  */  }; // runfed

}; // class RunFed
