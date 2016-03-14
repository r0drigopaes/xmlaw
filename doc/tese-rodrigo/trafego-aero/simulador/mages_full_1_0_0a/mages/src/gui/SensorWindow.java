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

import mages.agents.*;
import mages.gui.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class SensorWindow extends JInternalFrame{
  Sensor sensor;
  SensorPanel canvas;
  AgentWindow aw = null;
 
  public SensorWindow(AgentWindow a, Sensor s){
    super();
    sensor = s;
    aw = a;
    setTitle(sensor.getName());
    setIconifiable(true);
    setMaximizable(true);
    setResizable(true);
    setVisible(true);
    setOpaque(true);
    Dimension d = sensor.getPreferedSize();
    setBounds(0,0,d.width+40,d.height+35);    
                
    canvas = sensor.getSensorPanel();     
    setContentPane(canvas);
    validate();        
    initEvents();
  }
      
  private void initEvents(){              
  }    
          
  //http://developer.java.sun.com/developer/qow/archive/40/index.html
  private Image getImage(){
    Rectangle r = canvas.getBounds();
    Image img = createImage(r.width,r.height);
    Graphics g = img.getGraphics();
    canvas.paint(g);
    return img;
  }
    
  public Sensor getSensor(){
    return sensor;
  }
}