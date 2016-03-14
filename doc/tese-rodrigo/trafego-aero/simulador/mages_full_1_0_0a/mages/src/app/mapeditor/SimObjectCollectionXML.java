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

public class SimObjectCollectionXML extends XMLObject{
  
  String[] tags = {"name","col.id"};
                           
  public Document createDoc(Object obj) throws IOException{
      SimObjectCollection soc = (SimObjectCollection)obj;
      DocumentImpl document = new DocumentImpl();  
      Element root = (Element) document.createElement("simobjcol");            
      
      Attr[] attributes = new Attr[tags.length];
      for(int i = 0; i<attributes.length; i++){
        attributes[i] = (Attr) document.createAttribute(tags[i]);            
        switch(i){
          case 0: attributes[i].setValue(soc.getName());
          break;
          case 1: attributes[i].setValue(soc.getId());
          break;          
        }
      }                                       
      document.appendChild(root);      
      for(int j=0; j<attributes.length;j++){
        root.setAttributeNode(attributes[j]);      
      }
      
      //SimObject
      SimObject[] sos = soc.getSimObjects();
      SimObjectXML sox = new SimObjectXML();            
      for(int s=0; s<sos.length; s++){
        Document doc = sox.createDoc(sos[s]);        
        Element e = (Element)document.importNode(doc.getDocumentElement(),true);
        root.appendChild(e);        
      }                        
      return document;                                     
  }
  
  public Object newInstance(Element root) throws IOException{   
    SimObjectCollection soc = null;  
    String name = null;
    String id = null;    
    for(int i=0; i<tags.length; i++){
      Attr a = root.getAttributeNode(tags[i]);
      switch(i){
        case 0: name = a.getValue();
        break;
        case 1: id = a.getValue();
          soc = new SimObjectCollection(name,id);        
        break;                                
      }      
    }           
            
    NodeList sos = root.getElementsByTagName("simobj");      
    SimObjectXML sox = new SimObjectXML();    
    for(int s=0; s<sos.getLength(); s++){
      Element e = (Element)sos.item(s);      
      SimObject so = (SimObject)sox.newInstance(e);         
      soc.addSimObject(so);      
    }      
    return soc;  
  }    
}