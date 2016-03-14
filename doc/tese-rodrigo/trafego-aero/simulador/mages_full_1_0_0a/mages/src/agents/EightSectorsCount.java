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

public class EightSectorsCount extends NodeIntArray{

  int ret[] = new int[8];
  long lasttime = 0;
  
  public EightSectorsCount(EightSectors es){
    addNode(es);
  }
  
  public int[] value(long timestamp){
    if((timestamp>=lasttime)||(timestamp == -1)){
  	   if(timestamp>0){
	     lasttime = timestamp;
	   }   
            
      Vector values[] = ((EightSectors)getNode(0)).value(timestamp);

      for(int z=0; z<ret.length; z++){
        if(values[z] == null){
          ret[z]=0;
        }
        else{
          ret[z] = values[z].size();
        }  
      }      
    }                     
    return ret;    
  }      
}