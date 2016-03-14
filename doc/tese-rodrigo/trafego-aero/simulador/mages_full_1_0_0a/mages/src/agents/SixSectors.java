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

public class SixSectors extends NodeVectorArray{
  
  Vector values[] = null;
  long lasttime = -1;
  Directionable obj = null;
  
  public SixSectors(Directionable d, EightSectors es){
    addNode(es);
    obj = d;
  }
  
  public Vector[] value(long timestamp){
    if((timestamp>lasttime)){
	     lasttime = timestamp;

      EightSectors es = (EightSectors)getNode(0);
      Vector[] sec = es.value(timestamp);      
            
      values = new Vector[6];
      int view = obj.getDirection();
      int plan = (int)(view/45);

      int crt[] = new int[8];                  
      crt[0] = plan;
      crt[1] = plan-1;
      crt[2] = plan+1;
      crt[3] = plan-2;
      crt[4] = plan+2;
      crt[5] = plan+3;
      crt[6] = plan+4;
      crt[7] = plan+5;            
      for(int i=0; i<crt.length; i++){
        if(crt[i]<0){
          crt[i] = 8 + crt[i];
        }
        else{
          if(crt[i]>7){
            crt[i] = crt[i]-8;
          }          
        }
      }

      if(sec[crt[0]]!=null){
        values[0] = new Vector();      
        for(int a=0; a<sec[crt[0]].size(); a++){
          values[0].addElement(sec[crt[0]].elementAt(a));
        }
      }  

      if(sec[crt[1]]!=null){
        if(values[0]==null) values[0] = new Vector();      
        for(int a=0; a<sec[crt[1]].size(); a++){
          values[0].addElement(sec[crt[1]].elementAt(a));
        }
      }
      
      if(sec[crt[5]]!=null){
        values[4] = new Vector();      
        for(int a=0; a<sec[crt[5]].size(); a++){
          values[4].addElement(sec[crt[5]].elementAt(a));
        }
      }  
      
      if(sec[crt[6]]!=null){
        if(values[4]==null) values[4] = new Vector();      
        for(int a=0; a<sec[crt[6]].size(); a++){
          values[4].addElement(sec[crt[6]].elementAt(a));
        }
      }
                
      values[1] = sec[crt[2]]; 
      values[2] = sec[crt[3]];
      values[3] = sec[crt[4]];
      values[5] = sec[crt[7]];                        
    }                     
    return values;    
  }        
}