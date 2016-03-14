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
import mages.util.Parameters;
import mages.domain.ctf.*;
import mages.app.ctf.*;
import mages.*;
import java.net.*;
import java.io.*;

public class CtFDaemon extends MagesDaemon{
  CtFSession simulation = null;
  long maxComputerTime;
  long maxHumanTime;  
  long recTime;
  boolean useScreen = true;
  long tolerance;  
  String email;
    
  public CtFDaemon() throws ConfigurationException{
    //Port: p
    //MAX_SESSION = 1
    //MAX_CONNECS = 2
    super(7267,1,2);    
    setName("Capture The Flag Daemon - CtFDaemon");
  }
   
  public synchronized Session managerSession(User u){
    if(simulation==null){
      CtFUser tmp = (CtFUser)u;
      Parameters prm = tmp.getParams();
      String str = MagesEnv.getFilesDir("ctfTourn")+prm.getStringParameter(0);
      Tournament tourn = null;      
      try{
        TournamentController tc = new TournamentController();
        tourn = (Tournament)tc.load(str); 
      }  
      catch(IOException e){
        System.out.println("CTF : Session wasn't created. ("+e.getMessage()+")");
      }      
      int mbots =  tourn.getMaxBotsForTeam();
      int mteams = tourn.getMaxTeams();
      int maxCon = (mbots*mteams)+mteams;
      simulation = new CtFSession(this, maxCon, prm);      
      addSession(simulation);      
    }  
    return simulation;
  }  
  
  public User initUser(Socket s){
    CtFUser u = new CtFUser(this, s, new CtFProtocol());
    return u;
  }          
  
  public synchronized boolean canCreate(){
    return (simulation==null)?true:false;
  }
  
  public synchronized void resetSessions(){
    super.resetSessions();
    simulation = null;           
  }
  
  public Session getSession(int i){
     return simulation;     
  }

  public void setRecoveryTime(long rt){
    recTime = rt;
  }
    
  public void setMaxComputerTime(long mct){
    maxComputerTime = mct;
  }
  
  public void setMaxHumanTime(long mht){
    maxHumanTime = mht;    
  }
  
  public long getMaxComputerTime(){
    return maxComputerTime;
  }
  
  public long getMaxHumanTime(){
    return maxHumanTime;    
  }  
  
  public long getRecoveryTime(){
    return recTime;
  }
  
  public void setUseScreen(boolean f){
    useScreen = f;
  }
  
  public boolean isUsingScreen(){
    return useScreen;
  }  
  
  
  public void setTolerance(long t){
    tolerance = t;
  }
  
  public long getTolerance(){
    return tolerance;
  }    
  
  public void setAdminEmail(String am){
    email = am;
  }
  
  public String getAdminEmail(){
    return email;
  }  
}