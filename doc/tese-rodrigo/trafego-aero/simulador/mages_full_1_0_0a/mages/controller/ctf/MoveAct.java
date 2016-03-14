
package mages.controller.ctf;

import mages.agents.*;
import mages.domain.ctf.*;
import java.io.*;

//se não especificar usa a direção e a quantidade default
public class MoveAct extends Action implements Serializable{
      
  
  public static final int GO = 0;  
  public static final int SLOW_GO = 1;    
  public static final int RUN = 2;    
  
//  public static final int MIN = 0;      
  //public static final int MID = 1;      
  //public static final int MAX = 2;      
    
  int direction = CtFSettings.NORTH;
  int mode = GO;
 // public int qnt = MAX;
  
  public MoveAct(){
    setName("Move");
    setNeedParameters(true);
  }
  
  public void setDirection(int d){
    direction = d;
  }
  
  public void setMode(int m){
    mode = m;
  }  

  public int getDirection(){
    return direction;
  }
  
  public int getMode(){
    return mode;
  }
      
  public void executeAction() throws ExecActionException {
    //nothing
  }
  
  public String getInstructionParameters(){    
//    return new String("<direction=N/S/W/E/NW/NE/SW/SE> <mode=M/R/S> <type= MIN/MID/MAX>");
    return new String("<direction=N/S/W/E/NW/NE/SW/SE> <mode=S/M/R>");
  }  
  
  public String getString(){
    return "MOVE";
  }      
    
  public boolean defineParameters(String params[]){
    boolean isOk = true;
    if(params.length!=2){
      return false;
    }    
    //direction
    if(params[0].equals("N")){
      direction = CtFSettings.NORTH;
    }
    else{
      if(params[0].equals("S")){
        direction = CtFSettings.SOUTH;  
      }
      else{
        if(params[0].equals("W")){
          direction = CtFSettings.WEST;
        }
        else{
          if(params[0].equals("E")){
            direction = CtFSettings.EAST;
          }
          else{
            if(params[0].equals("NW")){
              direction = CtFSettings.NORTHWEST;
            }
            else{
              if(params[0].equals("SW")){              
                direction = CtFSettings.SOUTHWEST;
//                System.out.println("&& "+params[0]+" "+direction);
              }
              else{
                if(params[0].equals("NE")){
                  direction = CtFSettings.NORTHEAST;
                }
                else{
                  if(params[0].equals("SE")){
                    direction = CtFSettings.SOUTHEAST;
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
        
    //mode
    if(params[1].equals("M")){    
      mode = GO;
    }
    else{
      if(params[1].equals("R")){    
        mode = RUN;
      }
      else{
        if(params[1].equals("S")){    
          mode = SLOW_GO;
        }   
        else{
          isOk = false;
        }
      }    
    }
    return isOk;
  }    
  
  public String[] getParameters(){
//    String[] tmp = new String[3];
    String[] tmp = new String[2];    
 
    tmp[0] = Integer.toString(direction);      
    tmp[1] = Integer.toString(mode);                   
//    tmp[2] = Integer.toString(qnt);
    return tmp;    
  }  
  
  public Object clone(){
    MoveAct tmp = new MoveAct();
    tmp.setDirection(this.getDirection());
    tmp.setMode(this.getMode());  
    return tmp;
  }    

  public int getNumParameters(){
    return 2;
  }  
}

/*
  public static final int NORTH = 90;
  public static final int SOUTH = 270;
  public static final int WEST = 180;    
  public static final int EAST = 0;  
  public static final int NORTHWEST = 135;    
  public static final int NORTHEAST = 45;      
  public static final int SOUTHWEST = 235;    
  public static final int SOUTHEAST = 315;     


     NW	N	NE
      W     E
     Sw  S  SE
     
     235 270 315 
     180      0
     135  90  45
*/       