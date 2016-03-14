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

import mages.gui.*;
import mages.app.*;
import mages.domain.ctf.*;

import java.awt.*;
import java.io.*;
import java.awt.event.*;

public  class TournamentControlable extends Panel implements IControlable{
  GridLayout grid = new GridLayout();
  Panel pnlInfo = new Panel();
  Panel pnlDesc = new Panel();
  GridLayout gridInfo = new GridLayout();
  Label lblName = new Label();
  TextField edtName = new TextField();
  Label lblMaxBotsForTeam = new Label();
  TextField edtMaxBotsForTeam = new TextField();
  Label lblMaxBotsPtos = new Label();
  TextField edtMaxBotsPtos = new TextField();
  Label lblMaxFlags = new Label();
  TextField edtMaxFlags = new TextField();
  Label lblMaxTeams = new Label();
  TextField edtMaxTeams = new TextField();
  Label lblDesc = new Label();
  TextArea edtDesc = new TextArea();
  BorderLayout borderLayout1 = new BorderLayout();
  IEditor owner = null;
  
  //Domain
  Tournament tourn = null;  

  public void init(){
    edtName.setText("");
    edtMaxBotsForTeam.setText("");
    edtMaxBotsPtos.setText("");
    edtMaxFlags.setText("");
    edtMaxTeams.setText("");
    edtDesc.setText("");     
    setEnabled(true);     
    tourn= new Tournament();  
  }

  public Object getObject() throws IOException{
    return tourn;
  }

  public void setObject(Object obj){  
    tourn = (Tournament)obj;
    if(tourn.getName()==null) edtName.setText("");
    edtName.setText(tourn.getName());       
    edtMaxBotsForTeam.setText(Integer.toString(tourn.getMaxBotsForTeam()));
    edtMaxBotsPtos.setText(Integer.toString(tourn.getMaxBotsPtos()));
    edtMaxFlags.setText(Integer.toString(tourn.getMaxFlag()));
    edtMaxTeams.setText(Integer.toString(tourn.getMaxTeams()));
    edtDesc.setText(tourn.getDescription());   
  }

  public void setEditor(IEditor e){
    owner = e;
  }
  
  public void update() throws IOException{
    try{
      tourn.setName(edtName.getText());
      tourn.setMaxBotsForTeam(Integer.parseInt(edtMaxBotsForTeam.getText()));
      tourn.setMaxBotsPtos(Integer.parseInt(edtMaxBotsPtos.getText()));
      tourn.setMaxFlag(Integer.parseInt(edtMaxFlags.getText()));
      tourn.setMaxTeams(Integer.parseInt(edtMaxTeams.getText()));
      tourn.setDescription(edtDesc.getText());    
    }
    catch(NumberFormatException e){
      new IOException(e.getMessage());
    }  
  }
  
  public TournamentControlable() {
    try {
      jbInit();
      edtName.addKeyListener(new ValueChanged());
      edtMaxBotsForTeam.addKeyListener(new ValueChanged());
      edtMaxBotsPtos.addKeyListener(new ValueChanged());
      edtMaxFlags.addKeyListener(new ValueChanged());
      edtMaxTeams.addKeyListener(new ValueChanged());  
      edtDesc.addKeyListener(new ValueChanged());      
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private void jbInit() throws Exception {
    grid.setColumns(1);
    grid.setRows(2);
    this.setLayout(grid);
    pnlInfo.setLayout(gridInfo);
    gridInfo.setColumns(2);
    gridInfo.setRows(5);
    gridInfo.setVgap(5);
    lblName.setText("Name: ");
    lblMaxBotsForTeam.setText("Maximum Bots for Team: ");
    lblMaxBotsPtos.setText("Maximum Bot Points: ");
    pnlDesc.setLayout(borderLayout1);
    lblMaxFlags.setText("Maximum Flags: ");
    lblMaxTeams.setText("Maximum Teams: ");
    lblDesc.setText("Description: ");
    this.add(pnlInfo, null);
    pnlInfo.add(lblName, null);    
    pnlInfo.add(edtName, null);        
    pnlInfo.add(lblMaxBotsPtos, null);
    pnlInfo.add(edtMaxBotsPtos, null);    
    pnlInfo.add(lblMaxBotsForTeam, null);
    pnlInfo.add(edtMaxBotsForTeam, null);
    pnlInfo.add(lblMaxFlags, null);
    pnlInfo.add(edtMaxFlags, null);
    pnlInfo.add(lblMaxTeams, null);
    pnlInfo.add(edtMaxTeams, null);
    this.add(pnlDesc, null);
    pnlDesc.add(lblDesc, BorderLayout.NORTH);
    pnlDesc.add(edtDesc, BorderLayout.CENTER);
    this.setEnabled(false);
  }
 
  public boolean validateObject() throws FormatException{
    try{
      Integer.parseInt(edtMaxBotsForTeam.getText());
      Integer.parseInt(edtMaxBotsPtos.getText());      
      Integer.parseInt(edtMaxFlags.getText());            
      Integer.parseInt(edtMaxTeams.getText());   
      if(edtName.getText().trim().equals("")){
        throw new FormatException("Tournament needs a name");
      }        
      if(edtDesc.getText().trim().equals("")){
        throw new FormatException("Tournament needs a description");
      }       
      return true;
    }  
    catch(NumberFormatException e){
      throw new FormatException("Wrong number format");      
    }  
  }
  
  public class ValueChanged implements KeyListener{
    public void keyPressed(KeyEvent e){
      if(owner.getStatus()!=GraphicEditor.NEW_STATE){
        owner.setStatus(GraphicEditor.FILE_EDITED_STATE);    
      }  
    }   
    public void keyReleased(KeyEvent e){}       
    public void keyTyped(KeyEvent e){}    
  }
}