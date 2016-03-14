
package mages.controller.ctf;

import mages.agents.*;

import java.io.*;

//se não especificar usa a direção e a quantidade default
public class AttackAct extends Action implements Serializable{
  public static final int NEAR = 0;
  public static final int MID_TARGET = 1;  
  public static final int FAR = 2;  
  public static final int VIEW_FIELD = 3;  
    
  public static final int SIMPLE_SHOT = 0;  
  public static final int AUTOFIRE = 1;    
  public static final int HAND_TO_HAND = 2;      
  
  
  public static final int MIN = 0;    
  public static final int MID = 1;    
  public static final int MAX = 2;    
      
  int target = NEAR;
  int mode = SIMPLE_SHOT;
  int shots = MAX;
  
  public AttackAct(){
    setName("Attack");
    setNeedParameters(true);
  }
  
  public void setTarget(int t){
    target = t;
  }
  
  public void setMode(int m){
    mode = m;
  }  
    
  public void setShots(int s){
    shots = s;
  }
   
  public int getTarget(){
    return target;
  }
  
  public int getMode(){
    return mode;
  }  
    
  public int getShots(){
    return shots;
  }
        
  public void executeAction() throws ExecActionException {
    //nothing
  }
  
  public String getInstructionParameters(){    
    return new String("<target=N/M/F/V> <mode=S/A/HTH> <shots=MIN/MID/MAX>");
  }  
  
  public String getString(){
    return "ATTACK";
  }      
  
  public boolean defineParameters(String params[]){
    boolean isOk = true;
    if(params.length!=3){
      return false;
    }
    
    //target
    if(params[0].equals("N")){
      target = NEAR;
    }
    else{
      if(params[0].equals("M")){
        target = MID_TARGET;
      }
      else{
        if(params[0].equals("F")){
          target = FAR;
        }
        else{
          if(params[0].equals("V")){
            target = VIEW_FIELD;
          }
          else{
            isOk = false;
          }
        }      
      }    
    }
    
    //mode
    if(params[1].equals("S")){    
      mode = SIMPLE_SHOT;
    }
    else{
      if(params[1].equals("A")){
        mode = AUTOFIRE;
      }
      else{
        if(params[1].equals("HTH")){
          mode = HAND_TO_HAND;
        }  
        else{
          isOk = false;
        }    
      }
    }
    
    //shots
    if(params[2].equals("MIN")){    
      shots = MIN;
    }
    else{
      if(params[2].equals("MID")){    
        shots = MID;
      }
      else{
        if(params[2].equals("MAX")){    
         shots = MAX;
        }
        else{
          isOk = false;        
        }
      }    
    }    
    return isOk;
  }    
  
  public String[] getParameters(){
    String[] tmp = new String[3];
    
    tmp[0] = Integer.toString(target);      
    tmp[1] = Integer.toString(mode);                   
    tmp[2] = Integer.toString(shots);
    return tmp;    
  }  
  
  public Object clone(){
    AttackAct tmp = new AttackAct();
    tmp.setTarget(this.getTarget());
    tmp.setMode(this.getMode());    
    tmp.setShots(this.getShots());    
    return tmp;
  }  
  
  public int getNumParameters(){
    return 3;
  }  
}