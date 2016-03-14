
package mages.app;

import mages.app.*;
import mages.gui.*;
import mages.controller.*;

import java.io.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;

public class DomainXML extends XMLObject{
  
//  String[] tags = {"domain.name","domain.setup","domain.main"};
  String[] tags = {"domain.name","domain.setup"};
                 
  public Document createDoc(Object obj) throws IOException{
    Domain d = (Domain)obj;

    DocumentImpl document = new DocumentImpl();  
    Element root = (Element) document.createElement("domain");            
    
    Element[] elements = new Element[tags.length];
    for(int i = 0; i<elements.length; i++){
      elements[i] = (Element) document.createElement(tags[i]);            
      switch(i){
        case 0: elements[i].appendChild(document.createTextNode(d.getName()));
        break;      
        case 1: elements[i].appendChild(document.createTextNode(d.getSetupClassName()));
        break;
//        case 2: elements[i].appendChild(document.createTextNode(d.getAgentWindowsFactoryName()));
  //      break;        
      }
    }
                                 
    document.appendChild(root);      
    for(int j=0; j<elements.length;j++){
      root.appendChild(elements[j]);      
    }      
    return document;                                   
  }

  public Object newInstance(Element root) throws IOException{   
    NodeList list = root.getChildNodes();    
    String nameDomain = null;    
    String setupClass = null;
//    String factory = null;
    String textStr = null;
    for(int i=0; i<tags.length; i++){  
      NodeList elemList = root.getElementsByTagName(tags[i]);
      Element elem = (Element) elemList.item(0);
      if(elem!=null){            
        NodeList tagsElem = elem.getChildNodes();
        Node text = tagsElem.item(0);
        textStr = text.getNodeValue();
      }
      switch(i){
         case 0: nameDomain = textStr;         
         break;      
         case 1: setupClass = textStr;         
         break;   
//         case 2: factory = textStr;         
  //       break;            
      }
    } 
    Domain d = new Domain(nameDomain);
    d.setSetupWindow(setupClass);
//    d.setAgentWindowsFactory(factory);
    return d;
  }         
}