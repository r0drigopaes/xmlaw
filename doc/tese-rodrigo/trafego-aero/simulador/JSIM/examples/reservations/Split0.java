
/******************************************************************
 * Split0.java
 * JSIM Split0 entity
 */

import jsim.process.*;
import jsim.variate.*;


public class Split0 extends SimObject
{
	static Reservations e_;

	public void setEnvironment (Model env)
		{ e_ = (Reservations) env; }

	public Model getEnvironment ()
		{ return e_; }

	public void run ()
	{

		try {
			for (e_.Split0E1.join (this);
			     e_.Split0E1.move (this); );
			e_.BookRoom.request (this);
			e_.BookRoom.use (this);
			for (e_.BookRoomE0.join (this);
			     e_.BookRoomE0.move (this); );
			e_.Join0.use (this);
			for (e_.Join0E0.join (this);
			     e_.Join0E0.move (this); );
			e_.SendConfirmation.request (this);
			e_.SendConfirmation.use (this);
			for (e_.SendConfirmationE0.join (this);
			     e_.SendConfirmationE0.move (this); );
			e_.Exit.use (this);
		} catch (EntityException ee) { _end (); };

	}; // run

}; // class

