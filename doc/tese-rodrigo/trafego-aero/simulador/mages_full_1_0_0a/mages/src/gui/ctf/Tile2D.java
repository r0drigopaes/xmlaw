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

import java.awt.*;
import nu.bugbase.gamelib.sprite.*; 
import nu.bugbase.gamelib.utils.*; 

public class Tile2D{
  Sprite sprite;
  Point coord;
  
  public Tile2D(Image img, int w, int h){
    sprite = new Sprite(new SpriteImage(img));
    sprite.setBorder(new Rectangle(0,0,w,h));               
  }  
  
  public void setPosition(Point p){
    coord = p;
  }
  
  public void draw(Graphics2D g2){
    sprite.setPosition(coord.x,coord.y);
	 sprite.forceInsideBorder();     
    sprite.draw(g2,0);   	          
  }  
  
  public int getHeight(){
    return sprite.getImageHeight();  
  }
  
  public int getWidth(){
    return sprite.getImageWidth();  
  }   
}