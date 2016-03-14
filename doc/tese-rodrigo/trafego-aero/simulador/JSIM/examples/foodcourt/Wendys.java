
/******************************************************************
 * Wendys.java
 * JSIM Wendys entity
 */

import jsim.process.*;
import jsim.variate.*;


public class Wendys extends SimObject
{
	static FoodCourt e_;

	public void setEnvironment (Model env)
		{ e_ = (FoodCourt) env; }

	public Model getEnvironment ()
		{ return e_; }

	public void run ()
	{

		try {
			for (e_.WendysE0.join (this);
			     e_.WendysE0.move (this); );
			e_.WRegister.request (this);
			e_.WRegister.use (this);
			for (e_.WRegisterE0.join (this);
			     e_.WRegisterE0.move (this); );
			e_.WExit.use (this);
		} catch (EntityException ee) { _end (); };

	}; // run

}; // class

