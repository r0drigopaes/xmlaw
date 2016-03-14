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
import java.util.*;
import java.io.*;


public class CtFUserCleanDaemon extends ObserverDaemon{
  long timeSleep;
  long timeWaitParams;
  long timeWaitTeam;  

  public CtFUserCleanDaemon(CtFDaemon cd) throws ConfigurationException{
    super(cd);    
    setName("CLEAN User Capture The Flag Daemon - CtFUserCleanDaemon");
    try{    
      Properties prop = new Properties();    
      FileInputStream file = new FileInputStream("config");
      prop.load(file);    
      timeWaitParams = Integer.parseInt(prop.getProperty("TIME_OUT_PARAMS","120000"));
      timeSleep = Integer.parseInt(prop.getProperty("TIME_SLEEP","60000"));    
      timeWaitTeam = Integer.parseInt(prop.getProperty("TIME_WAIT_TEAM","300000"));      
      file.close();
    }  
    catch(IOException e){
      System.out.println("ERROR : File 'config' wasn't found");
    }  
  }
  
  private void cleanUsers(){
    CtFDaemon d = (CtFDaemon)getDaemon();
    User[] users = d.getUsers();
    for(int i=0; i<users.length; i++){
      CtFUser cuser = (CtFUser)users[i];
      if(cuser.getState()==CtFUser.WAITING_PARAMETERS){
        Date dt = new Date();
        if((dt.getTime()-cuser.getTimeState())>=timeWaitParams){
           cuser.quit("Parameters - Time Out");
        }
      }
    }
  }

  private void cleanSession(){
   CtFDaemon d = (CtFDaemon)getDaemon();
   CtFSession s = (CtFSession)d.getSession(0);
   Date dt = new Date();
   if(s!=null){
     long ref = (s.getTimeWait()>timeWaitTeam)?timeWaitTeam:s.getTimeWait();
     if((dt.getTime()-s.getTimeState())>=ref && s.getState()==CtFSession.WAITING_BOTS){
       s.quit("Team - Time Out");
       d.resetSessions();
     }
   }  
  }
       
  public void run(){
    while(true){
      cleanUsers();
      cleanSession();
      try{
        sleep(timeSleep);
      }
      catch(InterruptedException e){
        System.out.println("ERROR : Daemon couln't sleep");
      }  
    }  
  }
}