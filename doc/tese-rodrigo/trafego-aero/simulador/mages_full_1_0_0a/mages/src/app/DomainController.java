
package mages.app;

import java.io.*;

public class DomainController implements IController{
        
  public void init(){}
    
  public void save(Object obj, String name) throws IOException{   
    XMLController.save(obj, new DomainXML(),name, "../../dtd/domain.dtd");
  }
  
  public Object load(String name) throws IOException{
    return XMLController.load(new DomainXML(),name);
  }    
}