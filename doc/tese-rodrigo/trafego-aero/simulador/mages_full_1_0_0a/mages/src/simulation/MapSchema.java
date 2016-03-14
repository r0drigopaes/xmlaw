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

import mages.app.*;
import java.util.*;
import java.io.*;

public class MapSchema implements Serializable{
  String name;
  String validator;
  Vector cols = null;

  public MapSchema(){
    cols = null;  
    name = null;
    validator = null;
  }
    
  public MapSchema(String n){
    cols = new Vector();  
    name = n;
    validator = null;    
  }
  
  public void setName(String n){
    name = n;
  }
  
  public String getName(){
    return name;
  }
   
  public void setValidatorName(String n){
    validator = n;
  }
  
  public String getValidatorName(){
    return validator;
  }
        
  public void setMapSchemaEntry(MapSchemaEntry mse){
    cols.addElement(mse);
  }
  
  public void setMapSchemaEntry(MapSchemaEntry[] msec){
    for(int i=0; i<msec.length; i++){
      cols.addElement(msec[i]);
    }
  }
  
  public MapSchemaEntry getMapSchemaEntry(int l){
    return (MapSchemaEntry)cols.elementAt(l);
  }
  
  public MapSchemaEntry[] getMapSchemaEntry(){
    MapSchemaEntry[] tmp = new MapSchemaEntry[cols.size()];
    for(int i=0; i<tmp.length; i++){
      tmp[i] = getMapSchemaEntry(i);
    }  
    return tmp;
  }    
  
  public int getNumLayers(){
    return cols.size();
  }
  
  public IValidator getValidator() throws InstantiationException,IllegalAccessException,ClassNotFoundException{
    Class cls = Class.forName(validator);
    return (IValidator)cls.newInstance();		      
  }
  
  public Object clone(){
    MapSchema tmp = new MapSchema(getName());
    tmp.setValidatorName(getValidatorName());
    return tmp;
  }
}