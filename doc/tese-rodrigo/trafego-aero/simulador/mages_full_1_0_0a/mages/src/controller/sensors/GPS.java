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

public class GPS extends NodeCoord implements Sensor, Serializable{
  
  Posicionable pos;
  Coord c = null;
  String name = null;

  public GPS(){
    c = new Coord(0,0,0);
  }

  public GPS(Coord crd){
    c = crd;
  }

  public GPS(Coord crd, String n){
    c = crd;
    name = n;
  }
        
  public GPS(Posicionable p){
    pos = p;
  }
  
  public GPS(Posicionable p, String n){
    pos = p;
    name = n;
  }  
  
  //Logical
  public String getName(){
    if(name==null){
      return "GPS";
    }
    else{
      return "GPS"+name;
    }  
  }
   
  public void setValue(Object v){
    c = (Coord)v;
  }

  public Coord getValue(){
    return c;
  }
       
  //UI
  public void paint(Graphics g){  
    if(g!=null){
		Graphics2D g2 = (Graphics2D)g; 
   	g2.setRenderingHint(RenderingHints.KEY_RENDERING, 
         					  RenderingHints.VALUE_RENDER_SPEED);     
      int x = 0;					           
      int y = 0;
      int z = 0;            
		if(pos!=null){			           
        x = pos.getPosition().getX();
        y = pos.getPosition().getY();
        z = pos.getPosition().getZ();       
      }
      else{
        x = c.getX();
        y = c.getY();
        z = c.getZ();                
      }  
      String text = "x: "+x+"   y: "+y+"   z: "+z;
      g2.setColor(Color.black);
      g2.setFont(new Font("Dialog",Font.BOLD,13));      
      g2.drawString(text,0,20);      
    }        
  }
      
  public Dimension getPreferedSize(){
    return new Dimension(100,80);
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
    String[] tmp = new String[3];
    if(pos!=null){
      tmp[0] = Integer.toString(pos.getPosition().getX());
      tmp[1] = Integer.toString(pos.getPosition().getY());
      tmp[2] = Integer.toString(pos.getPosition().getZ());        
    }
    else{
      tmp[0] = Integer.toString(c.getX());
      tmp[1] = Integer.toString(c.getY());
      tmp[2] = Integer.toString(c.getZ());            
    }  
    return tmp;
  }
  
  //Node
  public Coord value(long timestamp){
    if(pos!=null){
      return pos.getPosition();
    }
    else{
      return c;
    }  
  }      
}