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

import java.util.*;
import java.awt.*;
import java.io.*;

public class IconsTable implements Serializable{
  public static final int NEW_INSTANCE = 0;
  public static final int LOAD_IMAGE = 1;  
  Hashtable table;
  Hashtable icons;
  Hashtable images;
  int count = 0;
  
  public IconsTable(){
    table = new Hashtable();
    icons = new Hashtable();    
    images = new Hashtable();
  }  
  
  public int addIcon(Icon icon){  
    Integer id = new Integer(count);
    
    if(!table.containsValue(icon)){           
      icon.setId(count);
      table.put(id,icon);
      icons.put(icon,icon);
      count++;    
      return id.intValue();      
    }
    else{
      return ((Icon)icons.get(icon)).getId();
    }
  }
  
  public Icon[] getIcons(){
    Icon[] tmp = new Icon[table.size()];
    
    for(int i=0; i<tmp.length; i++){
      tmp[i] = (Icon)table.get(new Integer(i));
    }
    return tmp;    
  }
  
  public Icon getIcon(int key){
    return (Icon)(table.get(new Integer(key)));
  }
  
  public Image getImage(String r, int id, int mode)  throws InterruptedException{
    Icon ic = getIcon(id);    
    if(mode==NEW_INSTANCE){
      return ic.getImage(r);
    }
    else{
      if(mode==LOAD_IMAGE){
         Image img = (Image)images.get(ic);
         if(img!=null){
           return img;
         }
         else{
           img = ic.getImage(r);
           images.put(ic,img);
           return img;
         }
      }
    }
    return null;
  }    
}