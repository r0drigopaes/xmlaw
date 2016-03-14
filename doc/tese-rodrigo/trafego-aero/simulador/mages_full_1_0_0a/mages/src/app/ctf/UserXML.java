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
import mages.util.*;

import java.io.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;

public class UserXML extends XMLObject{
  
  String[] tags = {"usr.name","email","site","inst","country"};
                 
  public Document createDoc(Object obj) throws IOException{
      User u = (User)obj;

      DocumentImpl document = new DocumentImpl();  
      Element root = (Element) document.createElement("user");            
      
      Element[] elements = new Element[tags.length];
      for(int i = 0; i<elements.length; i++){
        elements[i] = (Element) document.createElement(tags[i]);            
      }
                     
      elements[0].appendChild(document.createTextNode(u.getName()));
      elements[1].appendChild(document.createTextNode(u.getEmail()));
      elements[2].appendChild(document.createTextNode(u.getWebsite()));
      elements[3].appendChild(document.createTextNode(u.getInstitution()));
      elements[4].appendChild(document.createTextNode(u.getCountry()));
      
      document.appendChild(root);      
      for(int j=0; j<elements.length;j++){
        root.appendChild(elements[j]);      
      }
    return document;                                   
  }

  public Object newInstance(Element root) throws IOException{   
    User u = new User();

    //All tags, any kind of tag there is in document
    NodeList list = root.getChildNodes();    
  
    for(int i=0; i<tags.length; i++){
      NodeList elementList = root.getElementsByTagName(tags[i]);
      Element element = (Element) elementList.item(0);
      NodeList tagsElementList = element.getChildNodes();
      Node text = tagsElementList.item(0);
      String textStr = text.getNodeValue();
      switch(i){
        case 0: u.setName(textStr);
        break;
        case 1: u.setEmail(textStr);
        break;
        case 2: u.setWebsite(textStr); 
        break;
        case 3: u.setInstitution(textStr);
        break;
        case 4: u.setCountry(textStr);
        break;
      }
    }       
    return u;
  }         
}