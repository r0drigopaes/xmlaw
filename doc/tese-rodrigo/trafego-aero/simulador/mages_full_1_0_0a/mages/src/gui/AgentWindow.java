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

package mages.gui;

import mages.*;
import mages.agents.*;
import mages.simulation.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class AgentWindow extends JInternalFrame implements ProcessInfo, AgentInterface, 
  Runnable {
 
  public final static int HUMAN = 0;
  public final static int COMPUTER = 1;
    
  protected Agent agent;
  boolean ask = true;
  boolean canClose = false;
  MagesClient owner = null;
  JDesktopPane desktop = new JDesktopPane();
  JMenuBar menuBar = new JMenuBar();
  JMenu mnuFile = new JMenu("File");
  JMenu mnuWindows = new JMenu("Window");
  JMenu miSave = new JMenu("Save sensor image");  
  JMenuItem miSaveLog = new JMenuItem("Save actions log...");
  Thread thread = null;
    
  SensorWindow[] sensorsWin = null;  
  StaticInfoWindow siw = null;
  DynamicInfoWindow diw = null;
  ActionsPalette ap = null;
  ActionsLogger logger = null;
  mages.agents.Action action = null;
  TileAction tileAction = null;
  boolean avaliable = true;
  long id = 0;

  int mode;
  boolean log;

  public AgentWindow(MagesClient f, Agent a, int m, boolean l){
    owner = f;
    agent = a;    
    mode = m;     
    a.setAgentInterface(this);
    log = l;
    init(); 
    createSensorWindows();   
    setVisible(true);
  }
    
  public AgentWindow(MagesClient f, Agent a){
    super();
    owner = f;
    agent = a;    
    a.setAgentInterface(this);    
    mode = COMPUTER;    
    log = false;
    init();
    createSensorWindows();    
  }  

  private void init(){
    setTitle(agent.getName());
    setIconifiable(true);
    setMaximizable(true);
    setResizable(true);
    setClosable(false);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);    

    menuBar.add(mnuFile);
    mnuFile.add(miSave);
    
    mnuFile.add(miSaveLog);
    miSaveLog.setEnabled(false);
    
    mnuWindows.add(new CascadeAction(desktop));
    tileAction = new TileAction(desktop);    
    mnuWindows.add(tileAction);    
    menuBar.add(mnuWindows);    
    setJMenuBar(menuBar);    

    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds(0,0,(int)(d.width*0.30),(int)(d.height*0.30));
    desktop.setOpaque(true);
    setOpaque(true);
    setContentPane(desktop);
    initEvents();      
  }  
  
  public void setAskConfirmation(boolean b){
    ask = b;
  }
  
  public boolean isToAsk(){
    return ask;
  }
  
  public void paint(Graphics g){
    super.paint(g);
  }
  
  public boolean isOptimizedDrawingEnabled(){
    return false;
  }
  
  private void initEvents(){  	   
    miSaveLog.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveLog();
      }
    });    
  }  
  
  private void saveLog(){
    JFileChooser dialog = new JFileChooser();
    Extension ext = new Extension("txt","Text file (*.txt)"); 
    dialog.setFileFilter(ext);
    int res = dialog.showSaveDialog(getParent());
    if(res==JFileChooser.APPROVE_OPTION){
      try{
        String name = Extension.validate(dialog.getSelectedFile().getPath(),"txt");
        Turn.saveTurns(name,logger.getTurns());
      }
      catch(FileNotFoundException e){
        ErrorMessage em = new ErrorMessage(owner,"Error: Problem with "+dialog.getSelectedFile().getPath());
        em.show();              
      }  
    }
    repaint();  
  }
  
  public void dispose(){
    if(ask){
      YesOrNoDialog ynd = new YesOrNoDialog(owner,"Do you want to exit?",
                              "'"+getTitle()+"'",this,0);
      ynd.show();    
    }         
    if(canClose){
      agent.kill();
      super.dispose();
    }  
  }
  
  public void processInfo(Object info){
    Object[] tmp = (Object[])info;
    Boolean b = (Boolean)tmp[1];
    canClose = b.booleanValue();
  }        
    
  private void createSensorWindows(){
    Sensor[] sensors = agent.getAgentController().getSensors();              
    sensorsWin= new SensorWindow[sensors.length];
    int cnt1 = 0;
    for(int i=0; i<sensors.length; i++){   
      SensorWindow sw = new SensorWindow(this,sensors[i]);
      desktop.add(sw);
      sensorsWin[cnt1++] = sw;
      miSave.add(new MenuItem(sw));
    }
    
    siw = new StaticInfoWindow(agent.getStats());
     desktop.add(siw);
     
    diw = new DynamicInfoWindow(agent);
     desktop.add(diw);
     
    if(log){
      logger = new ActionsLogger();
      desktop.add(logger,JLayeredPane.PALETTE_LAYER);     
      miSaveLog.setEnabled(true);      
    }
    
    if(mode==HUMAN){
      ap = new ActionsPalette(this,agent.possiblesActions());
      desktop.add(ap,JLayeredPane.PALETTE_LAYER);      
    }         
  }  
    
  private class MenuItem extends JMenuItem{
    SensorWindow sensor;
    
    public MenuItem(SensorWindow sw){
      super(sw.getSensor().getName());
      sensor = sw;      
    }    
  }    
  
  public MagesClient getOwner(){
    return owner;
  }
  
  public void defineAction(mages.agents.Action a){
    if(logger!=null){
      Turn t = new Turn(id++);
      t.setAction(a);
      t.setAgent(agent);
      Sensor sensors[] = agent.getAgentController().getSensors();
      for(int i=0; i<sensors.length; i++){
        t.addSensor(sensors[i]);
      }      
      logger.addTurn(t);
      logger.repaint(); 
    }  
  }
  
  public synchronized void setAction(mages.agents.Action a){
     if(action==null && avaliable){
       action = a;    
       defineAction(action);
       setTitle(getTitle()+" * ");
       avaliable = false;
     }      
     notifyAll();     
  }
  
  public synchronized mages.agents.Action getAction(){
    while(action==null){
      try{
        wait();
      }
      catch(InterruptedException e){}
    }    
    mages.agents.Action tmp = (mages.agents.Action)action.clone();
    action = null;
    notifyAll();              
    return tmp;
  }
  
  public void start() {
    if (thread == null) {
      thread = new Thread(this);
      thread.setDaemon(true);
      thread.start();
    }
  }
  
  public synchronized void waitNotify(){
    try{
      wait();
    }
    catch(InterruptedException e){}  
  }
    
  public void run(){
    setEnabled(false);
    while(true){
      waitNotify();
      for(int i=0; i<sensorsWin.length; i++){
        sensorsWin[i].repaint();
      }  
      siw.repaint();
      diw.repaint();
      if(logger!=null) logger.repaint();
    }  
  }
  
     
  public synchronized void update(Object obj){  
    notifyAll();       
  }  
   
   public void botReady(){
     owner.getToolBar().setVisible(true);
     setEnabled(true);
   }
   
   public void setInfo(String s){
     setTitle(agent.getName()+" - "+s);
     avaliable = true;     
   }
   
   public void setVisible(boolean f){
     if(f){
      super.show();
     }
     else{
       super.hide();
     } 
   }
   
   public void tile(){
     tileAction.actionPerformed(null);                      
   }
   
   public Agent getAgent(){
     return agent;
   }
   
   public void setAgent(Agent a){
     agent = a;
   }   
}