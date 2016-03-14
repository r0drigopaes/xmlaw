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


package mages.simulation;

import java.util.*;

public class SimObjectCollection{
  String name;
  String id;
  int maxWidth, maxHeight;
  Vector sobjects;
  
  public SimObjectCollection(String n, String i){
    name = n;
    id = i;
    sobjects = new Vector();
  }
  
  public void setName(String n){
    name = n;
  }

  public String getName(){
    return name;
  }

  public void setId(String i){
    id = i;
  }

  public String getId(){
    return id;
  }

  public int getMaxWidth(){
    return maxWidth;
  }

  public int getMaxHeight(){
    return maxHeight;
  }  
  
  public void addSimObject(SimObject so){
    sobjects.addElement(so);
    if(so.getMaxWidth()>maxWidth){
      maxWidth = so.getMaxWidth();
    }
    if(so.getMaxHeight()>maxHeight){
      maxHeight = so.getMaxHeight();
    }    
  }
  
  public SimObject getSimObject(int index){
    return (SimObject)sobjects.elementAt(index);
  }
  
  public SimObject[] getSimObjects(){
    SimObject[] tmp = new SimObject[sobjects.size()];

    for(int i=0; i<sobjects.size(); i++){
      SimObject so = getSimObject(i);      
      tmp[i] = so;
    }
    return tmp;
  }   
  
  public int getSize(){
    return sobjects.size();
  }
  
  public SimObject[] split(){
    Vector tmp = new Vector();
    for(int i=0; i<sobjects.size(); i++){
      SimObject[] tmp2 = getSimObject(i).split();
      if(tmp2!=null){
        for(int j=0; j<tmp2.length; j++){
          tmp.addElement(tmp2[j]);
        }
      }
    }
    
    SimObject[] tmp3 = new SimObject[tmp.size()];
    for(int k=0; k<tmp3.length;k++){
      tmp3[k]=(SimObject)tmp.elementAt(k);
    }
    return tmp3;
  }
}