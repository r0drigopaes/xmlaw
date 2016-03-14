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

public class TileAction extends AbstractAction{
  JDesktopPane desktop = null;
  
  public TileAction(JDesktopPane desk){
    super("Tile");
    desktop = desk;
  }
  
  //Sample Chapter 9: Internal Frames
  //http://www.oreilly.com/catalog/jswing/chapter/ch09.html    
  //Java Swing (book)
  public void actionPerformed(ActionEvent e){
    JInternalFrame[] allframes = desktop.getAllFramesInLayer(JLayeredPane.DEFAULT_LAYER.intValue()); 
   int count = allframes.length; 
   if (count == 0) return; 
   // Determine the necessary grid size 
   int sqrt = (int)Math.sqrt(count); 
   int rows = sqrt; 
   int cols = sqrt; 
   if(rows*cols < count){ 
     cols++; 
     if(rows*cols < count){ 
       rows++; 
     } 
   } 

   Dimension size = desktop.getSize(); 
   
   int w = size.width/cols; 
   int h = size.height/rows; 
   int x = 0; 
   int y = 0; 
   for(int i=0; i<rows; i++){ 
     for(int j=0; j<cols && ((i*cols)+j<count); j++){ 
       JInternalFrame f = allframes[(i*cols)+j]; 
         try{ 
           if(f.isIcon() == true){                  
             f.setIcon(false);             
           }  
           if(f.isSelected() == true){                  
             f.setSelected(false);             
           }            
         }
         catch(PropertyVetoException ex){} 
         f.setBounds(new Rectangle(w,h));
         f.setLocation(x,y);
          x += w;                  
     } 
     y += h;
     x = 0; 
   }  
  }
}