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
import mages.*;
import mages.app.ctf.*;
import mages.util.Parameters;
import mages.controller.ctf.*;
import mages.domain.ctf.*;
import mages.agents.*;
import mages.comm.*;
import mages.simulation.*;
import mages.fuzion.*;
import mages.util.MagesDimension; 

import java.net.*;
import java.io.*;
import java.util.*;

public class CtFSession extends Session implements Runnable{ 
  public static final int READY = 0;
  public static final int WAITING_BOTS = 2;  
  public static final int RUNNING = 3;  
  public static final int RECOVERING = 4;    
  public static final boolean LOG = true;
  
  final int HUMAN = 0;
  final int COMPUTER = 1;  
      
  Tournament rules = null;
  World map = null;
  CtFRules manager = null;
  int timeWait = 0;
  int state = -1;
  int maxBots = 0;
  int cntBots = 0;  
  int cntReady =0;
  int cntActions = 0;
  Date timeState = null;   
  long timeComp;
  long timeHuman;  
  int worstMode;
  int bestMode;  
  int useMode;
  boolean hasBlueTeam = false;
  boolean hasRedTeam = false;
  Thread thread = null;
  Hashtable indexBots = new Hashtable();
  Registry reg = new Registry();  
  int botsDead = 0;
  World stateSim = null;
  World startCond = null;
  String idSession = null;
  boolean cancel = false;
  CtFScreenServer css = null; 
  int zombieBots = 0;
  boolean modeBatch = false;
  int runs = 1;
  CtFExperiment exp = null;
  Agency agency = null;
  long seed, newSeed;
  int sumRed = 0;
  int sumBlue = 0;  
  int sumRedFlags = 0;
  int sumBlueFlags = 0;    
  int maxTurns = 3600;
            
  public CtFSession(Daemon d, int mu, Parameters prm){
    super(d,mu);
    state = READY;
    
    createSession(prm);

    timeComp = ((CtFDaemon)d).getMaxComputerTime();
    timeHuman = ((CtFDaemon)d).getMaxHumanTime();    
    long maxTimeWaitAction = (timeComp>timeHuman)?timeComp:timeHuman;
    worstMode = (timeComp==maxTimeWaitAction)?COMPUTER:HUMAN; 
    bestMode = (worstMode==COMPUTER)?HUMAN:COMPUTER;    
    useMode = bestMode;    
  }
  
  private void createSession(Parameters prm){
    String str = MagesEnv.getFilesDir("ctfTourn")+prm.getStringParameter(0);
    try{
      TournamentController tc = new TournamentController();
      rules = (Tournament)tc.load(str); 
    }  
    catch(IOException e){
      System.out.println("CTF : Session wasn't created. ("+e.getMessage()+")");
    }  
        
    map = (World)prm.getObjectParameter(2);
    maxBots = prm.getIntParameter(1); 
    maxTurns = prm.getIntParameter(7);     
    manager = new CtFRules(map,maxTurns);    
    timeWait = prm.getIntParameter(3);

    seed = prm.getLongParameter(4);
    
    modeBatch = prm.getBooleanParameter(5);
    runs = prm.getIntParameter(6);       
    hasBlueTeam = true;
    idSession = Id.createId();
    state = WAITING_BOTS;
    setTimeState();          
  }
       
  public synchronized void setRedTeam(){
    hasRedTeam = true;
    notify();    
  }
  
  public void run(User u){ /*nothing*/}
  
  public synchronized int getState(){
    return state;
  }
  
  public synchronized void setTimeState(){
    timeState = new Date();
  }
  
  public synchronized long getTimeState(){
    return timeState.getTime();
  }  
  
  public int getTimeWait(){
    return timeWait;
  }    
  
  public void quit(String s){
    for(int i=0; i<getNumUser(); i++){
      if(getUser(i) instanceof CtFUser){
        CtFUser u = (CtFUser)getUser(i);
        u.quit(s);      
      }
      else{
        CtFBotServer b = (CtFBotServer)getUser(i);
        b.quit(s);
      }
    }
  }
  
  private void sendAll(Message m){
    for(int i=0; i<getNumUser(); i++){
      User u = getUser(i);
      u.send(m);
    }
  }
  
  public boolean validate(Team t){
    boolean ok = true;
    for(int i=0; i<t.getNumBots(); i++){
      Bot b = t.getBot(i);
      if(b.getBotPtos()>rules.getMaxBotsPtos()){
        ok = false;
      }
    }
    return ok;
  }
  
