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

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import clisp.*;

public abstract class AbstractSetupWindow extends Dialog implements SetupWindow, 
  Runnable, Connectable{
  
  public static final int CONNECT_MODE = 0;
  public static final int RECONNECT_MODE = 1;  
  
  //UI Classes
  Panel pnlHeader = new Panel();
  Panel pnlPlayer = new Panel();   
  Panel pnlLog = new Panel();  
  Panel pnlButtons = new Panel();    
  Panel pnlBatch = new Panel();      
  CheckboxGroup chkGroup = new CheckboxGroup();
  Checkbox chkLog = new Checkbox("Create Log");
  Checkbox chkBatch = new Checkbox("Batch");  
  Checkbox chkHuman = new Checkbox("Human");
  Checkbox chkComputer = new Checkbox("Computer");
  Button btnConnect = new Button();
  Button btnCancel = new Button("Exit");
  Label lblHost = new Label("Host name: ");
  Label lblRuns = new Label("Runs: ");  
  TextField txtRuns = new TextField(9);

  TextField txtHost = new TextField(20);
  protected MagesClient owner = null;
  FloatWindow waitAlert = null;      
  Vector panels = null;  
  Thread thread = null;  
  Hashtable setup = null;
  int cnt = 0;
  int mode = CONNECT_MODE;

  public AbstractSetupWindow(MagesClient f, int m, String title) {
    super(f);
    panels = new Vector();
    setup = new Hashtable();
    owner = f;
    mode = m;
    waitAlert = new FloatWindow(f,"Please, waiting...",new Dimension(110,50));
    setTitle(title);          
    setModal(true);
    btnConnect.setLabel((mode==CONNECT_MODE)?"Connect":"Reconnect");
  }
    
  public abstract Object[] getParameters();
  public abstract int getPortNum();
  public abstract void exec();
  public abstract void run();  
  protected abstract boolean checkParams();    
  public abstract void reconnect();
  public abstract void finish();
  protected abstract void connect();
  public abstract void createAgentWindow(Agent a);
  
  public String getHostName(){
    return txtHost.getText();
  }
        
  protected void addPanel(Panel pnl, boolean rec){    
    setup.put(new Integer(panels.size()),new Boolean(rec));
    panels.addElement(pnl);    
    if(!rec) cnt++;    
  }
  
  protected void header(){
    //Host name
    pnlHeader.setLayout(new FlowLayout(FlowLayout.LEFT));
    pnlHeader.add(lblHost);
    txtHost.setText("localhost");
    pnlHeader.add(txtHost);  
    addPanel(pnlHeader, true);
    
    //Interaction
    chkHuman.setCheckboxGroup(chkGroup);    
    chkComputer.setCheckboxGroup(chkGroup);    
    chkHuman.setState(true);        
    pnlPlayer.setLayout(new FlowLayout(FlowLayout.LEFT));
    pnlPlayer.add(chkHuman);
    pnlPlayer.add(chkComputer);        
    pnlPlayer.add(chkLog);            
    addPanel(pnlPlayer, true);
       
    //Batch    
    pnlBatch.setLayout(new BorderLayout());
    pnlBatch.add(chkBatch, BorderLayout.NORTH);
    
    Panel tmp = new Panel();
    tmp.setLayout(new FlowLayout(FlowLayout.LEFT));
    tmp.add(lblRuns);  tmp.add(txtRuns);    
    pnlBatch.add(tmp, BorderLayout.SOUTH);    
    addPanel(pnlBatch, false);            
  }
  
  protected void create(){
    if(mode==CONNECT_MODE){
      setLayout(new GridLayout(panels.size()+1,1));    
      for(int i=0; i<panels.size(); i++){
        add((Panel)panels.elementAt(i));
      }
    }
    else{
      if(mode==RECONNECT_MODE){
        for(int i=0; i<panels.size(); i++){
          if(useReconnect(i)){
            setLayout(new GridLayout(panels.size()+1-cnt,1));          
            add((Panel)panels.elementAt(i));
          }  
        }      
      }
    }  

    pnlButtons.add(btnConnect);
    pnlButtons.add(btnCancel);
    add(pnlButtons);
    
    pack();            
    center();
    initEvents();    
    setVisible(false);        
  }
                    
  protected void initEvents(){  
    addWindowListener(new WindowAdapter() {
   	public void windowClosing(WindowEvent e) {
         System.exit(0);
	   }
	 });
	   
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         System.exit(0);
      }
    });	     

    btnConnect.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(mode==CONNECT_MODE){
          if(checkParams()&&checkBatch()){
            connect();
          }  
        }
        else{
          reconnect();
        }  
      }
    });
    
    chkBatch.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if(chkBatch.getState()){
          txtRuns.setEnabled(true);
          txtRuns.setText("");
        }
        else{
          txtRuns.setEnabled(false);        
        }
      }
    });            
  }
    
  private void center(){
   Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); 
	Rectangle bounds = new Rectangle(d.width,d.height); 
	Rectangle abounds = getBounds(); 
	setLocation(bounds.x + (bounds.width - abounds.width) / 2, 
  	      	   bounds.y + (bounds.height - abounds.height) / 2);   
  }  
  
  public void start() {
    if (thread == null) {
      thread = new Thread(this);
      thread.start();
      thread = null;
    }
  }
  
  protected void quit(){
    if (thread != null) {
      thread.stop();
      thread = null;
    }  
  }       
      
  public void connect(String host){
    txtHost.setText(host);  
    connect();
  }
    
  protected void alert(boolean f){
    waitAlert.setVisible(f);
    setEnabled(!f);       
  }    
  
  private boolean useReconnect(int ind){
    Boolean b = (Boolean)setup.get(new Integer(ind));
    if(b!=null){
      return b.booleanValue();
    }
    else{
      return false;
    }  
  }
  
  public void setMode(int m){
    mode = m;
  }

  public int getMode(){
    return mode;
  }
    
  protected boolean isHuman(){
    return chkHuman.getState();
  }

  protected boolean useLog(){
    return chkLog.getState();
  }      
  
  protected boolean useBatch(){
    return chkBatch.getState();
  }      
    
  protected int getRuns(){
    return Integer.parseInt(txtRuns.getText());
  }      
      
  public void setVisible(boolean v){
    super.setVisible(v);
  }
  
  private boolean checkBatch(){
    if(chkBatch.getState()){
      try{
        int r = Integer.parseInt(txtRuns.getText());
        if(r<1){
          throw new NumberFormatException();
        }
      }
      catch(NumberFormatException e){
        ErrorMessage em = new ErrorMessage(owner,"Runs is an invalid number");
        em.show();    
        return false;              
      }      
      return true;
    }
    else{
      return true;
    }
  }  
}