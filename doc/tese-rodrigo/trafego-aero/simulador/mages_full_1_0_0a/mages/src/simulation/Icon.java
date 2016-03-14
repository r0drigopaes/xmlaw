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

package mages.simulation;

import mages.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import nu.bugbase.gamelib.sprite.*; 
import nu.bugbase.gamelib.utils.*; 

public class Icon implements Serializable{ 
  public static final int IMAGE_TYPE = 0;
  public static final int SPRITE_TYPE = 1;  

  public static final int ANGLE_0 = 0;  
  public static final int ANGLE_90 = 1;    
  public static final int ANGLE_180 = 2;    
  public static final int ANGLE_MINUS_90 = 3;    
  
  String path;
  int type;
  int angle;
  int width;
  int height;  
  String texture;
  boolean useText = false;
  int id = -1;
  Button c = new Button();
  
  public Icon(){
    path = null;
    texture = null;    
    type = IMAGE_TYPE;
    angle = ANGLE_0;
    width = -1;
    height = -1;    
  }
  
  public void setPath(String p){
    path = p;
  }

  public String getPath(){
    return path;
  }

  public void setType(int t){
    type = t;
  }
  
  public void setType(String t){
    if(t.equals("SPRITE")){
      type = SPRITE_TYPE;
    }
    else{
      type = IMAGE_TYPE;
    }  
  }

  public int getType(){
    return type;
  }
  
  public String getTypeName(){
    if(type==SPRITE_TYPE){
      return "SPRITE";
    }
    return "IMG";  
  }  

  public void setAngle(int a){
    angle = a;
  }

  public void setAngle(String a){
    if(a.equals("0")){
      angle = ANGLE_0;
    }
    else{
     if(a.equals("90")){
       angle = ANGLE_90;
     }
     else{
       if(a.equals("180")){       
         angle = ANGLE_180;
       }
       else{
         if(a.equals("-90")){
           angle = ANGLE_MINUS_90;
         }         
       }
     }
    }
  }
  
  public int getAngle(){
    return angle;
  }

  public String getAngleName(){
    String tmp = new String();
    switch(angle){
      case ANGLE_0: tmp = "0";        
      break;
      case ANGLE_90: tmp = "90";
      break;
      case ANGLE_180: tmp = "180";
      break;
      case ANGLE_MINUS_90: tmp = "-90";
      break;                  
    }
    return tmp;
  }
  
  public void setWidth(int w){
    width = w;
  }

  public int getWidth(){
    return width;
  }

  public void setHeight(int h){
    height = h;
  }

  public int getHeight(){
    return height;
  }  

  public void setTexture(String t){
    texture = t;
  }

  public String getTexture(){
    return texture;
  }

  private Image getImageTxtr(String r){
    return Toolkit.getDefaultToolkit().getImage(r+texture);                  
  }
    
  private int changeColor(int src, int txtr){
    Color colorSrc = new Color(src);
    Color colorTxtr = new Color(txtr);    
    
    float fred = (float)colorTxtr.getRed()/255.0f;
    float fgreen = (float)colorTxtr.getGreen()/255.0f;
    float fblue = (float)colorTxtr.getBlue()/255.0f;
            
    int red = (int)(fred*colorSrc.getRed());
    int green = (int)(fgreen*colorSrc.getGreen());
    int blue = (int)(fblue*colorSrc.getBlue());        

    Color colorTarget = new Color(red,green,blue);    
    return colorTarget.getRGB();
  }
  
