/*
GNU Mages, version 1.00 alpha
Multi-Agents Environment Simulator
Copyright (C) 2001-2002 João Ricardo Bittencourt <jrbitt@uol.com.br>
 
This program is free software; you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version. 

This program is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
GNU General Public License for more details. 

You should have received a copy of the GNU General Public License 
along with this program; if not, write to the Free Software 
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package mages.app.mapeditor;

import mages.gui.*;

import java.awt.*;
import java.awt.event.*;

public class InitialDialog extends Dialog{
  BorderLayout borderLayout1 = new BorderLayout();
  Panel pnlInfo = new Panel();
  GridLayout gridLayout1 = new GridLayout();
  Label lblSchema = new Label();
  TextField edtSchema = new TextField();
  Label lblWidth = new Label();
  TextField edtWidth = new TextField();
  Label lblHeight = new Label();
  TextField edtHeight = new TextField();  
  Panel panel1 = new Panel();
  FlowLayout flowLayout1 = new FlowLayout();
  Button btnOk = new Button();
  Button btnCancel = new Button();

  ProcessInfo pi = null;
  Frame own = null;
  
  public InitialDialog(Frame f,ProcessInfo p) {
    super(f);
    pi = p;
    own = f;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    this.setResizable(false);
    this.setModal(true);
    pnlInfo.setLayout(gridLayout1);
    gridLayout1.setColumns(2);
    gridLayout1.setHgap(5);
    gridLayout1.setRows(3);
    gridLayout1.setVgap(10);
    lblSchema.setText("Map Schema: ");
    lblWidth.setText("Width (x): ");
    lblHeight.setText("Height (y): ");
    panel1.setLayout(flowLayout1);
    btnOk.setLabel("Ok");
    btnCancel.setLabel("Cancel");
    this.add(pnlInfo, BorderLayout.CENTER);
    pnlInfo.add(lblSchema, null);
    pnlInfo.add(edtSchema, null);
    pnlInfo.add(lblWidth, null);
    pnlInfo.add(edtWidth, null);
    pnlInfo.add(lblHeight, null);
    pnlInfo.add(edtHeight, null);
    this.add(panel1, BorderLayout.SOUTH);
    panel1.add(btnOk, null);
    panel1.add(btnCancel, null);
    
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
   	  pi.processInfo(null);        
      }
    });
    	
    btnOk.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         dispose();
         Object[] values = new Object[3];
         values[0] = edtSchema.getText();
         values[1] = edtWidth.getText();
         values[2] = edtHeight.getText();         
		   pi.processInfo(values);
      }
    });
        
    pack();
    		          
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); 
	 Rectangle bounds = new Rectangle(d.width,d.height); 
	 Rectangle abounds = getBounds(); 
	 setLocation(bounds.x + (bounds.width - abounds.width) / 2, 
  	   		    bounds.y + (bounds.height - abounds.height) / 2);     
  }
}