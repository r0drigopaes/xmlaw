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
import java.text.*;
import javax.swing.*;

public class DynamicInfoWindow extends JInternalFrame{
  Agent agent;
  JProgressBar bars[] = null;
  JLabel labels[] = null;
  NumberFormat nf = null;
      
  public DynamicInfoWindow(Agent a){
    super();  
    agent = a;
    setTitle("Dynamic Stats");
    setOpaque(true);
    setIconifiable(true);
    setMaximizable(true);
    setResizable(true);
    setVisible(true);    
    DynamicStats stats = a.getDynamicStats();
    String[][] setup = stats.getStatsSetup();
    JPanel p = new JPanel();
    GridLayout grid = new GridLayout(setup.length,3);
    grid.setVgap(20);
    p.setLayout(grid);
    nf = NumberFormat.getPercentInstance();   
    bars = new JProgressBar[setup.length];
    labels = new JLabel[setup.length];
    for(int i=0; i<setup.length; i++){
      JLabel name = new JLabel(setup[i][0]+"   ");
      double min = Double.parseDouble(setup[i][1]);
      double def = Double.parseDouble(setup[i][2]);
      double max = Double.parseDouble(setup[i][3]);
      JProgressBar pb = new JProgressBar(JProgressBar.HORIZONTAL,(int)min,(int)max);
      pb.setValue((int)def);
      pb.setString(setup[i][2]);
      pb.setStringPainted(true);          
      double percent = pb.getPercentComplete();
      JLabel lblPerc = new JLabel("   "+nf.format(percent));
      p.add(name);
      p.add(pb);
      p.add(lblPerc);
      bars[i] = pb;
      labels[i] = lblPerc;
    }            
    setPreferredSize(new Dimension(270,130));  
    setContentPane(p);
    setSize(new Dimension(270,130));  
  }  
  
  public void paint(Graphics g){
    if(bars!=null){
      String str[] = agent.getDynamicStats().getValues();    
      for(int i=0; i<bars.length; i++){
        double d = Double.parseDouble(str[i]);
        bars[i].setValue((int)d);
        
        NumberFormat nstr = NumberFormat.getNumberInstance();
        nstr.setMaximumFractionDigits(2);
        bars[i].setString(nstr.format(d));          
        
        double percent = bars[i].getPercentComplete();
        labels[i].setText("   "+nf.format(percent));          
      }    
    }  
    super.paint(g);        
  }  
}