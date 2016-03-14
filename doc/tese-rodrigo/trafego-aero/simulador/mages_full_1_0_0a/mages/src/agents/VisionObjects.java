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
import java.util.Vector;

public class VisionObjects extends NodePosicionableArray{

  double ray = 1;
  Posicionable[] ret = null;  
  long lasttime = 0;
  
  public VisionObjects(NodePosicionable np, double r, FixedDimension dim){
    addNode(np);
    addNode(dim);
    ray = r;
  }
  
  public Posicionable[] value(long timestamp){
    if((timestamp>=lasttime)||(timestamp == -1)){
  	   if(timestamp>0){
	     lasttime = timestamp;
	   }  

      Vector tmp = new Vector();	     
      Node nodesChild[] = getChildNodes();    

      Posicionable pos = ((NodePosicionable)nodesChild[0]).value(timestamp);      
      MagesDimension dimension = ((FixedDimension)nodesChild[1]).value(timestamp);
      Coord ref = pos.getPosition();
      if(pos.getPositionMode()!=Posicionable.CARTESIAN){
        ref = BresenhamLine.screenToCartesian(ref,dimension.getWidth(),dimension.getHeight());
      }      

      for(int i=2; i<nodesChild.length; i++){
        Posicionable pos2 = ((NodePosicionable)nodesChild[i]).value(timestamp);    
        Coord other = pos2.getPosition();
        if(pos2.getPositionMode()!=Posicionable.CARTESIAN){
          other = BresenhamLine.screenToCartesian(other,dimension.getWidth(),dimension.getHeight());
        }         
        double dist = distance(ref,other);
        if(dist<=ray){
          tmp.addElement(pos2);
        }
      }
      ret = new Posicionable[tmp.size()];
      for(int j=0; j<tmp.size(); j++){
        ret[j] = (Posicionable)tmp.elementAt(j);
      }
    }
    return ret;
  }
  
  private double distance(Coord p1, Coord p2){
    return Math.sqrt(Math.pow(p2.getX()-p1.getX(),2)+
                     Math.pow(p2.getY()-p1.getY(),2)+
                     Math.pow(p2.getZ()-p1.getZ(),2));    
  }
}