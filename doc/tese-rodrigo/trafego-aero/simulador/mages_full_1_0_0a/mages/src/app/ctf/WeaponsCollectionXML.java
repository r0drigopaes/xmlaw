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
import java.util.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;

public class WeaponsCollectionXML extends XMLObject{
                 
  public Document createDoc(Object obj) throws IOException{   
    DocumentImpl document = new DocumentImpl();
    WeaponsCollection wc = (WeaponsCollection)obj;

    Element root = (Element) document.createElement("weapons");             
    
    Enumeration wps = wc.getElements();    
    WeaponXML wx = new WeaponXML();

    while(wps.hasMoreElements()){                                                               
      Weapon w = (Weapon)wps.nextElement();
      Document docWeapon = wx.createDoc(w);
      Element e = (Element)document.importNode(docWeapon.getDocumentElement(),true);      
      root.appendChild(e);
    }
    document.appendChild(root); 
    return document;                                   
  }
  
  public Object newInstance(Element root) throws IOException{   
    WeaponsCollection wc = new WeaponsCollection();

    //All tags, any kind of tag there is in document
    NodeList list = root.getChildNodes();    
    
    WeaponXML wx = new WeaponXML();    
    NodeList weaponsList = root.getElementsByTagName("weapon");
    for(int i=0; i<weaponsList.getLength();i++){
      Element element = (Element) weaponsList.item(i);  
      if(element.getNodeName().equals("weapon")){                  
        Weapon weapon = (Weapon)wx.newInstance(element);              
        wc.addWeapon(weapon);   
      }  
    }          
    return wc;
  }                                
}