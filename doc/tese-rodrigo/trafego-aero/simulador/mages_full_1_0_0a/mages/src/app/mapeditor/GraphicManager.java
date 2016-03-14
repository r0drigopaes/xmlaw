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

package mages.app.mapeditor;

import mages.simulation.*;
import mages.app.*;
import mages.gui.*;
import mages.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

public class GraphicManager extends Panel implements IManager, ProcessInfo{
  Label lblLayers = new Label();
  FlowLayout flowLayout1 = new FlowLayout();
  Choice cboLayers = new Choice();
  Label lblTools = new Label();
  Choice cboTools = new Choice();
  Label lblX = new Label();
  Label lblXValue = new Label();
  Label lblY = new Label();
  Label lblYValue = new Label();
  Label lblArgs = new Label();
  TextField edtArgs = new TextField(20);

  //Application
  GraphicEditor owner;
  int width, height;
  MapSchema mapSch = null;
  IFloatWindow[] collections = null;
  IFloatWindow activedCol = null;
  IFloatWindow board = null;
  int maxW, maxH;
    
  public GraphicManager() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void reset(){
    if(collections!=null){
      for(int i=0; i<collections.length; i++){
        if(!cboLayers.getItem(i).equals("Layer Runtime")){
          Frame f = (Frame)collections[i];
          f.hide();
          f.dispose();
        }  
      }
    }
    else{
      collections = null;
    }  

    if(board!=null){
      Frame f = (Frame)board;
      f.hide();
      f.dispose();    
    }
    else{
      board = null;
    }
    cboLayers.removeAll();
    cboLayers.add("                    ");    
    maxW = maxH = 0;    
  }
    
