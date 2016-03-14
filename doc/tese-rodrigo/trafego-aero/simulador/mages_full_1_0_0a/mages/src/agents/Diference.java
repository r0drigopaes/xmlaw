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

package mages.agents;

import mages.util.*;

public class Diference extends NodeDouble{
  public static final int X = 0;
  public static final int Y = 1;  
  public static final int Z = 2;  
  
  int mode = X;
  double value = 0;
  long lasttime = 0;
  
  public Diference(NodeCoord pa, NodeCoord pb, int m){
    this(pa,pb);
    mode = m;
  }
    
  public Diference(NodeCoord pa, NodeCoord pb){
    addNode(pa);
    addNode(pb);    
  }
  
  public double value(long timestamp){
    if((timestamp>=lasttime)||(timestamp == -1)){
  	   if(timestamp>0){
	     lasttime = timestamp;
	   }  
		
      NodeCoord n1 = (NodeCoord)getNode(0);
      NodeCoord n2 = (NodeCoord)getNode(1);
      Coord p1 = n1.value(timestamp);
      Coord p2 = n2.value(timestamp);
      switch(mode){
        case X:
          value = p1.getX()-p2.getX();
        break;
        case Y:
          value = p1.getY()-p2.getY();      
        break;
        case Z:
          value = p1.getZ()-p2.getZ();      
        break;            
      }		
    }  
    return value;
  }
}