
/*****************************************************************
 * ReservationsApp.java
 * JSIM Applet for Reservations simulation model
 */

import java.applet.*;

public class ReservationsApp extends Applet
{
	/*********************************************************
	 * Construct applet.
	 */
	public ReservationsApp ()
	{
		Reservations model = new Reservations ();
		model.start ();

	}; // ReservationsApp

}; // class

