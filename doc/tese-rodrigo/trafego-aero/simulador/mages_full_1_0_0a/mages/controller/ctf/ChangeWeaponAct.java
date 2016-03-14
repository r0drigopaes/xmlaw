
package mages.controller.ctf;

import mages.agents.*;
import java.io.*;

//se não especificar usa a direção e a quantidade default
public class ChangeWeaponAct extends Action implements Serializable{

  public static final int WEAPON_1 = 1;      
  public static final int WEAPON_2 = 2;  
    
  int weapon = WEAPON_1;
    
  public ChangeWeaponAct(){
    setName("Change Weapon");
    setNeedParameters(true);
  }
  
  public void setWeapon(int w){
    weapon = w;
  }
  
  public int getWeapon(){
    return weapon;
  }  
    
  public void executeAction() throws ExecActionException {
    //nothing
  }
  
  public String getInstructionParameters(){    
    return new String("<weapon id = 1/2>");
  }  
  
  public String getString(){
    return "CHANGE_WPN";
  }      
  
  public boolean defineParameters(String params[]){
    boolean isOk = true;
    if(params.length!=1){
      return false;
    }
    if(params[0].equals("1")){
      weapon = WEAPON_1;
    }
    else{
      if(params[0].equals("2")){
        weapon = WEAPON_2;
      }
      else{
        isOk = false;
      }
    }    
    return isOk;
  }    
  
  public String[] getParameters(){
    String[] tmp = new String[1];    
    tmp[0] = Integer.toString(weapon);      
    return tmp;    
  }  
  
  public Object clone(){
    ChangeWeaponAct tmp = new ChangeWeaponAct();
    tmp.setWeapon(this.getWeapon());
    return tmp;
  }
  
  public int getNumParameters(){
    return 1;
  }    
}