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
import mages.util.*;
import mages.gui.*;
import java.awt.*;
import java.io.*;

public class Sonar extends NodeIntArray implements Sensor, Serializable{
    
  int values[] = new int[6];
  Posicionable obj = null;
  String name;
  long lasttime = 0;
  SixSectors sixSec = null;
        
  public Sonar(String n){
    name = n;
  }
    
  public Sonar(Posicionable ref, Posicionable[] others, double ray, long id, 
               String n, MagesDimension d){  
    obj = ref;
    name = n;
    
    FixedDimension dim = new FixedDimension(d);
    VisionObjects vision = new VisionObjects(new NodePosicionable(obj),ray,dim);
    for(int i=0; i<others.length; i++){
      vision.addNode(new NodePosicionable(others[i]));
    }        
    FilterVisionObjects filter = new FilterVisionObjects(vision,id);

    EightSectors sectors = new EightSectors(new NodePosicionable(obj),filter,ray,dim);
    sixSec = new SixSectors((Directionable)obj, sectors);
    SectorsCount cntSec = new SectorsCount(sixSec);    
    addNode(cntSec);    
  }
  
  public SixSectors getSixSectors(){
    return sixSec;
  }
  
  //Logical
  public String getName(){
    return "Sonar - "+name;
  }
  
  public void setValue(Object v){
    values = (int[])v;
  }

  public int[] getValue(){
    return values;
  }
      
  private void draw(Graphics g){
    Font font = new Font("Dialog",Font.BOLD,12);        
    g.setFont(font);      
    FontMetrics fm = g.getFontMetrics(font);
    g.drawString(name,5,fm.getHeight()+5);
    
    Dimension d = getPreferedSize();
    Point c = new Point((int)(d.width/2)+15,(int)((d.height+(fm.getHeight()*2)+5)/2));
    int ray = (d.width>d.height)?(int)(d.width/2):(int)(d.height/2);    
    g.drawLine(c.x-ray,c.y,c.x+ray,c.y);

    int a = (int)(0.7071*ray);
    Point p1 = new Point(a+c.x,c.y-a);
    Point p2 = new Point(c.x-a,c.y-a);
    Point p3 = new Point(c.x-a,c.y+a);
    Point p4 = new Point(a+c.x,c.y+a);        
    g.drawLine(p1.x,p1.y,p3.x,p3.y);
    g.drawLine(p2.x,p2.y,p4.x,p4.y);        

    Polygon pol = new Polygon();
    pol.addPoint(c.x-ray,c.y);
    pol.addPoint(p2.x,p2.y);
    pol.addPoint(p1.x,p1.y);
    pol.addPoint(c.x+ray,c.y);
    pol.addPoint(p4.x,p4.y);
    pol.addPoint(p3.x,p3.y);
    g.drawPolygon(pol);

    Dimension dp = new Dimension((int)(d.width*0.2),(int)(d.height*0.2));
    int x = c.x-(dp.width/2);
    int y = c.y-(dp.height/2);
    g.setColor(Color.black);
    g.drawLine(c.x,c.y,c.x,c.y-((int)(dp.height*1.5)));
    g.fillOval(x,y,dp.width,dp.height);

    Point texts[] = new Point[6];
    int dray = (int)(d.width*0.3);
    texts[0] = new Point(c.x+dray-5,c.y-3);
    texts[1] = new Point(c.x-dray+13,c.y-ray+fm.getHeight()+15);
    texts[2] = new Point(c.x-ray+20,c.y-3);
    texts[3] = new Point(c.x-ray+20,c.y+3+fm.getHeight());
    texts[4] = new Point(c.x-dray+13,c.y+ray-fm.getHeight()-5);        
    texts[5] = new Point(c.x+dray-5,c.y+3+fm.getHeight());    
   
    g.drawString(Integer.toString(values[0]),texts[1].x,texts[1].y);  
    g.drawString(Integer.toString(values[1]),texts[2].x,texts[2].y);  
    g.drawString(Integer.toString(values[2]),texts[0].x,texts[0].y);          
    g.drawString(Integer.toString(values[3]),texts[3].x,texts[3].y);  
    g.drawString(Integer.toString(values[4]),texts[4].x,texts[4].y);  
    g.drawString(Integer.toString(values[5]),texts[5].x,texts[5].y);      
  }
   
  //UI
  public void paint(Graphics g){  
    if(g!=null){
		draw(g);
    }        
  }
      
  public Dimension getPreferedSize(){
    return new Dimension(100,120);
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
    
  //Learning
  public String[] getString(){
    String[] tmp = new String[6];
    for(int i=0; i<tmp.length; i++){
      tmp[i] = Integer.toString(values[i]);
    }
    return tmp;
  }
  
  //Node            
  public int[] value(long timestamp){
    if((timestamp>=lasttime)||(timestamp == -1)){
  	   if(timestamp>0){
	     lasttime = timestamp;
	   } 
      
      for(int z=0; z<values.length; z++){
        values[z] = 0;
      }
       
      SectorsCount sectors = (SectorsCount)getNode(0);
      values = sectors.value(timestamp);
    }
    return values;
  }
}