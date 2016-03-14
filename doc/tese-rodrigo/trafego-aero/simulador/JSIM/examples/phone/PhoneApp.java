
/*****************************************************************
 * PhoneApp.java
 * JSIM Applet for Phone simulation model
 */

import java.applet.*;

public class PhoneApp extends Applet
{
	/*********************************************************
	 * Construct applet.
	 */
	public PhoneApp ()
	{
		Phone model = new Phone ();
		model.start ();

	}; // PhoneApp

}; // class

