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

package mages.gui.ctf;

import mages.util.*;
import mages.*;
import mages.domain.ctf.*;
import java.awt.*;
import nu.bugbase.gamelib.sprite.*; 
import nu.bugbase.gamelib.utils.*; 

public class Flag2D{  

  Sprite sprite;    
  Flag flag = null;
  int xfactor, yfactor;
        
  public Flag2D(Flag f, int xf, int yf){    
    String path = null;
    flag = f;
    xfactor = xf;
    yfactor = yf;
    if(f.getId()==CtFSettings.RED){
      path = MagesEnv.getFilesDir("simObjects")+MagesEnv.validate("ctf/flagRed.gif");
    }
    else{
      if(f.getId()==CtFSettings.BLUE){
        path = MagesEnv.getFilesDir("simObjects")+MagesEnv.validate("ctf/flagBlue.gif");      
      }
    }     
    sprite = new Sprite(new SpriteImage(MagesEnv.loadImage(path)));
  }   
  
        
  public void draw(Graphics2D g2){
    if(flag.isVisible()){
      Coord c = flag.getPosition();
      sprite.setPosition(c.getX()*xfactor,c.getY()*yfactor);       
      sprite.draw(g2);  
    }  
  }     
}