  private Image texturize(Image src, String r) throws InterruptedException{   
    Image txtr = getImageTxtr(r);   
    if(txtr!=null){
      int[] pixelsTgt = new int[width*height];
      int[] pixelsSrc = new int[width*height]; 
      int[] textSrc = new int[width*height];    
      PixelGrabber grb = new PixelGrabber(src,0,0,width,height,pixelsSrc,0,width);
      grb.grabPixels();
      grb = new PixelGrabber(getImageTxtr(r),0,0,width,height,textSrc,0,width);
      grb.grabPixels();    


      for(int i=0; i<pixelsSrc.length; i++){
        pixelsTgt[i] = changeColor(pixelsSrc[i],textSrc[i]);
      }
      MemoryImageSource source = new MemoryImageSource(width,height,pixelsTgt,0,width);    
      Frame f = new Frame();      
      return f.createImage(source);      
    }
    return src;  
  }
  

    
  public Image getImage(String root, boolean withTexture) throws InterruptedException{
    Image img = loadImage(root+MagesEnv.validate(path));
    if(withTexture){
      img = texturize(img,root);
    }

    if(angle==ANGLE_0){
      return img;
    }
    else{   
      return rotation(img);
    }    
  }
  
  public Object clone(){
    Icon i = new Icon();
    String p = new String(getPath());
    i.setPath(p);
    i.setType(getType());
    i.setAngle(getAngle());
    i.setWidth(getWidth());
    i.setHeight(getHeight());
    i.setTexture(getTexture());
    i.setUseTexture(getUseTexture());
    return i;    
  }
  
  private Image rotation(Image tmp) throws InterruptedException{
    if(width==height){
      for(int i=0; i<angle; i++){
        tmp = rotate(tmp);
      }
      return tmp;
    }
    else{
      throw new InterruptedException("This icon is impossible rotate");
    }
  }
    
  private Image rotate(Image img) throws InterruptedException{
    int[] pixelsTgt = new int[width*height];
    int[] pixelsSrc = new int[width*height]; 
    PixelGrabber grb = new PixelGrabber(img,0,0,width,height,pixelsSrc,0,width);
    grb.grabPixels();    
    
    int[] initial = new int[height];
    int start = width-1;
    for(int i=0; i<initial.length; i++){
      initial[i] = start;
      start += width;
    }

    int pointer = 0;    
    for(int s=0; s<pixelsSrc.length; s++){
      pixelsTgt[initial[pointer++]] = pixelsSrc[s];
      if(pointer % width == 0){
        pointer = 0;
        for(int m=0; m<initial.length; m++){
          initial[m]--;
        }
      }
    }

    MemoryImageSource source = new MemoryImageSource(width,height,pixelsTgt,0,width);    
    Frame f = new Frame();
    
    return f.createImage(source);
  }  
  
  public void setUseTexture(boolean b){
    useText = b;
  }
  
  public boolean getUseTexture(){
    return useText;
  }
  
  //gamelib code  
  private Image loadImage(String s){
    Image img = Toolkit.getDefaultToolkit().getImage(s);              
    try{
      MediaTracker tracker = new MediaTracker(c);
      tracker.addImage(img, 0);
      tracker.waitForID(0);
    }
    catch (Exception e) {}  
    return img;
  }

  public Image getImage(String root) throws InterruptedException{
    Image img = loadImage(root+MagesEnv.validate(path));
    if(useText){
      img = texturize(img,root);
    }

    if(angle==ANGLE_0){
      return img;
    }
    else{   
      return rotation(img);
    }  
  }  
  
  public boolean equals(Object o){
    try{
      Icon i = (Icon)o;
      boolean f1 =(getPath().equals(i.getPath()))?true:false;
      boolean f2 =(getType() == i.getType())?true:false;
      boolean f3 =(getAngle() == i.getAngle())?true:false;
      boolean f4 =(getWidth() == i.getWidth())?true:false;
      boolean f5 =(getHeight() == i.getHeight())?true:false;                        
      boolean f6 =(getUseTexture() == i.getUseTexture())?true:false;      
      return (f1&&f2&&f3&&f4&&f5&&f6)?true:false;
    }
    catch(ClassCastException e){
      return false;
    }
  }
  
  public void setId(int i){
    id = i;
  }
  
  public int getId(){
    return id;
  }  
  
  public int hashCode(){
    String tmp = getPath()+"_"+getAngleName()+"_"+getWidth()+"_"+getHeight()+"_"+useText;
    return tmp.hashCode();
  }
}