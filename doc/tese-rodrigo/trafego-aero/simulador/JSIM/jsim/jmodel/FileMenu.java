/******************************************************************
 * @(#) FileMenu.java     1.3     98/4/16
 * 
 * Copyright (c) 2005, John Miller
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   
 * 1. Redistributions of source code must retain the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer. 
 * 2. Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials
 *    provided with the distribution. 
 * 3. Neither the name of the University of Georgia nor the names
 *    of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @version     1.3, 28 Jan 2005
 * @author      John Miller
 */

package jsim.jmodel;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import jsim.util.*;


/******************************************************************
 * The file menu for the JMODEL designer.
 */

public class FileMenu extends JMenu
                      implements ActionListener
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Containing frame.
     */
    private NetFrame   localFrame;

    /**
     * Containing frame.
     */
    private NetCanvas  designCanvas;

    /**
     * Create a new model.
     */
    private JMenuItem  doNew    = new JMenuItem ("New");

    /**
     * Open/load model.
     */
    private JMenuItem  doOpen   = new JMenuItem ("Open");

    /**
     * Save model.
     */
    private JMenuItem  doSave   = new JMenuItem ("Save");

    /**
     * Save as model.
     */
    private JMenuItem  doSaveAs = new JMenuItem ("Save As");

    /**
     * Save and exit.
     */
    private JMenuItem  doExit   = new JMenuItem ("Exit");

    /**
     * Exit only.
     */
    private JMenuItem  doQuit   = new JMenuItem ("Quit");


    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Filename to hold saved model.
     */
    //private String  fileName = "JModel.jm";
    private String  fileName; 
    //private String  fileName = new String (System.getProperty("user.dir")+System.getProperty("file.separator")+"JModel.jm");

    /**
     * Name of model.
     */
    private String  modelName;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 1554377226778654595L;
    
    /************************************************************************
     * Construct a menu for the file option in menubar.
     * @param title         menu title
     * @param localFrame    containing frame
     * @param designCanvas  drawing canvas
     */
    public FileMenu (String title, NetFrame localFrame, NetCanvas designCanvas)
    {
        super (title);
        this.localFrame   = localFrame;
        this.designCanvas = designCanvas;

        doNew.addActionListener   (this);
        doOpen.addActionListener   (this);
        doSave.addActionListener   (this);
        doSaveAs.addActionListener (this);
        doExit.addActionListener   (this);
        doQuit.addActionListener   (this);

        add (doNew);     addSeparator ();
        add (doOpen);    addSeparator ();
        add (doSave);    addSeparator ();
        add (doSaveAs);  addSeparator ();
        add (doExit);    addSeparator ();
        add (doQuit);

    //fileName = new String (System.getProperty("user.dir")+System.getProperty("file.separator")+"JModel.jm");
    fileName = new String ("JModel.jm");
    }; // FileMenu


    /************************************************************************
     * Get the file name for the current design model.
     * @return  String  name of file storing serialized model
     */
    public String getFileName ()
    {
        return fileName;

    }; // getFileName


    /************************************************************************
     * Set the file name for the current design model.
     * @param  fileName  name of file holding model
     */
    public void setFileName (String fileName)
    {
        this.fileName = fileName;

        int ext   = fileName.lastIndexOf (".jm");
        modelName = fileName.substring (0, ext);
        designCanvas.setModelName (modelName);

    }; // setFileName


    /************************************************************************
     * Handle menu selection event.
     * @param  evt  action event
     */
    public void actionPerformed (ActionEvent evt)
    {
        // NEW
        if ( evt.getSource () == doNew ) {
            System.out.println ( "NEW selected" );
            designCanvas.reset (); 

        // OPEN
        } else if (evt.getSource () == doOpen) {
            System.out.println ("OPEN selected");
            FileDialog fDialog = new FileDialog (localFrame, this);
            // fDialog.show (); the show method was deprecated as of jdk 1.5
			fDialog.setVisible(true);
            // dialog may reset fileName
            designCanvas.load (fileName);

        // SAVE AS
        } else if (evt.getSource () == doSaveAs) {
            System.out.println ("SAVE AS selected");
            FileDialog fDialog = new FileDialog (localFrame, this);
			// fDialog.show (); the show method was deprecated as of jdk 1.5
			fDialog.setVisible(true);            
            // dialog may reset fileName
            designCanvas.save (fileName);

        // SAVE 
        } else if (evt.getSource () == doSave) {
            System.out.println ("SAVE selected");
            // dialog may reset fileName
            designCanvas.save (fileName);

        // EXIT
        } else if (evt.getSource () == doExit) {
            System.out.println ("EXIT selected");
            designCanvas.save (fileName);
            localFrame.quit ();
            //System.exit (0);

        // QUIT
        } else {
            System.out.println ("QUIT selected");
            localFrame.quit ();
            //System.exit (0);
        }; // if

    }; // actionPerformed


}; // class

