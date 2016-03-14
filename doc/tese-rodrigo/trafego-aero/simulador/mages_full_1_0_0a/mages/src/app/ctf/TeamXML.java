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
import java.util.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;

public class TeamXML extends XMLObject{
 
  String[] tags = {"team.name","team.description"};
                           
  public Document createDoc(Object obj) throws IOException{  
    Team t = (Team)obj;
    DocumentImpl document = new DocumentImpl();  
    Element root = (Element) document.createElement("team");            
      
    Element[] elements = new Element[tags.length];
    for(int i = 0; i<elements.length; i++){
      elements[i] = (Element) document.createElement(tags[i]);            
    }
                     
    elements[0].appendChild(document.createTextNode(t.getName()));
    elements[1].appendChild(document.createTextNode(t.getDescription()));
      
    document.appendChild(root);      
    for(int j=0; j<elements.length;j++){
      root.appendChild(elements[j]);      
    }
    
    //Bots
    Vector bots = t.getBots();
    for(int i=0; i<bots.size(); i++){
      Bot bot = (Bot)bots.elementAt(i);
      BotXML bx = new BotXML();        
      Document docBot = bx.createDoc(bot);     
      Element newElem = (Element)document.importNode(docBot.getDocumentElement(),true);
      root.appendChild(newElem);       
    }
    return document;   
  }         
  
  public Object newInstance(Element root) throws IOException{
    Team team= new Team();

    //All tags, any kind of tag there is in document
    NodeList list = root.getChildNodes();    
  
    for(int i=0; i<tags.length; i++){
      NodeList elementList = root.getElementsByTagName(tags[i]);
      Element element = (Element) elementList.item(0);
      NodeList tagsElementList = element.getChildNodes();
      Node text = tagsElementList.item(0);
      String textStr = text.getNodeValue();
      switch(i){
        case 0: team.setName(textStr);
        break;
        case 1: team.setDescription(textStr);
        break;                                  
      }
    }       
    
    //Bots
    NodeList botsList = root.getElementsByTagName("bot");    
    for(int i=0; i<botsList.getLength();i++){
      Element be = (Element)botsList.item(i);
      BotXML bx = new BotXML();
      Bot bot = (Bot)bx.newInstance(be);
      team.addBot(bot);    
    }  
    return team;  
  }  
}