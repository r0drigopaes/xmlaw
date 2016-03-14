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
import java.util.Arrays;

public class Nearest extends NodePosicionable{
  
  public static final int FIRST = 0;
  public static final int SECOND = 1;
  public static final int THIRD = 2;  
  public static final int MIDDLE = 3;  
  public static final int LAST = 4;  
    
  long lasttime = 0;
  Posicionable ret = null;
  int mode = FIRST;
  
  public Nearest(NodePosicionable np, NodePosicionableArray others, int m ){
    super();
    mode = m;
    addNode(np);
    addNode(others);
  }
  
  public Posicionable value(long timestamp){
    if((timestamp>=lasttime)||(timestamp == -1)){
  	   if(timestamp>0){
	     lasttime = timestamp;
	   }   
	   
	   NodePosicionable np = (NodePosicionable)getNode(0);
	   Posicionable pos = np.value(timestamp);
	   Coord cPos = pos.getPosition();	   
  
      NodePosicionableArray others = (NodePosicionableArray)getNode(1);
      Posicionable[] target = others.value(timestamp);
      if(target!=null){    
      
        PositionToken tokens[] = new PositionToken[target.length];
        for(int i=0; i<target.length; i++){
          double d = Distance.distance(cPos,target[i].getPosition());
          tokens[i] = new PositionToken(target[i],d);
        }

        Arrays.sort(tokens);
      
        int index = 0;
        switch(mode){
          case FIRST: index = (target.length>=1)?0:0;
          break;
        
          case SECOND: index = (target.length>=2)?1:0;
          break;
        
          case THIRD: index = (target.length>=3)?2:0;
          break;
        
          case MIDDLE:  index = (int)(target.length/2);
         break;     
       
          case LAST: index = target.length-1;
          break;           
        }
  	    if(tokens.length>0){ 
         ret = tokens[index].getPosicionable();
       }  
  	  } 
	 }   
	 return ret;   
  }
  
  protected class PositionToken implements Comparable{
    double distance;
    Posicionable obj;
    
    public PositionToken(Posicionable o, double d){
      distance = d;
      obj = o;
    }
    
    public Posicionable getPosicionable(){
      return obj;
    }

    public double getDistance(){
      return distance;
    }
        
    public int compareTo(Object o){
      PositionToken pt = (PositionToken)o;    
      int ret = (distance<pt.getDistance())?-1:(distance==pt.getDistance())?0:1;
      return ret;
    }    
  }
}