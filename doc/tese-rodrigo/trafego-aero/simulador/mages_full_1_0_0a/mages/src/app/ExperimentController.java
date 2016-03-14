
package mages.app;

import mages.simulation.*;
import java.io.*;

public class ExperimentController implements IController{
        
  public void init(){}
    
  public void save(Object obj, String name) throws IOException{   
    XMLController.save(obj, new ExperimentXML(),name, "../../../dtd/experiment.dtd");
  }
  
  public Object load(String name) throws IOException{
    return XMLController.load(new ExperimentXML(),name);
  }    
}