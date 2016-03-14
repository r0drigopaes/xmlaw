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

import java.util.*;
import java.io.*;

public class WeaponsCollection implements Serializable{
  Vector weapons;
  
  public WeaponsCollection(){
    weapons = new Vector(6);
  }
  
  public void addWeapon(Weapon w){
    weapons.addElement(w);
  }
  
  public Weapon getWeapon(String n){
    Weapon tmp = null;
    for(int i=0; i<weapons.size(); i++){
      tmp = (Weapon)weapons.elementAt(i);
      if(tmp.getName().equals(n)){
        return tmp;
      }
    }
    return null;
  }
  
  public int getSize(){
    return weapons.size();
  }
  
  public Enumeration getElements(){
    return weapons.elements();
  }
  
  public void setWeapons(Weapon[] ws){
    weapons = new Vector();
    for(int i=0; i<ws.length; i++){
      addWeapon(ws[i]);
    }
  }
}