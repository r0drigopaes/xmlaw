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

package mages.gui.graphics2D;

import mages.*;
import java.awt.*;
import nu.bugbase.gamelib.sprite.*; 
import nu.bugbase.gamelib.utils.*; 
import nu.bugbase.gamelib.text.*;

public class Text2D{
  BitmapFont font = null;
  Panel comp = new Panel();

  public Text2D(String path, int fw, int fh, int cpr){
    Image img = Toolkit.getDefaultToolkit().getImage(MagesEnv.validate(path));
    try {
       MediaTracker tracker = new MediaTracker(comp);
       tracker.addImage(img, 0);
       tracker.waitForID(0);
    } catch (Exception e) {}  
    font = new BitmapFont(img,fw,fh,cpr,comp);  
  }    
  
  public void draw(Graphics2D g2, Point p, String str){
    font.drawString(g2,p.x,p.y,str);
  }
}