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

public class SimObjectXML extends XMLObject{
  
  String[] tags = {"name","x","y"};
                           
  public Document createDoc(Object obj) throws IOException{
      SimObject so = (SimObject)obj;
      DocumentImpl document = new DocumentImpl();  
      Element root = (Element) document.createElement("simobj");            
      
      Attr[] attributes = new Attr[tags.length];
      for(int i = 0; i<attributes.length; i++){
        attributes[i] = (Attr) document.createAttribute(tags[i]);            
        switch(i){
          case 0: attributes[i].setValue(so.getName());
          break;
          case 1: attributes[i].setValue(Integer.toString(so.getX()));
          break;          
          case 2: attributes[i].setValue(Integer.toString(so.getY()));
          break;
        }
      }                                       
      document.appendChild(root);      
      for(int j=0; j<attributes.length;j++){
        root.setAttributeNode(attributes[j]);      
      }
         
      if(so.getNumLinks()!=0){      
        //Links
        int[] links = so.getLinks();            
        for(int i=0; i<links.length; i++){
           Element e = (Element)document.createElement("link");   
           e.appendChild(document.createTextNode(Integer.toString(links[i])));                                
           root.appendChild(e);
        }        
      }
      else{
        //Icons
        Icon[] icons = so.getIcons();
        IconXML ix = new IconXML();            
        for(int i=0; i<icons.length; i++){
          Document doc = ix.createDoc(icons[i]);        
          Element e = (Element)document.importNode(doc.getDocumentElement(),true);
          root.appendChild(e);                   
        }                  
      }      
      
      //Parameters
      Parameter[] params = so.getParameters();
      ParameterXML px = new ParameterXML();            
      for(int p=0; p<params.length; p++){
        Document doc = px.createDoc(params[p]);        
        Element e = (Element)document.importNode(doc.getDocumentElement(),true);
        root.appendChild(e);        
      }                 
      return document;  
  }
  
  public Object newInstance(Element root) throws IOException{   
    SimObject so = new SimObject();

    int x = 0;
    int y = 0;
    for(int i=0; i<tags.length; i++){
      Attr a = root.getAttributeNode(tags[i]);      
      switch(i){
        case 0: so.setName(a.getValue());
        break;
        case 1: 
         if(a!=null){
           x = Integer.parseInt(a.getValue());
         }  
         else{
           x = -1;
         }
        break;          
        case 2: 
          if(a!=null){
            y = Integer.parseInt(a.getValue());
          }  
          else{
            y = -1;
          }
          so.setPosition(x,y);
        break;                          
      }      
    }           
    
    NodeList icons = root.getElementsByTagName("icon");      
    NodeList links = root.getElementsByTagName("link"); 
    if(icons.getLength()!=0){
      //Icons    
      IconXML ix = new IconXML();    
      for(int i=0; i<icons.getLength(); i++){
        Element e = (Element)icons.item(i);      
        Icon icon = (Icon)ix.newInstance(e);
        so.addIcon(icon);
      }    
    }
    else{
      if(links.getLength()!=0){
        //Links
        for(int i=0; i<links.getLength(); i++){
          Element e = (Element)links.item(i);                
          if(e!=null){            
            NodeList tagsElem = e.getChildNodes();
            Node text = tagsElem.item(0);
            String textStr = text.getNodeValue();
            if(!so.hasLink(Integer.parseInt(textStr))){
              so.addLink(Integer.parseInt(textStr));
            }  
          }          
        }            
      }      
    }        
    
    //Parameters
    NodeList params = root.getElementsByTagName("param");      
    if(params.getLength()!=0){
      ParameterXML px = new ParameterXML();    
      for(int p=0; p<params.getLength(); p++){
        Element e = (Element)params.item(p);      
        Parameter param = (Parameter)px.newInstance(e);
        so.addParameter(param);
      }                
    }  
    return so;  
  }    
}