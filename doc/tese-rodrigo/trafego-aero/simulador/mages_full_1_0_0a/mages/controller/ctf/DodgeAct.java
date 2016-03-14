
package mages.controller.ctf;

import mages.agents.*;
import java.io.*;

public class DodgeAct extends Action implements Serializable{

  public DodgeAct(){
    setName("Dodge");
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
    return "DODGE";
  }      
  
  public boolean defineParameters(String params[]){
    return true;
  }    

  public String[] getParameters(){
    return null;
  }    
  
  public Object clone(){    
    return new DodgeAct();
  }
  
  public int getNumParameters(){
    return 0;
  }  
}