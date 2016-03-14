
package mages.controller.ctf;

import mages.agents.*;
import java.io.*;

public class DropFlagAct extends Action implements Serializable{

  public DropFlagAct(){
    setName("Drop Flag");
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
    return "DROP_FLAG";
  }      
  
  public boolean defineParameters(String params[]){
    return true;
  }    

  public String[] getParameters(){
    return null;
  }    
  
  public Object clone(){
    return new DropFlagAct();
  }

  public int getNumParameters(){
    return 0;
  }  
}