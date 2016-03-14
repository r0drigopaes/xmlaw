
package mages.controller.ctf;

import mages.agents.*;
import java.io.*;

//se não especificar usa a direção e a quantidade default
public class GetFlagAct extends Action implements Serializable{
 
  public static final int NEAR = 0;      
  public static final int MID = 1;      
  public static final int FAR = 2;      
    
  int what = NEAR;
  
  public GetFlagAct(){
    setName("Get Flag");
    setNeedParameters(false);
  }
  
//  public void setWhat(int w){
  //  what = w;
  //}  
  
//  public int getWhat(){
  //  return what;
 // }    
  
  public void executeAction() throws ExecActionException {
    //nothing
  }
  
  public String getInstructionParameters(){    
   // return new String("<what=N/M/F>");
   return null;   
  }  
  
  public String getString(){
    return "GET_FLAG";
  }      
  
  public boolean defineParameters(String params[]){
    return true;
  }    
  
  public String[] getParameters(){
/*    String[] tmp = new String[1];
    
    tmp[0] = Integer.toString(what);      
    return tmp;    */
    return null;
  }  
  
  public Object clone(){
//    GetFlagAct tmp = new GetFlagAct();
  //  tmp.setWhat(this.getWhat());
    //return tmp;
    return new GetFlagAct();
  }
  
  public int getNumParameters(){
    return 0;
  }  
}