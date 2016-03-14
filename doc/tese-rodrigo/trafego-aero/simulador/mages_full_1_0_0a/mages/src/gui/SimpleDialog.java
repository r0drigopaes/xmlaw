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

public class SimpleDialog extends Dialog{
  public static final boolean USE_SEARCH = true;
  public static final boolean NO_USE_SEARCH = false;  
  
  Button btnCancel = null;
  Button btnOk = null;
  Button btnSearch = null;  
  TextField edtText = null;
  Frame own = null;
  ProcessInfo pi = null;
  boolean mode = false;
  int id = -1;

  public SimpleDialog(Frame owner, String msg, String title, ProcessInfo pri, boolean m, int i){
    this(owner,msg,title,pri,m);
    id = i;
  }

  public SimpleDialog(Frame owner, String msg, String title, ProcessInfo pri, int i){
    this(owner,msg,title,pri);
    id = i;
  }
  
  public SimpleDialog(Frame owner, String msg, String title, ProcessInfo pri, boolean m){
    super(owner);  
    mode = m;
    pi = pri;
    init(owner, msg,title);        
  }

  public SimpleDialog(Frame owner, String msg, String title, ProcessInfo pri){
    super(owner); 
    pi = pri;    
    init(owner, msg,title); 
  }
  
  private void init(Frame owner, String msg, String title){          
    btnCancel = new Button("Cancel");    
    btnOk = new Button("Ok");    
    btnSearch = new Button("...");        
    edtText = new TextField(20);
    own = owner;
    
    setTitle(title);
    setResizable(false);
    setModal(true);
    Panel tmp = new Panel();
      tmp.setLayout(new FlowLayout(FlowLayout.CENTER));
      tmp.add(btnOk);      
      tmp.add(btnCancel);      
    
    Panel tmp2 = new Panel();
      tmp2.setLayout(new GridLayout(2,1));  
    
    Panel tmp3 = new Panel();
       tmp3.add(edtText);
    if(mode==USE_SEARCH){
       tmp3.add(btnSearch);
       btnSearch.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           String name = null;
           FileDialog dialog = new FileDialog(own,"Search",FileDialog.LOAD);
           dialog.setModal(true);
           dialog.show();
           edtText.setText(dialog.getDirectory()+dialog.getFile());           
         }
       });       
    }  
      
    tmp2.add(new Label(msg));
    tmp2.add(tmp3);
          
    add(tmp2, BorderLayout.CENTER);
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
    	
    btnOk.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         dispose();
         if(id==-1){
  		     pi.processInfo(edtText.getText());
		   }
		   else{
		     Object prm[] = new Object[2];
		     prm[0] = new Integer(id);
		     prm[1] = edtText.getText();
		     pi.processInfo(prm);
		   }    
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