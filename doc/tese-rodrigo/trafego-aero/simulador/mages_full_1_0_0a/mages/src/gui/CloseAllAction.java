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

public class CloseAllAction extends AbstractAction{
  JDesktopPane desktop = null;
  
  public CloseAllAction(JDesktopPane desk){
    super("Close all");
    desktop = desk;
  }
  

  public void actionPerformed(ActionEvent e){
    JInternalFrame[] allFrames = desktop.getAllFrames();   
    for(int i=0;i<allFrames.length; i++){    
      allFrames[i].dispose();
    }  
  }
}