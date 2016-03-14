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
import mages.controller.ctf.*;
import mages.util.Parameters;
import mages.domain.ctf.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class CtFUser extends User{
  public static final int READY = 0;
  public static final int WAITING_PARAMETERS = 1;  
  public static final int WAIT_OTHER_TEAM = 2;    
  public static final int WITH_PARAMETERS = 3;  
 
  int state = -1;
  Date timeState = null;
  Parameters prm = null;
  boolean creator = false;
                
  public CtFUser(Daemon d, Socket sock) {
    super(d,sock);
    state = READY;
  }

  public CtFUser(Daemon d, Socket sock, Protocol p) {
    super(d,sock,p);  
    state = READY;    
  }
    
  public void run(){
    if(getDaemon().getNumUsers()<=getDaemon().getMaxUsers()){  
      send(new Message(null,null,CtFProtocol.S_CONNECTION,"OK"));    
      System.out.println("CtFServer: User "+getSocket().getLocalAddress().getHostAddress()+
                         " connected in "+(new Date()));
      boolean stop = false;         
      while(!stop){
        Message m = null;
        try{
          m = receive();                
        }
        catch(IOException e){
          System.out.println("User - Problem with message");
        }  

        //Process new message  
        if(m!=null){             
          if(m.getId()==CtFProtocol.C_USER_QUIT){
            if(((CtFSession)getDaemon().getSession(0))!=null){
             ((CtFSession)getDaemon().getSession(0)).cancelSim();                  
            } 
          }
          else{
            if(m.getId()==CtFProtocol.C_RECONNECT){             
              if(getDaemon().getSession(0)!=null){                                
                SessionId sid = (SessionId)m.getContent();     
                String session = ((CtFSession)getDaemon().getSession(0)).getSessionId();                                        
                if(sid.getSessionId().equals(session)){
                  if(((CtFSession)getDaemon().getSession(0)).getRegistry().contains(sid.getObjectId())){                       
                     getDaemon().getSession(0).addUser(this);  
                     send(new Message(null,null,CtFProtocol.S_RECONNECT_TEAM));                                                                 
                  }
                  else{
                    quit("Sorry, but you aren't registred in this session");                  
                    stop = true;
                  }
                }
                else{
                  quit("Sorry, but you aren't in this session");                  
                  stop = true;                  
                }
              }
              else{   
                quit("The old session was canceled to other user");
                 stop = true;                
              }                                           
            }
          }
                                        
          switch(state){
            case READY:
              CtFDaemon tmp = (CtFDaemon)getDaemon();
              switch(m.getId()){                
                case CtFProtocol.C_CAN_CREATE_SIM:                             
                  if(tmp.canCreate()){                                  
                    send(new Message(null,null,CtFProtocol.S_CAN_CREATE_SIM,"OK"));                                    
                    state = WAITING_PARAMETERS;                     
                    setTimeState();
                  }
                  else{                
                    send(new Message(null,null,CtFProtocol.S_CAN_CREATE_SIM,"NO"));                
                    finalize();
                  }                
                break;
                                            
                case CtFProtocol.C_CAN_JOIN_SIM:
                  if(!tmp.canCreate()){
                    send(new Message(null,null,CtFProtocol.S_CAN_JOIN_SIM,"OK"));                
                    state = WAITING_PARAMETERS;
                    setTimeState();
                  }
                  else{                
                    send(new Message(null,null,CtFProtocol.S_CAN_JOIN_SIM,"NO"));                
                    finalize();
                  }                
                break;                
              }            
            break;            
            
            case WAITING_PARAMETERS:           
              prm = (Parameters)m.getContent();      
              state = WITH_PARAMETERS;
              CtFSession ses = (CtFSession)getDaemon().managerSession(this);                          
              switch(m.getId()){
                case CtFProtocol.C_CREATE_SIM:                                                         
                  if(ses!=null){                    
                    creator = true;
                    ses.addUser(this);     
                    sendSessionId(ses.getSessionId(),"C");
                    ses.start();
                  }
                  else{
                    send(new Message(null,null,CtFProtocol.S_CREATE_SIM,"NO"));    
                    finalize();
                  }                
                break;
                
                case CtFProtocol.C_JOIN_SIM:
                  if(ses!=null){
                    Team t = (Team)prm.getObjectParameter(0);
                    if(ses.validate(t)){
                      ses.addUser(this);
                      sendSessionId(ses.getSessionId(),"J");                      
                      ses.setRedTeam();                      
                    }
                    else{
                      quit("Team invalid !!");
                    }  
                  }
                  else{
                    send(new Message(null,null,CtFProtocol.S_JOIN_SIM,"NO"));   
                    finalize();                                              
                  }                     
                break;                                
              }
            break;
          }                              
        }
        else{
          if(!((CtFSession)getDaemon().getSession(0)).isCanceled()){
            //Can be problem with client
            int n = ((CtFDaemon)getDaemon()).getTimesToTry();
            int cnt = 0;
            int error = 0;
            while(m==null && cnt<n){ 
              try{
                m = receive();
              }
              catch(IOException e){
                System.out.println("Bot - Problem with message");
              }                   
              if(m==null) error++;
              cnt++;
              try{
                Thread.sleep(200);
              }
              catch(InterruptedException e){}  
            }
            if(error==n){
              stop = true;              
              finalize();            
            }
          }  
        }        
      }         
    }
    else{
      send(new Message(null,null,CtFProtocol.S_CONNECTION,"Sorry, but server is full!!"));
      finalize();   
    }  
  } 
  
  public synchronized Parameters getParams(){
     return prm;
  }
  
  public synchronized int getState(){
    return state;
  }
  
  public synchronized void setTimeState(){
    timeState = new Date();
  }
  
  public synchronized long getTimeState(){
    return timeState.getTime();
  }    
  
  public synchronized boolean isCreator(){
    return creator;
  }    
  
  public void finalize(){
    if(getSocket()!=null){
      System.out.println("CtFServer: User "+getSocket().getLocalAddress().getHostAddress()+
                          " was disconnected. "+(new Date()));          
    }
    else{
      System.out.println("CtFServer: FALT !! User was disconnected");         
    }                                 
    
    if(getDaemon().getSession(0)!=null){
      getDaemon().getSession(0).removeUser(this);    
    }  
    getDaemon().removeUser(this);
    super.finalize();       
  }
  
  public void quit(String s){
    send(new Message(null,null,CtFProtocol.S_QUIT,s));
    finalize();                           
  }
  
  public void sendSessionId(String idSes, String prefix){               
     String idObj = Id.createId();
     idObj = prefix + idObj;
     ((CtFSession)getDaemon().getSession(0)).getRegistry().addId(idObj);
     SessionId si = new SessionId();
     si.setSessionId(idSes);
     si.setObjectId(idObj);
     send(new Message(null,null,CtFProtocol.S_SESSION_ID,si));  
  }
  
  public void sendRecoveryMessage(String msg){
    send(new Message(null,null,CtFProtocol.S_RECOVERY,msg));
  }
}