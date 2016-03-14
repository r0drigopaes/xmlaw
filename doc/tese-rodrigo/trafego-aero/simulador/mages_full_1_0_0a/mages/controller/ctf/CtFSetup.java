
package mages.controller.ctf;

import mages.*;
import mages.agents.*;
import mages.gui.*;
import mages.util.*;
import mages.app.ctf.*;
import mages.app.mapeditor.*;
import mages.domain.ctf.*;
import mages.simulation.*;
import mages.controller.ctf.client.*;

import java.awt.*;
import java.io.*;
import java.util.Date;
import java.util.Vector;
import java.awt.event.*;
import clisp.*;

public class CtFSetup extends AbstractSetupWindow{
  public static final int INTERACTIVE_CREATE = 0;
  public static final int INTERACTIVE_JOIN = 1;
//  public static final int BATCH = 2;
      
  //UI Classes
  Panel pnlMode = new Panel();
  CheckboxGroup chkGroup2 = new CheckboxGroup();  
  Checkbox chkInter = new Checkbox();
  Checkbox chkInterJoin = new Checkbox();  
//  Checkbox chkBatch = new Checkbox();
  Panel pnlParams = new Panel();
//  Panel pnlBatch = new Panel();    
  Panel pnlInter = new Panel();
  Panel pnlInterJoin = new Panel();  
  Panel pnlButtons = new Panel();
  CardLayout layParams = new CardLayout();
  Label lblRule = new Label("Rules: ");
  Label lblTeam = new Label("Team: ");    
  Label lblTeamJ = new Label("Team: ");   
  Label lblMap = new Label("Environment: ");    
  Label lblTimeWait = new Label("Time wait (s): ");        
  Label lblSeed = new Label("Seed: ");          
  Label lblTurns = new Label("Turns: ");   
  TextField txtRule = new TextField("official.xml",20);
  TextField txtTeam = new TextField("default.xml",20);    
  TextField txtTeamJ = new TextField("default.xml",20);      
  TextField txtMap = new TextField("default.xml",20);    
  TextField txtTimeWait = new TextField("30000",20);    
  TextField txtTurns = new TextField("3600",20);  
  Date dt = new Date();
  TextField txtSeed = new TextField(Long.toString(dt.getTime()),20);      
  FloatWindow waitAlert = null;

  //Domain classes
  Tournament rules = null;
  Team team = null;
  World environment = null;
  CtFUserClient ctfUser = null;
  Thread thread;
  CtFBot[] ctfBots = null;
  int cntBots = 0;
  long seed;
  String idUser = null;    
  String idSession = null;
  String idObject = null;  
            
  public CtFSetup(MagesClient f, Integer mode) {
    super(f,mode.intValue(),"CtFSetup");
    init();
  }

  public void exec(){}

  public Object[] getParameters(){
    return null;
  }

  public int getPortNum(){
    return 7267;
  }
  
  private void init(){
    header();
    chkInter.setCheckboxGroup(chkGroup2);
    chkInter.setLabel("Interactive mode (create)");
    chkInter.setState(true);
    chkInterJoin.setCheckboxGroup(chkGroup2);
    chkInterJoin.setLabel("Interactive mode (join)");
    pnlMode.setLayout(new FlowLayout(FlowLayout.LEFT));
//    chkBatch.setCheckboxGroup(chkGroup2);
//    chkBatch.setLabel("Batch mode");      
    pnlMode.add(chkInter);
    pnlMode.add(chkInterJoin);    
//    pnlMode.add(chkBatch);
    addPanel(pnlMode,true);
        
    //Interactive & Batch
    createPanelParams();            
    
    create();
  }
  
  private void createPanelParams(){ 
    //Interactive (create)   
    pnlInter.setLayout(new GridLayout(6,2));
    pnlInter.add(lblRule); pnlInter.add(txtRule);
    pnlInter.add(lblTeam); pnlInter.add(txtTeam);    
    pnlInter.add(lblMap); pnlInter.add(txtMap);    
    pnlInter.add(lblTurns); pnlInter.add(txtTurns);
    pnlInter.add(lblTimeWait); pnlInter.add(txtTimeWait);    
    pnlInter.add(lblSeed); pnlInter.add(txtSeed); 

    //Interactive (join)
    pnlInterJoin.setLayout(new FlowLayout(FlowLayout.LEFT));    
    pnlInterJoin.add(lblTeamJ); pnlInterJoin.add(txtTeamJ);
    
//    pnlBatch.add(new Label("Batch"));        
    pnlParams.setLayout(layParams);
    pnlParams.add("Inter",pnlInter);
    pnlParams.add("InterJoin",pnlInterJoin);    
//    pnlParams.add("Batch",pnlBatch);
    addPanel(pnlParams,false);
  }
    