  private Message getMessage(User u){
     Message m = null;
     try{
       m = u.receive();        
     }
     catch(IOException e){
       System.out.println("CtFSession: Problems with message");
     } 
     return m;
  }  
 
  public void start() {
    if (thread == null) {
      thread = new Thread(this);
      thread.start();
    }
  }
      
  private synchronized void waitTeam(){
   if(hasBlueTeam && !hasRedTeam){
     try{
       System.out.println("CtFSession waiting other team "+(new Date()));
       wait();
       sendAll(new Message(null,null,CtFProtocol.S_CONNECT_TEAM));           
     }
     catch(InterruptedException e){ //implements 
     }
   }       
  }
   
  private synchronized void waitBots(){
    try{
      System.out.println("CtFSession waiting bots "+(new Date()));
      wait();      
    }
    catch(InterruptedException e){ //implements 
    }
  }
  
  private synchronized void posBotsOnWorld(){
    if(cntReady<(getMaxUsers()-2)){
      try{
        wait();      
      }
      catch(InterruptedException e){ //implements 
      }    
    }
    for(int i=0; i<getNumUser(); i++){
      User u = getUser(i);
      if(u instanceof CtFBotServer){  
        CtFBotServer cbs = (CtFBotServer)u;
        Bot b = cbs.getBot();
        indexBots.put(b,cbs);
        manager.getWorld().position(b);
      }
    }
  }
    
  //0 Bot source  
  //1 Map
  //2 array of flags (all)
  //3 array of bases
  //4 array of bots (include myself)
  private synchronized void configSensors(){
    Object[] tmp = new Object[5];
    tmp[1] = map;
    tmp[2] = map.getFlags();
    tmp[3] = map.getBases();
    tmp[4] = getBots();
    for(int i=0; i<getNumUser(); i++){    
      User u = getUser(i);
      if(u instanceof CtFBotServer){  
        tmp[0] = ((CtFBotServer)u).getBot();      
        ((CtFBotServer)u).configSensors(tmp);
        if(((CtFBotServer)u).getMode()==worstMode){               
          useMode = worstMode;
        }        
      }
    }     
  }
  
  private synchronized void configComm(){  
    agency = new Agency(10); //maximum 9 bots
    for(int i=0; i<getNumUser(); i++){    
      User u = getUser(i);
      if(u instanceof Receiver){
        agency.registry((Receiver)u);
        ((Receiver)u).setAgency(agency);
      }
    }      
  }  
  
  private synchronized void createScreen(){
    if(((CtFDaemon)getDaemon()).isUsingScreen()){          
      //Open frame
      css = new CtFScreenServer(this); 
      css.initGame();
      css.createFrame(css);
      css.startGame();     

      //Wait create screen
      try{
        wait();      
      }
      catch(InterruptedException e){ //implements 
      }      
    }    
  }

  private synchronized void waitRecovery(){
    try{
      wait(((CtFDaemon)getDaemon()).getRecoveryTime());      
    }
    catch(InterruptedException e){ //implements 
    }  
  }
              
  private synchronized void waitActions(){
    try{    
      wait((useMode==COMPUTER)?timeComp:timeHuman);      
    }
    catch(InterruptedException e){ //implements 
    }  
  }
  
  private boolean connectionsAreOk(){  
    if(getNumUser()<(getMaxUsers()-botsDead) || cancel){    
      return false;
    }
    else{
      return true;
    }  
  }            
  
  private void saveState(){
    stateSim = (World)map.clone();    
  }
  
  private void infoAllUsers(String inf){
    for(int i=0; i<getNumUser(); i++){
      User u = getUser(i);
      if(u instanceof CtFUser){  
        CtFUser ctu = (CtFUser)u;
        ctu.sendRecoveryMessage(inf);
      }
    }    
  }
  
