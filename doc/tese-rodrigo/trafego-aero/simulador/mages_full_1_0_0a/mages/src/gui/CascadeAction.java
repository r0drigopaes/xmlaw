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

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class CascadeAction extends AbstractAction{
  JDesktopPane desktop = null;
  
  public CascadeAction(JDesktopPane desk){
    super("Cascade");
    desktop = desk;
  }
  

  public void actionPerformed(ActionEvent e){
    int x = 30;
    int y = 30;
    int c = 0;
    JInternalFrame[] frames = desktop.getAllFrames();
    int i =0;
    for(i=(frames.length-1);i>=0; i--){      
      if(frames[i].getLayer()!=JLayeredPane.PALETTE_LAYER.intValue()){
        try{
          if(frames[i].isIcon()){                   
            frames[i].setIcon(false);                          
          }       
          if(frames[i].isSelected()){
            frames[i].setSelected(false);      
          }        
          frames[i].setLocation(x*c,y*c);
          c++;        
          frames[i].setSelected(true);              
        }
        catch(PropertyVetoException ex){}                                   
      }            
    }  
  }
}