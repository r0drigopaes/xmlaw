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

package mages.agents;

import java.util.*;

public class ActionsCollection{
  Vector actions;
  int maxPrm = 0;
  
  public ActionsCollection(){
    actions = new Vector();
  }
  
  public void addAction(Action a){
    actions.addElement(a);
    maxPrm = (a.getNumParameters()>maxPrm)?a.getNumParameters():maxPrm;
  }
  
  public Action getAction(int index){
    return (Action)actions.elementAt(index);
  }
  
  public Action[] getActions(){
    Action[] act = new Action[actions.size()];
    for(int i=0; i<actions.size(); i++){
      act[i] = getAction(i);
    }
    return act;
  }
  
  public int getSize(){
    return actions.size();
  }
  
  public int getMaxParameters(){
    return maxPrm;
  }
}