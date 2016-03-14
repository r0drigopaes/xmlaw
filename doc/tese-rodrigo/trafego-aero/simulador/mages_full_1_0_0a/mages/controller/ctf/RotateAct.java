
package mages.controller.ctf;

import mages.agents.*;
import mages.domain.ctf.*;
import java.io.*;

//se não especificar usa a direção e a quantidade default
public class RotateAct extends Action implements Serializable{
        
  int degree = CtFSettings.NORTH;
  
  public RotateAct(){
    setName("Rotate");
    setNeedParameters(true);
  }
  
  public void setDegree(int d){
    degree = d;
  }

  public int getDegree(){
    return degree;
  }
        
  public void executeAction() throws ExecActionException {
    //nothing
  }
  
  public String getInstructionParameters(){    
    return new String("<direction=N/S/W/E/NW/NE/SW/SE>");
  }  
  
  public String getString(){
    return "ROT";
  }      
  
  public boolean defineParameters(String params[]){
    boolean isOk = true;  
    if(params.length!=1){
      return false;
    }    
    if(params[0].equals("N")){
      degree = CtFSettings.NORTH;
    }
    else{
      if(params[0].equals("S")){
        degree = CtFSettings.SOUTH;
      }
      else{
        if(params[0].equals("W")){
          degree = CtFSettings.WEST;
        }
        else{
          if(params[0].equals("E")){
            degree = CtFSettings.EAST;
          }
          else{
            if(params[0].equals("NW")){
              degree = CtFSettings.NORTHWEST;
            }
            else{
              if(params[0].equals("SW")){
                degree = CtFSettings.SOUTHWEST;
              }
              else{
                if(params[0].equals("NE")){
                  degree = CtFSettings.NORTHEAST;
                }
                else{
                  if(params[0].equals("SE")){
                    degree = CtFSettings.SOUTHEAST;
                  }
                  else{
                    isOk = false;                  
                  }
                }              
              }            
            }
          }
        }
      }    
    }
    return isOk;
  }    
  
  public String[] getParameters(){
    String[] tmp = new String[1];
    
    tmp[0] = Integer.toString(degree);      
    return tmp;    
  }  
  
  public Object clone(){
    RotateAct tmp = new RotateAct();
    tmp.setDegree(this.getDegree());
    return tmp;
  }
  
  public int getNumParameters(){
    return 1;
  }  
}