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


package mages.simulation;

import mages.agents.*;
import mages.comm.*;
import java.util.*;
import java.io.*;

public class Turn{
  long id;
  Vector sensors;
  Action action;
  Agent agent = null;
  
  public Turn(long i){
    sensors = new Vector();    
    id = i;
  }  

  public void addSensor(Sensor s){
    sensors.addElement(s);
  }    
  
  public void setId(long i){
    id = i;
  }
  
  public void setAction(Action a){
     action = a;
  }

  public void setAgent(Agent a){
     agent = a;
  }
    
  public void setTurn(long i, Vector s, Action a){
    sensors = s;
    setId(i);
    setAction(a);
  }
  
  public long getId(){
    return id;
  }
  
  public Sensor[] getSensors(){
    Sensor[] tmp = new Sensor[sensors.size()];
    for(int i=0; i<sensors.size();i++){
      tmp[i] = (Sensor)sensors.elementAt(i);
    }
    return tmp;
  }
  
  public Action getAction(){
    return action;  
  }
  
  public String getString(){
    //all sensors + stats (optional) + memory (optional)+ communication (optional) + action
    
    //Sensors
    String strId = Long.toString(id);
    String str = new String();
    str = str.concat(strId+" ");
    for(int i=0; i<sensors.size(); i++){
      Sensor s = (Sensor)sensors.elementAt(i);
      String[] tmp = s.getString();
      for(int j=0; j<tmp.length; j++){
        str = str.concat(tmp[j]+" ");
      }  
    }    
    
    //Memory
    for(int i=0; i<sensors.size(); i++){
      if(sensors.elementAt(i) instanceof Memory){
        Memory mem = (Memory)sensors.elementAt(i);
        String[] tmp = mem.getMemoryString();
        for(int j=0; j<tmp.length; j++){
          str = str.concat(tmp[j]+" ");
        }  
      }
    }    
       
    //Stats
    if(agent!=null){
      DynamicStats dstats = agent.getDynamicStats();
      if(dstats!=null){
        String vl[] = dstats.getValues();
        for(int i=0; i<vl.length; i++){
          str = str.concat(vl[i])+" ";          
        }
      }
      
      //If agent uses Communicator 
      if(agent.getAgentController() instanceof Communicator){
        CommInformation ci = ((Communicator)agent.getAgentController()).getCommInformation();
        String[] tmp2 = ci.getString();
        for(int j=0; j<tmp2.length; j++){
          str = str.concat(tmp2[j]+" ");
        }        
      }      
    }
    
    //Action
    str = str.concat(action.toString());
    return str;
  }
  
  public static void saveTurns(String name, String[] turns) throws FileNotFoundException{
    if(turns.length!=0){
      FileOutputStream file = new FileOutputStream(name);
      PrintStream stream = new PrintStream(file);    
      for(int i=0; i<turns.length; i++){
        stream.println(turns[i]);
      }
      stream.close();
    }
  }
}