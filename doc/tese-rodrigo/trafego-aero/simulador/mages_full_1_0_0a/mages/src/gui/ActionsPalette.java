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

import mages.agents.Action;
import mages.agents.ActionsCollection;
import java.awt.*;
import javax.swing.*;

public class ActionsPalette extends JInternalFrame{
  ActionsCollection ac = null;
  AgentWindow agentWindow = null;
  final int MAX_TO_LINE = 5;
  final int WIDTH_BUTTON = 80;
  final int HEIGHT_BUTTON = 100;  
     
  public ActionsPalette(AgentWindow aw, ActionsCollection a){
    super();
    ac = a;
    agentWindow = aw;
    setTitle("Actions");
    setIconifiable(true);
    setMaximizable(false);
    setResizable(true);
    setVisible(true);
    setOpaque(true);
    
                   
    JPanel p = new JPanel();
    createButtons(p);                        
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(p,BorderLayout.CENTER);
    validate();        
  }
  
  private void createButtons(JPanel p){
    int len = ac.getSize();
    if(len<=MAX_TO_LINE){
      p.setLayout(new GridLayout(1,len));
      setBounds(0,0,len*WIDTH_BUTTON,HEIGHT_BUTTON);      
    }
    else{
      int lines = ((int)(len/MAX_TO_LINE))+1;
      p.setLayout(new GridLayout(lines,MAX_TO_LINE));
      setBounds(0,0,MAX_TO_LINE*WIDTH_BUTTON,lines*HEIGHT_BUTTON);    
    }
    for(int i=0; i<len; i++){
      ActionButton ab = new ActionButton(this,ac.getAction(i));
      p.add(ab);
    }    
  }
  
  public void paint(Graphics g){
    super.paint(g);    
  }
  
  public void setAction(Action a){
    agentWindow.setAction(a);         
  }
   
  public JFrame getOwner(){
    return agentWindow.getOwner();
  }
}