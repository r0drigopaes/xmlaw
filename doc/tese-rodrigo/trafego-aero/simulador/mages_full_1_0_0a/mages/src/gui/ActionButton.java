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
import mages.gui.*;

import javax.swing.JButton;
import java.awt.event.*;
import java.util.*;

public class ActionButton extends JButton implements ProcessInfo{
  Action action = null;
  ActionsPalette palette = null;
  
  public ActionButton(ActionsPalette ap, Action a){
    action = a;
    palette = ap;
    setText(action.getName());
    setOpaque(true);
    
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(action.getNeedParameters()){
          SimpleDialog dialog = new SimpleDialog(palette.getOwner(),
                                                 action.getInstructionParameters(),
                                                 "Action - "+action.getName(),
                                                 ActionButton.this);
          dialog.show();                                                 
        }
        else{
          palette.setAction((Action)action.clone());
        } 
      }
    });    
  }
   
  public void processInfo(Object info){
    String str = (String)info;
    StringTokenizer token = new StringTokenizer(str," ");
    Vector tmp = new Vector();
    while(token.hasMoreTokens()){
      tmp.addElement(token.nextToken());
    }
    String[] prm = new String[tmp.size()];
    for(int i=0; i<tmp.size(); i++){
      prm[i] = (String)tmp.elementAt(i);
    }    
    if(action.defineParameters(prm)){
      palette.setAction((Action)action.clone());          
    }  
    else{
      ErrorMessage em = new ErrorMessage(palette.getOwner(),
                                         "Action parameters with problems");
      em.show();            
    }
  }  
}