
/*****************************************************************
 * FoodCourtApp.java
 * JSIM Applet for FoodCourt simulation model
 */

import java.applet.*;

public class FoodCourtApp extends Applet
{
	/*********************************************************
	 * Construct applet.
	 */
	public FoodCourtApp ()
	{
		FoodCourt model = new FoodCourt ();
		model.start ();

	}; // FoodCourtApp

}; // class

