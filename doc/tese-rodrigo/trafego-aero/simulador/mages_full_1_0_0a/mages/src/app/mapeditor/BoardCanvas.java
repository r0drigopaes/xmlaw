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
import mages.app.*;
import mages.*;

import java.awt.*;
import java.awt.event.*;

public class BoardCanvas extends Canvas implements IFloatWindow{
  int rows, cols, widthCell, heightCell;
  int widthMap, heightMap;
  int x, y, lx, ly, layers, layer;
  IManager owner = null;
  boolean first = true;
  Image buffer;
  Graphics bg;
  Map map;
  Image[][][] images = null;
            
  public BoardCanvas(int r, int c, int wc, int hc, int l, IManager o){
    rows = r;
    cols = c;
    widthCell = wc;
    heightCell = hc;    
    owner = o;
    lx = ly = -1;
    widthMap = (c*wc)+(c+1);
    heightMap =(r*hc)+(r+1);
    setSize(widthMap,heightMap);
    setBackground(Color.gray); 
    layers = l;
    images = new Image[c][r][l];      
    addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
    	  	   updateCoord(e);
			}
	 });    
    addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
    	      owner.setStatus(IEditor.FILE_EDITED_STATE);
			   if(e.getClickCount()==2){
		         editObject(e);			   
		      }
		      else{
   			  if(e.getClickCount()==1){		      
       	  	    putObject(e);		      
		        }    
		      }
			}
	 });	 
	 createMap();	 
  }

  private void createMap(){
    map = new Map(cols,rows,layers);  
    for(int i=0; i<layers; i++){
      Layer ly = new Layer(i,cols,rows);
      map.setLayer(ly,i);      
    }     
  }
  
  private void drawBack(){
    bg.setColor(Color.black);
    bg.fillRect(0,0,widthMap,heightMap);
  }
  
  private void drawLines(){
    bg.setColor(new Color(79,135,255));
    int startX = 0;
    for(int w=0; w<cols+1;w++){
      bg.drawLine(startX,0,startX,heightMap-1);
      startX += (widthCell+1);
    }
    
    int startY = 0;
    for(int h=0; h<rows+1;h++){
      bg.drawLine(0,startY,widthMap-1,startY);
      startY += (heightCell+1);
    }    
  }
  
  private void drawObjects(){
    for(int i=0; i<layers; i++){
      for(int r=0; r<rows; r++){
        for(int c=0; c<cols; c++){ 
          if(images[c][r][i]!=null){
             bg.drawImage(images[c][r][i],(c*widthCell)+c+1,(r*heightCell)+r+1,this);              
          }
        }
      }
    }  
  }
      
  public void paint(Graphics g){
    if(bg==null){
      buffer = createImage(widthMap,heightMap);    
      bg = buffer.getGraphics();
      repaint();
    }
    else{
      drawBack();
      drawLines();  
      drawObjects();
      g.drawImage(buffer,0,0,this); 
    }    
  }
  
  public void update(Graphics g){  
    paint(g);
  }
  
  public Dimension getPreferredSize(){
    return new Dimension(widthMap,heightMap);                         
  }
    
  private void updateCoord(MouseEvent e){
    if(e.getX()<widthMap && e.getY()<heightMap){
      x = (int)(e.getX()/(widthCell+1));
      y = (int)(e.getY()/(heightCell+1));    
      int maxX =(int)(widthMap/(widthCell+1))-1; 
      int maxY =(int)(heightMap/(heightCell+1))-1;
      x = Math.min(x,maxX);
      y = Math.min(y,maxY);      
      owner.setCoord(new Point(x,y));
    }
  }

  private Point convert(Point cp){
    x = (int)(cp.x/(widthCell+1));
    y = (int)(cp.y/(heightCell+1));    
    int maxX =(int)(widthMap/(widthCell+1))-1; 
    int maxY =(int)(heightMap/(heightCell+1))-1;
    x = Math.min(x,maxX);
    y = Math.min(y,maxY);    
    return new Point(x,y);
  }

  private void erase(int x, int y, int l){
    images[x][y][l] = null;  
    map.removeSimObject(x,y,l);
    repaint();
  }
    
  private void point(int x, int y, int layer, SimObject so){
    if(map.getSimObject(x,y,layer)==null){
      try{
        so.setPosition(x,y);
        map.setSimObject(x,y,layer,(SimObject)so.clone());  
        images[x][y][layer] = so.getIcon(0).getImage(MagesEnv.getFilesDir("simObjects"),false);                  
      }        
      catch(InterruptedException ex){
         ErrorMessage em = new ErrorMessage((Frame)owner,ex.getMessage());
         em.show();          
      }    
    }
  }

  private void square(Point src, Point tgt, int layer, SimObject so){
    int ox = (src.x<tgt.x)?src.x:tgt.x;
    int oy = (src.y<tgt.y)?src.y:tgt.y;
    int dx = (tgt.x>src.x)?tgt.x:src.x;
    int dy = (tgt.y>src.y)?tgt.y:src.y;
    for(int r=oy; r<=dy; r++){
      for(int c=ox; c<=dx; c++){  
        point(c,r,layer,so);
      }
    }
  }

  private void line(Point src, Point tgt, int layer, SimObject so){
    
    if(src.x==tgt.x){
      int oy = (src.y<tgt.y)?src.y:tgt.y;    
      int dy = (tgt.y>src.y)?tgt.y:src.y;        
      for(int c=oy; c<=dy; c++){
        point(src.x,c,layer,so);
      }
    }
    else{
      if(src.y==tgt.y){
        int ox = (src.x<tgt.x)?src.x:tgt.x;
        int dx = (tgt.x>src.x)?tgt.x:src.x;      
        for(int r=ox; r<=dx; r++){
          point(r,src.y,layer,so);
        }      
      }
    }
  }

  private void editObject(MouseEvent e){
     try{
       SimObject s = map.getSimObject(x,y,layer); 
       if(s!=null){
         SimObjectWindow sow = new SimObjectWindow((Frame)this.getParent().getParent(),s);
         sow.setEdit(SimObjectWindow.ANGLE,true);
         sow.setEdit(SimObjectWindow.PARAMETERS,true);       
         sow.show();
         if(s.paramIsModified()){
           images[x][y][layer] = s.getIcon(0).getImage(MagesEnv.getFilesDir("simObjects"),true);                  
         }
         else{
           images[x][y][layer] = s.getIcon(0).getImage(MagesEnv.getFilesDir("simObjects"),false);                           
         }
         repaint();                      
       }  
     }
     catch(InterruptedException e1){
       ErrorMessage em = new ErrorMessage((Frame)owner,e1.getMessage());
       em.show();                 
     }  
  }
           
  private void putObject(MouseEvent e){
    int tool = owner.getTool();                   
    layer = owner.getLayer();
    SimObject so = null;            
    if(tool==0 || tool==3){
      so = owner.getSimObject();    
      if(so!=null){
        if(tool==0){                
          point(x,y,layer,so);             
        }
        else{
          erase(x,y,layer);                     
        }  
      }    
    }
    else{
      if(first){
        lx = x;
        ly = y;
        first = false;
      }
      else{
        so = owner.getSimObject();        
        if(so!=null){
          switch(tool){
            case 1: line(new Point(x,y),new Point(lx,ly),layer,so);          
            break;
            case 2: square(new Point(lx,ly),new Point(x,y),layer,so);             
            break;
          }
        }           
        lx = ly = -1;
        first = true;
      }    
    }
    repaint();
  }  
    
  public Object getObject(){
    return map;  
  }
  
  public void setObject(Object o){
    try{
      map = (Map)o;      
      for(int i=0; i<map.getNumLayers(); i++){
        for(int r=0; r<rows; r++){
          Layer ly = map.getLayer(i);
          for(int c=0; c<cols; c++){
            SimObject so = ly.getCell(c,r);
            if(so!=null){
              images[c][r][i] = so.getImage(MagesEnv.getFilesDir("simObjects"),0);                        
            }  
          }
        }   
      }    
       repaint();       
    }
    catch(InterruptedException e1){
      ErrorMessage em = new ErrorMessage((Frame)owner,e1.getMessage());
      em.show();                 
    }
  }
}
