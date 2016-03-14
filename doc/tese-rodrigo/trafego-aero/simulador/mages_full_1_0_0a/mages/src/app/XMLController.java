/*
GNU MAGES - Multi AGents Environment Simulator
Copyright (C) 2001-2002 João Ricardo Bittencourt <jrbitt@netu.unisinos.br>
 
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

package mages.app;

import mages.app.*;
//import mages.domain.ctf.*;
import org.apache.xerces.parsers.DOMParser; 


import java.io.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;


/**
* This class is responsable to create and load XML files.
* @author João Ricardo Bittencourt
*/
public class XMLController{
    
  public static void save(Object obj, XMLObject xobj, String name, String dtd) throws IOException{   
    XMLFormatter.write(xobj.createDoc(obj),name,dtd);       
  }  
  
  public static Object load(XMLObject xobj, String name) throws IOException{
    Object obj = null;      
    try{     
      obj = xobj.newInstance(XMLFormatter.read(name));    
    }
    catch(SAXException e){
       throw new IOException(e.getMessage());
    }  
    catch(ParserConfigurationException e0){
       throw new IOException(e0.getMessage());
    }      
    return obj;    
  }   
}