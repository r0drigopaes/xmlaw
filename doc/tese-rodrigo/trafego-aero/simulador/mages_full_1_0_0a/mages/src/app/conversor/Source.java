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

package mages.app.conversor;

import java.util.*;

public class Source{
  String target[][] = null;
  BitSet bitSet = null;
    
  public void setSource(String[][] t){
    target = t;    
    bitSet = new BitSet(target.length);
  }
  
  public int getRows(){
    return target.length;
  }
  
  public int getCols(int r){
    return target[r].length;
  }  
  
  public String getValue(int r, int c){
    return target[r][c];
  }
  
  public void markValue(int r, boolean m){
    if(m){
      bitSet.set(r);
    }
    else{
      bitSet.clear(r);    
    }  
  }
  
  public boolean isMarked(int r){
    return bitSet.get(r);
  }
}