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

import java.awt.*;

public class GameFrame extends Frame{
  Game game;
  
  Dimension offDimension;
  Image offImage;
  Graphics offGraphics;
      
  public GameFrame(Game g){
    super(g.getName());        
    game = g;
  }
  
  public void paint(Graphics g){
    update(g);
  }
  
  public void update(Graphics g){
    Dimension d = game.getSize();

    //Create the offscreen graphics context, if no good one exists.
    if ( (offGraphics == null)
      || (d.width != offDimension.width)
      || (d.height != offDimension.height) ) {
        offDimension = d;
        offImage = createImage(d.width, d.height);
        offGraphics = offImage.getGraphics();
    }
     
    //Erase the previous image.
    offGraphics.setColor(getBackground());
    offGraphics.fillRect(0, 0, d.width, d.height);
    offGraphics.setColor(Color.black);
        
             
    game.paint(offGraphics);
    
    //Paint the image onto the screen.
    g.drawImage(offImage, 0, 0, this);    
  }
}