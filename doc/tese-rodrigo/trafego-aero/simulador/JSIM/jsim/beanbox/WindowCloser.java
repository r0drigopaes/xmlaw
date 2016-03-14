
/*****************************************************************
 * Utility class to catch window close events on a target
 * window and actually dispose the window.
 */

package jsim.beanbox;

import java.awt.*;
import java.awt.event.*;

public class WindowCloser implements WindowListener
{

    /*****************************************************************
    * Create an adaptor to listen for window closing events
    * on the given window and actually perform the close.
    */
    public WindowCloser (Window w)
    {
	this(w, false);

    }; // WindowCloser


    /*****************************************************************
    * Create an adaptor to listen for window closing events
    * on the given window and actually perform the close.
    * If "exitOnClose" is true we do a System.exit on close.
    */

    public WindowCloser (Window w, boolean exitOnClose)
    {
	this.exitOnClose = exitOnClose;
	w.addWindowListener(this);

    }; // WindowCloser


    /*****************************************************************
     *
     */
    public void windowClosing (WindowEvent e)
    {
	if (exitOnClose) {
            System.exit(0);
	}
	e.getWindow().dispose();

    }; // windowClosing


    /*****************************************************************
     *
     */
    public void windowClosed (WindowEvent e)
    {
	if (exitOnClose) {
            System.exit(0);
	}; // if

    }; // windowClosed


    /*****************************************************************
     * Null handlers.
     */

    public void windowOpened(WindowEvent e) { }

    public void windowIconified(WindowEvent e) { }

    public void windowDeiconified(WindowEvent e) { }

    public void windowActivated(WindowEvent e) { }

    public void windowDeactivated(WindowEvent e) { }

    private boolean exitOnClose;

}; // class

