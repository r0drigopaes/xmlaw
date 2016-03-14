
/******************************************************************
 * @(#) PropertyDialog.java     1.3
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
 * @version     1.3 (13 December 2000)
 * @author      Xueqin Huang, John Miller
 */

package jsim.jrun;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import jsim.util.*;
import jsim.variate.*;
import jsim.jmessage.ModelProperties;


/******************************************************************
 * Dialog box for updating a models properties.
 */

public class PropertyDialog extends JFrame
                            implements ActionListener
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * The calling model agent
     */
    private final ModelAgent      modelAgent;

    /**
     * Properties of the model.
     */
    private final ModelProperties prop;
    
    /**
     * JTable
     */
    private final JTable          table;

    /**
     * Apply button
     */
    private final JButton         applyButton = new JButton ("Apply");
    
    /**
     * Trace
     */
    private final Trace           trc;


    /**********************************************************************
     * Constructs a dialog box to adjust the properties for a node.
     * @param  pList  property list
     */
    public PropertyDialog (ModelAgent modelAgent, ModelProperties prop)
    {
        super ("Edit Properties of " + prop.getModelName () + " Model");

	trc = new Trace ("jrun", "PropertyDialog");
        this.modelAgent = modelAgent;
	this.prop = prop;
	
	PropertyTableModel propmodel = new PropertyTableModel (prop);
	table = new JTable (propmodel);
	table.setPreferredScrollableViewportSize (new Dimension(600, 70));

        JScrollPane scrollPane = new JScrollPane (table);
        initColumnSizes (table, propmodel);
        setUpDistColumn (table.getColumnModel().getColumn(2));
        getContentPane().add (scrollPane, BorderLayout.CENTER);
	applyButton.addActionListener (this);
	getContentPane().add (applyButton, BorderLayout.SOUTH);
 
        addWindowListener (new WindowAdapter () {
            public void WindowClosing (WindowEvent e) {
                System.exit (0);
            }});

	pack ();
	setVisible (true);

     }; // PropertyDialog


    /**********************************************************************
     * Sets up the width of the columns
     * @param table	The JTable that will display the model properties
     * @param model	The table model
     */
    private void initColumnSizes (JTable table, PropertyTableModel model) 
    {
        TableColumn column = null;
        for (int i = 0; i < 8; i++) {
            column = table.getColumnModel().getColumn(i);
	    column.sizeWidthToFit ();
	}; // for

    }; // initColumnSizes


    /**********************************************************************
     * Sets up the distribution column.
     * @param distColumn	The distribution column
     */
    public void setUpDistColumn (TableColumn distColumn) 
    {
        //Set up the editor for the time distribution cells
        JComboBox comboBox = new JComboBox();

	for (int i = 0; i < VarNames.NAME.length; i++) {
           comboBox.addItem (VarNames.NAME [i]);
	}; // for

        distColumn.setCellEditor (new DefaultCellEditor(comboBox));

        //Set up tool tips for the distribution cells.
        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        distColumn.setCellRenderer(renderer);

        //Set up tool tip for the distribution column header.
        TableCellRenderer headerRenderer = distColumn.getHeaderRenderer();
        if (headerRenderer instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) headerRenderer).setToolTipText(
                     "Click the distribution to see a list of choices");
        }; // if

    }; // setUpDistColumn


    /**********************************************************************
     * Inner class PropertyTableModel.
     */
    class PropertyTableModel extends AbstractTableModel 
    {
        /**
         * The column labels
         */
        final String []  columnNames = {"Token Type", 
                                      "Token Name",
                                      "Distribution",
                                      "# of Tokens",
                                      "Alpha",
				      "Beta",
				      "Gamma",
				      "Stream"};
	/**
	 * The data to be displayed
	 */
        final Object[][] data;

	/**
	 * The Trace facility
	 */
	final Trace	 trc = new Trace ("jrun", "PropertyDialog");


        /**********************************************************************
         * Constructs a PropertyTableModel instance.
	 * @param prop	The model properties
	 */
	public PropertyTableModel (ModelProperties prop)
	{
	    // set up data

            int []    nodeType = prop.getNodeType ();
	    int       numNodes = prop.getNumOfNodes ();
	    String [] nType = new String [numNodes];
	    for (int i = 0; i < numNodes; i++) {
		nType [i] = Node.TYPE_NAME [nodeType [i]];
	    }; // for

            String []  nName = prop.getNodeName ();
            String []  distType = prop.getDistributionType ();

            int []     numTokens = prop.getNumOfTokens ();  
	    Integer [] nTokens = new Integer [numNodes];
	    for (int i = 0; i < numNodes; i++) {
		nTokens [i] = new Integer (numTokens [i]);
	    }; // for

            Double []  alpha = prop.getAlpha ();
            Double []  beta = prop.getBeta ();
            Double []  gamma = prop.getGamma ();
            Integer [] stream = prop.getStream ();

	    data = new Object [numNodes][8];
	    for (int i = 0; i < numNodes; i++) {
	        data [i][0] = nType [i];
		data [i][1] = nName [i];
		data [i][2] = distType [i];
		data [i][3] = nTokens [i];
		data [i][4] = alpha [i];
		data [i][5] = beta [i];
		data [i][6] = gamma [i];
		data [i][7] = stream [i];
	    }; // for

	}; // PropertyTableModel constructor


        /**********************************************************************
         * Returns the column count.
	 * @return int	The column count
	 */
        public int getColumnCount () 
	{
            return columnNames.length;

        }; // getColumnCount


        /**********************************************************************
         * Returns the row count.
	 * @return 	The row count
	 */       
        public int getRowCount () 
	{
            return data.length;

        }; // getRowCount


            
        /**********************************************************************
         * Returns the column label.
	 * @return String	The column label
	 */
        public String getColumnName (int col) 
	{
            return columnNames [col];

        }; // getColumnName


        /**********************************************************************
         * Returns the value at a given position.
	 * @param row	The row index
	 * @param col	The column index
	 * @return Object	The value
	 */
        public Object getValueAt (int row, int col) 
	{
            return data [row][col];

        }; // getValueAt


        /**********************************************************************
         * JTable uses this method to determine the default renderer/
         * editor for each cell.
	 * @param c		The column
	 * @return Class	The Class object of column c
         */
        public Class getColumnClass (int c) 
	{
	    try {
	        if (c == 0 || c == 1) {
		    return Class.forName ("java.lang.String");
	        } else if (c == 2) {
		    return Class.forName ("javax.swing.JComboBox");
	        } else if (c == 3 || c == 7) {
		    return Class.forName ("java.lang.Integer");
	    	} else {
		    return Class.forName ("java.lang.Double");
	        } // if
	    } catch (Exception e) {
		e.printStackTrace ();
		return null;
	    }

        }; // getColumnClass


        /**********************************************************************
         * Returns a boolean value indicating whether a cell is editable.
	 * @param row		The row
	 * @param col		The column
	 * @return boolean	True if this cell is editable, else False
	 */
        public boolean isCellEditable (int row, int col) 
 	{
	    if (col < 2) {
		return false;
	    } else {
                return true;
	    } // if

        }; // isCellEditable


        /**********************************************************************
         * Sets the value at a cell.
	 * @param value		The new value
	 * @param row		The row
	 * @param col		The column
	 */
        public void setValueAt (Object value, int row, int col) 
	{
            trc.show ("setValueAt", "Setting value at " + row + "," + col
                                   + " to " + value);
	    if (value != null) {
		trc.show ("setValueAt", " (an instance of " 
                                   + value.getClass() + ")");
	    }; // if

	    if (value == null) {
		data [row][col] = null;
		fireTableCellUpdated (row, col);
	    } else if (data [0][col] instanceof Integer) {

                //With JFC/Swing 1.1 and JDK 1.2, we need to create
                //an Integer from the value; otherwise, the column
                //switches to contain Strings.  Starting with v 1.3,
                //the table automatically converts value to an Integer,
                //so you only need the code in the 'else' part of this
                //'if' block.
                try {
                    data [row][col] = new Integer (value.toString());
                    fireTableCellUpdated (row, col);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog (PropertyDialog.this,
                        "The \"" + getColumnName (col)
                        + "\" column accepts only integer values.");
                }
	    } else if (data [0][col] instanceof Double) {
                try {
                    data [row][col] = new Double (value.toString());
                    fireTableCellUpdated (row, col);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog (PropertyDialog.this,
                        "The \"" + getColumnName (col)  
                        + "\" column accepts only integer values.");
                }
            } else {
                data [row][col] = value;
                fireTableCellUpdated (row, col);
            }; // if

            printDebugData ();

        }; // setValueAt


        /**********************************************************************
         * Prints out debugging information.
	 */
        private void printDebugData() 
	{

            int numRows = getRowCount();
            int numCols = getColumnCount();

            trc.show ("PropertyDialog", "New value of data:");
            for (int i = 0; i < numRows; i++) {
                System.out.print ("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                    System.out.print("  " + data[i][j]);
                }; // for
                System.out.println();
            }; // for
            System.out.println("--------------------------");

        }; // printDebugData

    }; // inner class PropertyTableModel


    /**********************************************************************
     * Handles button events.
     *     Apply:  update the nodes parameters and then dismiss dialog box.
     * @param  evt  The button action event
     */
    public void actionPerformed (ActionEvent evt)
    {

        if (evt.getSource () == applyButton) {

	    table.editCellAt (0, 0);

            int numOfNodes = prop.getNumOfNodes ();
            String []  distType = new String [numOfNodes];
            Integer [] numTokens = new Integer [numOfNodes];
	    int []     nTokens = new int [numOfNodes];
            Double []  alpha = new Double [numOfNodes];
            Double []  beta = new Double [numOfNodes];
            Double []  gamma = new Double [numOfNodes];
            Integer [] stream = new Integer [numOfNodes];
	    Object     value;

	    TableModel model = table.getModel ();
            for (int i = 0; i < numOfNodes; i++) {
		distType [i] = (String) model.getValueAt (i, 2);

		numTokens [i] = (Integer) model.getValueAt (i, 3);
		nTokens [i] = numTokens [i].intValue ();
	
	 	value = model.getValueAt (i, 4);
		if (value == null) {
			alpha [i] = null;
		} else {
			alpha [i] = (Double) value;
		}; // if

		value = model.getValueAt (i, 5);
		if (value == null) {
			beta [i] = null;
		} else {
			beta [i] = (Double) value;
		}; // if

		value = model.getValueAt (i, 6);
		if (value == null) {
			gamma [i] = null;
		} else {
			gamma [i] = (Double) value;
		}; // if

		value = model.getValueAt (i, 7);
		if (value == null) {
			stream [i] = null;
		} else {
			stream [i] = (Integer) value;
		}; // if
	    }; // for

            prop.setDistributionType (distType);
            prop.setNumOfTokens (nTokens);
            prop.setAlpha (alpha);
            prop.setBeta (beta);
            prop.setGamma (gamma);
            prop.setStream (stream);

        }; // if

        modelAgent.fireChangeEvent (prop);
        dispose ();

    }; // actionPerformed


    /**********************************************************************
     * Main method for testing PropertyDialog.
     * @param  args  command-line arguments
     */
    public static void main (String [] args)
    {
    }; // main


}; // class PropertyDialog

