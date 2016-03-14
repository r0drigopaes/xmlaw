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

package mages.domain.ctf;

import mages.fuzion.*;
import mages.util.*;
import mages.agents.*;
import java.io.*;
import java.awt.*;

public class Flag implements FuzionObject,Serializable, Posicionable, Identificable{
      
  int color;
  Coord pos;
  boolean visible = true;
  
  public Flag(int c, Coord p){
    color = c;
    pos = p;
  }   
  
  public Coord getPosition(){
    return pos;
  }
  
 public void setPosition(Coord c){
   pos = c;
 }
 
  public boolean isSolid(){
    return true;
  }
  
  public int getPositionState(){
    return 0;
  }
  
  public Object clone(){
    Flag f = new Flag(color,pos);    
    return f;
  }
  
  public Point getPoint(){
    return new Point(pos.getX(),pos.getY());
  }
  
  public void setPosition(Point p){
    pos = new Coord(p.x,p.y);
  }  
  
  public int getId(){
    return color;
  }  
  
  public int getColor(){
    return color;
  }
  
  public void setVisible(boolean v){
    visible = v;
  }
  
  public boolean isVisible(){
    return visible;
  }
  
  public int getPositionMode(){
    return SCREEN;
  }  
  
  public Object copy(){
    Flag tmp = new Flag(getColor(),(Coord)getPosition().clone());   
    tmp.setVisible(isVisible());
    return tmp;
  }
}