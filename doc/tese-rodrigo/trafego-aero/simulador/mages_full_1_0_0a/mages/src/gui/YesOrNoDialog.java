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

public class YesOrNoDialog extends Dialog{
  
  Button btnYes = null;
  Button btnNo = null;
  Label txt = null;
  Frame own = null;
  ProcessInfo pi = null;
  int id = -1;

  public YesOrNoDialog(Frame owner, String msg, String title, ProcessInfo p, int i){
    super(owner);  
    pi = p;   
    id = i; 
    init(owner, msg,title);        
  }
  
  private void init(Frame owner, String msg, String title){          
    btnYes = new Button("Yes");    
    btnNo = new Button("No");    
    txt = new Label(msg);
    own = owner;
        
    setTitle(title);
    setResizable(false);
    setModal(true);
    Panel tmp = new Panel();
      tmp.setLayout(new FlowLayout(FlowLayout.CENTER));
      tmp.add(btnYes);      
      tmp.add(btnNo);      
    
    
    Panel tmp2 = new Panel();
       tmp2.add(txt);
      
    add(tmp2, BorderLayout.NORTH);
    add(tmp,BorderLayout.SOUTH);
    
    addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
     	   dispose();
		   pi.processInfo(new Boolean(false));	        
	   }
	 });
	 	 
    btnYes.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
        Object[] vector = new Object[2];
        vector[0] = new Integer(id);
        vector[1] = new Boolean(true);        
   	  pi.processInfo(vector);        
      }
    });
    	
    btnNo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         dispose();
         Object[] vector = new Object[2];
         vector[0] = new Integer(id);
         vector[1] = new Boolean(false);        
     	   pi.processInfo(vector);                 
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