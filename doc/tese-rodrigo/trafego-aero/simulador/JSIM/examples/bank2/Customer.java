
/******************************************************************
 * Customer.java
 * JSIM Customer entity
 */

import jsim.process.*;
import jsim.variate.*;


public class Customer extends SimObject
{
	static Bank e_;

	public void setEnvironment (Model env)
		{ e_ = (Bank) env; }

	public Model getEnvironment ()
		{ return e_; }

	public void run ()
	{

		try {
			for (e_.CustomerE0.join (this);
			     e_.CustomerE0.move (this); );
			e_.Teller.request (this);
			e_.Teller.use (this);
			for (e_.TellerE0.join (this);
			     e_.TellerE0.move (this); );
			e_.Exit.use (this);
		} catch (EntityException ee) { _end (); };

	}; // run

}; // class

