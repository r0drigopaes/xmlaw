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
import java.io.*;

public abstract class Node implements Serializable{
  protected Vector embedded_nodes = null;
  
  public Node(){
    embedded_nodes = new Vector(5);
  }
  
  public void addNode(Node n){
    embedded_nodes.addElement(n);
  }
  
  public Node getNode(int i){
    return (Node)embedded_nodes.elementAt(i);
  }
  
  public Node[] getChildNodes(){
    Node[] tmp = new Node[embedded_nodes.size()];
    for(int i=0; i<tmp.length; i++){
      tmp[i] = getNode(i);
    }
    return tmp;
  }

  public int getLength(){
    return embedded_nodes.size();
  }
    
  public boolean hasChildNodes(){
    if(embedded_nodes==null && embedded_nodes.size()==0){
      return false;
    }
    return true;
  }
}