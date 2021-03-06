/*
GNU Mages, version 1.00 alpha
Multi-Agents Environment Simulator
Copyright (C) 2001-2002 Jo�o Ricardo Bittencourt <jrbitt@uol.com.br>
 
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

public class Base2D{  

  Sprite sprite;    
        
  public Base2D(Coord c, int clr, int xf, int yf){    
    String path = null;
    if(clr==CtFSettings.RED){
      path = MagesEnv.getFilesDir("simObjects")+MagesEnv.validate("ctf/baseRed.gif");
    }
    else{
      if(clr==CtFSettings.BLUE){
        path = MagesEnv.getFilesDir("simObjects")+MagesEnv.validate("ctf/baseBlue.gif");      
      }
    }    
    sprite = new Sprite(new SpriteImage(MagesEnv.loadImage(path)));
    sprite.setPosition(c.getX()*xf,c.getY()*yf);
    sprite.setBorder(new Rectangle(c.getX()*xf,c.getY()*yf,xf,yf));         
  }   
          
  public void draw(Graphics2D g2){
	 sprite.forceInsideBorder();       
    sprite.draw(g2);  
  }     
}