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

package mages.controller;

import mages.gui.*;
import mages.*;
import mages.agents.*;
import mages.controller.ctf.client.*;
import javax.swing.*;
import java.awt.*;

public class DefaultMain{

  public static void factory(MagesClient mc, Agent obj, boolean human, boolean log){
    int mode = (human)?AgentWindow.HUMAN:AgentWindow.COMPUTER;        
    AgentWindow aw = new AgentWindow(mc,obj,mode, log);
    aw.setFrameIcon(new ImageIcon(mc.getIconImage()));
    mc.getDesktop().add(aw);
    mc.getToolBar().add(new FocusInternalFrame(obj.getName(),
                        aw,
                        mc.getDesktop()));   
    mc.getDesktop().getDesktopManager().activateFrame(aw);                                 
    aw.start();
  }
  
  public static void factory(MagesClient mc, Agent[] objs, boolean human, boolean log){     
    mc.getToolBar().setVisible(false);      
    for(int i=0; i<objs.length; i++){
      factory(mc,objs[i],human,log);
    }            
  }
}