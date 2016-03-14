/******************************************************************
 * @(#) DBConnectionUI.java   1.3
 *
 * Copyright (c) 2000 John Miller
 * All Right Reserved
 *-----------------------------------------------------------------
 * Permission to use, copy, modify and distribute this software and
 * its documentation without fee is hereby granted provided that
 * this copyright notice appears in all copies.
 * WE MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY 
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WE SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY ANY USER AS A RESULT OFUSING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *-----------------------------------------------------------------
 *  
 * @version 1.3 (12 December 2000)
 * @author  Xueqin Huang, John Miller
 */

package jsim.jrun;

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import jsim.util.Trace;
import jsim.jmessage.*;

/***************************************************************
 * The DBConnectionUI class provides a GUI interface for the user
 * to enter database connection information.
 */
public class DBConnectionUI extends JDialog 
			implements ActionListener
{
    /**
     * The Trace facility
     */
    private final Trace    trc = new Trace ("jrun", "DBConnectionUI");

    /**
     * The framework JComboBox
     */
    private JComboBox      framework;

    /**
     * The database URL JComboBox
     */
    private JComboBox      url;

    /**
     * The user name JTextField
     */
    private JTextField     username = new JTextField ("");

    /**
     * The password JPasswordField
     */
    private JPasswordField passwd = new JPasswordField ("");

    /**
     * The JDBC driver class name JComboBox
     */
    private JComboBox      driver;

    /**
     * The OK button
     */
    private JButton        okButton = new JButton ("OK");

    /**
     * The cancel button
     */
    private JButton        cancelButton = new JButton ("Cancel");

    /**
     * The DBConnectionUIEvent listener list
     */
    private Vector         connectionListeners = new Vector ();


    /***************************************************************
     * Constructs a DBConnectionUI instance.
     * @param owner	The owner JFrame
     */
    public DBConnectionUI (JFrame owner) 
    {
	super (owner, "Get Database Connection", true);

        JLabel	   frameworkL = new JLabel ("Connection Framework: ");
        JLabel	   urlLabel = new JLabel ("Database URL: ");
        JLabel	   usernameL = new JLabel ("User Name: ");
        JLabel	   passwdL = new JLabel ("Password: ");
        JLabel	   driverL = new JLabel ("Database Driver: ");

	Vector frameworkItems = new Vector ();
	frameworkItems.add ("rmijdbc");
	frameworkItems.add ("embedded");
	frameworkItems.add ("sysconnect");
	framework = new JComboBox (frameworkItems);
	framework.setEditable (true);

	Vector urlItems = new Vector ();

	urlItems.add ("jdbc:cloudscape:rmi://chief.cs.uga.edu:2001/JsimDB");
	urlItems.add ("jdbc:cloudscape:rmi://maxwell.cs.uga.edu:2001/JsimDB");
	urlItems.add ("jdbc:cloudscape:JsimDB");
	urlItems.add ("jdbc:cloudscape:weblogic:JsimDB");
	urlItems.add ("jdbc:cloudscape:weblogic-ssl:JsimDB");
	url = new JComboBox (urlItems);  
	url.setEditable (true);

	Vector driverItems = new Vector ();
	driverItems.add ("COM.cloudscape.core.RmiJdbcDriver");
	driverItems.add ("COM.cloudscape.core.JDBCDriver");
	driverItems.add ("COM.cloudscape.core.WebLogicDriver");
	driver = new JComboBox (driverItems);
	driver.setEditable (true);

	JPanel connection = new JPanel ();
	GridBagLayout gridbag = new GridBagLayout ();
	connection.setLayout (gridbag);
	GridBagConstraints c = new GridBagConstraints ();
	c.fill = GridBagConstraints.HORIZONTAL;
	c.insets = new Insets (10, 10, 10, 10);
	c.gridwidth = 1;  c.gridx = 0;  c.gridy = 0;
	gridbag.setConstraints (frameworkL, c);
	connection.add (frameworkL);	  
	c.gridwidth = 2;  c.gridx = 1;  c.gridy = 0;
	gridbag.setConstraints (framework, c);
	connection.add (framework);
	c.gridwidth = 1;  c.gridx = 0;  c.gridy = 1;
	gridbag.setConstraints (urlLabel, c);
	connection.add (urlLabel);	  
	c.gridwidth = 2;  c.gridx = 1;  c.gridy = 1;
	gridbag.setConstraints (url, c);
	connection.add (url);
	c.gridwidth = 1;  c.gridx = 0;  c.gridy = 2;
	gridbag.setConstraints (usernameL, c);
	connection.add (usernameL);	  
	c.gridwidth = 2;  c.gridx = 1;  c.gridy = 2;
	gridbag.setConstraints (username, c);
	connection.add (username); 
	c.gridwidth = 1;  c.gridx = 0;  c.gridy = 3;
	gridbag.setConstraints (passwdL, c);
	connection.add (passwdL);	  
	c.gridwidth = 2;  c.gridx = 1;  c.gridy = 3;
	gridbag.setConstraints (passwd, c);
	connection.add (passwd);
	c.gridwidth = 1;  c.gridx = 0;  c.gridy = 4;
	gridbag.setConstraints (driverL, c);
	connection.add (driverL);
	c.gridwidth = 2;  c.gridx = 1;  c.gridy = 5;
	gridbag.setConstraints (driver, c);
	connection.add (driver);
        connection.setBorder (BorderFactory.createTitledBorder ("DBConnection"));

	okButton.addActionListener (this);
	cancelButton.addActionListener (this);
	JPanel cmdPanel = new JPanel (new GridLayout (1, 2));
	cmdPanel.add (okButton);
	cmdPanel.add (cancelButton);
	cmdPanel.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 20));

	JPanel content = new JPanel (new BorderLayout ());
	content.add (connection, BorderLayout.CENTER);
	content.add (cmdPanel, BorderLayout.SOUTH);

	setContentPane (content);
        addWindowListener (new WindowAdapter () {
            public void WindowClosing (WindowEvent e) {
                System.exit (0);
            }});

    }; // DBConnectionUI constructor


    /***************************************************************
     * Handles an ActionEvent.
     * @param evt	The ActionEvent
     */
    public void actionPerformed (ActionEvent evt)
    {
	DBConnectionUIEvent e;

	if (evt.getSource () == cancelButton) {
		e = new DBConnectionUIEvent
				("DBConnectionUIEvent", null);
		fireDBConnectionUIEvent (e);
		dispose ();
	} else if (evt.getSource () == okButton) {

		String frameworkStr = ((String) framework.getSelectedItem ()).trim ();
		String urlStr = ((String) url.getSelectedItem ()).trim ();
		String usernameStr = new String ((username.getText ()).trim ());
		String passwdStr = new String (passwd.getPassword ()).trim ();
		String driverStr = ((String) driver.getSelectedItem ()).trim ();
		trc.show ("actionPerformed", "db connection information:" +
				"\nframework is " + frameworkStr +
				"\nurl is " + urlStr +
				"\nusername is " + usernameStr +
				"\npasswd is " + passwdStr + 
				"\ndriver is " + driverStr);
		DBConnectionInfo connInfo = new DBConnectionInfo (frameworkStr, urlStr, 
					usernameStr, passwdStr, driverStr);
		e = new DBConnectionUIEvent ("DBConnectionUIEvent",
								connInfo);
		fireDBConnectionUIEvent (e);
		dispose ();
	}; // if

    }; // actionPerformed


    /***************************************************************
     * Adds a DBConnectionUIListener.
     * @param t		The target DBConnectionListener
     */
    public synchronized void addDBConnectionUIListener (DBConnectionUIListener t)
    {
        connectionListeners.add (t);

    }; // addDBConnectionUIListener
    
     
    /***************************************************************
     * Fires a DBConnectionUIEvent.
     * @param evt	The DBConnectionUIEvent
     */
    private void fireDBConnectionUIEvent (DBConnectionUIEvent evt)
    {
        DBConnectionUIListener  target;
        Vector                 allTargets;
    
        trc.show ("fireDBConnectionUIEvent", "now");
        
        synchronized (this) {
            allTargets = (Vector) connectionListeners.clone ();
        }; // synchronized

        for (int i = 0; i < allTargets.size (); i++) {
            target = (DBConnectionUIListener) allTargets.elementAt (i);
            target.notify (evt);
        }; // for
        
    };

}; // class DBConnectionUI		

