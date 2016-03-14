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

public class CtFSettings implements CustomSettings{
  public static final int NORTH = 90;
  public static final int SOUTH = 270;
  public static final int WEST = 180;    
  public static final int EAST = 0;  
  public static final int NORTHWEST = 135;    
  public static final int NORTHEAST = 45;      
  public static final int SOUTHWEST = 225;    
  public static final int SOUTHEAST = 315;
      
  public static final int RED = 0;  
  public static final int BLUE = 1;  
  
  public static final double MOVE_CST = 0.025;
  public static final double DEFENSE_CST = 0.04;  
  public static final double ATTACK_CST = 0.05;  
  
  public int tableRange(Weapon w, double d){
    if(d>0 && d<5){
      return 0;
    }
    else{
      if(d>5 && d<((int)(w.getRange()/4))){
        return -2;
      }
      else{
        if(d>((int)(w.getRange()/4)) && d<((int)(w.getRange()/2))){
          return -4;
        }
        else{
          if(d<((int)(w.getRange()/2)) && d<w.getRange()){
            return -6;
          }           
          else{
            return -7;
          }
        }
      }
    }
  }
  
  public int tableShotgun(Weapon w, double d){
    if(w.getType()==Weapon.SHOTGUN){
      if(d>=66){
        return 0;
      }
      else{
        double a = Math.pow(d,2)*0.0016;
        double b = -0.228*d;
        return (int)(Math.floor(a+b+7.9872));    
      }
    }
    else{
      return 0;
    }      
  }
  
  public int tableAutofire(double dif, int rof){
    if(dif>=0){
      return 0;
    }
    else{
      if(dif==-1){
        return (int)(Math.round(rof/8));
      }
      else{
        if(dif==-2){
          return (int)(Math.round(rof/4));
        }      
        else{
          if(dif==-3){
            return (int)(Math.round(rof/2));
          }        
          else{
            if(dif>-4){
              return rof;
            }          
          }
        }
      }
    }
    return rof;
  }  
}