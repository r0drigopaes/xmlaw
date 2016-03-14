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

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;

import java.io.File;

public class Extension extends FileFilter{
  private String description, extension;
    
  public Extension(String e, String d){
    extension = e.toLowerCase();
    description = d;
  }
  
  public boolean accept(File f){
    if(f.isDirectory()){
      return true;
    }
    
    String name = f.getName();
    String ext = name.substring(name.indexOf('.')+1,name.length());
    ext = ext.toLowerCase();
    if(extension!=null){
      if(ext.equals(extension)){
       return true;
      }
      else{
        return false;
      }      
    }  
    return false;    
  }
  
  public String getDescription(){
    return description;   
  }

  public String getExtension(){
    return extension;
  }
    
  public void setDescription(String d){
    description = d;
  }  
  
  public static String validate(String path, String ext){
    if(path.endsWith("."+ext)){
      return path;
    }
    else{
      return path.concat("."+ext);
    }
  }
}