  private void execution(){
    state = RUNNING;
    while(!manager.isFinished()&&connectionsAreOk()){    
      if (LOG) System.out.println("CtFSession - Turn #"+manager.getTurn());      
      agency.reset();
      infoTurn();
      perception(manager.getTurn());
      communication();      
      waitActions();     
      if(connectionsAreOk()) cntActions = 0;
      processing(); //more important !!
      postProcessing();
      saveState();
      if(connectionsAreOk()) manager.passTurn();      
    }  

    if(connectionsAreOk()){
      if(modeBatch){
        exp.addNewReplication(manager,newSeed);
      }
    }
    else{
      if(!cancel){
        state = RECOVERING;    
        infoAllUsers("Please wait. Connections problem. Server recovering");
        if (LOG) System.out.println("CtFSession - Recovering ... ");
        cntReady = getNumUser()-1;
        cntReady = (cntReady<0)?0:cntReady;
        waitRecovery();
        if(connectionsAreOk()){
          if (LOG) System.out.println("CtFSession - Recovering ... OK");      
          map = (World)stateSim.clone();
          sendBots();     
          waitBots(); 
          doIndexingBots();                        
          configSensors();          
          configComm();
          css.updateScreen();
          infoAllUsers("OK");           
          startSim();                                   
          execution();
        }
        else{
          if (LOG) System.out.println("CtFSession - Recovering ... It wasn't possible");
          infoAllUsers("NO");
          for(int i=0; i<getNumUser();i++){
            getUser(i).finalize();
          }
        }
      }
      else{
        quit("Simulation was canceled for an user");
      }  
      quit("Finish");      
      resetSession();      
    }
  }
  
  private void communication(){
    if (LOG) System.out.println("CtFSession - Start communication process ..."+(new Date()));  
    agency.setTimeStamp(getTurn());
    for(int i=0; i<getNumUser(); i++){
      if(getUser(i) instanceof CtFBotServer){
        CtFBotServer b = (CtFBotServer)getUser(i);
        if(b.hasCommunicator()){
          b.getCommInformation().sendMessages(agency,getTurn()); 
        }
      }  
    }      
    if (LOG) System.out.println("CtFSession - Finish communication process "+(new Date()));        
  }

  private void doIndexingBots(){
    indexBots = new Hashtable();
    for(int i=0; i<getNumUser(); i++){
      if(getUser(i) instanceof CtFBotServer){
        CtFBotServer b = (CtFBotServer)getUser(i);
        indexBots.put(b.getBot(),b);
      }  
    }  
  }

      
  private void sendBots(){  
    Bot tmp[] = map.getBots();
    int cntDie = 0;
    Hashtable index = new Hashtable();
    for(int i=0; i<tmp.length; i++){
      index.put(tmp[i].getOwnerId(),tmp[i]);
      if(tmp[i].isDead()){
         cntDie++;
      }              
    }
    botsDead = cntDie;
        
    for(int j=0; j<getNumUser(); j++){
      if(getUser(j) instanceof CtFBotServer){      
        CtFBotServer bu = (CtFBotServer)getUser(j);
        if(bu.getObjectId()!=null){
          Bot ret = (Bot)index.get(bu.getObjectId());                          
          bu.sendBot(ret);
        }  
      }
    }
  }
  
  private Hashtable doIndexBatch(){
    Hashtable temp = new Hashtable();
    for(int i=0; i<getNumUser(); i++){
      if(getUser(i) instanceof CtFBotServer){
        CtFBotServer bu = (CtFBotServer)getUser(i);
        temp.put(bu.getBot().getOwnerId(),bu);
      }
    }  
    return temp;
  }
  
  private void resetBotsBatch(Hashtable t){
    Bot bots[] = map.getBots();
    for(int i=0; i<bots.length; i++){
      CtFBotServer bu = (CtFBotServer)t.get(bots[i].getOwnerId());
      if(bu!=null){
        bu.setBot(bots[i]);
      }
    }
  }
  
  public void run(){
    //Setup
    if (LOG) System.out.println("CtFSession setup ...");    
    waitTeam(); 
    waitBots();    
    posBotsOnWorld();
    configSensors();    
    configComm();
    createScreen();
    
    if (LOG) System.out.println("CtFSession starts "+(new Date())); 
    startSim();
    saveState();
    startCond = (World)map.clone();        
    if(connectionsAreOk()){
      if(modeBatch){
        exp = new CtFExperiment(manager,rules.getMaxBotsPtos());                        
        Random general = new Random(seed);        
        Hashtable ind = doIndexBatch();
        
        for(int r=0; r<runs; r++){        
          System.out.println("CtFSession - Batch - Run #"+(r+1));         
          Distribution.setRandom(general);       
          newSeed = (int)(Distribution.uniform(0.0,1.0)*2147483647);
          Random rndRun = new Random(newSeed);
          Distribution.setRandom(rndRun);          
          execution();

          sumRed += countLiveBots(CtFSettings.RED);
          sumBlue += countLiveBots(CtFSettings.BLUE);          
          sumRedFlags += map.getNumFlagsInBase(CtFSettings.RED,CtFSettings.BLUE);
          sumBlueFlags += map.getNumFlagsInBase(CtFSettings.BLUE,CtFSettings.RED);              

          map = (World)startCond.clone();
          manager = new CtFRules(map);              
          resetBotsBatch(ind);               
          doIndexingBots();          
        }
        try{
          exp.save(MagesEnv.getFilesDir("exp/ctf/"+idSession));
        }
        catch(IOException ioe){
          //implements
        }  
        endSimulationBatch();                 
      }
      else{
        Distribution.setSeed(seed);      
        execution(); 
        endSimulation();         
      } 
    }
    else{
      quit("Problems with connection");
    }  
    if (LOG) System.out.println("CtFSession finish "+(new Date()));        
  } 
  