  protected void initEvents(){  
    super.initEvents();    
    chkInterJoin.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if(chkInterJoin.getState()){
          layParams.show(pnlParams,"InterJoin");
        }
      }
    });  
    
    chkInter.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if(chkInter.getState()){
          layParams.show(pnlParams,"Inter");
        }
      }
    });    
    
  /*  chkBatch.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if(chkBatch.getState()){
          layParams.show(pnlParams,"Batch");
        }
      }
    });        */
  }
     
  protected boolean checkParams(){
    if(chkInter.getState()){
      return modeInter();
    }
    else{
      return modeInterJoin();
    }
  }

  private boolean modeInterJoin(){
    boolean ret = true;
    //Team   
    try{
      TeamController tmc = new TeamController();
      team = (Team)tmc.load(MagesEnv.getFilesDir("ctfTeams")+txtTeamJ.getText());         
      ctfBots = new CtFBot[team.getNumBots()];      
    }
    catch(IOException e){
      ErrorMessage em = new ErrorMessage(owner,"There isn't this team");
      ret = false;
      em.show();    
    }  
    return ret;    
  }
    
  private boolean modeInter(){  
    boolean ret = true;  
    //Rules
    try{
      TournamentController tc = new TournamentController();
      rules = (Tournament)tc.load(MagesEnv.getFilesDir("ctfTourn")+txtRule.getText());               
    }
    catch(IOException e){
      ErrorMessage em = new ErrorMessage(owner,"There isn't this tournament rules");
      ret = false;
      em.show();    
    }  
    
    //Team   
    try{
      TeamController tmc = new TeamController();
      team = (Team)tmc.load(MagesEnv.getFilesDir("ctfTeams")+txtTeam.getText());               
      ctfBots = new CtFBot[team.getNumBots()];
    }
    catch(IOException e){
      ErrorMessage em = new ErrorMessage(owner,"There isn't this team");
      ret = false;      
      em.show();    
    }
        
    //Environment
    try{
      MapController mc = new MapController();
      Map tmp = (Map)mc.load(MagesEnv.getFilesDir("maps")+txtMap.getText());         
      environment = new World(tmp);
    }
    catch(IOException e){
      ErrorMessage em = new ErrorMessage(owner,"There isn't this map");
      ret = false;      
      em.show();    
    }
        
    //TimeWait
    try{
      long t = Long.parseLong(txtTimeWait.getText());
      if(t<0 || t>300000){
        throw new NumberFormatException();
      }
    }
    catch(NumberFormatException e){
      ErrorMessage em = new ErrorMessage(owner,"Time wait is an invalid number");
      ret = false;      
      em.show();    
    }    
    
    //Seed
    try{
      long seed = Long.parseLong(txtSeed.getText());
      if(seed<0){
        throw new NumberFormatException();
      }
    }
    catch(NumberFormatException e){
      ErrorMessage em = new ErrorMessage(owner,"Seed is an invalid number");
      ret = false;      
      em.show();    
    }    

    //Turns    
    try{
      long t = Integer.parseInt(txtTurns.getText());
      if(seed<0){
        throw new NumberFormatException();
      }
    }
    catch(NumberFormatException e){
      ErrorMessage em = new ErrorMessage(owner,"Turn is an invalid number");
      ret = false;      
      em.show();    
    }        
    return ret;    
  }

  
  protected void quit(){
    ctfUser.finalize();
    super.quit();
  }       
  
  public void run(){
    //Receive messages
    boolean flag = true;    
    ErrorMessage em = null;
    while(flag){
      Message m = null;                 
      try{
        m = ctfUser.receive();
      }
      catch(IOException e){
        alert(false);
        em = new ErrorMessage(owner,"Message with problem");
        em.show();
      }  
    
      if(m!=null){
        switch(m.getId()){
          case CtFProtocol.S_CONNECTION:          
            if(getMode()==CONNECT_MODE){
              String str = m.getStringContent();
              if(str.equals("OK")){
                alert(true);            
                //Choice mode
                if(chkInter.getState()){
                  ctfUser.canCreateSimulation();      
                }
                else{
                  if(chkInterJoin.getState()){
                    ctfUser.canJoinSimulation();
                  }
                  else{
                    //modo batch
                  }
                }            
              }  
              else{
                quit();
                flag = false;
                alert(false);                      
                em = new ErrorMessage(owner,str);
                em.show();              
              }  
            }  
          break;
          
          case CtFProtocol.S_CAN_CREATE_SIM:
             String msg = m.getStringContent();                             
             if(msg.equals("OK")){  
               Parameters prm = new Parameters();
               prm.add(txtRule.getText());
               prm.add(team.getNumBots());
               prm.add(environment);
               prm.add(Integer.parseInt(txtTimeWait.getText()));
               prm.add(Long.parseLong(txtSeed.getText()));
               prm.add(useBatch());
               prm.add((useBatch())?getRuns():1);  
               prm.add(Integer.parseInt(txtTurns.getText()));                            
               ctfUser.createSimulation(prm);       
               alert(true);
             }
             else{
               quit();
               flag = false;                         
               alert(false);
               em = new ErrorMessage(owner,"Server can't create simulation");
               em.show();
             }
          break;
          
          case CtFProtocol.S_CAN_JOIN_SIM:
             msg = m.getStringContent();
             if(msg.equals("OK")){
               Parameters prm = new Parameters();
               prm.add(team);
               ctfUser.joinSimulation(prm);             
               alert(true);
             }
             else{
               quit();
               flag = false;
               alert(false);                         
               em = new ErrorMessage(owner,"You can't join simulation");
               em.show();
             }             
          break;      
          
          case CtFProtocol.S_QUIT:
            flag = false;
            msg = m.getStringContent();
            ctfUser.finalize();
            alert(false);
            em = new ErrorMessage(owner,msg);            
            em.show();
          break;    

          case CtFProtocol.S_RECONNECT_TEAM:
            alert(true);                          
            if(ctfBots == null){
           	   File dirSession = new File(MagesEnv.getFilesDir("sessions"));
         	   File files[] = dirSession.listFiles();
	            Vector tmp = new Vector();
	            Vector ses = new Vector();
         	   for(int i=0; i<files.length; i++){
                 SessionId sid = new SessionId();
        	        try{
	                sid.read(files[i].getParent()+MagesEnv.getFileSeparator(),files[i].getName());
                   if(sid.getSessionId().equals(idSession) && sid.getOwnerId().equals(idObject)){
                     int md = (isHuman())?CtFBot.HUMAN:CtFBot.COMPUTER;                   
                     CtFBot cbot = new CtFBot(getHostName(),md,this);                     
                     tmp.addElement(cbot);
                     ses.addElement(sid);
                     cntBots++;                     
	                }
	              }
       	        catch(IOException e){
	                System.out.println("MagesClient Error: Couldn't read "+files[i].getName());
         	     }        
          	   } 
	            ctfBots = new CtFBot[tmp.size()];                  
	            for(int i=0; i<ctfBots.length; i++){
	              ctfBots[i] = (CtFBot)tmp.elementAt(i);
                 ctfBots[i].reconnect((SessionId)ses.elementAt(i)); 
                 ctfBots[i].start();      	              
	            }
	            alert(false);
	            hide();	            
               owner.repaint();  	            
            }
            else{
              //reconectando e a interface
              //gráfica está criada
            }            
          break;
                    
          case CtFProtocol.S_CONNECT_TEAM:    
             if(getMode()==CONNECT_MODE){
               connectMode();
             }
          break;   
          
          case CtFProtocol.S_SESSION_ID:
             SessionId ssi = (SessionId)m.getContent(); 
             idObject = ssi.getObjectId();
             idSession = ssi.getSessionId();             
             ssi.setDate(new Date());
             if(chkInter.getState()){
               idUser = "user_creator";             
             }
             else{
               if(chkInterJoin.getState()){
                 idUser = "user_join";             
               }
             }
             
             try{
               ssi.write(MagesEnv.getFilesDir("sessions"),idUser);
             }
             catch(IOException e){}   
          break;
          
          case CtFProtocol.S_RECOVERY:
            String st = (String)m.getStringContent();
            if(st.equals("OK")){
              waitAlert = new FloatWindow(owner,"Please, waiting...",new Dimension(110,50));              
              owner.setEnabled(true);
              alert(false);            
            }
            else{
              if(st.equals("NO")){
                owner.setEnabled(true);
                alert(false);                                             
                quit();
                em = new ErrorMessage(owner,"Simulation Finished. Connection problems");                
                em.show();                               
              }
              else{
                waitAlert = new FloatWindow(owner,st,new Dimension(310,50));
                owner.setEnabled(false);
                alert(true);
              }  
            }
          break;
          
          case CtFProtocol.S_SCORE:
            Parameters prm = (Parameters)m.getContent();
            CtFScore cs = new CtFScore(owner,prm.getIntParameter(0),
                                             prm.getIntParameter(1),
                                             prm.getIntParameter(2),
                                             prm.getIntParameter(3)
                                       );
          break;
          
          case CtFProtocol.S_SCORE_BATCH:
            Parameters prm2 = (Parameters)m.getContent();          
            CtFScore cs2 = new CtFScore(owner,prm2.getStringParameter(0),
                                             prm2.getStringParameter(1),
                                             prm2.getDoubleParameter(2),
                                             prm2.getDoubleParameter(3),
                                             prm2.getDoubleParameter(4),
                                             prm2.getDoubleParameter(5)
                                       );                       
          break;
        }    
      }  
      else{
        //Can be problem with client
        int n = 5;
        int cnt = 0;
        int error = 0;
        while(m==null && cnt<n){ 
          try{
            m = ctfUser.receive();
          }
          catch(IOException e){
            System.out.println("User - Problem with message");
          }                   
          if(m==null) error++;
          cnt++;
          try{
            Thread.sleep(200);
          }
          catch(InterruptedException e){}  
        }
        if(error==n){
          flag = false;              
          ctfUser.stop();
          owner.enableReconnect(true);
          em = new ErrorMessage(owner,"Problems with connection");                
          em.show();                                                     
        }      
      }
    }        
  }
  
  public void reconnect(){
    connect();
    SessionId sid = new SessionId();
    if(chkInter.getState()){
      idUser = "user_creator";             
    }
    else{
      if(chkInterJoin.getState()){
        idUser = "user_join";             
      }
    }    
    try{
      sid.read(MagesEnv.getFilesDir("sessions"),idUser);  
      idSession = sid.getSessionId();
      idObject = sid.getObjectId();      
    }
    catch(IOException e){
      ErrorMessage em = new ErrorMessage(owner,"You can't reconnect");                
      em.show();                               
    }
    ctfUser.doReconnection(sid);
  }
  
  
  protected void connect(){
    try{
      ctfUser = new CtFUserClient(this,new CtFProtocol());                                    
      start();          
    }
    catch(java.net.NoRouteToHostException e){  
      ErrorMessage em = new ErrorMessage(owner,"Server "+getHostName()+" not found");
      alert(false);      
      em.show();        
    }    
    catch(IOException e1){    
      ErrorMessage em = new ErrorMessage(owner,"Server "+getHostName()+" not found");
      alert(false);      
      em.show();        
    }                    
  }
    
  public void finish(){
    ctfUser.quit();
  }  
  
  private void connectMode(){
    alert(true);  
    ErrorMessage em = null;
    for(int i=0; i<team.getNumBots(); i++){
      Bot bot = team.getBot(i);               
      int mode = (isHuman())?CtFBot.HUMAN:CtFBot.COMPUTER;
      if(chkInter.getState()){
        bot.setId(CtFSettings.RED);               
      }
      else{             
        bot.setId(CtFSettings.BLUE);               
      }
      CtFBot cbot = new CtFBot(bot,getHostName(),mode);
      try{
        cbot.createController();
      }
      catch(Exception e0){
        em = new ErrorMessage(owner,"Problems with Agent Controller");
        em.show();               
      }
      ctfBots[cntBots++] = cbot;                  
      cbot.connect();
      cbot.start();
      try{
        SessionId si = new SessionId();
        si.read(MagesEnv.getFilesDir("sessions"),idUser);
        cbot.sendBot(si.getObjectId()); 
      }
      catch(IOException e){
        em = new ErrorMessage(owner,"User ID with problem");
        em.show();               
      }
        
//      cbot.sendMode();   
      cbot.sendComm();         
      cbot.sendSensory();
    }
    Object[] tmp = new Object[5];
    tmp[0] = ctfUser;
    tmp[1] = ctfBots;
    tmp[2] = new Boolean(isHuman()); //it's human?
    tmp[3] = new Boolean(useLog()); //Use action log?
    if(chkInter.getState()){
      tmp[4] = "Red team";
    }
    else{
      tmp[4] = "Blue team";
    }
    owner.processParameters(tmp);
    alert(false);
    //waitAlert.dispose(); 
    //dispose();      
    setVisible(false);
    owner.repaint();  
  }
  
  public void createAgentWindow(Agent a){
    String title = null;
    if(chkInter.getState()){
      title = "Red team";
    }
    else{
      title = "Blue team";
    }  
    alert(false);    
//    setVisible(false);    
    owner.addAgentWindow(title, a,isHuman(),useLog());
  }
}

/*

    Object[] tmp = new Object[5];
    tmp[0] = ctfUser;
    tmp[1] = ctfBots;
    tmp[2] = new Boolean(isHuman()); //it's human?
    tmp[3] = new Boolean(useLog()); //Use action log?
    if(chkInter.getState()){
      tmp[4] = "Red team";
    }
    else{
      tmp[4] = "Blue team";
    }
==    owner.processParameters(tmp);
    alert(false);
    
    setVisible(false);
    owner.repaint();  
*/