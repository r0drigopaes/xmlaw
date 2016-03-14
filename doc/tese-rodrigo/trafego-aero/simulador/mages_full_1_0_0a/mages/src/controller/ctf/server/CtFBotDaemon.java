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

package mages.controller.ctf.server;

import clisp.*;
import mages.controller.*;
import mages.controller.ctf.*;
import java.net.*;

public class CtFBotDaemon extends MagesDaemon{
  CtFSession simulation = null;
      
  public CtFBotDaemon(CtFDaemon cd, int mc) throws ConfigurationException{
    //Port: p
    //MAX_SESSION = 1
    //MAX_CONNECS = 2
    super(cd,7268,1,mc);    
    setName("Capture The Flag BOT Daemon - CtFBotDaemon");
  }
   
  public synchronized Session managerSession(User u){
    if(simulation==null){
      CtFDaemon d = (CtFDaemon)getDaemon();
      simulation = (CtFSession)d.getSession(0);
      if(simulation.getState()!=CtFSession.WAITING_BOTS){
        simulation = null;
      }
    }  
    
    if(simulation!=null){
      addSession(simulation);
    }    
    return simulation;
  }  
  
  public User initUser(Socket s){
    CtFBotServer b = new CtFBotServer(this, s, new CtFProtocol());
    return b;
  }            
}