  private int countLiveBots(int id){
    int cnt = 0;
    Bot bots[]  = getBots();
    for(int i=0; i<bots.length; i++){
      if(!bots[i].isDead()){
        if((bots[i].getId()==id)) cnt++;
      }
    }  
    return cnt;
  }
  
  private void endSimulation(){
    Parameters prm = new Parameters();

    prm.add(countLiveBots(CtFSettings.RED));
    prm.add(countLiveBots(CtFSettings.BLUE));
    prm.add(map.getNumFlagsInBase(CtFSettings.RED,CtFSettings.BLUE));
    prm.add(map.getNumFlagsInBase(CtFSettings.BLUE,CtFSettings.RED));    
    
    sendAll(new Message(null,null,CtFProtocol.S_SCORE,prm));
  }

  private void endSimulationBatch(){
    Parameters prm = new Parameters();
    
    prm.add(((CtFDaemon)getDaemon()).getAdminEmail());
    prm.add(idSession);    
    prm.add((double)sumRed/(double)runs);
    prm.add((double)sumBlue/(double)runs);
    prm.add((double)sumRedFlags/(double)runs);
    prm.add((double)sumBlueFlags/(double)runs);    
    
    sendAll(new Message(null,null,CtFProtocol.S_SCORE_BATCH,prm));
  }
    
  private void infoTurn(){  
    if(connectionsAreOk()){          
      sendAll(new Message(null,null,CtFProtocol.S_TURN,manager.getTurn()));    
    }
  }
  
  private synchronized void startSim(){
    sendAll(new Message(null,null,CtFProtocol.S_START_SIM));
    try{
      wait(500);      
    }
    catch(InterruptedException e){ //implements 
    }    
  }
  
  private Bot[] getBots(){
    Vector tmp = new Vector();
    for(int i=0; i<getNumUser(); i++){
      User u = getUser(i);
      if(u instanceof CtFBotServer){  
        CtFBotServer cbs = (CtFBotServer)u;
        tmp.addElement(cbs.getBot());
      }
    }  
    
    Bot tmp2[] = new Bot[tmp.size()];
    for(int j=0; j<tmp.size(); j++){
      tmp2[j] = (Bot)tmp.elementAt(j);
    }
    return tmp2;
  }

  private void postProcessing(){
    if(connectionsAreOk()){  
      Bot bots[] = getBots(); 
      for(int i=0; i<bots.length; i++){            
        //Bot in defense
        if(bots[i].isDodge()){
          bots[i].setDodge(false);
        }                    
      }    
      
      //Kill zombie bots
      long tol = ((CtFDaemon)getDaemon()).getTolerance();
      for(int i=0; i<getNumUser(); i++){
        if(getUser(i) instanceof CtFBotServer){
          CtFBotServer cbs = (CtFBotServer)getUser(i);
          if(cbs.getBot().isDead()){
            botsDead++;  
            if(!modeBatch){
              cbs.quit("");  
              cbs.finalize();
            } 
          }
                                     
          if(cbs.getNoActions()>=tol){
            botsDead++;
            zombieBots++;
            cbs.getBot().defineHits(0);
            if(!modeBatch){
              cbs.quit("");
              cbs.finalize();
            }  
          } 
        }
      }      
    }  
  }
    
  private void processing(){
    if(connectionsAreOk()){  
      if (LOG) System.out.println("Processing...");        
      Bot bots[] = getBots();  
      bots = manager.initiative(bots);        
      for(int i=0; i<bots.length; i++){
        if(!bots[i].isDead()){
          CtFBotServer cbs = (CtFBotServer)indexBots.get(bots[i]);                         
          Action a = cbs.getAction();           
          System.out.println("do "+a);
          if(a!=null){                 
            doAction(bots[i],a);
            cbs.setAct(true);                            
          }
          else{                     
            System.out.println("no action");        
            cbs.setAct(false);               
          }
          cbs.resetAction();          
        }
      }    
    }  
  }
      
