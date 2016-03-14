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
import mages.util.Parameters;
import mages.domain.ctf.*;
import mages.controller.ctf.*;
import mages.agents.*;
import mages.comm.*;
import mages.fuzion.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class CtFBotServer extends User implements Observer, ServerReceiver{
  public static final int READY = 0;
 
  int state = -1;
  Date timeState = null;
  Bot bot;
  SensoryInformation info = null;
  Object valuesSensorInfo[] = null;
  CommInformation commInfo = null;  
  Action act = null;
  int mode = 0;
  String idObject = null;
  long cntNoAction = 0;
  Agency agency = null;
    
  public CtFBotServer(Daemon d, Socket sock) {
    super(d,sock);
    state = READY;
  }

  public CtFBotServer(Daemon d, Socket sock, Protocol p) {
    super(d,sock,p);  
    state = READY;    
  }
  
  public Bot getBot(){
    return bot;
  }
  
  public void setBot(Bot b){
    bot = b;
  }
    
  public SensoryInformation getSensoryInformation(){
    return info;
  }

  public CommInformation getCommInformation(){
    return commInfo;
  }  
    
  public int getMode(){
    return mode;
  }    
  
    
  public void run(){
    if(getDaemon().getNumUsers()<=getDaemon().getMaxUsers()){
      System.out.println("CtFServer: Bot "+getSocket().getLocalAddress().getHostAddress()+
                         " connected in "+(new Date()));                             
      CtFSession s = null;
      boolean stop = false;
      while(!stop){     
        Message m = null;
        try{
          m = receive(false);
        }
        catch(IOException e){
          System.out.println("Bot - Problem with message");
        }         
        //Process new message  
        if(m!=null){
          if(m instanceof AgentMessage){
            AgentMessage am = (AgentMessage)m;
            am.setIdSender(getIdReceiver());
            am.setIdGroup(getIdGroup());            
            dispatchMessage(am);
          }
          else{
		     if(m.getId()==CtFProtocol.C_RECONNECT){             
		       if(getDaemon().getSession(0)!=null){                                
		         SessionId sid = (SessionId)m.getContent();     
		         String session = ((CtFSession)getDaemon().getSession(0)).getSessionId();                                                     
		         idObject = sid.getObjectId();
		         if(sid.getSessionId().equals(session)){
		            if(((CtFSession)getDaemon().getSession(0)).getRegistry().contains(sid.getObjectId())){                       
                    s = (CtFSession)((CtFBotDaemon)getDaemon()).managerSession(this);		               
		               getDaemon().getSession(0).addUser(this);  		               
		            }
		            else{
		              quit("Sorry, but you aren't registred in this session");                  
		            }
		          }
		          else{
		            quit("Sorry, but you aren't in this session");                  
		          }
		        }
		        else{   
		          quit("The old session was canceled to other user");
	  	        }                                           
	  	      }    
		      
            switch(m.getId()){
              case CtFProtocol.C_SEND_BOT:               
                s = (CtFSession)((CtFBotDaemon)getDaemon()).managerSession(this);                              
                s.addUser(this);              
                Parameters prm = (Parameters)m.getContent();              
                bot = (Bot)prm.getObjectParameter(0);              
                String idUser = prm.getStringParameter(1);    
                mode = prm.getIntParameter(2);
                bot.setOwnerId(idUser);              
                boolean r = ((CtFSession)getDaemon().getSession(0)).getRegistry().contains(idUser);          
                if(!r){
                  quit("User isn't registried");
                }
                else{
                  sendSessionId(s.getSessionId(),idUser);              
                }       
              break;
                                    
              case CtFProtocol.C_SEND_SENSORS:  
                info = (SensoryInformation)m.getContent();                                                                  
                System.out.println("CtFSession: Bot "+bot.getName()+"@"+getSocket().getLocalAddress().getHostAddress()+
                                    " is ready !");
                s.botReady();                  
              break;

              case CtFProtocol.C_SEND_COMM:  
                commInfo = (CommInformation)m.getContent();                                                                  
                commInfo.addObserver(this);
                commInfo.setIdSender(getIdReceiver());
                commInfo.setIdGroup(getIdGroup());
                commInfo.setTimeWait(((CtFBotDaemon)getDaemon()).getTimeComm());
                commInfo.createTable();
                commInfo.start();
              break;
                        
              case CtFProtocol.C_SEND_ACTION:
                act = (Action)(m.getContent());
                s.botWithAction();            
              break;                        
            }
          } 
        }
        else{
          if(!((CtFSession)getDaemon().getSession(0)).isCanceled()){        
            //Can be problem with client bot
            int n = ((CtFBotDaemon)getDaemon()).getTimesToTry();
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
      finalize();   
      getDaemon().removeUser(this); 
    }  
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
 
  public synchronized Action getAction(){
    return act;
  }
   
  public synchronized void resetAction(){
    act = null;
  }
        
  public void quit(String s){
    send(new Message(null,null,CtFProtocol.S_QUIT,s));
    finalize();   
    getDaemon().removeUser(this);    
  }  
  
  public void configSensors(Object[] list){
    info.config(list);
  }
  
  public void sendPerception(Vector p){
    valuesSensorInfo = (Object[])p.elementAt(0);  
    Message msg = new Message(null,null,CtFProtocol.S_PERCEPTION,p);
    send(msg);
  }

  private void sendComm(Object[] info){
    Message msg = new Message(null,null,CtFProtocol.S_COMM,info);
    send(msg);      
  }
    
  public void finalize(){
    if(getSocket()!=null){
      System.out.println("CtFServer: Bot "+getSocket().getLocalAddress().getHostAddress()+
                          " was disconnected. "+(new Date()));          
    }
    else{
      System.out.println("CtFServer: FALT !! Bot was disconnected");         
    }
                           
    getDaemon().getSession(0).removeUser(this);    
    getDaemon().removeUser(this);  
    super.finalize();       
  }  
  
  public void sendSessionId(String idSes, String idOwn){               
     String idObj = Id.createId();
     if(idOwn.charAt(0)=='C'){
       idObj = "C"+idObj;     
     }
     else{
       if(idOwn.charAt(0)=='J'){
         idObj = "J"+idObj;            
       }     
     }          
     ((CtFSession)getDaemon().getSession(0)).getRegistry().link(idOwn,idObj);     
     SessionId si = new SessionId();
     si.setSessionId(idSes);
     si.setObjectId(idObj);
     si.setOwnerId(idOwn);
     bot.setOwnerId(idObj);
     send(new Message(null,null,CtFProtocol.S_SESSION_ID,si));  
  }  
  
  public String getObjectId(){
    return idObject;
  }
    
  public void sendBot(Bot b){
    bot = b;  
    send(new Message(null,null,CtFProtocol.S_SEND_BOT,b));    
  }  
  
  public void setAct(boolean f){
    if(f) cntNoAction = 0; 
    else cntNoAction++;
  }
  
  public long getNoActions(){
    return cntNoAction;
  }
  
  public boolean hasCommunicator(){
    return (commInfo==null)?false:true;
  }
    
  public void update(Observable o, Object arg){
    CtFSession s = (CtFSession)getDaemon().getSession(0);
    sendComm(commInfo.value(s.getTurn()));   
  }
  
  public void setAgency(Agency a){
    agency = a;
  }
  
  public int getIdReceiver(){
    return bot.getNumId();
  }
  
  public int getIdGroup(){
    return bot.getId();
  }
    
  public void dispatchMessage(AgentMessage am){
    if(am.getFunctionMessage()==AgentMessage.QUESTION){    
      if(am.getIdMessage()!=AgentProtocol.WHAT_IS_YOUR_ACTION){
        Transceiver.processing(this,am,agency);
      }
      else{
          send(am); //send to client
      }
    }
    else{
      if(am.getFunctionMessage()==AgentMessage.ANSWER){    
        commInfo.addAnswer(am);    
      }
    }  
  }

  public String onQueryAttribute(Object attr){
    String arg = (String)attr;
    String result = null;
    if(arg.equals("HITS")){
      result = Integer.toString(bot.getHitsCur());
    }
    else{
      if(arg.equals("FAT")){
        result = Double.toString(bot.getFatCur());      
      }
      else{
        if(arg.equals("WPN1")){
          Weapon wnp[] = bot.getWeapons();
          if(wnp[0]!=null){
            result = Integer.toString(wnp[0].getAmnoCur());
          }
        }
        else{
          if(arg.equals("WPN2")){
            Weapon wnp[] = bot.getWeapons();
            if(wnp.length==2){
              result = Integer.toString(wnp[1].getAmnoCur());
            }          
          }
        }
      }
    }
    return result;
  }
  
  public Object[] onQuerySensory(){
    return valuesSensorInfo;
  }  
}

