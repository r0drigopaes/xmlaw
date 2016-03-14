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

package mages.app.ctf;

import mages.app.*;
import mages.fuzion.*;

import java.io.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;

public class WeaponXML extends XMLObject{

  String[] tags = {"wpn.name","dc","rof","amno","range"};
  String[] attribs = {"pts","class","type","skill"};
  
  public Document createDoc(Object obj) throws IOException{
      Weapon w = (Weapon)obj;
      DocumentImpl document = new DocumentImpl();  
      
      Element root = document.createElement("weapon");
   
      for(int a=0; a<attribs.length; a++){
        Attr at = document.createAttribute(attribs[a]); 
        switch(a){
          case 0: at.setValue(Integer.toString(w.getCost()));
          break;
          case 1: at.setValue(w.getStrWeaponClass());
          break;
          case 2: at.setValue(w.getStrType());
          break;          
          case 3: at.setValue(w.getSkill());
          break;              
        }
        root.setAttributeNode(at);      
      }      

      Element[] elements = new Element[tags.length];
      for(int i = 0; i<elements.length; i++){
        elements[i] = (Element) document.createElement(tags[i]);            
      }

      elements[0].appendChild(document.createTextNode(w.getName()));
      elements[1].appendChild(document.createTextNode(Integer.toString(w.getDc())));
      elements[2].appendChild(document.createTextNode(Integer.toString(w.getRof())));
      elements[3].appendChild(document.createTextNode(Integer.toString(w.getAmno())));
      elements[4].appendChild(document.createTextNode(Integer.toString(w.getRange())));
  
      document.appendChild(root);      
      for(int j=0; j<elements.length;j++){
        root.appendChild(elements[j]);      
      }  
    return document;                                   
  }
  
  public Object newInstance(Element root) throws IOException{                     
    Weapon w = new Weapon();

    //All tags, any kind of tag there is in document
    NodeList list = root.getChildNodes();
    
    for(int a=0; a<attribs.length; a++){
      Attr at = root.getAttributeNode(attribs[a]);
      switch(a){
        case 0: w.setCost(Integer.parseInt(at.getValue()));
        break;      
        case 1: w.setStrWeaponClass(at.getValue());
        break;
        case 2: w.setStrType(at.getValue());
        break;                
        case 3: w.setSkill(at.getValue());
        break;         
      }          
    }   
           
    for(int i=0; i<tags.length; i++){
      NodeList elementList = root.getElementsByTagName(tags[i]);
      Element element = (Element) elementList.item(0);
      NodeList tagsElementList = element.getChildNodes();
      Node text = tagsElementList.item(0);
      String textStr = text.getNodeValue();
      switch(i){
        case 0: w.setName(textStr);
        break;
        case 1: w.setDc(Integer.parseInt(textStr));
        break;
        case 2: w.setRof(Integer.parseInt(textStr)); 
        break;
        case 3: w.setAmno(Integer.parseInt(textStr));
        break;
        case 4: w.setRange(Integer.parseInt(textStr));
        break;                                       
      }
    }               
    return w;
  }                                
}