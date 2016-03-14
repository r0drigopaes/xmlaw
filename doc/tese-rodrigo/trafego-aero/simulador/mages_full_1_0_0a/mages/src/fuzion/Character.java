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

import mages.util.*;

public interface Character extends FuzionObject{
  
  public static final String STR = "STR";
  public static final String DEX = "DEX";  
  public static final String BODY = "BODY";  
  public static final String MOV = "MOV";  
  public static final String COU = "COU";  
  public static final String FA = "FA";  
  public static final String HVW = "HVW";  
  public static final String HTH = "HTH";  
  public static final String EV = "EV";  

  public static final int USE_WEAPON = 0;
  public static final int USE_HAND = 1;  
  
  //Own  
  public int getAttribute(String a);
  public int getSkill(String a);  
  public int getInitiative();
  public void hits(int p, int t);  
  public int getHitsCur();  
  public int move();  
  public int run();
  public int slowWalk();
  public void rotate(int degree);
  public int getDirection();
  public Weapon getActiveWeapon();
  public void activeWeapon(int id);
  public void setDodge(boolean f);
  public boolean isDodge();
  public int rangeVision();
  
  //FuzionObject
  public Coord getPosition();  
}