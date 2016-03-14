

package mages.controller.ctf;

import mages.controller.sensors.*;
import mages.agents.*;
import mages.domain.ctf.*;
import mages.gui.*;
import mages.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

public class CtFGrid extends NodeDoubleArray implements Sensor, Memory, Serializable{
  int width;
  int height;
  int size;
  Posicionable ref = null;
  double[][] map = null;  
  SensorPanel panel = null;
  protected double ret[] = null;
  Coord lastpos = null;
  JLabel labels[] = null;
  World world = null;
  double myflag, enemy,othflag,partner;
  int id;
  double[] oldValue = null;
  boolean mem;
  
  public CtFGrid(int s){
    size = s;    
    width = height = (2*size)+1;  
    ret = new double[width*height];    
    for(int i=0; i<ret.length; i++){
      ret[i] = -1.0;
    }
//    init();    
  }
    
  public CtFGrid(Posicionable r, World w, int s, int i, double mf, double p, double of, double e){
    ref = r;
//    map = m;
    world = w;
    size = s;    
    width = height = (2*size)+1;
    ret = new double[width*height];
    myflag = mf;
    enemy = e;
    id = i;
    partner = p;
    othflag = of;
//    init();
  }
  
  private void init(){
//    labels = new JLabel[width*height];
  //  for(int i=0; i<labels.length; i++){
    //  labels[i] = new JLabel("      ");
      //panel.add(labels[i]);
  //  }    
  }
  
  //Left to Rigth
  //Top to Down
  public double[] doubleValue(long timestamp){
    if(lastpos==null || !ref.getPosition().equals(lastpos)){
//    System.out.println("nova_pos");
	   lastpos = (Coord)ref.getPosition().clone();  	   
      if(mem){
        oldValue = (double[])ret.clone();
      }	   
      ret = world.getRectCosts(ref.getPosition(),size, id, myflag, partner, othflag, enemy);    
	 }
	 return ret;    
  }
    
  //Logical
  public String getName(){
    return "Grid";
  }
  
  public void setValue(Object v){
    if(mem){
      oldValue = (double[])ret.clone();
    }  
    ret = (double[])v;
//    System.out.println("v "+ret[0]+" "+ret[1]+" "+ret[2]+" "+ret[3]+" "+ret[4]+" "+ret[5]+" "+ret[6]+" "+ret[7]+" "+ret[8]);
    convert();
  }

  public double[] getValue(){
    return ret;
  }
  
  //UI
  public void paint(Graphics g){
    if(g!=null){  
//      convert();
//      panel.paint(g);    
    }  
  }
  
  protected void convert(){
    if(ret!=null){
      for(int i=0; i<ret.length; i++){
        if(ret[i]==Double.POSITIVE_INFINITY){
          labels[i].setText("-1");        
        }
        else{
          labels[i].setText(Double.toString(ret[i]));              
        }
        labels[i].repaint();              
      }
    }  
  }
  
  public Dimension getPreferedSize(){  
    return new Dimension(100,100);
//    return panel.getPreferredSize();
  }
  
  public SensorPanel getSensorPanel(){
    panel = new SensorPanel(this);
    panel.setSize(getPreferedSize());
    panel.setPreferredSize(getPreferedSize());
    panel.setMinimumSize(getPreferedSize());       
    panel.setMinimumSize(getPreferedSize());    
    panel.setOpaque(true);      
    panel.setBackground(Color.white);    
    panel.setLayout(new GridLayout(width,height));
    labels = new JLabel[width*height];
    for(int i=0; i<labels.length; i++){
      labels[i] = new JLabel("      ");
      panel.add(labels[i]);
    }        
    return panel;
  }
  
  //Learning
  public String[] getString(){
    String tmp[] = new String[ret.length];
    for(int i=0; i<tmp.length; i++){
      tmp[i] = Double.toString(ret[i]);
    }
    return tmp;
  }
  
  public String[] getMemoryString(){
    String tmp[] = new String[ret.length];
    if(oldValue!=null){
      double[] p = (double[])oldValue;
      for(int i=0; i<tmp.length; i++){
        tmp[i] = Double.toString(p[i]);
      }
    }  
    else{
      for(int i=0; i<tmp.length; i++){
        tmp[i] = "-";
      }
    }
    return tmp;    
  }
  
  public boolean isUsingMemory(){
    return mem;
  }
  
  public void setUseMemory(boolean flag){
    mem = flag;
  }
  
  public Object getOldValue(){
    return oldValue;
  }   
}