
/******************************************************************
 * Customer.java
 * JSIM Customer entity
 */

import jsim.process.*;
import jsim.variate.*;


public class Customer extends SimObject
{
	static Reservations e_;

	public void setEnvironment (Model env)
		{ e_ = (Reservations) env; }

	public Model getEnvironment ()
		{ return e_; }

	public void run ()
	{

		try {
			for (e_.CustomerE0.join (this);
			     e_.CustomerE0.move (this); );
			e_.TravelAgent.request (this);
			e_.TravelAgent.use (this);
			for (e_.TravelAgentE0.join (this);
			     e_.TravelAgentE0.move (this); );
			e_.Split0.use (this);
			for (e_.Split0E0.join (this);
			     e_.Split0E0.move (this); );
			e_.BookTicket.request (this);
			e_.BookTicket.use (this);
			for (e_.BookTicketE0.join (this);
			     e_.BookTicketE0.move (this); );
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

