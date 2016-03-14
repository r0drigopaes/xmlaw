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
import mages.domain.ctf.*;
import mages.fuzion.*;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;

public class BotXML extends XMLObject{       
  
  String[] tags = {"header","features","hits","fat","rec","bot.name","bot.description","date","controller","month","day","year","features","weapons","user","armor"};
  
  public Document createDoc(Object obj) throws IOException{
      Bot bot = (Bot)obj;
      User user = bot.getUser();
      Armor armor = bot.getArmor();
      WeaponsCollection weapons = new WeaponsCollection();
      weapons.setWeapons(bot.getWeapons());
      
      DocumentImpl document = new DocumentImpl();  
      Element root = (Element) document.createElement("bot");            

      Attr bp = document.createAttribute("bp");            
      bp.setValue(Float.toString(bot.getBotPtos()));        
      root.setAttributeNode(bp);
     
      if(bot.getNumId()!=-1){
        Attr botid = document.createAttribute("bot.id");            
        botid.setValue(Integer.toString(bot.getNumId()));        
        root.setAttributeNode(botid);
      }  
           
      Element[] elements = new Element[tags.length];
      for(int i = 0; i<elements.length; i++){
        elements[i] = (Element) document.createElement(tags[i]);            
      }
                      
      //Header      
      elements[5].appendChild(document.createTextNode(bot.getName()));
      elements[6].appendChild(document.createTextNode(bot.getDescription())); 
      elements[8].appendChild(document.createTextNode(bot.getControlName()));                                    
      Calendar date = new GregorianCalendar();
      date.setTime(bot.getDate());     
      elements[9].appendChild(document.createTextNode(Integer.toString(date.get(Calendar.MONTH)+1)));            
      elements[10].appendChild(document.createTextNode(Integer.toString(date.get(Calendar.DATE))));            
      elements[11].appendChild(document.createTextNode(Integer.toString(date.get(Calendar.YEAR))));                        
            
      //Features - ATTR
      Feature[] atts = bot.getAttributes();
      Attr type = document.createAttribute("type");            
      type.setValue("ATTR");        
      elements[1].setAttributeNode(type);      
      for(int i=0; i<atts.length;i++){
        Element e =(Element) document.createElement("feature");
        Attr name = document.createAttribute("name");            
        name.setValue(atts[i].getName());
        e.setAttributeNode(name);
        
        Attr level = document.createAttribute("level");            
        level.setValue(Integer.toString(atts[i].getLevel()));
        e.setAttributeNode(level);        
        
        Attr pts = document.createAttribute("pts");            
        pts.setValue(Integer.toString(atts[i].getCost()));        
        e.setAttributeNode(pts);        
        
        elements[1].appendChild(e);              
      }

      //Features - SKILL
      Feature[] skills = bot.getSkills();
      Attr type2 = document.createAttribute("type");            
      type2.setValue("SKILL");              
      elements[12].setAttributeNode(type2);      
      for(int i=0; i<skills.length;i++){
        Element e =(Element) document.createElement("feature");
        Attr name = document.createAttribute("name");            
        name.setValue(skills[i].getName());
        e.setAttributeNode(name);
        
        Attr level = document.createAttribute("level");            
        level.setValue(Integer.toString(skills[i].getLevel()));
        e.setAttributeNode(level);        
        
        Attr pts = document.createAttribute("pts");            
        pts.setValue(Integer.toString(skills[i].getCost()));        
        e.setAttributeNode(pts);        
        
        elements[12].appendChild(e);              
      }
              
              
      //Hits        
      Attr level = document.createAttribute("level");            
      level.setValue(Integer.toString(bot.getHits()));
      elements[2].setAttributeNode(level);        
        
      Attr add = document.createAttribute("add");            
      add.setValue(Integer.toString(bot.getAddHits()));
      elements[2].setAttributeNode(add);
              
      Attr pts = document.createAttribute("pts");            
      pts.setValue(Integer.toString(bot.getPointsHits()));        
      elements[2].setAttributeNode(pts);        
                    
      //Fat        
      Attr level2 = document.createAttribute("level");            
      level2.setValue(Double.toString(bot.getFat()));
      elements[3].setAttributeNode(level2);        
        
      Attr add2 = document.createAttribute("add");            
      add2.setValue(Integer.toString(bot.getAddFat()));
      elements[3].setAttributeNode(add2);
              
      Attr pts2 = document.createAttribute("pts");            
      pts2.setValue(Integer.toString(bot.getPointsFat()));        
      elements[3].setAttributeNode(pts2);        
                          
      //Rec
      Attr rec = document.createAttribute("level");            
      rec.setValue(Double.toString(bot.getRec()));        
      elements[4].setAttributeNode(rec);        
                                      
      //--- Create tree ---  
      //User
      UserXML ux = new UserXML();
      Document docUser = ux.createDoc(user);
      elements[14] = (Element)document.importNode(docUser.getDocumentElement(),true);
      root.appendChild(elements[14]);      
          
      //Date
      elements[7].appendChild(elements[9]);      
      elements[7].appendChild(elements[10]);      
      elements[7].appendChild(elements[11]);      
                        
      //Header
      elements[0].appendChild(elements[5]);
      elements[0].appendChild(elements[6]);      
      elements[0].appendChild(elements[7]);            
      elements[0].appendChild(elements[8]);                  
                  
      //Bot
      root.appendChild(elements[0]);                  
      root.appendChild(elements[1]); //Features                       
      root.appendChild(elements[12]);//Features       
      root.appendChild(elements[2]);                                         
      root.appendChild(elements[3]);                                               
      root.appendChild(elements[4]);                                               

      //Armor
      ArmorXML ax = new ArmorXML();
      Document docArmor = ax.createDoc(armor);
      elements[15] = (Element)document.importNode(docArmor.getDocumentElement(),true);
      root.appendChild(elements[15]);      
      
      //Weapons
      WeaponsCollectionXML wcx = new WeaponsCollectionXML();
      WeaponXML wx = new WeaponXML();     
      Document docWc = wcx.createDoc(weapons);
      elements[13] = (Element)document.importNode(docWc.getDocumentElement(),true);
      root.appendChild(elements[13]);            
            
      //Root
      document.appendChild(root);          
    return document;                                   
  }
  

  
  public Object newInstance(Element root) throws IOException{      
    Bot bot = null;   
    
    NodeList list = root.getChildNodes();    
    Attr at = root.getAttributeNode("bot.id");
    
    if(at==null){
      bot = new Bot();   
    }
    else{
      int id = Integer.parseInt(at.getValue());
      bot = new Bot(id);         
    }  

    //User
    NodeList ul = root.getElementsByTagName("user");
    Element ue = (Element)ul.item(0);
    UserXML ux = new UserXML();
    User user = (User)ux.newInstance(ue);
    bot.setUser(user);

    //Header
    NodeList hl = root.getElementsByTagName("header");
    Element he = (Element)hl.item(0);    
    NodeList tagsHeader = he.getChildNodes();          

    for(int i=1; i<tagsHeader.getLength(); i+=2){
      Element e = (Element)tagsHeader.item(i);
      switch(i){
        case 1:
          bot.setName(XMLFormatter.getText(e));
        break;

        case 3:
          bot.setDescription(XMLFormatter.getText(e));
        break;

        case 5:
          NodeList tags = e.getChildNodes();
          String day,month,year;
          for(int j=1; j<tags.getLength(); j+=2){
            Element e1 = (Element)tags.item(j); 
            switch(j){
              case 1: month = XMLFormatter.getText(e1);
              break;
              case 3: day = XMLFormatter.getText(e1);
              break;
              case 5: year = XMLFormatter.getText(e1);
              break;                            
            }
          }
        break;

        case 7:
          if(e!=null){
            bot.setControlName(XMLFormatter.getText(e));
          }  
        break;                        
      }
    }  
    
      //Features
      NodeList fl = root.getElementsByTagName("features"); 
      //Atributes
      Element attris = (Element)fl.item(0);
      NodeList attributes = attris.getChildNodes();
      for(int k=1; k<attributes.getLength(); k+=2){
        Element attrb = (Element) attributes.item(k);
        Attr name = attrb.getAttributeNode("name");        
        Attr level = attrb.getAttributeNode("level");
        bot.setAttribute(name.getValue(),Integer.parseInt(level.getValue()));
      }
      
      //Skills
      Element sks = (Element)fl.item(1);      
      NodeList skills = sks.getChildNodes();
      for(int l=1; l<skills.getLength(); l+=2){
        Element skill = (Element) skills.item(l);
        Attr name = skill.getAttributeNode("name");        
        Attr level = skill.getAttributeNode("level");
        bot.setSkill(name.getValue(),Integer.parseInt(level.getValue()));
      }

      //Hits
      NodeList htl = root.getElementsByTagName("hits");       
      Element hits = (Element)htl.item(0);
      Attr level = hits.getAttributeNode("level");      
      bot.setHits(Integer.parseInt(level.getValue()));
      
      //Fat
      NodeList ftl = root.getElementsByTagName("fat");       
      Element fat = (Element)ftl.item(0);
      Attr level2 = fat.getAttributeNode("level");      
      bot.setFat(Double.parseDouble(level2.getValue()));

      //Armor
      NodeList al = root.getElementsByTagName("armor");
      Element ae = (Element)al.item(0);
      ArmorXML ax = new ArmorXML();      
      Armor armor = (Armor)ax.newInstance(ae);
      bot.setArmor(armor);      

      //Weapons      
      NodeList wl = root.getElementsByTagName("weapons");
      Element we = (Element)wl.item(0);
      WeaponsCollectionXML wcx = new WeaponsCollectionXML();      
      WeaponsCollection wc = (WeaponsCollection)wcx.newInstance(we);
      bot.setWeapons(wc);              
    return bot;
  }             
}
