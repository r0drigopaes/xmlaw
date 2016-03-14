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


package mages;

import java.awt.*;

public class MagesEnv{
 
 public static String getRoot(){
   return System.getProperty("mages"); 
 }
 
 public static String getFileSeparator(){
   return System.getProperty("file.separator"); 
 }
 
 public static String getFilesDir(){   
   return getRoot() + getFileSeparator() +"files"+ getFileSeparator();
 }
 
 public static String getFilesDir(String dir){   
   String sep = getFileSeparator();
   return getRoot() +  sep +"files"+ sep+ dir + sep;
 }
  
 public static String getDir(String dir){
   return getRoot() + getFileSeparator() + dir + getFileSeparator(); 
 }

 public static String validate(String str){
   String sep = System.getProperty("file.separator");    
   if(!sep.equals("/")){
     return str.replace('/',sep.charAt(0));
   }
   return str;
  } 
  
  public static Image loadImage(String s){
    Button tmp= new Button();  
    Image img = Toolkit.getDefaultToolkit().getImage(s);              
    try{
      MediaTracker tracker = new MediaTracker(tmp);
      tracker.addImage(img, 0);
      tracker.waitForID(0);
    }
    catch (Exception e) {}  
    return img;
  }  
}