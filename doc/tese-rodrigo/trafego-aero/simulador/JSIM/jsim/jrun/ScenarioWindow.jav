package jsim.jrun;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jsim.util.*;
import jsim.jmessage.*;

public class ScenarioWindow extends JFrame
			    implements ActionListener,
				       DBConnectionUIListener
{
    private final Trace	trc = new Trace ("jrun", "ScenarioWindow");
    private final int WIDTH = 600;
    private final int HEIGHT = 800;
    private final String SLASH;
    private final String IMAGE_BASE;
    private final JMenuItem startItem = new JMenuItem ("start simulation");
    private final JMenuItem queryItem = new JMenuItem ("query database");
    private final JMenuItem quitItem = new JMenuItem ("quit");
    private final JTextArea msgArea;
    private final JTextArea queryArea = new JTextArea ();
    private final JScrollPane msgPane;
    private final JButton execute = new JButton ("Excecute Query");
    private final ScenarioAgent scenarioAgent;
    private DBConnectionUI connUI;
    private DBConnectionInfo connInfo = null;
    private boolean startRequested = false;

    public ScenarioWindow (ScenarioAgent scenarioAgent,
			   JTextArea msgArea)
    {
	super ("Scenario Control Window");

	this.scenarioAgent = scenarioAgent;
	this.msgArea = msgArea;
	msgArea.setEditable (false);
	msgPane = new JScrollPane (msgArea);

	JMenuBar menuBar = new JMenuBar ();
	setJMenuBar (menuBar);
	JMenu menu = new JMenu ("Action");
	menuBar.add (menu);
	startItem.addActionListener (this);  menu.add (startItem);
	queryItem.addActionListener (this);  menu.add (queryItem);
	quitItem.addActionListener (this);   menu.add (quitItem);
	
	SLASH = System.getProperty ("file.separator");
        IMAGE_BASE = System.getProperty ("user.home") + SLASH
                                + "JSIM" + SLASH + "icon" + SLASH;
	JLabel cover = new JLabel (new ImageIcon (IMAGE_BASE + "cover.gif"));
	JScrollPane coverPane = new JScrollPane (cover);
	setContentPane (coverPane);

	addWindowListener (new WindowAdapter () {
		public void windowClosing (WindowEvent e) {
			System.exit (0);
		}
	});

	setSize (WIDTH, HEIGHT);	
	setVisible (true);

    }; // ScenarioWindow constructor

    public void actionPerformed (ActionEvent evt)
    {
	if (evt.getSource () == quitItem) {
		dispose ();
	} else if (evt.getSource () == startItem) {

		// set startRequested flag
		startRequested = true;

		// construct start simulation UI
		msgArea.setText ("Start simulation is requested...\n");
		msgPane.setBorder (BorderFactory.createTitledBorder ("Status Watch"));
		setContentPane (msgPane);
		repaint ();
		setSize (WIDTH, HEIGHT);
		setVisible (true);

		// pop up DB connection UI
		connUI = new DBConnectionUI (this);
		connUI.addDBConnectionUIListener (this);
		connUI.pack ();
		connUI.setVisible (true);
	} else if (evt.getSource () == queryItem) {

		// construct query UI

		String imageFile = IMAGE_BASE + "queryguide.GIF";
		trc.show ("actionPerformed", "loading image: " + imageFile);
		JLabel guide = new JLabel (new ImageIcon (imageFile));
		JScrollPane guidePane = new JScrollPane (guide);
		guidePane.setBorder (BorderFactory.createTitledBorder ("Query Guide"));

		JScrollPane queryPane = new JScrollPane (queryArea);
		queryPane.setBorder (BorderFactory.createTitledBorder ("SQL Statement"));

		msgArea.setText ("Query JsimDB is requested...\n");
		JPanel content = new JPanel (new BorderLayout ());
		content.add (guidePane, BorderLayout.NORTH);
		content.add (queryPane, BorderLayout.CENTER);
		content.add (msgPane, BorderLayout.SOUTH);
		content.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 20));

		execute.addActionListener (this);
		execute.setBorder (BorderFactory.createEmptyBorder (0, 50, 50, 50));
		JPanel cmdPane = new JPanel (new BorderLayout ());
		cmdPane.add (execute, BorderLayout.CENTER);

		JPanel contentPane = new JPanel (new BorderLayout ());
		contentPane.add (content, BorderLayout.CENTER);
		contentPane.add (cmdPane, BorderLayout.SOUTH);

		setContentPane (contentPane);
		repaint ();
		setSize (WIDTH, HEIGHT);
		setVisible (true);

		// pop up DB connection UI
		connUI = new DBConnectionUI (this);
		connUI.addDBConnectionUIListener (this);
		connUI.pack ();
		connUI.setVisible (true);
	} else if (evt.getSource () == execute) {		
		String queryStr = queryArea.getText ();
		msgArea.append ("Executing query:\n" + queryStr + "\n");
		queryItemClicked (queryStr);
	}; // if

    }; // actionPerformed

    void startItemClicked ()
    {
        if (connInfo == null) {
            trc.show ("startItemClicked", "using default value for "
			+ "scenarioID: no database connection");
            msgArea.append ("Not connected to database. Using default value..");
            scenarioAgent.fireInstructEvent (null);
            return;
        } // if
              
        String queryStr = "SELECT seq FROM sequence";
        Query query = new Query ("Sequence query", queryStr);
        scenarioAgent.fireQueryEvent (query);
                
    }; // startItemClicked
     
    void queryItemClicked (String queryStr)
    {
        if (connInfo == null) {
            trc.show ("queryItemClicked", "cannot connect to database: "
                                + "connInfo = " + connInfo);
            msgArea.append ("Can not create connection to the database.");
            return;
        } // if

	Query query = null;

	String cmd = queryStr.toLowerCase ();
	if (cmd.startsWith ("select")) {
            query = new Query ("Normal query", queryStr);
	} else {
	    query = new Query ("Update", queryStr);
	}; // if

        scenarioAgent.fireQueryEvent (query);
                     
    }; // queryItemClicked


    public void notify (DBConnectionUIEvent evt)
    {
	connInfo = evt.getDBConnectionInfo ();
	scenarioAgent.setDBConnectionInfo (connInfo);

	trc.show ("notify", "startRequested: " + startRequested);
	if (startRequested) {
	    startRequested = false;
	    startItemClicked ();
	}; // if

	connUI.dispose ();
    }

    public static void main (String [] args)
    {
	new ScenarioWindow (null, new JTextArea (5, 30));
    }; // main

}; // class ScenarioWindow
