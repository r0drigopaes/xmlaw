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

public class ParameterXML extends XMLObject{
  
  String[] tags = {"name","value","default"};
                           
  public Document createDoc(Object obj) throws IOException{
      Parameter prm = (Parameter)obj;
      DocumentImpl document = new DocumentImpl();  
      Element root = (Element) document.createElement("param");            
      
      Attr[] attributes = new Attr[tags.length];
      for(int i = 0; i<attributes.length; i++){
        attributes[i] = (Attr) document.createAttribute(tags[i]);            
        switch(i){
          case 0: attributes[i].setValue(prm.getName());
                  root.setAttributeNode(attributes[i]);          
          break;
          case 1: attributes[i].setValue(prm.getValue());
                  root.setAttributeNode(attributes[i]);                    
          break;          
          case 2: 
            if(prm.isModified()){
              attributes[i].setValue(prm.getDefaultValue());
              root.setAttributeNode(attributes[i]);                        
            }   
          break;                    
        }        
      }
                                       
    document.appendChild(root);      
    return document;                                     
  }
  
  public Object newInstance(Element root) throws IOException{   
   
    String name = null;
    String value = null;
    String dvalue = null;
    for(int i=0; i<tags.length; i++){
      Attr a = root.getAttributeNode(tags[i]);
      if(a!=null){
        switch(i){
          case 0: name = a.getValue();
          break;
          case 1: value = a.getValue();
          break;          
          case 2: dvalue = a.getValue();
          break;                  
        }      
      }  
    }  
    
    Parameter prm = null;
    if(dvalue!=null)
      prm = new Parameter(name,value,dvalue);         
    else{
      prm = new Parameter(name,value);             
    }
    return prm;  
  }    
}