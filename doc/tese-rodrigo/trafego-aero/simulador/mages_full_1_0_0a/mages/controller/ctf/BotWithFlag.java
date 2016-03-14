

package mages.controller.ctf;

import mages.agents.*;
import mages.domain.ctf.*;
import mages.gui.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

public class BotWithFlag extends NodeBoolean implements Sensor, Serializable{
   
  boolean flag = false; 
  Bot bot = null;
  JLabel label = null;
  SensorPanel panel = null;
    
  public BotWithFlag(){}
    
  public BotWithFlag(Bot b){
    bot = b;
  }
    
  public boolean value(long timestamp){
    if(bot!=null){
      return bot.hasFlag();
    }
    else{
      return flag;
    } 
  }
    
  //Logical
  public String getName(){
    return "Bot with Flag";
  }
  
  public void setValue(Object v){
    flag = ((Boolean)v).booleanValue();
    convert();
  }

  public boolean getValue(){
    return flag;
  }
  
  //UI
  public void paint(Graphics g){}
  
  protected void convert(){
    label.setText(new Boolean(flag).toString());              
    label.repaint();              
  }
  
  public Dimension getPreferedSize(){  
    return new Dimension(50,50);
  }
  
  public SensorPanel getSensorPanel(){
    panel = new SensorPanel(this);
    panel.setSize(getPreferedSize());
    panel.setPreferredSize(getPreferedSize());
    panel.setMinimumSize(getPreferedSize());       
    panel.setMinimumSize(getPreferedSize());    
    panel.setOpaque(true);      
    panel.setBackground(Color.white);    
    label = new JLabel("   ");    
    panel.add(label);        
    return panel;
  }
  
  //Learning
  public String[] getString(){
    String tmp[] = new String[1];
    tmp[0] = (flag)?"1":"0";
    return tmp;
  }
}