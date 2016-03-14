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

import mages.app.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public abstract class GraphicEditor extends Frame implements IEditor, ProcessInfo{
  public static final boolean USE_FILEDIALOG = true;  
  public static final boolean NO_USE_FILEDIALOG = false;    
  
  public static final int NEW_ACTION = 0;      
  public static final int OPEN_ACTION = 1;   
  public static final int SAVE_ACTION = 2;
  public static final int SAVE_AS_ACTION = 3;               
  public static final int EXIT_ACTION = 4;   
    
  private final int SAVE_ON_EXIT = 0;
  private final int REWRITE_FILE = 1;  

  MenuBar menu = new MenuBar();
  Menu mnuFile = new Menu();
  MenuItem miNew = new MenuItem();
  MenuItem miSave = new MenuItem();
  MenuItem miSaveAs = new MenuItem();
  MenuItem miOpen = new MenuItem();
  MenuItem miExit = new MenuItem();
  FlowLayout flow = new FlowLayout();
  String filename = null;
  String oldName = null;
  FilenameFilter filefilter = null;
  int state;
  boolean useFileDialog;
  String defaultDir;
  boolean canSave = true;
  boolean saved = false;
  int lastAction;

  //Application Layer
  IController control = null;
  IControlable controled = null;

  public GraphicEditor(boolean m) {  
    try {
      useFileDialog = m;
      jbInit();
      initEvents();
      init(); //Application
      setResizable(false);      
      setStatus(READY_STATE);      
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void setAction(int a){
    lastAction = a;
  }
  
  public int getAction(){
    return lastAction;
  }  
  
  public void setStatus(int s){
    state = s;
    miNew.setEnabled(true);
    miOpen.setEnabled(true);
    switch(state){
      case READY_STATE:
      case NEW_STATE:
         miSave.setEnabled(false);
         miSaveAs.setEnabled(false);
      break;

      case FILE_SAVED_STATE:
         miSave.setEnabled(false);
         miSaveAs.setEnabled(true);
      break;

      case FILE_EDITED_STATE:
         miSave.setEnabled(true);
         miSaveAs.setEnabled(true);
      break;
    }
  }

  public int getStatus(){
    return state;
  }
  
  public void setDefaultDir(String dir){
    if(!useFileDialog){
      defaultDir = dir;
    }
  }
  
  private void initEvents() throws Exception {
    addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
    	  	   exitEditor(e);
			}
	 });

    miNew.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        newObject(e);
      }
    });

    miOpen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openObject(e);
      }
    });

    miSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveObject(e);
      }
    });

    miSaveAs.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveAsObject(e);
      }
    });

    miExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exitEditor(e);
      }
    });
    
    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        modify(e);
      }
    });    
  }

  private void jbInit() throws Exception {
    this.setMenuBar(menu);
    this.setLayout(flow);
    mnuFile.setLabel("File");
    miNew.setActionCommand("");
    miNew.setLabel("New");
    miSave.setLabel("Save ...");
    miSaveAs.setLabel("Save as ...");
    miOpen.setLabel("Open ...");
    miExit.setLabel("Exit");
    flow.setAlignment(FlowLayout.LEFT);
    flow.setHgap(0);
    flow.setVgap(0);
    menu.add(mnuFile);
    mnuFile.add(miNew);
    mnuFile.add(miOpen);
    mnuFile.addSeparator();
    mnuFile.add(miSave);
    mnuFile.add(miSaveAs);
    mnuFile.addSeparator();
    mnuFile.add(miExit);
  }

  public abstract void init();

  public void setFilenameFilter(FilenameFilter ff){
    filefilter = ff;
  }

  private boolean existFile(String name){
    File tmp = new File(name);
    return tmp.exists();
  }
  
  public boolean save(){
    if(filename==null){
      if(useFileDialog){
        FileDialog dialog = new FileDialog(this,"Save",FileDialog.SAVE);
        dialog.setModal(true);
        dialog.setFilenameFilter(filefilter);
        dialog.show();
        filename = dialog.getDirectory()+dialog.getFile();
      }
      else{
        SimpleDialog sd = new SimpleDialog(this,"File name: ","Save",
                                           this,SimpleDialog.NO_USE_SEARCH);
        sd.show();                                           
      }  
    }

    if(saved){        
      try{       
        if(controled.validateObject() && state!=FILE_SAVED_STATE && filename!=null){
          canSave = true;
          if(existFile(filename)){
             YesOrNoDialog ynd = new YesOrNoDialog(this,"Do you want to rewrite "+filename+"?",
                                     "Save",this, REWRITE_FILE);          
             ynd.show();                                   
          } 
          if(canSave){
            controled.update();    
            control.save(controled.getObject(),filename);
            setStatus(FILE_SAVED_STATE);                    
          }
        }  
      }
      catch(IOException e){
          ErrorMessage em = new ErrorMessage(this,e.getMessage());
          em.show();    
      }  
      catch(FormatException f){
        ErrorMessage em = new ErrorMessage(this,f.getMessage());
        em.show();    
      }        
    }            
    return saved;
  }

  public void saveAs(){
    String name = null;  
    if(useFileDialog){      
      FileDialog dialog = new FileDialog(this,"Save as",FileDialog.SAVE);
      dialog.setModal(true);
      dialog.setFilenameFilter(filefilter);
      dialog.show();
      name = dialog.getFile();
    }
    else{
        SimpleDialog sd = new SimpleDialog(this,"File name: ","Save As",
                                           this,SimpleDialog.NO_USE_SEARCH);
        sd.show();                                           
        name = filename; 
    }  

    try{
      boolean val = controled.validateObject();
      if(val&&name!=null){  
        canSave = true;
        if(existFile(name)){             
           YesOrNoDialog ynd = new YesOrNoDialog(this,"Do you want to rewrite "+name+"?",
                                   "Save",this, REWRITE_FILE);          
           ynd.show();                                   
        }
        if(canSave){      
          controled.update();                  
          control.save(controled.getObject(),name);
          setStatus(FILE_SAVED_STATE);           
        }                  
        if(oldName!=null){
         filename = new String(oldName);
        }
        else{
          filename = null;
        } 
        oldName = null;
      }  
      else{
        if(!val){
          ErrorMessage em = new ErrorMessage(this,"This object has problem. It wasn't possible to save.");
          em.show();      
        }  
      }
    }
    catch(IOException e){
      ErrorMessage em = new ErrorMessage(this,e.getMessage());
      em.show();
    }  
    catch(FormatException f){
      ErrorMessage em = new ErrorMessage(this,f.getMessage());
      em.show();    
    }        
  }

  public void open(){
    if(useFileDialog){      
      FileDialog dialog = new FileDialog(this,"Open",FileDialog.LOAD);
      dialog.setModal(true);
      dialog.setFilenameFilter(filefilter);
      dialog.show();
      filename = dialog.getDirectory()+dialog.getFile();
    }
    else{
        SimpleDialog sd = new SimpleDialog(this,"File name: ","Open",
                                           this,SimpleDialog.NO_USE_SEARCH);
        sd.show();                                           
    }
    
    if(saved){  
      try{
        if(filename!=null){
          controled.init();
          controled.setObject(control.load(filename));
          setStatus(FILE_SAVED_STATE);            
        }  
      }
      catch(IOException e){
        ErrorMessage em = new ErrorMessage(this,e.getMessage());
        em.show();
      }  
    }  
  }

  public void exit(){
    System.exit(0);        
  }

  public void setController(IController ctrl){
    control = ctrl;
  }

  public void setControled(IControlable ctrled){
    controled = ctrled;
  }

  private boolean asking(){
    YesOrNoDialog ynd = new YesOrNoDialog(this,"Do you want to save this object?",
                            "Save",this,REWRITE_FILE);
    ynd.show();  
    if(canSave){
      return save();
    }  
    return true;
  }
  
  void newObject(ActionEvent e) {
    boolean f = true;
    if(getStatus()==FILE_EDITED_STATE){
      f = asking();      
    }
    if(f){
      setAction(NEW_ACTION);    
      controled.init();
      String filename = null;               
    }  
    setStatus(NEW_STATE);
  }

  void openObject(ActionEvent e) {
    boolean f = true;  
    if(getStatus()==FILE_EDITED_STATE){
      f = asking();      
    }  
    if(f){
      setAction(OPEN_ACTION);  
      open();        
    }  
  }

  void saveObject(ActionEvent e) {
    setAction(SAVE_ACTION);  
    save();
  }

  void saveAsObject(ActionEvent e) {
    setAction(SAVE_AS_ACTION);  
    saveAs();
  }

  void exitEditor(AWTEvent e) {
    boolean f = true;
    if(getStatus()==FILE_EDITED_STATE){
      f = asking();      
    }  
    if(f){
      setAction(EXIT_ACTION);        
      exit();
    }  
  }
  
  void modify(KeyEvent e){
    if(state!=READY_STATE){
      setStatus(FILE_EDITED_STATE);
    }  
  }  
  
  public void processInfo(Object info){
    if(info instanceof String){
      if((String)info!=null){
        if(filename!=null){
          oldName = new String(filename.toString());                      
        }  
        filename = defaultDir+(String)info;
        saved = true;
      }  
      else{
        saved = false;
      }
    }  
    else{
      if(info instanceof Object[]){
        Object[] vector = (Object[])info;
        Integer id = (Integer)vector[0];  
        Boolean b = (Boolean)vector[1];
        switch(id.intValue()){
          case SAVE_ON_EXIT:
            if(b.booleanValue()){
             save();
            }
            System.exit(0);              
          break;
    
          case REWRITE_FILE:
             canSave = b.booleanValue();                       
          break;          
        }                    
      }
    }
  }  
}