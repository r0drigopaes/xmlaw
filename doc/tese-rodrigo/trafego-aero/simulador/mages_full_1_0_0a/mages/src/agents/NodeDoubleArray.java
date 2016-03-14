/*
GNU Mages, version 1.00 alpha
Multi-Agents Environment Simulator
Copyright (C) 2001-2002 Jo�o Ricardo Bittencourt <jrbitt@uol.com.br>
 
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

package mages.agents;

public abstract class NodeDoubleArray extends NodeNumberArray{

  public int[] value(long timestamp){
    double[] tmp0 = doubleValue(timestamp);
    int[] tmp = new int[tmp0.length];

    for(int i=0; i<tmp0.length;i++){
      tmp[i] = (int)tmp0[i];
    }
    return tmp;    
  }
  
  public abstract double[] doubleValue(long timestamp);
  
  public boolean[] booleanValue(long timestamp){
    double[] tmp0 = doubleValue(timestamp);
    boolean[] tmp = new boolean[tmp0.length];
    
    for(int i=0; i<tmp0.length;i++){
      if(tmp0[i]!=0){
        tmp[i] = true;
      }  
      else{
        tmp[i] = false;      
      }
    }
    return tmp;      
  }  

  public int[] intValue(long timestamp){
    return value(timestamp);  
  }      
}