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

import clisp.*;
import mages.util.*;
import mages.controller.ctf.*;
import mages.domain.ctf.*;
import mages.agents.*;
import mages.comm.*;

import java.net.*;
import java.io.*;


public class CtFBotClient extends Client{
  String host;
  
  public CtFBotClient(Connectable own, Protocol p) throws IOException{ 
    super(own,p);  
    host = own.getHostName();
  }  
  
  public void quit(){
    Message msg = new Message(getLocalHost(),host,
                              CtFProtocol.C_BOT_QUIT);
    send(msg);      
  }
  
  public void sendBot(Bot b, String id, int m){
    Parameters prm = new Parameters();
    prm.add(b);
    prm.add(id);
    prm.add(m);
    Message msg = new Message(null,null,CtFProtocol.C_SEND_BOT, prm);
    send(msg);          
  }
  
  public void sendSensory(SensoryInformation si){
    Message msg = new Message(null,null,CtFProtocol.C_SEND_SENSORS, si);
    send(msg);          
  }  
  
  public void sendComm(CommInformation ci){
    Message msg = new Message(null,null,CtFProtocol.C_SEND_COMM, ci);
    send(msg);          
  }    
    
  public void sendAction(Action a){
    Message msg = new Message(null,null,CtFProtocol.C_SEND_ACTION, a);
    send(msg);          
  }      
  
  public void doReconnection(SessionId id){
    Message msg = new Message(null,null,CtFProtocol.C_RECONNECT, id);    
    send(msg);     
  }      
}