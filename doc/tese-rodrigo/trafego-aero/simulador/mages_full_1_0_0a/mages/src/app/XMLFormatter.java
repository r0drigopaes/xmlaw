
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

import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;

/**
* This class is responsable to read and write XML files.
* @author João Ricardo Bittencourt
*/
public class XMLFormatter{

  /**
  * Read a XML document.
  * @param name is path to file that has XML tags.
  * @return a formated Document (node structure)
  */            
  public static Document read(String name) throws SAXException, IOException,ParserConfigurationException{  
    System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");                
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(name);
    return document;   
  }
     
  /**
  * Write an object in XML.
  * @param node is the Documente with all XML tags.
  * @param name is path to save to file with XML tags.  
  * @param dtd is DTD's name.  
  * @throws IOException if it was possible to create file  
  */                 
  public static void write(Document node, String name, String dtd) throws IOException{
    try{
      FileWriter  out = new FileWriter(name);
      out.flush();

      OutputFormat o = new OutputFormat(node);
      o.setIndent(5);
      o.setIndenting(true);
    
      o.setDoctype(null,dtd);      
      XMLSerializer X = new XMLSerializer(o);           
      X.setOutputCharStream(out);
      X.serialize(node); 
      out.flush();
      out.close();
    }
    catch(Exception e){
      throw new IOException(e.getMessage());
    }  
  }
    
  /**
  * Returns element's text. For example, <name>Joe</name> this method
  * returns "Joe"
  * @param e is element with text.
  * @return text value's element
  */                     
  public static String getText(Element e){     
    NodeList tags = e.getChildNodes();
    Node n = (Text)tags.item(0);
    if(n==null){
      return new String();
    }
    else{
      return n.getNodeValue();    
    }              
  }  
}