
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

import org.w3c.dom.*;
import java.io.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;

/**
* This class is responsable to describe a object in XML.
* @author João Ricardo Bittencourt
*/
public abstract class XMLObject{

  /**
  * Abstract method creates a new Document. For it, this uses
  * an object. It's very important that each domain object must
  * implement a XMLObject.
  * @param obj is Object will be used to create a Document
  * @return a XML Document (node structure). It is used to write a file  
  */                   
  public abstract Document createDoc(Object obj) throws IOException;    
  
  /**
  * Abstract method creates a new Object. For it, this uses
  * an XML root element.   
  * @param element is XML root element
  * @return an Object
  */  
  public abstract Object newInstance(Element node) throws IOException;  
  
  
  /**
  * Create a XML root element.
  * @param obj is a Document
  * @return root element
  */  
  public Element createNode(Object obj) throws IOException{  
    Document d = createDoc(obj);
    return d.getDocumentElement();
  }
  
  /**
  * Create a new object.
  * @param doc is a Document
  * @return an Object
  */    
  public Object newInstance(Document doc) throws IOException{
     return newInstance(doc.getDocumentElement());    
  }  
}