
/******************************************************************
 * Caller.java
 * JSIM Caller entity
 */

import jsim.process.*;
import jsim.variate.*;


public class Caller extends SimObject
{
	static Phone e_;

	public void setEnvironment (Model env)
		{ e_ = (Phone) env; }

	public Model getEnvironment ()
		{ return e_; }

	public void run ()
	{

		try {
			for (e_.CallerE0.join (this);
			     e_.CallerE0.move (this); );
			e_.Phone.request (this);
			e_.Phone.use (this);
			for (e_.PhoneE0.join (this);
			     e_.PhoneE0.move (this); );
			e_.Hangup.use (this);
		} catch (EntityException ee) { _end (); };

	}; // run

}; // class

