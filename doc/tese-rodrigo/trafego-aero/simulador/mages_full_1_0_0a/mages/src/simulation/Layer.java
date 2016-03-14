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
import java.io.*;

public class Layer implements Serializable{
  SimObject[][] cells = null;
  int width, height;			//Cells
  int id;

  public Layer(int w, int h){
    width = w;
    height = h;
    cells = new SimObject[height][width];
  }
    
  public Layer(int i, int w, int h){
    width = w;
    height = h;
    id = i;
    cells = new SimObject[height][width];
  }    
      
  public int getWidth(){
    return width;
  }  
    
  public int getHeight(){
    return height;
  }    
  
  public int getId(){
    return id;
  }      
  
  public void setId(int i){
    id = i;
  }          
  
  public void setCell(int x, int y, SimObject so)  throws ArrayIndexOutOfBoundsException{
    cells[y][x] = so;
  }
  
  public SimObject getCell(int x, int y)  throws ArrayIndexOutOfBoundsException{
    return cells[y][x];
  }  
  
  public void removeCell(int x, int y)  throws ArrayIndexOutOfBoundsException{
    cells[y][x] = null;
  }  
  
  public IconsTable indexTable(){
    IconsTable table = new IconsTable();
    for(int x=0; x<width; x++){
      for(int y=0; y<height; y++){
        SimObject so = cells[y][x];
        if(so!=null){
          Icon ic = so.getIcon(0);
          if(so.paramIsModified()){
            ic.setUseTexture(true);
          }
          int index = table.addIcon(ic);
          if(!so.hasLink(index)){
            so.addLink(index);          
          }
        }
      }
    }
    return table;
  }
  
  public SimObject[] getList(){
    Vector tmp = new Vector();
    for(int x=0; x<width; x++){
      for(int y=0; y<height; y++){
        SimObject so = cells[y][x];
        if(so!=null){
          so.setPosition(x,y);//can be changed position (y,x)
          tmp.addElement(so);
        }      
      }
    }    
    
    SimObject tmp2[] = new SimObject[tmp.size()];
    for(int i=0; i<tmp2.length; i++){
      tmp2[i] = (SimObject)tmp.elementAt(i);
    }
    return tmp2;
  }  
  
  public void setUseTable(boolean f){
    for(int x=0; x<width; x++){
      for(int y=0; y<height; y++){
        SimObject so = cells[y][x];
        if(so!=null){
          so.setUseTable(f);
        }            
      }
    }      
  } 
  
  public Object clone(){
    Layer tmp = new Layer(getId(),getWidth(),getHeight());
    for(int i=0; i<getWidth(); i++){
      for(int j=0; j<getHeight();j++){
        tmp.setCell(i,j,getCell(i,j));
      }
    }
    return tmp;
  }
}