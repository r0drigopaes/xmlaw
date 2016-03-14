
/*****************************************************************
 * BankApp.java
 * JSIM Applet for Bank simulation model
 */

import java.applet.*;

public class BankApp extends Applet
{
	/*********************************************************
	 * Construct applet.
	 */
	public BankApp ()
	{
		Bank model = new Bank ();
		model.start ();

	}; // BankApp

}; // class

