/*
GNU Mages, version 1.00 alpha
Multi-Agents Environment Simulator
Copyright (C) 2001-2002 João Ricardo Bittencourt <jrbitt@uol.com.br>
 
This program is free software; you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version. 

This program is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
GNU General Public License for more details. 

You should have received a copy of the GNU General Public License 
along with this program; if not, write to the Free Software 
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package mages.agents;

public abstract class Action{
  String name;
  boolean needParams = false;
      
  public abstract void executeAction() throws ExecActionException;
  public abstract String getInstructionParameters();  
  public abstract String getString();
  public abstract boolean defineParameters(String params[]);
  public abstract String[] getParameters();
  public abstract int getNumParameters();  
  public abstract Object clone();
  
  public void setName(String n){
    name = n;
  }
  
  public String getName(){
    return name;
  }
  
  public void setNeedParameters(boolean f){
    needParams = f;
  }
  
  public boolean getNeedParameters(){
    return needParams;
  }  
  
  public String toString(){
    String str = new String(getString()+" ");
    if(getNeedParameters()){
      String tmp2[] = getParameters();
      for(int k=0; k<tmp2.length;k++){
        str = str.concat(tmp2[k]+" ");
      }
    }  
    return str.trim();
  }
}