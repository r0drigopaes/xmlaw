
package mages.controller.ctf;

import mages.agents.*;
import java.io.*;

public class RecoveryAct extends Action implements Serializable{

  public RecoveryAct(){
    setName("Recovery");
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
    return "REC";
  }    
  
  public boolean defineParameters(String params[]){
    return true;
  } 
  
  public String[] getParameters(){
    return null;
  }       
  
  public Object clone(){
    return new RecoveryAct();
  }
  
  public int getNumParameters(){
    return 0;
  }  
}