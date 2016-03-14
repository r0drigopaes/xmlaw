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

import mages.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AboutBox extends JDialog{  
  Image logoSoftware, logoCompany;
  JPanel pnlLogoSoft, pnlLogoCompany;
  JTextArea txtCopy, txtCred;
  JButton btnClose;
  
  public AboutBox(Frame own, String logo, String copy, 
                  String[] cred, String comp, String title){
    super(own);
        
    logoSoftware = MagesEnv.loadImage(logo);
    int widthSoft = logoSoftware.getWidth(this);
    int heightSoft = logoSoftware.getHeight(this);        
    pnlLogoSoft = new JPanel();
    pnlLogoSoft.setSize(widthSoft,heightSoft);
    
    txtCopy = new JTextArea(copy);
    txtCopy.setEditable(false);
    txtCopy.setOpaque(false);    
    txtCopy.setWrapStyleWord(true);
    
    if(cred!=null){
      txtCred = new JTextArea("\n\nCredits\n");
      txtCred.setEditable(false);    
      txtCred.setOpaque(false);
      txtCred.setWrapStyleWord(true);    
      for(int i=0; i<cred.length; i++){
        txtCred.append(cred[i]+"\n");
      }
    }  

    int heightComp = 0;            
    if(comp!=null){
      logoCompany = MagesEnv.loadImage(comp);
      heightComp = logoCompany.getHeight(this);        
      pnlLogoCompany = new JPanel();
      pnlLogoCompany.setSize(widthSoft,heightComp);    
    }
    
    btnClose = new JButton("Close");
    JPanel tmp = new JPanel();
    tmp.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmp.add(btnClose);

    int nl = 5;
    if(comp==null) nl--;
    if(cred==null) nl--;
    
    setModal(true);
    setTitle(title);
    setResizable(false);    
    Insets ins = getInsets();
    int width = ins.left+ins.right+widthSoft;
    int height = ins.top+ins.bottom+heightComp+(heightSoft*(nl+1));
    setSize(new Dimension(width,height));
        
    
    getContentPane().setLayout(new GridLayout(nl,1));
    getContentPane().add(pnlLogoSoft);
    getContentPane().add(txtCopy);
    if(cred!=null) getContentPane().add(txtCred);    
    if(comp!=null) getContentPane().add(pnlLogoCompany);    
    getContentPane().add(tmp);    
    
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();     

    Rectangle bounds = new Rectangle(screen.width,screen.height);     
	 Rectangle abounds = getBounds(); 
	 setLocation(bounds.x + (bounds.width - abounds.width) / 2, 
  					 bounds.y + (bounds.height - abounds.height) / 2); 
					       
    initEvents();
  }
 
  public void paint(Graphics g){
    super.paint(g);
    if(logoSoftware!=null)
      pnlLogoSoft.getGraphics().drawImage(logoSoftware,0,0,this);
    if(logoCompany!=null)
      pnlLogoCompany.getGraphics().drawImage(logoCompany,0,0,this);      
  }
    
  private void initEvents(){
    addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
  	  	   dispose();
   	}
	 });  
	 
    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         dispose();
      }
    });	 	   
  }
}