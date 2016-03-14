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
import org.apache.xml.serialize.*;

public class MapSchemaXML extends XMLObject{
  
  String[] tags = {"mapsch.name","mapsch.validator"};
  String[] attribs = {"layer","type","path","row","col","index"};  
                 
  public Document createDoc(Object obj) throws IOException{
      MapSchema ms = (MapSchema)obj;

      DocumentImpl document = new DocumentImpl();  
      Element root = (Element) document.createElement("mapschema");            
      
      Element[] elements = new Element[tags.length];
      for(int i = 0; i<elements.length; i++){
        elements[i] = (Element) document.createElement(tags[i]);            
      }
                     
      elements[0].appendChild(document.createTextNode(ms.getName()));
      elements[1].appendChild(document.createTextNode(ms.getValidatorName()));      
      
      document.appendChild(root);      
      for(int j=0; j<elements.length;j++){
        root.appendChild(elements[j]);      
      }
      
      //Entry
      MapSchemaEntry[] mse = ms.getMapSchemaEntry();
      for(int k=0; k<mse.length; k++){
        Element e = document.createElement("mapsch.entry");
        
        for(int m=0; m<attribs.length; m++){
          Attr a = document.createAttribute(attribs[m]);            
          switch(m){
            case 0:a.setValue(Integer.toString(mse[k].getLayer()));
            break;
            case 1:a.setValue(mse[k].getTypeName());
            break;            
            case 2:a.setValue(mse[k].getPath());
            break;            
            case 3:a.setValue(Integer.toString(mse[k].getRows()));
            break;            
            case 4:a.setValue(Integer.toString(mse[k].getCols()));
            break;                                                
            case 5:Boolean b = new Boolean(mse[k].isUsedIndex());
                   a.setValue(b.toString());
            break;            
          }                  
          e.setAttributeNode(a);                
        }                                
        root.appendChild(e);              
      }
    return document;                                   
  }

  public Object newInstance(Element root) throws IOException{    
    MapSchema ms = null;
    //All tags, any kind of tag there is in document
    NodeList list = root.getChildNodes();    

    String name = null;
    String val = null;    
    String textStr = null;    
    int nlayer = 0;
    for(int i=0; i<tags.length; i++){  
      NodeList elemList = root.getElementsByTagName(tags[i]);
      Element elem = (Element) elemList.item(0);
      if(elem!=null){            
        NodeList tagsElem = elem.getChildNodes();
        Node text = tagsElem.item(0);
        textStr = text.getNodeValue();
      }
      switch(i){
         case 0: name = textStr;         
         break;
         case 1: val = textStr;         
         break;         
      }
    }        
    ms = new MapSchema(name);       
    ms.setValidatorName(val);
    

    //Entry
    NodeList entries = root.getElementsByTagName("mapsch.entry");
    for(int i=0; i<entries.getLength(); i++){
      Element e = (Element)entries.item(i);
      int layer = 0;
      String type = null;
      String path = null;     
      String r = null;
      String c = null;     
      String index = null;
      for(int j=0; j<attribs.length; j++){
        Attr a = e.getAttributeNode(attribs[j]);
        String textStr2 = null;        
        if(a!=null){      
          textStr2 = a.getValue();
        }  
        switch(j){
          case 0: layer = Integer.parseInt(textStr2);
          break;
          case 1: type = textStr2;
          break;
          case 2: path = textStr2;
          break;
          case 3: r = textStr2;
          break;
          case 4: c = textStr2;  
          break;                                    
          case 5: 
                  index = textStr2;
                  MapSchemaEntry mse = new MapSchemaEntry(layer,0,path);      
                  mse.setTypeName(type);
                  if(r!=null){
                    mse.setRows(Integer.parseInt(r));
                  }  
                  if(c!=null){
                    mse.setCols(Integer.parseInt(c));                  
                  }          
                  if(index!=null){
                    mse.setUseIndex((Boolean.valueOf(index)).booleanValue());                  
                  }                  
                  ms.setMapSchemaEntry(mse);                                         
          break;
        }               
      }
    }
    return ms;
  }         
}