  private void jbInit() throws Exception {
    lblLayers.setText("Layers: ");
    this.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(10);
    lblTools.setText("Tool: ");
    lblX.setFont(new java.awt.Font("Dialog", 1, 12));
    lblX.setText("X: ");
    lblXValue.setFont(new java.awt.Font("Dialog", 1, 12));
    lblXValue.setForeground(Color.blue);
    lblY.setFont(new java.awt.Font("Dialog", 1, 12));
    lblY.setText("Y: ");
    lblYValue.setFont(new java.awt.Font("Dialog", 1, 12));
    lblYValue.setForeground(Color.blue);
    lblArgs.setText("Arguments: ");
    cboLayers.add("                    ");
    cboTools.add("Point");
    cboTools.add("Line");
    cboTools.add("Square");    
    cboTools.add("Eraser");    
    this.add(lblLayers, null);
    this.add(cboLayers, null);
    this.add(lblTools, null);
    this.add(cboTools, null);
    this.add(lblX, null);
    this.add(lblXValue, null);
    this.add(lblY, null);
    this.add(lblYValue, null);
    this.add(lblArgs);
    this.add(edtArgs);    
    
    cboLayers.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        selection(e);
      }
    });    
  }
  
  public void init(){
    if(owner.getAction()==GraphicEditor.NEW_ACTION){
      InitialDialog idiag = new InitialDialog((Frame)owner,this);
      idiag.show();      
      
      if(mapSch!=null){ 
        reset();          
        createCollections();
        if(collections.length!=0){
          cboLayers.select(0);     
          activedCol = collections[cboLayers.getSelectedIndex()];
          Frame f = (Frame)activedCol;
          f.show();                  
        } 
        board = new BoardWindow(mapSch.getName(),height, width, maxW, maxH,
                                mapSch.getNumLayers(),this);  
        Frame b = (Frame)board;
        b.show();
        setEnabled(true);
        setStatus(IEditor.FILE_SAVED_STATE);        
        repaint();      
      }  
    }
    else{
      if(owner.getAction()==GraphicEditor.OPEN_ACTION){
        reset();
      }
    }
  }  
  
  private void createCollections(){
    collections = new IFloatWindow[mapSch.getNumLayers()];  
    SimObjectColController socc = new SimObjectColController();   
    maxW = maxH = 0;
    if(collections.length!=0){
      cboLayers.remove(0);
    }                                
    for(int i=0; i<collections.length; i++){    
      MapSchemaEntry mse = mapSch.getMapSchemaEntry(i);
      try{
        if(mse.getType()!=MapSchemaEntry.RT){
          String name = MagesEnv.getFilesDir("simObjects")+ mse.getPath();                           
          SimObjectCollection soc = (SimObjectCollection)socc.load(name);
          collections[i] = new CollectionWindow(soc,mse.getRows(),mse.getCols());    
          cboLayers.add("Layer "+Integer.toString(mse.getLayer()));
          maxW = (soc.getMaxWidth()>maxW)?soc.getMaxWidth():maxW;
          maxH = (soc.getMaxHeight()>maxH)?soc.getMaxHeight():maxH;          
        }  
        else{
          cboLayers.add("Layer Runtime");        
        }
      }
      catch(Exception e){
        ErrorMessage em = new ErrorMessage((Frame)owner,e.getClass().getName()+" : "+e.getMessage());
        em.show();       
      }              
    } 
  }
    
  public Object getObject() throws IOException{
    Map map = (Map)board.getObject();
    map.setMapSchema(mapSch);
    return map;
  }
  
  public void setObject(Object obj){
    Map map = (Map)obj;
    mapSch = map.getMapSchema();
    width = map.getWidth();
    height = map.getHeight();   
    createCollections();
    if(collections.length!=0){
      cboLayers.select(0);     
      activedCol = collections[cboLayers.getSelectedIndex()];
      Frame f = (Frame)activedCol;
      f.show();      
    } 
    board = new BoardWindow(mapSch.getName(),height, width, maxW, maxH,
                            mapSch.getNumLayers(),this);
    board.setObject(map);                            
    Frame b = (Frame)board;
    b.show();
    setEnabled(true);
    repaint();  
  }
  
  public void setEditor(IEditor e){
    owner = (GraphicEditor)e;
  }
  
  public void update() throws IOException{
  }
  
  public boolean validateObject() throws FormatException{  
    if(mapSch.getValidatorName()!=null){
      IValidator val = null;   
      if(getArgs(edtArgs.getText())!=null){
        try{
           val = mapSch.getValidator();
        }
        catch(Exception e){
          throw new FormatException(e.getClass().getName()+" : "+e.getMessage());
        }   
        
        try{
          if(val.validate(getObject(),getArgs(edtArgs.getText()))){
             return true;             
          }
          else{
             throw new FormatException(val.getErrorMessage());           
          }
        }  
        catch(IOException e1){
          throw new FormatException(e1.getClass().getName()+" : "+e1	.getMessage());
        }        
      }   
      else{
        throw new FormatException("You must specify arguments");
      }
    }     
    return true;    
  }
  
  public int getLayer(){
    return cboLayers.getSelectedIndex();
  }
  
  public int getTool(){
    return cboTools.getSelectedIndex();
  }
  
  public void setCoord(Point p){
    lblXValue.setText(Integer.toString(p.x));
    lblYValue.setText(Integer.toString(p.y));    
  }
  
  private String[] getArgs(String src){
    String tmp = src.replace(';',' ');    
    Vector tmp2 = new Vector();
    StringTokenizer token = new StringTokenizer(tmp," ");
    String[] args = null;
        
    int c = 0;
    
    while(token.hasMoreTokens()){
      tmp2.addElement(token.nextToken());
    }

    if(tmp2.size()>0){
      args = new String[tmp2.size()];
    }

    for(int i=0; i<tmp2.size(); i++){
      args[i] = (String)tmp2.elementAt(i);
    }  
    return args;
  }
  
  public void processInfo(Object info){
    if(info!=null){
      MapSchemaController msc = new MapSchemaController();    
      mapSch = null;      
      Object[] values = (Object[])info;
      String fileSchema = (String)values[0];
      try{      
        width = Integer.parseInt((String)values[1]);
        height = Integer.parseInt((String)values[2]);    
        mapSch = (MapSchema)msc.load(MagesEnv.getFilesDir("simObjects")+fileSchema);
      }
      catch(Exception e){
        ErrorMessage em = new ErrorMessage((Frame)owner,e.getClass().getName()+
                                            " : "+e.getMessage());
        em.show();       
      }  
    }  
    else{
      width = 0;
      height = 0;    
      maxW = maxH = 0;
      mapSch = null;    
    }
  }  
  
  void selection(ItemEvent e) {
    if(activedCol!=null){
      Frame f = (Frame)activedCol;
      f.hide();
      if(collections[cboLayers.getSelectedIndex()]!=null){      
        activedCol = collections[cboLayers.getSelectedIndex()];
        f = (Frame)activedCol;
        f.show();            
      }
    }
  }  

  public SimObject getSimObject(){
    if(activedCol!=null){
      return (SimObject)activedCol.getObject();   
    }
    return null;  
  }  
  
  public void setStatus(int s){
    owner.setStatus(s);
  }
}