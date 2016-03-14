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

package mages.controller.ctf.client;

import mages.agents.*;
import mages.comm.*;
import java.util.*;

public class CtFCommInfo extends CommInformation{
  
  
  public CtFCommInfo(){
    super();
    
    //pedir a energia de todos os companheiros
    AgentMessage am1 = new AgentMessage(AgentProtocol.QUERY_ATTRIBUTE,
                                        "HITS");
    //pedir o sensorial de todos os companheiros                                        
    AgentMessage am2 = new AgentMessage(AgentProtocol.QUERY_SENSORY,
                                        null);                                        
    //pedir o sensorial de todos os companheiros                                        
    AgentMessage am3 = new AgentMessage(AgentProtocol.WHAT_IS_YOUR_ACTION,
                                        null);                                            
	 addMessage(am1);                                        
    addMessage(am2);                                        	 
  }    
  
  protected void createIfNullValues(){
    ifNullValues = new int[6];
    ifNullValues[0] = 1;
    ifNullValues[1] = 1;
    ifNullValues[2] = 1;        
    ifNullValues[3] = 61;
    ifNullValues[4] = 61;
    ifNullValues[5] = 61;    
  }
  
  public void createTable(){  
    table = new CommTable();
    int cnt = 0;
    int myId = getIdSender(); 
    int myGroup = getIdGroup();
    int maxId = 3;
    
    for(int i=0; i<=maxId; i++){
      if(i!=myId){
        table.add(cnt,AgentProtocol.QUERY_ATTRIBUTE,i,myGroup,"HITS",ifNullValues[cnt]);      
        cnt++;
      }
    }
    
    cnt = 3;
    for(int i=0; i<=maxId; i++){
      if(i!=myId){
        table.add(cnt,AgentProtocol.QUERY_SENSORY,i,myGroup,null,ifNullValues[cnt]);
        cnt++;
      }
    }        
  }  
}