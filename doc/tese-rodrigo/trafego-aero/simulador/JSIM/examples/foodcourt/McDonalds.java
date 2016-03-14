
/******************************************************************
 * McDonalds.java
 * JSIM McDonalds entity
 */

import jsim.process.*;
import jsim.variate.*;


public class McDonalds extends SimObject
{
	static FoodCourt e_;

	public void setEnvironment (Model env)
		{ e_ = (FoodCourt) env; }

	public Model getEnvironment ()
		{ return e_; }

	public void run ()
	{

		try {

		double prob1 = branch.gen ();
		if (prob1 < 0.5) {

			for (e_.McDonaldsE0.join (this);
			     e_.McDonaldsE0.move (this); );
			e_.MRegister2.request (this);
			e_.MRegister2.use (this);
			for (e_.MRegister2E0.join (this);
			     e_.MRegister2E0.move (this); );

		} else {

			for (e_.McDonaldsE1.join (this);
			     e_.McDonaldsE1.move (this); );
			e_.MRegister1.request (this);
			e_.MRegister1.use (this);
			for (e_.MRegister1E0.join (this);
			     e_.MRegister1E0.move (this); );

		}; // if

			e_.MExit.use (this);
		} catch (EntityException ee) { _end (); };

	}; // run

}; // class

