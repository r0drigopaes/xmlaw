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
import java.io.*;

public class Weapon implements Serializable, Cloneable{
  public static final int UNKNOWN_CLASS = -1;
  public static final int MELEE = 0;
  public static final int RANGE = 1;
  
  public static final int UNKNOWN_TYPE = -1;  
  public static final int SIMPLE_SHOT = 0;  
  public static final int SHOTGUN = 1;  
  public static final int AUTOFIRE = 2;  
    
  String name;
  int dc;						//Damage Class
  int rof;
  int amno;
  int amnoCur;
  int range;
  int cost;
  int type;
  int weaponClass;
  String skill;

  public Weapon(){
    name = "Default";
    dc = rof = amno = amnoCur = 1;    
    range = 10;
    cost = 0;
    weaponClass = UNKNOWN_CLASS;
    type = UNKNOWN_TYPE;
  }
  
  public void setName(String n){
    name = n;
  }

  public String getName(){
    return name;
  }

  public void setDc(int d){
    dc = d;
  }

  public int getDc(){
    return dc;
  }

  public void setRof(int r){
    rof = r;
  }

  public int getRof(){
    return rof;
  }

  public void setAmno(int a){
    amno = amnoCur = a;
  }

  public int getAmno(){
    return amno;
  }

  public int getAmnoCur(){
    return amnoCur;
  }
  
  public void setRange(int r){
    range = r;
  }

  public int getRange(){
    return range;
  }

  public void setCost(int c){
    cost = c;
  }
  
  public int getCost(){
    return cost;
  }  

  public void setType(int t){
    type = t;
  }  
  
  public int getType(){
    return type;
  }  
  
  public void setWeaponClass(int wc){
    weaponClass = wc;
  }  
  
  public int getWeaponClass(){
    return weaponClass;
  }  
  
  public String getStrWeaponClass(){
    switch(weaponClass){
      case MELEE: return "MELEE";
      case RANGE: return "RANGE";
    }
    return "UNKNOWN_CLASS";
  }
  
  public String getStrType(){
    switch(type){
      case SIMPLE_SHOT: return "SIMPLE_SHOT";
      case SHOTGUN: return "SHOTGUN";
      case AUTOFIRE: return "AUTOFIRE";            
    }  
    return "UNKNOWN_TYPE";    
  }
  
  public void setStrWeaponClass(String str){
    if(str.toUpperCase().equals("MELEE")){
      weaponClass = MELEE;
    }
    else{
      if(str.toUpperCase().equals("RANGE")){
        weaponClass = RANGE;
      }
      else{
        weaponClass = UNKNOWN_CLASS;
      }
    }
  }
  
  public void setStrType(String str){
    if(str.toUpperCase().equals("SIMPLE_SHOT")){
      type = SIMPLE_SHOT;
    }
    else{
      if(str.toUpperCase().equals("SHOTGUN")){
        type = SHOTGUN;
      }
      else{
        if(str.toUpperCase().equals("AUTOFIRE")){
          type = AUTOFIRE;
        }
        else{
          type = UNKNOWN_TYPE;
        }
      }
    }  
  }    
  
  public void setSkill(String s){
    skill = s;
  }
  
  public String getSkill(){
    return skill;
  }
  
  public void amno(int value){
    amnoCur += value;
    amnoCur = (amnoCur<0)?0:amnoCur;
    amnoCur = (amnoCur>amno)?amno:amnoCur;
  }
  
  public void defineAmno(int v){
    amnoCur = v;
  }
  
  public Object clone(){
     try{ 
       Weapon w = (Weapon)super.clone();
       w.setName(new String(getName()));
       w.setSkill(new String(getSkill()));
       return w;
     }
     catch(CloneNotSupportedException e){
       return null;
     } 
   }  
}

