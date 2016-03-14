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

import mages.simulation.*;
import java.awt.*;
import nu.bugbase.gamelib.sprite.*; 
import nu.bugbase.gamelib.utils.*; 

public class Layer2D{
  Tile2D[][] cels = null;
  int widthTile, heightTile;
                 
  public Layer2D(Map map, int pos, String path, int w, int h){        
    Layer layer = map.getLayer(pos);
    MapSchema ms = map.getMapSchema();
    layer.setUseTable(ms.getMapSchemaEntry(pos).isUsedIndex());
    
    if(ms.getMapSchemaEntry(pos).isUsedIndex()){
      initWithTable(layer,path,w,h);    
    }
    else{
      initWithOutTable(layer,path,w,h);    
    }    
  }   
  
  public void initWithTable(Layer layer, String path, int w, int h){    
    cels = new Tile2D[layer.getHeight()][layer.getWidth()];
    widthTile = layer.getWidth();
    heightTile = layer.getHeight();    
    int incW = w;
    int incH = h;
    
    int x = 0;
    int y = 0;    
    for(int r=0; r<layer.getHeight(); r++){
      for(int c=0; c<layer.getWidth(); c++){
        SimObject so = layer.getCell(c,r);
        if(so!=null){
          try{
            Image img = so.getImage(path,0,IconsTable.LOAD_IMAGE);                                      
            cels[r][c] = new Tile2D(img,widthTile*incW,heightTile*incH);
            cels[r][c].setPosition(new Point(x,y));        
          }
          catch(InterruptedException e){//error
            System.out.println("error");
          }  
        }
        x += incW;
      }
      x = 0;
      y += incH;      
    }  
  }
  
  public void initWithOutTable(Layer layer, String path, int w, int h){
    cels = new Tile2D[layer.getHeight()][layer.getWidth()];
    widthTile = layer.getWidth();
    heightTile = layer.getHeight();
    
    int incW = w;
    int incH = h;    
    int x = 0;
    int y = 0;      
    for(int r=0; r<layer.getHeight(); r++){
      for(int c=0; c<layer.getWidth(); c++){
        SimObject so = layer.getCell(c,r);
        if(so!=null){
          Icon icon = so.getIcon(0);          
          if(icon!=null){
            try{
              Image img = icon.getImage(path,(so.paramIsModified())?true:false);                     
              cels[r][c] = new Tile2D(img,widthTile*incW,heightTile*incH);
              cels[r][c].setPosition(new Point(x,y));        
            }
            catch(InterruptedException e){//error}
              System.out.println("error");            
            }  
          }
        }
        x += incW;
      }
      x = 0;
      y += incH;      
    }  
  }  
        
  public void draw(Graphics2D g2){
    for(int r=0; r<heightTile; r++){
      for(int c=0; c<widthTile; c++){
        if(cels[r][c]!=null){
          cels[r][c].draw(g2);
        }  
      }
    }  
  }  
}