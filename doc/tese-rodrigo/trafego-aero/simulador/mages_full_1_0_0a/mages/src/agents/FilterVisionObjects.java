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

public class FilterVisionObjects extends NodePosicionableArray{

  long id;
  long lasttime = 0;
  
  Posicionable ret[] = null;
      
  public FilterVisionObjects(VisionObjects vo, long i){
    addNode(vo);
    id = i;
  }
  
  public Posicionable[] value(long timestamp){
    if((timestamp>=lasttime)||(timestamp == -1)){
  	   if(timestamp>0){
	     lasttime = timestamp;
	   }  
      VisionObjects vo = (VisionObjects)getNode(0);
      Posicionable[] objs = vo.value(timestamp);
      Vector tmp = new Vector();
      for(int i=0; i<objs.length; i++){
        if(objs[i] instanceof Identificable){
          Identificable objId = (Identificable)objs[i];
          if(objId.getId()==id){
            tmp.addElement(objs[i]);
          }
        }
      }
    
      ret = new Posicionable[tmp.size()];
      for(int j=0; j<ret.length; j++){
        ret[j] = (Posicionable)tmp.elementAt(j);
      }
    }
    return ret;
  }
}