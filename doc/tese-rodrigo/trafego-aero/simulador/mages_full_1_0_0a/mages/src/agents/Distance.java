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

public class Distance extends NodeDouble{
  
  long lasttime = 0;
  double value = 0;
    
  public Distance(NodeCoord pa, NodeCoord pb){
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
      value = distance(p1,p2);
    } 
    return value;                    
  }
  
  public static double distance(Coord p1, Coord p2){
    double v = Math.sqrt(Math.pow(p2.getX()-p1.getX(),2)+
                         Math.pow(p2.getY()-p1.getY(),2)+
                         Math.pow(p2.getZ()-p1.getZ(),2));  
    return v;                      
  }
}