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
import java.text.*;
import java.io.*;
import java.util.*;

public class DistanceAndAngle extends NodeDoubleArray implements Sensor, Serializable{

  String name;
  SixSectors sixSec = null;
  Directionable ref = null;
  double result[] = null;
  int max = 0;
  MagesDimension dim = null;
  
  public DistanceAndAngle(String n){
    name = n; 
  }

  public DistanceAndAngle(String n, Directionable r, SixSectors ss, int m, MagesDimension d){    
    name = n;
    sixSec = ss;
    ref = r;
    max = m;
    dim = d;
  }
  
  public double[] doubleValue(long timestamp){
    Vector temp[] = sixSec.value(timestamp);
    result = new double[max*2];    
        
    if(temp[0]!=null){      
      int cntDist = 0;
      int cntDeg = 1;
      for(int i=0; i<temp[0].size(); i++){
        Posicionable target = (Posicionable)temp[0].elementAt(i);
        result[cntDist] = distance(target);            
        result[cntDeg] = degree(target);        
        cntDist+=2;
        cntDeg+=2;        
      }
      return sort(format(temp[0].size()));
    }
    else{    
      return format(0);
    }
  }
  
  private double[] sort(double[] source){
    Object vec[] = new Object[source.length/2];
    int cnt = 0;
    for(int i=0; i<source.length; i+=2){
      Chunck tmp = new Chunck(source[i],source[i+1]);
      vec[cnt++] = tmp;
    }

    Arrays.sort(vec);
    
    cnt = 0;
    for(int j=0; j<vec.length; j++){
      Chunck tmp2 = (Chunck)vec[j];
      result[cnt++] = tmp2.getDistance();
      result[cnt++] = tmp2.getDegree();
    }
    return result;
  }
  
  private double[] format(int num){
    if(num<max){
      for(int index = num*2; index<=((max*2)-1); index++){
        result[index] = Double.POSITIVE_INFINITY;
      }
    }
    return result;    
  }
  
  private double distance(Posicionable t){
    return Distance.distance(t.getPosition(),ref.getPosition());  
  }
  
  private double degree(Posicionable t){
    Coord p0 = BresenhamLine.screenToCartesian(ref.getPosition(),dim.getWidth(),dim.getHeight());
    Coord p1 = BresenhamLine.screenToCartesian(t.getPosition(),dim.getWidth(),dim.getHeight());
    double kx = -p0.getX();
    double ky = -p0.getY();
    double ax = Math.cos((ref.getDirection()*Math.PI)/180);
    double ay = Math.sin((ref.getDirection()*Math.PI)/180);
    p0 = new Coord(ax,ay);
    p1 = new Coord(p1.getX()+kx,p1.getY()+ky);    
    
    double ret = EightSectors.degree(p0,p1);  
    if(ret==-1){
      return Double.POSITIVE_INFINITY;
    }
    else{          
      int dirRef = ref.getDirection()+45;
      dirRef = (dirRef>360)?dirRef-360:dirRef;
      double rx = Math.cos((dirRef*Math.PI)/180);
      double ry = Math.sin((dirRef*Math.PI)/180);      
      Coord pr = new Coord(rx,ry);
      double deg = EightSectors.degree(pr,p1);
      System.out.println("daa "+deg+" "+ret);      
      if(((int)deg)>=45){
        return ret;
      }
      else{
         return (ret==0)?0:-ret;            
      }  
    }
  }
  
  //Logical
  public String getName(){
    return name;
  }
  
  public void setValue(Object v){
    result = (double[])v;
  }

  public double[] getValue(){
    return result;
  }
  
  //UI
  public void paint(Graphics g){
    if(g!=null){
		draw(g);
    }      
  }
  
  private void draw(Graphics g){  
    Font font = new Font("Dialog",Font.BOLD,12);        
    g.setFont(font);      
    FontMetrics fm = g.getFontMetrics(font);
    
    max = result.length/2;     
  
    int index = 0;
    String values[][] = new String[max+1][2];
    values[0][0] = "Distance";   
    values[0][1] = "Degree";  
    int wCol1 = fm.stringWidth(values[0][0]);
    int wCol2 = fm.stringWidth(values[0][1]);      
  
    for(int i=1; i<max+1; i++){
      for(int j=0; j<2; j++){
        if(result[index]==Double.POSITIVE_INFINITY){
          values[i][j] = "-";                 
        }
        else{
          values[i][j] = formatNumber(result[index]);                         
        }
        int size = fm.stringWidth(values[i][j]);
        if(index % 2 == 0){
          wCol1 = (size>wCol1)?size:wCol1;
        }
        else{
          wCol2 = (size>wCol2)?size:wCol2;        
        }
        index++;
      }
    }
  
    int x1 = 5;
    int y = fm.getHeight()+5;
    int x2 = x1 + wCol1 + 5;

    for(int i=0; i<max+1; i++){
      for(int j=0; j<2; j++){
        if(j==0){
          g.drawString(values[i][j],x1,y);
        }
        else{
          g.drawString(values[i][j],x2,y);        
        }
      }
      y += fm.getHeight()+5;              
    }      
  }
  
  public Dimension getPreferedSize(){
    return new Dimension(50,100);
  }
  
  public SensorPanel getSensorPanel(){
    SensorPanel canvas = new SensorPanel(this);        
    Dimension d = getPreferedSize(); 
    canvas.setSize(getPreferedSize());
    canvas.setPreferredSize(getPreferedSize());
    canvas.setMinimumSize(getPreferedSize());       
    canvas.setMinimumSize(getPreferedSize());    
    canvas.setOpaque(true);      
    canvas.setBackground(Color.white);    
    return canvas;  
  }
  
  private String formatNumber(double d){
    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);  
    nf.setMaximumFractionDigits(6);    
    return nf.format(d);
  }
  
  //Learning
  public String[] getString(){
    String tmp[] = new String[result.length];
    for(int i=0; i<tmp.length; i++){
      tmp[i] = (result[i]==Double.POSITIVE_INFINITY)?"-":formatNumber(result[i]);
    }    
    return tmp;
  }    
  
  class Chunck implements Comparable{
    double dist;
    double deg;
    
    public Chunck(double dt, double dg){
      dist = dt;
      deg = dg;
    }
    
    public double getDistance(){
      return dist;
    }
    
    public double getDegree(){
      return deg;
    }

    public boolean equals(Object o){
      try{
        Chunck tmp = (Chunck)o;
        return (tmp.getDistance()==dist) && (tmp.getDegree()==deg);
      }
      catch(ClassCastException e){
        return false;
      }
    }
        
    public int compareTo(Object o){
      try{
        Chunck tmp = (Chunck)o;
        if(tmp.getDistance()<dist){
          return -1;
        }
        else{
          if(tmp.getDistance()==dist){
            return 0;
          }
          else{
            return 1;
          }
        }
      }
      catch(ClassCastException e){
        return -1;
      }
    }
  }
}