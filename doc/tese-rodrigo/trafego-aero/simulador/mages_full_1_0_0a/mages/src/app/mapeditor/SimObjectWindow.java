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
import mages.gui.*;
import mages.*;

import java.awt.*;
import java.awt.event.*;

public class SimObjectWindow extends Dialog{

  public static final int TYPE = 0;
  public static final int ANGLE = 1;
  public static final int WIDTH = 2;  
  public static final int HEIGHT = 3;
  public static final int PARAMETERS = 4;    
    
  Frame owner;
  SimObject so;
  boolean[] edits = new boolean[5];
  Choice angle = null;  
  
  public SimObjectWindow(Frame f, SimObject s){
    super(f);
    owner = f;
    so = s;    
    angle = new Choice();      
  }  
  
  private void createDialog(){
    setTitle(so.getName());
    setResizable(false);
    setModal(true);    
    setLayout(new GridLayout(so.getNumParameters()+5,2));
    add(new Label("Path: ")); 
    add(new Label(so.getIcon(0).getPath()));    
    add(new Label("Type: "));
    if(edits[TYPE]){
      //use texture
      add(new Label());   
    }
    else{
      add(new Label("Image"));    
    }

    add(new Label("Angle: "));
    if(edits[ANGLE]){
      angle.addItemListener(new ComboChanged(ANGLE));
      angle.add("Angle 0");
      angle.add("Angle 90");      
      angle.add("Angle 180");      
      angle.add("Angle -90");     
      angle.select(so.getIcon(0).getAngle()); 
      add(angle);
    }    
    else{
      String tmp = null;
      switch(so.getIcon(0).getAngle()){
        case 0: tmp = "Angle 0";
        break;
        case 1: tmp = "Angle 90";
        break;
        case 2:  tmp = "Angle 180";
        break;
        case 3:  tmp = "Angle -90";
        break;                        
      }
      add(new Label(tmp));
    }
    
    add(new Label("Width: "));
    if(edits[WIDTH]){    
       add(new TextField(20));
    }
    else{
      add(new Label(Integer.toString(so.getIcon(0).getWidth())));
    }
    
    add(new Label("Height: "));
    if(edits[HEIGHT]){    
       add(new TextField(20));
    }
    else{
      add(new Label(Integer.toString(so.getIcon(0).getHeight())));
    }    
    
    Parameter[] prm = so.getParameters();
    for(int i=0; i<prm.length; i++){
      add(new Label(prm[i].getName()+": "));
      if(edits[PARAMETERS]){          
        TextField tf = new TextField(prm[i].getValue());
        tf.addTextListener(new TextChanged(prm[i].getName()));
        add(tf);
      }
      else{
        add(new Label(prm[i].getValue()));
      }
    }
    
    addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
    	  	   dispose();
			}
	 });
	     
    pack();
    		          
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); 
	 Rectangle bounds = new Rectangle(d.width,d.height); 
	 Rectangle abounds = getBounds(); 
	 setLocation(bounds.x + (bounds.width - abounds.width) / 2, 
  	   		    bounds.y + (bounds.height - abounds.height) / 2);        
  }
  
  public void show(){
    createDialog();
    super.show();
  }
  
  public void setEdit(int p, boolean v){
    edits[p] = v;
  }
  
  public class ComboChanged implements ItemListener{
    int id;
    
    public ComboChanged(int i){
      id = i;
    }
    
    public void itemStateChanged(ItemEvent e){
      switch(id){
        case SimObjectWindow.ANGLE:
           so.getIcon(0).setAngle(angle.getSelectedIndex());      
           so.modified(SimObject.ICON, true);
        break;
      }  
    }
  }
  
  public class TextChanged implements TextListener{
    String prm;
    
    public TextChanged(String p){
      prm = p;
    }
    
    public void textValueChanged(TextEvent e){
      TextField a = (TextField)e.getSource();
      so.setParameterValue(prm,a.getText());
    }
  }  
}