  private void perception(long turn){
    if(connectionsAreOk()){
      if (LOG) System.out.println("CtFSession - Start perception process ..."+(new Date()));
      for(int i=0; i<getNumUser(); i++){
        User u = getUser(i);
        if(u instanceof CtFBotServer){  
          SensoryInformation si = ((CtFBotServer)u).getSensoryInformation();      
          Bot b = ((CtFBotServer)u).getBot();        
          Vector prm = new Vector();
          prm.addElement(si.value(turn));  //sensory perception
          prm.addElement(b.getValues()); //dynamic values
          ((CtFBotServer)u).sendPerception(prm);
        }
      }  
      if (LOG) System.out.println("CtFSession - Finish perception process "+(new Date()));    
    }  
  }

  
  private synchronized void verifyNumBots(){
    if(state!=RECOVERING){
      if(getNumUser()==(getMaxUsers()-2-botsDead)){
        state = RUNNING;          
        notify();
      }
    }
    else{
      if(getNumUser()==(getMaxUsers()-botsDead)){
        state = RUNNING;          
        notify();
      }
    }  
  }

  public synchronized void botReady(){
    cntReady++;    
    if(state!=RECOVERING){
      if(cntReady==(getMaxUsers()-botsDead-2)){
        notify();
      }
    }
    else{
      if(cntReady==(getMaxUsers()-botsDead)){
        notify();
      }    
    }  
  }
      
  public synchronized void addUser(User u){
    super.addUser(u);  
    verifyNumBots();
  }
  
  public World getWorld(){
    return map;
  }
  
  public int getTurn(){
    return (manager!=null)?manager.getTurn():-1;
  }
  
  public synchronized void wakeUp(){
    notify();
  }  
  
  public Registry getRegistry(){
    return reg;
  }
  
  public synchronized void botWithAction(){
    cntActions++;  
    if(cntActions==(getMaxUsers()-2-botsDead)){
      notify();
    }
  }  
  
  private Bot[] getTargets(Bot b, int target, int mode){
    NodePosicionable nodePosBot = new NodePosicionable(b);
    MagesDimension fix = new MagesDimension(map.getMap().getWidth(),map.getMap().getHeight());
    VisionObjects vision = new VisionObjects(nodePosBot, 
                                             10, new FixedDimension(fix));    
    Bot targets[] = getBots();                                             
    for(int i=0; i<targets.length; i++){
      if(mode==CtFRules.AUTOFIRE){
        if(!targets.equals(b) && !b.isDead()){
          vision.addNode(new NodePosicionable(targets[i]));
        }      
      }
      else{
        if(!targets[i].equals(b) && !b.isDead() && b.getId()!=targets[i].getId()){
          vision.addNode(new NodePosicionable(targets[i]));
        }            
      }
    }                                             
  
    EightSectors es = new EightSectors(nodePosBot, vision, 10, new FixedDimension(fix));
    SixSectors ss = new SixSectors((Directionable)b,es);
    ViewField viewField = new ViewField(ss);
               
    Bot tmp[] = null;            
    if(mode==CtFRules.HAND_TO_HAND){
      Nearest near = new Nearest(nodePosBot,viewField,Nearest.FIRST);   
      Bot btrg = (Bot)near.value(0);               
      if(btrg!=null){  
      System.out.println("dist "+Distance.distance(b.getPosition(),btrg.getPosition()));
        if(Distance.distance(b.getPosition(),btrg.getPosition())<=1){    
          tmp = new Bot[1];
          tmp[0] = btrg; 
        }  
      }  
    }
    else{
      if(mode==CtFRules.SIMPLE){
        int t = 0;
        switch(target){
          case AttackAct.NEAR: t = Nearest.FIRST;
          break;
          case AttackAct.MID_TARGET: t = Nearest.MIDDLE;
          break;            
          case AttackAct.FAR: t = Nearest.LAST;
          break;      
        }             
        Nearest near = new Nearest(nodePosBot,viewField,t);            
        Bot btrg = (Bot)near.value(0);               
        if(btrg!=null){      
          tmp = new Bot[1];
          tmp[0] = btrg; 
        }          
      }
      else{
        if(mode==CtFRules.AUTOFIRE){
          Posicionable[] tmp2 = viewField.value(0);
          if(tmp2!=null){
            tmp = new Bot[tmp2.length];
            for(int i=0; i<tmp.length; i++){
              tmp[i] = (Bot)tmp2[i];
            }
          }  
        }
      }
    }
    return tmp;
  }
  
