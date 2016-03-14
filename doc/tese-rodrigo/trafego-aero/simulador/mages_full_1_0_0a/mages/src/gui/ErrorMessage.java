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


package mages.gui;

import java.awt.*;
import java.awt.event.*;

public class ErrorMessage extends Dialog{
  Button btnCancel = null;
  
  public ErrorMessage(Frame owner, String msg){
    super(owner);
    btnCancel = new Button("Ok");
    
    setTitle("Error");
    setResizable(false);
    Panel tmp = new Panel();
      tmp.setLayout(new FlowLayout(FlowLayout.CENTER));
      tmp.add(btnCancel);
    add(new Label(msg), BorderLayout.CENTER);
    add(tmp,BorderLayout.SOUTH);
    
    addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
    	  	   dispose();
			}
	 });
	 	 
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
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