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

package mages.controller.sensors;

import mages.agents.*;
import mages.gui.*;
import mages.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

public class Grid extends NodeDoubleArray implements Sensor, Serializable{
  int width;
  int height;
  int size;
  Posicionable ref = null;
  double[][] map = null;  
  SensorPanel panel = null;
  protected double ret[] = null;
  Coord lastpos = null;
  JLabel labels[] = null;
  
  public Grid(int s){
    size = s;    
    width = height = (2*size)+1;  
    ret = new double[width*height];    
  }
    
  public Grid(Posicionable r, double[][] m, int s){
    ref = r;
    map = m;
    size = s;    
    width = height = (2*size)+1;
    ret = new double[width*height];
  }
  
  private void init(){ }
  
  //Left to Rigth
  //Top to Down
  public double[] doubleValue(long timestamp){
    if(lastpos==null || !ref.getPosition().equals(lastpos)){
	   lastpos = (Coord)ref.getPosition().clone();  
	   int startX = ref.getPosition().getX()-size;
	   int startY = ref.getPosition().getY()-size;	   
	   int cnt = 0;
	   
      ret = new double[width*height];    
	   for(int k=0; k<ret.length;k++){
	     ret[k] = Double.POSITIVE_INFINITY;
	   }  	 
	     
	   for(int i=0; i<height; i++){
	     for(int j=0; j<width; j++){
	       try{
  	         ret[cnt] = map[j+startX][i+startY];
	       }
	       catch(ArrayIndexOutOfBoundsException e){
	         ret[cnt] = Double.POSITIVE_INFINITY;
	       }    
	       cnt++;
	     }
	   }
	 }
	 return ret;    
  }
    
  //Logical
  public String getName(){
    return "Grid";
  }
  
  public void setValue(Object v){
    ret = (double[])v;
    convert();
  }

  public double[] getValue(){
    return ret;
  }
  
  //UI
  public void paint(Graphics g){
    if(g!=null){  
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
}