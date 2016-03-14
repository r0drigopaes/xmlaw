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

public class MapSchemaEntry implements Serializable{
  public static final int NML = 0;
  public static final int RT = 1;  

  int layer;
  int type;
  String path;
  int rows = 1;
  int col = 1;
  boolean useIndex = false;
  
  public MapSchemaEntry(int l, int t, String p){  
    layer = l;
    type = t;
    path = p;
  }
  
  public int getLayer(){
    return layer;
  }
  
  public int getType(){
    return type;
  }
  
  public String getTypeName(){
    if(type==RT){
      return "RT";
    }
    return "NML";
  }  
  
  public String getPath(){
    return path;
  }
  
  public void setTypeName(String n){
    if(n.equals("NML")){
      type =NML;
    }
    else{
      if(n.equals("RT")){
        type = RT;
      }
    }
  }
  
  public void setRows(int r){
    rows = r;
  }
  
  public int getRows(){
    return rows;
  }  
  
  public void setCols(int c){
    col = c;
  }
  
  public int getCols(){
    return col;
  }    
  
  public void setUseIndex(boolean b){
    useIndex = b;  
  }
  
  public boolean isUsedIndex(){
    return useIndex;  
  }  
}