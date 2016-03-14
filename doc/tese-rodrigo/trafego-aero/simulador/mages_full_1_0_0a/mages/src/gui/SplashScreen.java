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
import javax.swing.*;

public class SplashScreen extends JWindow implements Runnable{

  Thread thread = null;
  int time = 5000;
  Image img = null;
  JFrame own;
  
  //time in seconds
  public SplashScreen(JFrame parent, String path, int tm){
    super(parent);
    own = parent;
    own.setEnabled(false);    
    time = tm*1000;
    img = MagesEnv.loadImage(path);
    int width = img.getWidth(this);
    int height = img.getHeight(this);
    
    setSize(width,height); 
    
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();     

    Rectangle bounds = new Rectangle(screen.width,screen.height);     
	 Rectangle abounds = getBounds(); 
	 setLocation(bounds.x + (bounds.width - abounds.width) / 2, 
  					 bounds.y + (bounds.height - abounds.height) / 2); 
					       
    thread = new Thread(this); 
    show();       
    thread.start();    
  }
  
  public void paint(Graphics g){
    super.paint(g);
    if(img!=null)
      g.drawImage(img,0,0,this);
  }
    
  public void run(){
    try{
      thread.sleep(time);
    }
    catch(Exception e){}
    dispose();  
    own.setEnabled(true);          
  }
}