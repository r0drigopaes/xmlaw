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

import java.io.*;

public class Feature implements Serializable{
  String name;
  int cost;
  int level;
  int ratio;

  public Feature(String n, int r){
    name = n;  
    ratio = r;
  }

  public Feature(String n){
    name = n;
    ratio = 1;
  }    
  
  public void setName(String n){
    name = n;
  }

  public String getName(){
    return name;
  }

  public int getCost(){
    return cost;
  }

  public void setLevel(int l){
    level = l;
    cost = l*ratio;
  }

  public int getLevel(){
    return level;
  }
  
  public void setRatio(int r){
    ratio = r;
  }

  public int getRatio(){
    return ratio;
  }  
}