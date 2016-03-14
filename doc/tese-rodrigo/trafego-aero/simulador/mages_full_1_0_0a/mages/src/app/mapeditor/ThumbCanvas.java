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
import mages.*;
import java.awt.*;
import java.awt.event.*;

public class ThumbCanvas extends Canvas implements MouseListener{
  Image img;
  Point coord;
  boolean select = false;
  CollectionWindow owner = null;
  
  public ThumbCanvas(Point p, Icon ic, CollectionWindow o){
    img = Toolkit.getDefaultToolkit().getImage(MagesEnv.getFilesDir("simObjects")+ic.getPath());
    coord = p;
    owner = o;
    this.addMouseListener(this);
  }

  public void paint(Graphics g){
    if(select){
      g.drawImage(img,0,0,this);        
      g.setColor(Color.magenta);
      g.drawRect(0,0,img.getWidth(this)-1,img.getHeight(this)-1);
    }
    else{
      g.drawImage(img,0,0,this);    
    }
  }
  
  public void update(Graphics g){
    paint(g);
  }
  
  public Point getCoord(){
    return coord;
  }

  public void selected(boolean b){
    select = b;
  }
      
  public void mouseClicked(MouseEvent e){ 
    if(e.getClickCount()>1){
      SimObjectWindow sow = new SimObjectWindow((Frame)owner,
                                (SimObject)owner.getObject());
      sow.show();                                
    }   
  }
    
  public void mousePressed(MouseEvent e){
    if(select){ }
    else{
      if(owner.getActive()!=null){
        ThumbCanvas tc = owner.getActive();
        tc.selected(false);      
        tc.repaint();
      }      
      select = true;
      owner.setActive(this);
    }
    repaint();
  }
  
  public void mouseReleased(MouseEvent e){}
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}    
}