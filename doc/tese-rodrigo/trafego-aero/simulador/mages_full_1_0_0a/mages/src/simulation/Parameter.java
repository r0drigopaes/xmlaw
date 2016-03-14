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

import java.io.*;

public class Parameter implements Serializable{
  String name;
  String value;
  String defaultValue;
  boolean mod;

  public Parameter(){
    name = null;
    value = null;
    defaultValue = null;    
    mod = false;   
  }
    
  public Parameter(String n, String v){
    name = n;
    value = v;
    defaultValue = new String(value);
    mod = false;
  }
  
  public Parameter(String n, String v, String dv){
    name = n;
    value = v;
    defaultValue = dv;
    mod = false;
  }  
  
  public void setName(String n){
    name = n;
  }  
  
  public String getName(){
    return name;
  }   

  public String getDefaultValue(){
    return defaultValue;
  }    
  
  public void setValue(String v){
    value = v;
    if(value.equals(defaultValue)){
      mod = false;
    }
    else{
      mod = true;
    }
  }  
  
  public String getValue(){
    return value;
  }  
  
  public Object clone(){
    String n = new String(getName());
    String v = new String(getValue());
    String dv = new String(getDefaultValue());    
    Parameter p = new Parameter(n,v,dv);
    return p;
  }
  
  public boolean isModified(){
    return (value.equals(defaultValue))?false:true;
  }
}