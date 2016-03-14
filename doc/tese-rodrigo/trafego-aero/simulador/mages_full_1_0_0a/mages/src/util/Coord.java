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


package mages.util;

import java.io.*;

public class Coord implements Serializable{
  public double x,y,z;

  public Coord(double xx, double yy){
    x = xx;
    y = yy;
  }
  
  public Coord(double xx, double yy, double zz){
    x = xx;
    y = yy;
    z = zz;
  }
      
  public Coord(int xx, int yy){
    x = xx;
    y = yy;
  }
  
  public Coord(int xx, int yy, int zz){
    x = xx;
    y = yy;
    z = zz;
  }  
  
  public int getX(){
    return (int)x;
  }
  
  public int getY(){
    return (int)y;
  }  
  
  public int getZ(){
    return (int)z;
  }  
  
  public double getDoubleX(){
    return x;
  }
  
  public double getDoubleY(){
    return y;
  }  
  
  public double getDoubleZ(){
    return z;
  }  
  
  public Object clone(){
    Coord tmp = new Coord(getDoubleX(),getDoubleY(),getDoubleZ());
    return tmp;
  }
  
  public String toString(){
    return new String(x+" "+y+" "+z);
  }
  
  public boolean equals(Object o){
    if(o!=null){
      try{
        Coord x = (Coord)o;
        return (x.getDoubleX()==getDoubleX())&&
               (x.getDoubleY()==getDoubleY())&&
               (x.getDoubleZ()==getDoubleZ());
      }
      catch(ClassCastException e){
        return false;
      }
    }
    else{
     return false;
    }  
  }
}