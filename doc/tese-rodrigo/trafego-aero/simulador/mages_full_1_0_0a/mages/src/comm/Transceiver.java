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
import mages.agents.*;

public class Transceiver{
  public static void processing(ServerReceiver r, AgentMessage am, Agency ag){
    AgentMessage answer = null;
    switch(am.getIdMessage()){
      case AgentProtocol.QUERY_ATTRIBUTE:
         String value = r.onQueryAttribute(am.getContent());
         Parameter result = new Parameter((String)am.getContent(),value);         
         answer = new AgentMessage(r.getIdReceiver(),
                                   am.getIdSender(),
                                   am.getIdGroup(),
                                   AgentMessage.UNICAST,
                                   AgentProtocol.QUERY_ATTRIBUTE,
                                   result,
                                   ag.getTimeStamp()
                                   );
      break;
      
      case AgentProtocol.QUERY_SENSORY:
         Object[] result2 = r.onQuerySensory();
         answer = new AgentMessage(r.getIdReceiver(),
                                   am.getIdSender(),
                                   am.getIdGroup(),
                                   AgentMessage.UNICAST,
                                   AgentProtocol.QUERY_SENSORY,
                                   result2,
                                   ag.getTimeStamp()
                                   );      
      break;
    }
    answer.setFunctionMessage(AgentMessage.ANSWER);                                                
    ag.send(answer);    
  }
}