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

package mages.controller.ctf.server;

import mages.*;
import mages.gui.*;
import mages.domain.ctf.*;
import mages.gui.graphics2D.*;
import mages.gui.ctf.*;
import clisp.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class CtFScreenServerController implements GameController{
  CtFSession manager = null;
  Layer2D background = null;
  Base2D redBase = null;
  Base2D blueBase = null;  
  Flag2D flags[] = null;
  Bot2D bots[] = null;
  Text2D ctfFont = null;
  boolean canStart = false;
  boolean firstTime = true;
  
  public CtFScreenServerController(CtFSession cs){
    manager = cs;
    ctfFont = new Text2D(MagesEnv.getFilesDir("simObjects")+"ctf/font2.gif",32,34,10);    
  }

  public void init(){
    background = new Layer2D(manager.getWorld().getMap(),0,
                             MagesEnv.getFilesDir("simObjects"),32,32);    
    redBase = new Base2D(manager.getWorld().getPosRedBase(),CtFSettings.RED,32,32);
    blueBase = new Base2D(manager.getWorld().getPosBlueBase(),CtFSettings.BLUE,32,32);    

    Flag fls[] = manager.getWorld().getFlags();    
    flags = new Flag2D[fls.length];
    for(int i=0; i<fls.length; i++){
      flags[i] = new Flag2D(fls[i],32,32);
    }

    User users[] = manager.getUsers();
    Vector tmp = new Vector();
    for(int i=0; i<users.length; i++){
      if(users[i] instanceof CtFBotServer){
        tmp.addElement(users[i]);
      }
    }

    bots = new Bot2D[tmp.size()];
    for(int j=0; j<tmp.size(); j++){    
      CtFBotServer bs = (CtFBotServer)tmp.elementAt(j);
       bots[j] = new Bot2D(bs,32,32);    
    }
  }
  
  private void drawFlags(Graphics2D g2){
    for(int f=0; f<flags.length; f++){
      flags[f].draw(g2);
    }
  }

  private void drawBases(Graphics2D g2){
    redBase.draw(g2);
    blueBase.draw(g2);
  }
  
  private void drawBots(Graphics2D g2){
    for(int b=0; b<bots.length; b++){
      bots[b].draw(g2);
    }
  }
        
  public void draw(Graphics g){
    Graphics2D g2 = (Graphics2D)g;  
    
    if(manager!=null){
      //Graphics
      background.draw(g2); 
      drawBases(g2);    
      drawFlags(g2);         
      drawBots(g2);
      ctfFont.draw(g2,new Point(3,20), Integer.toString(manager.getTurn()));         

      if(firstTime){
        manager.wakeUp();
        firstTime = false;
      }          
    }  
  }
  
  public void updateScreen(){
    Vector tmp = new Vector();
    
    for(int i=0; i<manager.getNumUser(); i++){
      if(manager.getUser(i) instanceof CtFBotServer){
        tmp.addElement(manager.getUser(i));
      }  
    }      
    
    bots = new Bot2D[tmp.size()];    
    for(int j=0; j<bots.length; j++){
      bots[j] = new Bot2D((CtFBotServer)tmp.elementAt(j),32,32);
    }    
  }
    
  public void processKey(KeyEvent e){}
  
  public void processMouse(MouseEvent e){}  
}