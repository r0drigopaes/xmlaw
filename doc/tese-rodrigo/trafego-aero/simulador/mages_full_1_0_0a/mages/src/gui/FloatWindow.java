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

public class FloatWindow extends Window{
  String message;
  Label lblMsg;
  
  public FloatWindow(Frame f, String m, Dimension dim){
    super(f);
    message = m;
    lblMsg = new Label("   "+message+"   ");
    add(lblMsg,BorderLayout.CENTER);
    setSize(dim.width, dim.height);    
    center();
    repaint();
  }  
  
  public void setMessage(String m){
    message = "   "+m+"   ";
    lblMsg.setText(message);
    pack();
    repaint();
  }
    
  public String getMessage(){
    return message.trim();
  }
  
  public void update(Graphics g){
    paint(g);
  }
  
  public void paint(Graphics g){
    super.paint(g);  
    lblMsg.getGraphics().setColor(Color.black);
    lblMsg.getGraphics().drawRect(0,0,lblMsg.getWidth()-1,lblMsg.getHeight()-1);  
  }
  
  private void center(){
   Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); 
	Rectangle bounds = new Rectangle(d.width,d.height); 
	Rectangle abounds = getBounds(); 
	setLocation(bounds.x + (bounds.width - abounds.width) / 2, 
  	      	   bounds.y + (bounds.height - abounds.height) / 2);   
  }    
}