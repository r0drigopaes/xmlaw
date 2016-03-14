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
import mages.domain.ctf.*;

import java.io.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;

public class TournamentXML extends XMLObject{
  
  String[] tags = {"tourn.name","tourn.description","mbt","mbp","mfl","mtm"};
                           
  public Document createDoc(Object obj) throws IOException{
      Tournament t = (Tournament)obj;
      DocumentImpl document = new DocumentImpl();  
      Element root = (Element) document.createElement("tournament");            
      
      Element[] elements = new Element[tags.length];
      for(int i = 0; i<elements.length; i++){
        elements[i] = (Element) document.createElement(tags[i]);            
      }
                     
      elements[0].appendChild(document.createTextNode(t.getName()));
      elements[1].appendChild(document.createTextNode(t.getDescription()));
      elements[2].appendChild(document.createTextNode(Integer.toString(t.getMaxBotsForTeam())));
      elements[3].appendChild(document.createTextNode(Integer.toString(t.getMaxBotsPtos())));
      elements[4].appendChild(document.createTextNode(Integer.toString(t.getMaxFlag())));
      elements[5].appendChild(document.createTextNode(Integer.toString(t.getMaxTeams())));                              
      
      document.appendChild(root);      
      for(int j=0; j<elements.length;j++){
        root.appendChild(elements[j]);      
      }
    return document;                                     
  }
  
  public Object newInstance(Element root) throws IOException{   
    Tournament tourn = new Tournament();

    //All tags, any kind of tag there is in document
    NodeList list = root.getChildNodes();    
  
    for(int i=0; i<tags.length; i++){
      NodeList elementList = root.getElementsByTagName(tags[i]);
      Element element = (Element) elementList.item(0);
      NodeList tagsElementList = element.getChildNodes();
      Node text = tagsElementList.item(0);
      String textStr = text.getNodeValue();
      switch(i){
        case 0: tourn.setName(textStr);
        break;
        case 1: tourn.setDescription(textStr);
        break;
        case 2: tourn.setMaxBotsForTeam(Integer.parseInt(textStr)); 
        break;
        case 3: tourn.setMaxBotsPtos(Integer.parseInt(textStr));
        break;
        case 4: tourn.setMaxFlag(Integer.parseInt(textStr));
        break;
        case 5: tourn.setMaxTeams(Integer.parseInt(textStr));
        break;                                        
      }
    }       
    return tourn;  
  }    
}