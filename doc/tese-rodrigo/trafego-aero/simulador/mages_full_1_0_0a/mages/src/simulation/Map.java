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

import mages.util.*;
import java.io.*;

public class Map implements Serializable{
  Layer[] layers = null;
  int width, height;			//Cells
  MapSchema useSchema;
  
  public Map(int w, int h){
    width = w;
    height = h;
    layers = new Layer[1];
    useSchema = null;
  }    
  
  public Map(int w, int h, int ly){
    width = w;
    height = h;
    layers = new Layer[ly];  
    useSchema = null;    
  }    
    
  public int getWidth(){
    return width;
  }  
    
  public int getHeight(){
    return height;
  }    
  
  public int getNumLayers(){
    return layers.length;
  }  
  
  public void setLayer(Layer ly, int p) throws ArrayIndexOutOfBoundsException{
    layers[p] = ly;
  }    

  public Layer getLayer(int p) throws ArrayIndexOutOfBoundsException{
    return layers[p];
  }  
  
  public void setSimObject(int x, int y, int p, SimObject so){
    Layer l = getLayer(p);
    l.setCell(x,y,so);
  }
  
  public SimObject getSimObject(int x, int y, int p){
    Layer l = getLayer(p);
    return l.getCell(x,y);
  }  
  
  public void removeSimObject(int x, int y, int p){
    Layer l = getLayer(p);
    l.removeCell(x,y);
  }  
  
  public void setMapSchema(MapSchema ms){
    useSchema = ms;
  }

  public MapSchema getMapSchema(){
    return useSchema;
  }       
  
  public Object clone(){
    Map tmp = new Map(getWidth(),getHeight(),getNumLayers());
    if(useSchema!=null){
      tmp.setMapSchema((MapSchema)useSchema.clone());    
    }
 
    for(int i=0; i<getNumLayers(); i++){
      tmp.setLayer((Layer)getLayer(i).clone(),i);
    }
    return tmp;
  }
}