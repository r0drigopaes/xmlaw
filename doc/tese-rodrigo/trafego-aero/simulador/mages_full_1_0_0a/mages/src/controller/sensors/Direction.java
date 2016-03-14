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

package mages.controller.sensors;

import mages.agents.*;
import mages.util.*;
import mages.gui.*;
import java.awt.*;
import java.io.*;

public class Direction extends NodeInt implements Sensor, Memory, Serializable{
  
  Directionable pos = null;
  int dir = -1;
  Integer oldValue = null;
  boolean mem = false;

  public Direction(){
    dir = -1;
  }
    
  public Direction(Directionable p){
    pos = p;
  }
  
  //Logical
  public String getName(){
    return "Direction";
  }
   
  public void setValue(Object v){
    if(mem){
      oldValue = new Integer(dir);
    }
    dir = ((Integer)v).intValue();
  }
  
  public int getValue(){
    return dir;
  }  
     
  //UI
  public void paint(Graphics g){  
    if(g!=null){
		Graphics2D g2 = (Graphics2D)g; 
   	g2.setRenderingHint(RenderingHints.KEY_RENDERING, 
         					  RenderingHints.VALUE_RENDER_SPEED);     

      String text = null;      
      if(pos!=null){ 
        text = "Direction: "+convert(pos.getDirection());
      } 
      else{
        text = "Direction: "+convert(dir);      
      } 
      g2.setColor(Color.black);
      g2.setFont(new Font("Dialog",Font.BOLD,13));      
      g2.drawString(text,0,20);            
    }        
  }
      
  public Dimension getPreferedSize(){
    return new Dimension(80,70);
  }
  
  public SensorPanel getSensorPanel(){
    SensorPanel canvas = new SensorPanel(this);
    Dimension d = getPreferedSize(); 
    canvas.setSize(getPreferedSize());
    canvas.setPreferredSize(getPreferedSize());
    canvas.setMinimumSize(getPreferedSize());       
    canvas.setMinimumSize(getPreferedSize());    
    canvas.setOpaque(true);      
    canvas.setBackground(Color.white);
    return canvas;
  }
    
  //Learning    
  public String[] getString(){
    String[] tmp = new String[1];
    if(pos!=null){
      tmp[0] = Integer.toString(pos.getDirection());
    }
    else{
      tmp[0] = Integer.toString(dir);    
    }  
    return tmp;
  }
  
  private String convert(int d){
    switch(d){
      case 0: return "East";
      case 45: return "Northeast";
      case 90: return "North";
      case 135: return "Northwest";
      case 180: return "West";
      case 225: return "Southwest";
      case 270: return "South";
      case 315: return "Southeast";
      case 360: return "East";
    }
    return Integer.toString(d);
  }
  
  //Node
  public int value(long timestamp){
    if(pos!=null){
      if(mem) oldValue = new Integer(dir);
      dir = pos.getDirection();
    }
    return dir;      
  }      
  
  public String[] getMemoryString(){
    String[] tmp = new String[1];  
    if(oldValue!=null){
      int p = ((Integer)oldValue).intValue();
      tmp[0] = Integer.toString(p);
    }  
    else{
      tmp[0] = "-";    
    }
    return tmp;    
  }
  
  public boolean isUsingMemory(){
    return mem;
  }
  
  public void setUseMemory(boolean flag){
    mem = flag;
  }
  
  public Object getOldValue(){
    return oldValue;
  }  
}