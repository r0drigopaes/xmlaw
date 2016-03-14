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


package mages;

import mages.controller.*;
import mages.controller.ctf.client.*;
import mages.app.*;
import mages.gui.*;
import mages.agents.*;
import clisp.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class MagesClient extends JFrame implements ProcessInfo{   
   public static final String COPYLEFT = "GNU MAGES version 1.0a, Copyright (C) 2002 João Ricardo Bittencourt\n"+
                                         "GNU MAGES comes with ABSOLUTELY NO WARRANTY; for details see\n"+
                                         "'Warranty' in Help menu. This is free software, and you are\n"+
                                         "welcome to redistribute it under certain conditions; see \n"+
                                         "`Conditions' in Help menu for details.\n";
       
   public static final String TITLE = "GNU MAGES Client v.1.0a";
   
   final int NORMAL = 0;         
   final int RECONNECT = 1;
      
  JMenuBar menu = new JMenuBar();
  JMenu mnuFile = new JMenu("File");
  JMenuItem miLoad = new JMenuItem("Load domain...");
  JMenuItem miRecon = new JMenuItem("Reconnect...");  
  JMenuItem miExit = new JMenuItem("Exit");    
  JMenuItem miLicense = new JMenuItem("GNU General Public License");
  JMenuItem miWarranty = new JMenuItem("Warranty");  
  JMenuItem miConditions = new JMenuItem("Conditions");  
  JMenuItem miAbout = new JMenuItem("About GNU Mages");        
  JMenu mnuWindows = new JMenu("Window");
  JMenu mnuHelp = new JMenu("Help");  
  JToolBar toolBar = new JToolBar();  
  JDesktopPane desktop = new JDesktopPane();
  DomainController dcontrol = new DomainController();
  Domain domain = null;
  String filename = null;
  
  boolean useFileDialog = false;
  boolean canExit = true;
  boolean withDomain = false;
  boolean isHuman = true;
  FilenameFilter filefilter = null;
  SetupWindow user = null;
  int mode = NORMAL;
  boolean first = true;
      
  public MagesClient(){  
    SplashScreen splash = new SplashScreen(this,MagesEnv.getRoot()+MagesEnv.validate("/splashc.jpg"),5);
    setTitle(TITLE);
    setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon_mages.gif")));    
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
     
    menu.add(mnuFile);
    mnuFile.add(miLoad);
    mnuFile.add(miRecon);    
    mnuFile.addSeparator();
    mnuFile.add(miExit);    
    
    menu.add(mnuWindows);
    
    menu.add(mnuHelp);
    mnuHelp.add(miLicense);
    mnuHelp.add(miWarranty);
    mnuHelp.add(miConditions);
    mnuHelp.addSeparator();    
    mnuHelp.add(miAbout);        

    mnuWindows.add(new CascadeAction(desktop));    
    mnuWindows.add(new TileAction(desktop));    
    mnuWindows.add(new CloseAllAction(desktop));        
    mnuWindows.setEnabled(false);

    toolBar.setVisible(false);
    toolBar.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    toolBar.setFloatable(false);
    
    getContentPane().setLayout(new BorderLayout());      
    getContentPane().add(toolBar,BorderLayout.NORTH);  
    getContentPane().add(desktop,BorderLayout.CENTER);      
    setJMenuBar(menu);   
    initEvents();     
    center();    
    cleanOldSessionId();
  }
  
  private void initEvents(){  
    addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
  	  	   exit();
   	}
	 });  

    miExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         exit();
      }
    });	 
    
    miLoad.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         mode = NORMAL;
         load();
      }
    });	         
    
    
    miRecon.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         if(user==null){
           mode = RECONNECT;
         }  
         reconnect();         
      }
    });
        
    miAbout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         String pathLogo = MagesEnv.getRoot()+MagesEnv.validate("/logoTop.jpg");
         AboutBox about = new AboutBox(MagesClient.this,pathLogo,COPYLEFT,null,null,TITLE);
         about.show();
      }
    });	        
    
    miLicense.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {       
         String path = MagesEnv.getRoot()+MagesEnv.validate("/GPL.txt");
         InfoBox info = new InfoBox(MagesClient.this,path,"GNU General Public License", new Dimension(500,400));
         info.show();
      }
    });    
    
    miWarranty.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {       
         String path = MagesEnv.getRoot()+MagesEnv.validate("/warranty.txt");
         InfoBox info = new InfoBox(MagesClient.this,path,"Warranty", new Dimension(500,400));
         info.show();
      }
    });        
    
    miConditions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {       
         String path = MagesEnv.getRoot()+MagesEnv.validate("/conditions.txt");
         InfoBox info = new InfoBox(MagesClient.this,path,"Conditions", new Dimension(500,400));
         info.show();
      }
    });                        
  }
 
  private void loadDomain(boolean v, int md){
    try{
      if(filename!=null){
        miLoad.setEnabled(false);            
        miRecon.setEnabled(false);         
        domain = (Domain)dcontrol.load(filename);
        withDomain = true;
        try{
          domain.run(this,md);          
          user = domain.getSetupWindow();
          user.setVisible(v);
        }  
        catch(Exception e){
          ErrorMessage m = new ErrorMessage(this,e.getClass().getName());
          m.show();        
        }  
      }  
    }
    catch(IOException e){
      ErrorMessage em = new ErrorMessage(this,e.getMessage());
      em.show();
      miLoad.setEnabled(true); 
      miRecon.setEnabled(true);               
      filename = null;     
    }  
  }
  
  private void load(){
    if(useFileDialog){      
      FileDialog dialog = new FileDialog(this,"Open",FileDialog.LOAD);
      dialog.setModal(true);
      dialog.setFilenameFilter(filefilter);
      dialog.show();
      filename = dialog.getDirectory()+dialog.getFile();
    }
    else{    
      String name = JOptionPane.showInputDialog(this,"Domain name","Load",JOptionPane.QUESTION_MESSAGE);
      if(name!=null){
        filename = MagesEnv.getFilesDir("domains")+name;      
        loadDomain(true,AbstractSetupWindow.CONNECT_MODE);
       } 
    }    
  }
   
  private void center(){
   Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); 
   setSize((int)(d.width*0.9),(int)(d.height*0.9));
	Rectangle bounds = new Rectangle(d.width,d.height); 
	Rectangle abounds = getBounds(); 
	setLocation(bounds.x + (bounds.width - abounds.width) / 2, 
  	      	   bounds.y + (bounds.height - abounds.height) / 2);   
  }
 
  private void exit(){  
    if(withDomain){
      YesOrNoDialog ynd = new YesOrNoDialog(this,"Do you want to exit?",
                              "Exit",this,0);
      ynd.show();  
      if(canExit){
        user.finish();
        System.exit(0);
      }    
    }
    else{
      System.exit(0);
    }  
  }
  
  public void processInfo(Object info){
    if(info instanceof String){
    }
    else{
      if(info instanceof Object[]){
        Object[] tmp = (Object[])info;
        Boolean b = (Boolean)tmp[1];
        canExit = b.booleanValue();
      }
    }  
  }

  private void linkToAgentInWindow(Agent a){
    JInternalFrame tmp[] = desktop.getAllFrames();
    for(int i=0; i<tmp.length; i++){
      AgentWindow aw = (AgentWindow)tmp[i];
      if(aw.getAgent().getObjectId().equals(a.getObjectId())){
         aw.setAgent(a);
      }
    }
  }
  
  public synchronized void addAgentWindow(String t, Agent a, boolean human, boolean log){
    if(mode==RECONNECT){
      DefaultMain.factory(this,a,human,log);                     
      if(first){
        setTitle(getTitle()+" - "+t);    
        mnuWindows.setEnabled(true); 
        miRecon.setEnabled(false);        
        first = false;
      }
    }
    else{
      linkToAgentInWindow(a);
    }  
  }
  
  public void processParameters(Object[] params){
    try{
      //Não foi usado o params[0] que refere-se a conexão do user
      DefaultMain.factory(this,(Agent[])params[1],
                          ((Boolean)params[2]).booleanValue(),
                          ((Boolean)params[3]).booleanValue());                   
      String tile = (String)params[4];
      if(tile!=null){
        setTitle(getTitle()+" - "+tile);
      }
      mnuWindows.setEnabled(true); 
      miRecon.setEnabled(false);       
    }
    catch(Exception e){
      ErrorMessage m = new ErrorMessage(this,"Error in DefaultMain "+e.getClass().getName());
      m.show();            
    }  
  }

  public JDesktopPane getDesktop(){  
    return desktop;
  }

  public JToolBar getToolBar(){  
    return toolBar;
  }
    
  public void paint(Graphics g){
    super.paint(g);
  }
 
  private void reconnect(){
    if(mode==RECONNECT){
      String name = JOptionPane.showInputDialog(this,"Domain name","Reconnect",JOptionPane.QUESTION_MESSAGE);
      if(name!=null){
        filename = MagesEnv.getFilesDir("domains")+name;
        loadDomain(true, AbstractSetupWindow.RECONNECT_MODE);
      }       
    }
    else{
      user.reconnect();
    }        
  }
 
  private void cleanOldSessionId(){
    File dirSession = new File(MagesEnv.getFilesDir("sessions"));
    File files[] = dirSession.listFiles();
    for(int i=0; i<files.length; i++){
      SessionId sid = new SessionId();
      try{
        sid.read(files[i].getParent()+MagesEnv.getFileSeparator(),files[i].getName());
        Date dses = sid.getDate();
        boolean del = false;
        if(dses!=null){
          Date cur = new Date();
          if((cur.getTime()-dses.getTime())>=1200000){
            del = true;
          }
        }
        else{
          del = true;
        }
        if(del){
          files[i].delete();
        }
      }
      catch(IOException e){
        System.out.println("MagesClient Error: Couldn't read "+files[i].getName());
      }        
    }
  }
  
  public void enableReconnect(boolean flag){
    miRecon.setEnabled(flag);
  }
     
  public static void main(String[] args){
    MagesClient mc = new MagesClient();
    mc.show();
  }    
}