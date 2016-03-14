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

package mages.app.ctf;

import mages.*;
import mages.gui.*;
import mages.app.*;
import mages.domain.ctf.*;

import java.awt.*;
import java.io.*;
import java.awt.event.*;

public  class TeamControlable extends Panel implements IControlable, ProcessInfo{
  final int TOURN = 0;
  final int BOT = 1;

  Panel pnlTourn = new Panel();
  Label lblTourn = new Label();
  TextField edtTourn = new TextField(20);
  Label lblTeamName = new Label();
  TextField edtTeamName = new TextField(20);
  Button btnSearchTourn = new Button();
  Panel pnlBots = new Panel();
  BorderLayout borderLayout1 = new BorderLayout();
  Panel pnlButtons = new Panel();
  FlowLayout flowLayout3 = new FlowLayout();
  Button btnAdd = new Button();
  Button btnRemove = new Button();
  Panel pnlDescr = new Panel();
  int state = -1;
  IEditor owner = null;

  //Domain
  Tournament tourn = null;
  Bot bot = null;
  Team team = null;
  List lstBots = new List();
  GridLayout gridLayout2 = new GridLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  Label lblDescr = new Label();
  TextArea edtDescr = new TextArea();
  BorderLayout borderLayout3 = new BorderLayout();

  public void init(){
    team = new Team();
    setEnabled(true);
    edtTourn.setText("");
    edtTeamName.setText("");    
    edtDescr.setText("");    
    lstBots.removeAll();
  }

  public Object getObject() throws IOException{
    return team;
  }

  public void setObject(Object obj){
    team = (Team)obj;
    edtTeamName.setText(team.getName());
    edtDescr.setText(team.getDescription());
    for(int i=0; i<team.getNumBots();i++){
      lstBots.add(team.getBot(i).getName());      
    }
    if(lstBots.getItemCount()>0){
      btnRemove.setEnabled(true);
    }
  }

  public void setEditor(IEditor e){
    owner = e;
  }

  public void update() throws IOException{
    team.setName(edtTeamName.getText());
    team.setDescription(edtDescr.getText());   
  }

  public TeamControlable() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    pnlTourn.setLayout(gridLayout2);
    lblTourn.setText("Tournament: ");
    lblTeamName.setText("Team name: ");
    btnSearchTourn.setLabel("...");
     edtTourn.setEditable(false);      
    btnSearchTourn.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        openSearchTourn(e);
      }
    });
    pnlBots.setLayout(borderLayout1);
    pnlButtons.setLayout(flowLayout3);
    btnAdd.setLabel("Add ...");
    btnAdd.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        addBot(e);
      }
    });
    btnRemove.setEnabled(false);
    btnAdd.setEnabled(false);    
    btnRemove.setLabel("Remove");
    btnRemove.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        removeBot(e);
      }
    });
    pnlDescr.setLayout(borderLayout2);
    flowLayout3.setAlignment(FlowLayout.LEFT);
    flowLayout3.setVgap(0);
    gridLayout2.setColumns(3);
    gridLayout2.setHgap(10);
    gridLayout2.setRows(2);
    gridLayout2.setVgap(10);
    lblDescr.setText("Description: ");
    this.setLayout(borderLayout3);
    this.add(pnlBots, BorderLayout.CENTER);
    pnlBots.add(pnlButtons, BorderLayout.SOUTH);
    pnlButtons.add(lstBots, null);
    pnlButtons.add(btnAdd, null);
    pnlButtons.add(btnRemove, null);
    this.add(pnlTourn, BorderLayout.NORTH);
    pnlTourn.add(lblTourn, null);
    pnlTourn.add(edtTourn, null);
    pnlTourn.add(btnSearchTourn, null);
    pnlTourn.add(lblTeamName, null);
    pnlTourn.add(edtTeamName, null);
    this.add(pnlDescr, BorderLayout.SOUTH);
    pnlDescr.add(edtDescr, BorderLayout.CENTER);
    pnlDescr.add(lblDescr, BorderLayout.NORTH);
    edtTourn.addKeyListener(new ValueChanged());
    edtTeamName.addKeyListener(new ValueChanged());    
    edtDescr.addKeyListener(new ValueChanged());             
    lstBots.addActionListener(new ValueChanged());         
  }

  void openSearchTourn(ActionEvent e) {
    state = TOURN;
    SimpleDialog sd = new SimpleDialog((Frame)owner,"File name: ","Tournament",
                                       this,SimpleDialog.NO_USE_SEARCH);
    sd.show();
  }

  void addBot(ActionEvent e) {
    state = BOT;
    SimpleDialog sd = new SimpleDialog((Frame)owner,"File name: ","Bot",
                                       this,SimpleDialog.NO_USE_SEARCH);
    sd.show();
  }

  void removeBot(ActionEvent e) {
    if(lstBots.getItemCount()>0 && lstBots.getSelectedIndex()!=-1){
      int id = lstBots.getSelectedIndex();
      team.removeBot(id);
      lstBots.remove(id);
      if(lstBots.getItemCount()==0){
        btnRemove.setEnabled(false);
      }
    }
  }

  public void processInfo(Object info){
    String tmp = (String)info;
    switch(state){
      case TOURN:
        try{
          TournamentController tc = new TournamentController();
          tourn = (Tournament)tc.load(MagesEnv.getFilesDir("ctfTourn")+tmp);
          edtTourn.setText(tourn.getName());
          btnAdd.setEnabled(true);
        }
        catch(IOException e){
          ErrorMessage em = new ErrorMessage((Frame)owner,e.getMessage());
          em.show();
        }
      break;

      case BOT:
        try{     
          if(team.getNumBots()<tourn.getMaxBotsForTeam()){
            BotsController bc = new BotsController();                   
            bot = (Bot)bc.load(MagesEnv.getFilesDir("ctfBots")+tmp);
            team.addBot(bot);
            lstBots.add(bot.getName());
            btnRemove.setEnabled(true);
          }
          else{
            ErrorMessage em = new ErrorMessage((Frame)owner,"You can't add more bots on team");
            em.show();          
          }  
        }
        catch(Exception e){
          ErrorMessage em = new ErrorMessage((Frame)owner,e.getClass().getName());
          em.show();
        }
      break;
    }
  }

  public boolean validateObject() throws FormatException{
    if(!Team.validate(team,tourn)){
      throw new FormatException("There are errors in Bot. See tournament rule");    
    }  
    if(tourn==null){
      throw new FormatException("You must specify a tournament rule");
    }       
    if(team.getNumBots()==0){
      throw new FormatException("You must add Bots in your team");
    }       
    if(edtTeamName.getText().trim().equals("")){
      throw new FormatException("Tournament needs a name");
    }        
    if(edtDescr.getText().trim().equals("")){
      throw new FormatException("Tournament needs a description");
    }     
    return true;
  }		
  
  public class ValueChanged implements KeyListener,ActionListener{
    
    private void run(){
      owner.setStatus(GraphicEditor.FILE_EDITED_STATE);        
    }
    
    public void actionPerformed(ActionEvent e){
      run();
    }
    
    public void keyPressed(KeyEvent e){
      run();
    }   
    
    public void keyReleased(KeyEvent e){}       
    public void keyTyped(KeyEvent e){}    
  }  
}