  public String getSessionId(){
    return idSession;
  }
  
  public synchronized void removeUser(User u){
    super.removeUser(u);
    cntReady--;
    if(!connectionsAreOk()&&state==RUNNING){
      notify();
    }
  }
  
  public synchronized void cancelSim(){
    cancel = true;
    notify();
  }
  
  public synchronized boolean isCanceled(){
    return cancel;
  }
    
  private void resetSession(){
    rules = null;
    map = null;
    manager = null;
    timeWait = 0;
    state = -1;
    maxBots = 0;
    cntBots = 0;  
    cntReady =0;
    cntActions = 0;
    Date timeState = null;   
    hasBlueTeam = false;
    hasRedTeam = false;
    thread = null;
    indexBots = new Hashtable();
    reg = new Registry();  
    botsDead = 0;
    stateSim = null;
    idSession = null;
    cancel = false;  
    if(css!=null){
      css.hide();
      css.shutdown();
      css = null;
    }
    getDaemon().resetSessions();
    try{
      finalize();      
    }
    catch(Throwable e){
      System.out.println("CtFSession couln't finalized");
    }  
    System.out.println("CtFSession was canceled");        
  }
    
  private void doAction(Bot bot, Action action){
    String prm[] = action.getParameters();
    Settings.setCustomSettings(new CtFSettings());
    
    //-- Move -- //
    if(action instanceof MoveAct){
      int dir = Integer.parseInt(prm[0]);
      int mode = Integer.parseInt(prm[1]);      
      double act = 0;
      int qnt2 = 0;
      
      if(mode==MoveAct.GO){
        act = 0.025;
        qnt2 = bot.getAttribute("MOV")*4;        
      }
      else{
        if(mode==MoveAct.RUN){
          act = 0.05;
          qnt2 = bot.getAttribute("MOV")*8;                  
        }
        else{
          act = 0.0125;
          qnt2 = 1;                          
        }
      }        
      manager.move(bot,dir,qnt2,act);
    }
    else{
      //-- Change Weapon --//
      if(action instanceof ChangeWeaponAct){    
        int idWeapon = Integer.parseInt(prm[0]);
        manager.changeWeapon(bot,idWeapon);
      }
      else{
        //-- Recovery --//
        if(action instanceof RecoveryAct){    
          manager.recovery(bot);
        }
        else{
          //-- Dodge --//
          if(action instanceof DodgeAct){    
            manager.defense(bot,true);
          }
          else{
            //-- Rotate --//
            if(action instanceof RotateAct){    
              int dir = Integer.parseInt(prm[0]);   
              manager.rotate(bot,dir);
            }         
            else{
              //-- Get flag --//
              if(action instanceof GetFlagAct){    
					  manager.getFlag(bot);
              }     
              else{
                //-- Drop flag --//
                if(action instanceof DropFlagAct){    
	  				    manager.dropFlag(bot);
                }        
                else{
                  //-- Attack --//
                  if(action instanceof AttackAct){    
	    				   int target = Integer.parseInt(prm[0]);
					      int mode = Integer.parseInt(prm[1]);
					      int shots = Integer.parseInt(prm[2]);
					      int m;
					      
					      //Mode
					      if(mode==AttackAct.SIMPLE_SHOT){
					        m = CtFRules.SIMPLE;
					      }
					      else{
					        if(mode==AttackAct.AUTOFIRE){
					          m = CtFRules.AUTOFIRE; 
					        }
					        else{
					          m = CtFRules.HAND_TO_HAND; 					        
					        }
					      }
					      
					      //Number of shots
					      int ns;
					      if(shots==AttackAct.MIN){
					        ns = 1;
					      }
					      else{
					        if(shots==AttackAct.MID){
					          ns = (int)(bot.getActiveWeapon().getRof()/2);
					        }
					        else{
					          ns = bot.getActiveWeapon().getRof();
					        }
					      }			      
					      Bot tmp[] = getTargets(bot,target,mode);				      				      
					      if(tmp!=null){
  					        manager.attack(bot,tmp,m,ns);					      
					      }    
                  }            
                }      
              }       
            } 
          }
        }
      }
    }
  }  
}