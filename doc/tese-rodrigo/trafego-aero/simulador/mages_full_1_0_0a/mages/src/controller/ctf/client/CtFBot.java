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

import mages.domain.ctf.*;
import mages.controller.ctf.*;
import mages.agents.*;
import mages.comm.*;
import mages.gui.*;
import mages.fuzion.*;
import mages.util.Coord;
import mages.*;
import mages.comm.*;
import clisp.*;

import java.io.*;
import java.awt.*;
import java.util.Vector;
import java.util.Date;

public class CtFBot implements Connectable, Agent, Directionable, 
  Stats, DynamicStats, Runnable, ClientReceiver{  
  
  public static final int HUMAN = 0;
  public static final int COMPUTER = 1;  
  
  Bot bot;
  String host;
  CtFBotClient managerSimulation;
  RemoteAgentController control;
  int mode = COMPUTER;  
  Thread thread;
  AgentInterface aint = null;
  SetupWindow sw = null;
  String idObj;

  public CtFBot(String h, int m, SetupWindow set){
    host = h;
    mode = m;
    sw = set;
  }
    
  public CtFBot(Bot b, String h){
    bot = b;
    host = h;
  }
  
  public CtFBot(Bot b, String h, int m){
    bot = b;
    host = h;
    mode = m;
  }

  public CtFBot(AgentInterface ai, Bot b, String h){
    aint = ai;
    bot = b;
    host = h;
    mode = HUMAN;
  }  
  
  public void setAgentInterface(AgentInterface ai){ 
    aint = ai;
  }
    
  public void connect(){
    try{
      managerSimulation = new CtFBotClient(this, new CtFProtocol());  
    }
    catch(IOException e){
      System.out.println("Bot error");
    }
  }
   
  public void reconnect(SessionId sid){
    connect();
    idObj = sid.getObjectId();
    managerSimulation.doReconnection(sid);
  }
      
  public String getHostName(){
    return host;
  }
  
  public int getPortNum(){
    return 7268;
  }  
  
  public String getName(){
    return bot.getName();
  }
  
  public Action act(long timestamp){
    return control.getAction(timestamp);
  }
  
  public void createController() throws ClassNotFoundException,IllegalAccessException,InstantiationException{
    Class cls = Class.forName(bot.getControlName());
    RemoteAgentController ac = (RemoteAgentController)cls.newInstance();
    ac.setAgent(this);
    ac.init();     
    setAgentController(ac); 
  }
  
  public void setAgentController(AgentController ac){
    control = (RemoteAgentController)ac;  
  }
  
  public AgentController getAgentController(){  
    return control;
  }
  
  public ActionsCollection possiblesActions(){
    ActionsCollection tmp = new ActionsCollection();
    tmp.addAction(new AttackAct());    
    tmp.addAction(new DodgeAct());            
    tmp.addAction(new DropFlagAct());                
    tmp.addAction(new GetFlagAct());     
    tmp.addAction(new MoveAct());
    tmp.addAction(new RecoveryAct());    
    tmp.addAction(new RotateAct());                           
    tmp.addAction(new ChangeWeaponAct());                               
    tmp.addAction(new StopAct());
    return tmp;
  }
    
  public void kill(){
     managerSimulation.quit();   
  }
  
  public Coord getPosition(){
    return bot.getPosition();
  }
  
  public void setPosition(Coord c){
    bot.setPosition(c);
  }
  
  public Point getPoint(){
    Coord c = bot.getPosition();
    return new Point(c.getX(),c.getY());
  }
  
  public void setPosition(Point p){
    bot.setPosition(new Coord(p.x,p.y));
  }
  
  public String[] getStatsNames(){
    return bot.getStatsNames();
  }
  
  public String[] getStatsValues(){
    return bot.getStatsValues();
  }
  
  public Stats getStats(){
    return this;
  }
  
  public String[][] getStatsSetup(){
    return bot.getStatsSetup();
  }
  
  public String[] getValues(){
    return bot.getValues();
  }
  
  public DynamicStats getDynamicStats(){
    return this;
  }  
 
  public void start() {
    if (thread == null) {
      thread = new Thread(this);
      thread.start();
    }
  }
  
  public void run(){
    //Receive messages
    boolean flag = true;    
    while(flag){
      Message m = null;                 
      try{
        m = managerSimulation.receive(false);
      }
      catch(IOException e){
        //implements
      }  
          
      if(m!=null){
        if(m instanceof AgentMessage){
          AgentMessage am = new AgentMessage(AgentProtocol.WHAT_IS_YOUR_ACTION,
                                             whatsYourAction()
                                             );
          int tmp[] = new int[1];
          tmp[0] = ((AgentMessage)m).getIdSender();                                             
          am.setIdReceiver(tmp);                                             
          am.setFunctionMessage(AgentMessage.ANSWER);
          am.setTypeMsg(AgentMessage.UNICAST);
          managerSimulation.send(am);
        }
        else{
          switch(m.getId()){
            case CtFProtocol.S_PERCEPTION:  
              Vector tmp = (Vector)m.getContent();
              control.setSensorsValue((Object[])tmp.elementAt(0));            
              setValues((String[])tmp.elementAt(1));                  
              aint.update(bot.getValues());
              if(!(getAgentController() instanceof Communicator)){
                doAction();            
              }
            break;
          
            case CtFProtocol.S_COMM:
              if(getAgentController() instanceof Communicator){
                Object v[] = (Object[])m.getContent(); 
                ((Communicator)control).setCommValue(v);      
                doAction();    
              }                    
            break;
                  
            case CtFProtocol.S_START_SIM:
              aint.botReady();
            break;
          
            case CtFProtocol.S_TURN:
              int i = m.getIntContent();           
              aint.setInfo("TURN # "+Integer.toString(i));
            break;          
          
            case CtFProtocol.S_SESSION_ID:
               SessionId ssi = (SessionId)m.getContent(); 
               ssi.setDate(new Date());
               idObj = ssi.getObjectId();
               try{
                 ssi.write(MagesEnv.getFilesDir("sessions"));
               }
               catch(IOException e){}                
            break;          
          
            case CtFProtocol.S_QUIT:
              try{
               finalize();
              }
              catch(Throwable e){}
              aint.setInfo(" Quit");
              flag = false;
            break;
          
            case CtFProtocol.S_SEND_BOT:
              bot = (Bot)m.getContent();
              try{
                createController();            
              }
              catch(Exception e){
                //implements
              }  

              sendComm();
              sendSensory();
              sw.createAgentWindow(this);
            break;
          }  
        }
      }  
    }      
  }
    
  public void sendBot(String idUser){
    managerSimulation.sendBot(bot, idUser, mode);  
  }
     
  public void sendSensory(){
    managerSimulation.sendSensory(control.getSensoryInformation());    
  }
  
  public void sendComm(){
    if(control instanceof Communicator){
      managerSimulation.sendComm(((Communicator)control).getCommInformation());
    }
  }
    
  public void setValues(String[] values){
    bot.defineHits(Integer.parseInt(values[0]));  
    bot.defineFat(Double.parseDouble(values[1]));
    bot.defineAmno(Bot.WEAPON_1,Integer.parseInt(values[2]));
    if(!values[3].equals("-1")){
      bot.defineAmno(Bot.WEAPON_2,Integer.parseInt(values[3]));    
    }
  }
  
  public int getMode(){
    return mode;
  }    
    
  public int getPositionMode(){
    return bot.getPositionMode();
  }
 
  public String getObjectId(){
    return idObj;
  }  

  public void doAction(){
    Action a = null;  
    if(mode==HUMAN){
      a = aint.getAction();    
    }
    else{
      a = control.getAction(0);
      aint.defineAction(a);      
    }  
    managerSimulation.sendAction(a);                                     
  }
    
  public int getDirection(){
    return bot.getDirection();
  }
  
  public void setAgency(Agency a){}
  
  public int getIdReceiver(){
    return bot.getNumId();  
  }
  
  public int getIdGroup(){
    return bot.getId();  
  }
  
  public void dispatchMessage(AgentMessage am){}

  public Action whatsYourAction(){
    return control.getAction(0);
  }
}