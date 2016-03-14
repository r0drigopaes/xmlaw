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

public class CollectionWindow extends Frame implements IFloatWindow{
  BorderLayout borderLayout1 = new BorderLayout();
  Button btnUp = new Button();
  Button btnDown = new Button();
  Panel pnlInfo = new  Panel();
  CardLayout layout = new CardLayout();
  Panel[] panels = null;
  ThumbCanvas[][][] canvas = null;
  SimObject[] sobjects = null;

  int rows, cols;
  int w, h;
  int pointer = 0;
  int npanels;
  int flag;
  ThumbCanvas active;

  public CollectionWindow(SimObjectCollection soc, int r, int c) {
    try {
      rows = r;
      cols = c;
      w = soc.getMaxWidth();
      h = soc.getMaxHeight();
      sobjects = soc.split();
      setTitle(soc.getName());
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setResizable(false);
    btnUp.setFont(new java.awt.Font("Dialog", 1, 16));
    btnUp.setLabel("Up");
    	     
    btnUp.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        up(e);
      }
    });
    this.setLayout(borderLayout1);
    btnDown.setFont(new java.awt.Font("Dialog", 1, 16));
    btnDown.setLabel("Down");
    btnDown.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        down(e);
      }
    });
    pnlInfo.setLayout(layout);
    this.add(btnUp, BorderLayout.NORTH);
    this.add(btnDown, BorderLayout.SOUTH);
    this.add(pnlInfo, BorderLayout.CENTER);

    //Dynamic
    npanels = 0;
    if(sobjects.length % (rows*cols) == 0){
      npanels = sobjects.length/(rows*cols);
    }
    else{
      npanels = (int)(sobjects.length/(rows*cols))+1;
    }

    if(npanels==1){
      btnUp.setEnabled(false);
      btnDown.setEnabled(false);      
    }
     
    //Create panels
    panels = new Panel[npanels];
    canvas = new ThumbCanvas[rows][cols][npanels];
    int start = 0;
    for(int i=0; i<npanels; i++){
      panels[i] = new Panel();
      GridLayout grid = new GridLayout(rows,cols);
      grid.setHgap(0);
      grid.setVgap(0);
      panels[i].setLayout(grid);
      
      for(int r=0; r<rows; r++){
        for(int c=0; c<cols; c++){
          if(start<sobjects.length){
            canvas[r][c][i] = new ThumbCanvas(new Point(r,c),sobjects[start++].getIcon(0),this);
            canvas[r][c][i].setSize(w,h);
            panels[i].add(canvas[r][c][i]);
          }  
        }
      }
      pnlInfo.add(Integer.toString(i),panels[i]);
    }
    btnUp.setEnabled(false);
    pointer = 0;
    if(npanels==0){
      btnDown.setEnabled(false);
    }
    canvas[0][0][pointer].selected(true);     
    setActive(canvas[0][0][pointer]);
    pack();
  }
  
  void up(ActionEvent e) {
    if(pointer==(npanels-1)){
      btnDown.setEnabled(true);
    }
    if(pointer>0){
      layout.previous(pnlInfo);
      pointer--;
      btnDown.setEnabled(true);      
    }
    if(pointer==0){
      btnUp.setEnabled(false);
    }
  }

  void down(ActionEvent e) {  
    if(pointer==0){
      btnUp.setEnabled(true);
    }
    if(pointer<npanels){
      layout.next(pnlInfo);
      pointer++;
      btnUp.setEnabled(true);      
    }
    if(pointer==(npanels-1)){
      btnDown.setEnabled(false);
    }
  }
  
  public Object getObject(){
    SimObject so = null;  
    if(active!=null){
      Point p = active.getCoord();
      int index = (p.x*cols)+p.y;
      so = sobjects[index];
    } 
    return so;
  }  

  public void setObject(Object o){}    
  
  public ThumbCanvas getActive(){
    return active;
  }
  
  public void setActive(ThumbCanvas p){
    active = p;
  }
}

