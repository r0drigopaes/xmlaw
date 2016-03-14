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

import java.net.*;
import java.io.*;

public class CtFUserClient extends Client{
  String host;
  
  public CtFUserClient(Connectable own, Protocol p) throws IOException{ 
    super(own,p);  
    host = own.getHostName();
  }
  
  public void canCreateSimulation(){
    Message msg = new Message(getLocalHost(),host,
                              CtFProtocol.C_CAN_CREATE_SIM);
    send(msg);     
  }
  
  public void createSimulation(Parameters p){
    Message msg = new Message(getLocalHost(),host,
                              CtFProtocol.C_CREATE_SIM,p);         
    send(msg);          
  }

  public void canJoinSimulation(){
    Message msg = new Message(getLocalHost(),host,
                              CtFProtocol.C_CAN_JOIN_SIM);    
    send(msg);                                   
  }
    
  public void joinSimulation(Parameters p){
    Message msg = new Message(getLocalHost(),host,
                              CtFProtocol.C_JOIN_SIM,p);    
    send(msg);                                   
  }
  
  public void quit(){
    Message msg = new Message(getLocalHost(),host,CtFProtocol.C_USER_QUIT);    
    send(msg);       
  }  
  
  public void doReconnection(SessionId id){
    Message msg = new Message(getLocalHost(),host,CtFProtocol.C_RECONNECT, id);    
    send(msg);     
  }    
}