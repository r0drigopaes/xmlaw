
package mages.controller.ctf;
import clisp.*;

/*
  Possibilidade de generalizar no futuro
*/
public class CtFProtocol implements Protocol{
     
  public final static int C_CAN_CREATE_SIM = 1;    
  public final static int S_CAN_CREATE_SIM = 2;  
  public final static int C_CREATE_SIM = 3;    
  public final static int S_CREATE_SIM = 4;   
  public final static int C_CAN_JOIN_SIM = 5;    
  public final static int S_CAN_JOIN_SIM = 6;  
  public final static int C_JOIN_SIM = 7;    
  public final static int S_JOIN_SIM = 8;  
  public final static int C_BOT_QUIT = 9;  
  public final static int S_QUIT = 10;  
  public final static int C_SEND_BOT = 11; 
  public final static int S_CONNECT_TEAM = 12;  
  public final static int C_SEND_SENSORS = 13;
  public final static int S_PERCEPTION = 14;
//  public final static int C_SEND_MODE = 15;  
  public final static int S_CONNECTION = 16;    
  public final static int C_SEND_ACTION = 17;    
  public final static int S_START_SIM = 18;
  public final static int C_USER_QUIT = 19;   
  public final static int S_TURN = 20;  
  public final static int C_RECONNECT = 21;    
  public final static int S_SESSION_ID = 22;      
  public final static int C_SEND_COMM = 23;  
  public final static int S_RECOVERY = 24;    
  public final static int S_RECONNECT_TEAM = 26;  
  public final static int S_SEND_BOT = 28;    
  public final static int S_SCORE = 30;  
  public final static int S_SCORE_BATCH = 32;    
  public final static int S_COMM = 34;      
      
  // Operations
  public boolean validateMessage(Message m){
    if(m!=null){
      switch(m.getId()){
        case C_CAN_CREATE_SIM:
        case S_CAN_CREATE_SIM:
        case C_CREATE_SIM:        
        case S_CREATE_SIM:
        case C_CAN_JOIN_SIM:
        case S_CAN_JOIN_SIM:
        case C_JOIN_SIM:        
        case S_JOIN_SIM:
        case S_QUIT:
        case C_SEND_BOT:
        case S_CONNECT_TEAM: 
        case C_BOT_QUIT: 
        case C_SEND_SENSORS: 
        case S_PERCEPTION: 
//        case C_SEND_MODE: 
        case C_SEND_ACTION: 
        case S_CONNECTION: 
        case S_START_SIM: 
        case C_USER_QUIT:
        case S_TURN: 
        case C_RECONNECT:
        case S_SESSION_ID: 
        case C_SEND_COMM:
        case S_RECOVERY: 
        case S_RECONNECT_TEAM: 
        case S_SEND_BOT: 
        case S_SCORE: 
        case S_SCORE_BATCH: 
        case S_COMM: return true;
      }
    }
    return false;
  }
  
  public String getName(){
    return "CTFP - (Capture The Flag Protocol)";    
  }
  
  public String getVersion(){
    return "1.0a";
  }
}
