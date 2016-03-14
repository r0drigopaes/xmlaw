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

public class Initiative implements Comparable{
  Character ctr;
  double value;
  
  public Initiative(Character c, double v){
    ctr = c;
    value = v;
  }

  public double getValue(){
    return value;
  }
  
  public Character getCharacter(){
    return ctr;
  }
  
  public int compareTo(Object o){
    Initiative i = (Initiative)o;
    int ret = (value<i.getValue())?-1:(value==i.getValue())?0:1;
    return ret;
  }
}