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

package mages.app.mapeditor ;

import mages.simulation.*;
import mages.gui.*;
import mages.*;

import java.awt.*;
import java.awt.event.*;

public class BoardWindow extends Frame implements IFloatWindow{
  ScrollPane panel;
  BoardCanvas board;
  
  public BoardWindow(String t, int r, int c, int wc, int hc, int ly,IManager o){

    setTitle(t);
    board = new BoardCanvas(r,c,wc,hc,ly,o);
    panel = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
      panel.add(board);
    setLayout(new BorderLayout());
    add(panel,BorderLayout.CENTER);               
	         
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    setSize((int)(d.width*0.6),(int)(d.height*0.6));             
    
  }
   
  public Object getObject(){
    return board.getObject();
  }  

  public void setObject(Object o){  
    board.setObject(o);
  }      
}