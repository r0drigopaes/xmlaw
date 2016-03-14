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

package mages.fuzion;

import mages.simulation.*;
import mages.util.Coord;
import mages.util.BresenhamLine;
import java.util.Vector;

public abstract class Settings{

  public static final int DICE_SIX_SIDES = 6;
  public static final int DICE_TEN_SIDES = 10;  
  public static final int CLEAR_VIEW = 0;
  public static final int OBSCURED_VIEW = -2;   
  public static final int FULL_BLOCKED_VIEW = -9999;  
  

  private static int dice = DICE_SIX_SIDES;
  private static CustomSettings settings = new DefaultSettings();
  
  public static int tableRange(Weapon w, double d){
    return settings.tableRange(w,d);  
  }
  
  public static int tableShotgun(Weapon w, double d){
    return settings.tableShotgun(w,d);
  }

  public static int tableAutofire(double dif, int rof){
    return settings.tableAutofire(dif,rof);
  }
  
  public static void setCustomSettings(CustomSettings cs){
    settings = cs;
  }
      
  public static void setDice(int type){
    dice = type;
  }
  
 
  public static int roll(){
    return (int)rollDouble();
  }
  
  public static double rollDouble(){
    if(dice==DICE_TEN_SIDES){
       return Distribution.uniform(1.0,10.0);
    }
    else{
      double sum = 0;
      for(int i=0;i<3; i++){
        sum += Distribution.uniform(1.0,6.0);                 
      }
      return sum;
    }     
  }  
  
  public static int getStateLineSight(FuzionObject o1, FuzionObject o2, FuzionMap map){
    Coord line[] = createLine(o1,o2,map);
    int state = CLEAR_VIEW;
    for(int i=1; i<line.length-1; i++){
      for(int j=0; j<map.getLayers(); j++){    
        FuzionObject fo = map.get(line[i].getX(),line[i].getY(),j);       
        if(fo!=null){
           if(fo.isSolid()){         
             state = (FULL_BLOCKED_VIEW<state)?FULL_BLOCKED_VIEW:state;
           }
           else{
             state = (fo.getPositionState()<state)?fo.getPositionState():state;
           }
        }              
      }
    }
    return state;
  }
      
  public static Coord[] createLine(FuzionObject o1, FuzionObject o2, FuzionMap map){  
    return BresenhamLine.line(o1.getPosition(),o2.getPosition(),map.getWidth(),map.getHeight());
  }      
}
