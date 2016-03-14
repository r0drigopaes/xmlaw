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
import mages.app.*;

import java.io.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;

public class IconXML extends XMLObject{
  
  String[] tags = {"path","type","angle","width","height","texture","useTextr"};
                           
  public Document createDoc(Object obj) throws IOException{
      Icon icon = (Icon)obj;
      DocumentImpl document = new DocumentImpl();  
      Element root = (Element) document.createElement("icon");            
      
      Attr[] attributes = new Attr[tags.length];
      for(int i = 0; i<attributes.length; i++){
        attributes[i] = (Attr) document.createAttribute(tags[i]);            
        switch(i){
          case 0: attributes[i].setValue(icon.getPath());
          break;
          case 1: attributes[i].setValue(icon.getTypeName());
          break;          
          case 2: attributes[i].setValue(icon.getAngleName());
          break;
          case 3: attributes[i].setValue(Integer.toString(icon.getWidth()));
          break;
          case 4: attributes[i].setValue(Integer.toString(icon.getHeight()));
          break;                              
          case 5: attributes[i].setValue(icon.getTexture());
          break;                                        
          case 6: Boolean b = new Boolean(icon.getUseTexture());
                  attributes[i].setValue(b.toString());
          break;                                                  
        }
      }                                       
      document.appendChild(root);      
      for(int j=0; j<attributes.length;j++){
        root.setAttributeNode(attributes[j]);      
      }
    return document;                                     
  }
  
  public Object newInstance(Element root) throws IOException{   
    Icon icon = new Icon();

    for(int i=0; i<tags.length; i++){
      Attr a = root.getAttributeNode(tags[i]);
      switch(i){
        case 0: icon.setPath(a.getValue());
        break;
        case 1: 
          if(a!=null){
            icon.setType(a.getValue());
          } 
        break;          
        case 2: 
          if(a!=null){
            icon.setAngle(a.getValue());
          }
        break;          
        case 3: 
          if(a!=null){
            icon.setWidth(Integer.parseInt(a.getValue()));
          } 
        break;          
        case 4: 
          if(a!=null){
            icon.setHeight(Integer.parseInt(a.getValue()));
          }        
        break;                                  
        case 5: 
          if(a!=null){
            icon.setTexture(a.getValue());
          }        
        break;                                          
        case 6:
          if(a!=null){
			   icon.setUseTexture((Boolean.valueOf(a.getValue())).booleanValue());
          }
        break;
      }      
    }             
    return icon;  
  }    
}