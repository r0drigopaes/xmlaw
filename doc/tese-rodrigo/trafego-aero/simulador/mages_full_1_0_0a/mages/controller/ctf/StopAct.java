
package mages.controller.ctf;

import mages.agents.*;
import java.io.*;

public class StopAct extends Action implements Serializable{

  public StopAct(){
    setName("Stop");
    setNeedParameters(false);
  }
    
  public void executeAction() throws ExecActionException {
    //nothing
  }
  
  public String getInstructionParameters(){
    //nothing
    return null;
  }  
  
  public String getString(){
    return "STOP";
  }      
  
  public boolean defineParameters(String params[]){
    return true;
  }    
  
  public String[] getParameters(){
    return null;
  }  
  
  public Object clone(){
    return new StopAct();
  }
  
  public int getNumParameters(){
    return 0;
  }  
}