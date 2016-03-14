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

public class EightSectors extends NodeVectorArray{
  double a,b;
  NodePosicionableArray node = null;
  NodePosicionable ref = null;
  double ray = 1;
  Coord O,A,B,C,D,E,F,G,H;
  Vector[] ret = new Vector[8];      
  long lasttime = 0;
  FixedDimension dimNode = null;
  
  public EightSectors(NodePosicionable np, NodePosicionableArray o, double r, FixedDimension d){
    node = o;
    ref = np;
    ray = r;
    dimNode = d;
  }
  
  public Vector[] value(long timestamp){
    if((timestamp>=lasttime)||(timestamp == -1)){
  	   if(timestamp>0){
	     lasttime = timestamp;
	   }   
      
      for(int z=0; z<ret.length; z++){
        ret[z]=null;
      }
      
      Posicionable[] objs = node.value(timestamp);
           
      MagesDimension dim = dimNode.value(timestamp);
      int width = dim.getWidth();
      int height = dim.getHeight();
      
      Coord o = ref.value(timestamp).getPosition();          
      if(ref.value(timestamp).getPositionMode()!=Posicionable.CARTESIAN){
        o = BresenhamLine.screenToCartesian(o,width,height);
      }
      int cx = -o.getX();
      int cy = -o.getY();                  
              
      for(int i=0; i<objs.length; i++){
        int cntQuad = 0;
        boolean find = false;
        
        while(!find && cntQuad<4){
          Coord p = objs[i].getPosition();
          if(objs[i].getPositionMode()!=Posicionable.CARTESIAN){
            p = BresenhamLine.screenToCartesian(p,width,height);
          }          
          
          if(isQuad(cntQuad,p,o)){
            //Convert to 0,0 system
            Coord or = new Coord(0,0);
            p = new Coord(p.getX()+cx,p.getY()+cy);          
            
            double degree = calcDegree(cntQuad,p,or);          
            find = true;
            if(degree!=-1){
              int pos = ((int)(degree)<45)?cntQuad*2:(cntQuad*2)+1;
              if(ret[pos]==null){
                ret[pos] = new Vector();
              }
              ret[pos].addElement(objs[i]);            
            }
          }
          cntQuad++;
        }
      }
    }                     
    return ret;    
  }    
  
  private boolean isQuad(int quad, Coord p, Coord o){
    double px = p.getX();
    double py = p.getY();
    double ox = o.getX();
    double oy = o.getY();    
    
    boolean ret = false;
    switch(quad){
      case 0: ret = (px>=ox && py>=oy)?true:false;        
      break;
      case 1: ret = (px<ox && py>=oy)?true:false;
      break;      
      case 2: ret = (px<ox && py<oy)?true:false;
      break;      
      case 3: ret = (px>=ox && py<oy)?true:false;        
      break;      
    }
    return ret;
  }
  
  private double calcDegree(int quad, Coord p, Coord o){
    double x = 0;
    double y = 0;
    switch(quad){
      case 0:
        x = o.getX()+ray;
        y = o.getY();
      break;
      
      case 1:
        x = o.getX();
        y = o.getY()+ray;
      break;
      
      case 2:
        x = o.getX()-ray;
        y = o.getY();        
      break;
      
      case 3:
        x = o.getX();
        y = o.getY()-ray;      
      break;                  
    }    
    return degree(p, new Coord(x,y));
  }
  
  public static double degree(Coord p, Coord o){
    double a = o.getDoubleX()*p.getDoubleX();
    double b = o.getDoubleY()*p.getDoubleY();
    double c = Math.sqrt((o.getDoubleX()*o.getDoubleX())+(o.getDoubleY()*o.getDoubleY())); 
    double d = Math.sqrt((p.getDoubleX()*p.getDoubleX())+(p.getDoubleY()*p.getDoubleY()));            
    if((c*d)==0){
      return -1;
    }
    else{
      double op = (a+b)/(c*d);    
      op = (op>1.0)?1.0:op;
      return (Math.acos(op)*180)/Math.PI;      
    }
  }   
}