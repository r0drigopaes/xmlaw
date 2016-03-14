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

package mages.comm;

import mages.simulation.*;
import java.util.*;
import java.io.*;

public class CommTable implements Serializable{
  Hashtable elementToPos;
  
  public CommTable(){
    elementToPos = new Hashtable();
  }

  public void add(int idStart, int idEnd, int posStart, int idMsg, int idGroup, Object arg, int ifNull){
    for(int i=idStart; i<=idEnd; i++){
      add(posStart++,idMsg,i,idGroup,arg,ifNull);
    }
  }
    
  public void add(int pos, int idMsg, int idSender, int idGroup,Object arg, int ifNull){
    CommElement key = new CommElement(idMsg,idSender,idGroup,arg);
    Integer value = new Integer(pos);
    elementToPos.put(key,value);
  }
  
  public int getPos(AgentMessage am){
    Object arg = null;
    if(am.getIdMessage()==AgentProtocol.QUERY_ATTRIBUTE){
      arg = ((Parameter)am.getContent()).getName();
    }
    CommElement key = new CommElement(am.getIdMessage(),
                                      am.getIdSender(),
                                      am.getIdGroup(),
                                      arg);
    Integer i = ((Integer)elementToPos.get(key));
    if(i==null){
      return -1;
    }
    else{
      return i.intValue();                               
    }  
  }
    
  public int getLength(){
    return elementToPos.size();
  }  
}