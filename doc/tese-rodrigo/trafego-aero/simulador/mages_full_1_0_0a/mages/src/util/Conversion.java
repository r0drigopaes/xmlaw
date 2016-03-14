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

public class Conversion{
  final static int INT = 0;
  final static int DOUBLE = 1;  
  final static int LONG = 2;  
  
  private static String convert(Object o, int mode){
    String ret = new String();
    switch(mode){
      case INT:
        int tmp[] = (int[])o;    
        for(int i=0; i<tmp.length; i++){
          ret = ret.concat(tmp[i]+" ");
        }      
      break;
      case DOUBLE:
        double tmp2[] = (double[])o;    
        for(int i=0; i<tmp2.length; i++){
          ret = ret.concat(tmp2[i]+" ");
        }      
      break;
      case LONG:
        long tmp3[] = (long[])o;    
        for(int i=0; i<tmp3.length; i++){
          ret = ret.concat(tmp3[i]+" ");
        }      
      break;            
    }
    return ret.trim();  
  }
  
  public static String toString(Object o){
    if(o instanceof int[]){
      return convert(o,INT);
    }
    else{
      if(o instanceof double[]){
        return convert(o,DOUBLE);      
      }
      else{
        if(o instanceof long[]){
          return convert(o,LONG);        
        }
        else{
          return o.toString();
        }
      }
    }
  }
}