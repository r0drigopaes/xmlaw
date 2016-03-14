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

package mages.comm;

import clisp.*;
import mages.simulation.*;

public class AgentMessage extends Message implements Comparable{
  
  public static final int NO_GROUP = Integer.MIN_VALUE;
  
  public static final int MULTICAST = 0;
  public static final int UNICAST = 1;  
  public static final int BROADCAST = 2;  
  
  public static final int SERVER = 0;    
  public static final int CLIENT = 1;    
  
  public static final int QUESTION = 0;    
  public static final int ANSWER = 1;    
      
  int sender;
  int receiver[];
  int idGroup;
  int typeMsg;  
  int idMessage;
  Object content;
  long timestamp;
  int fntMsg;  

  public AgentMessage(int s, int r[], int gr, int type, int idM, Object c, long ts){  
    super();
    sender = s;
    receiver = r;
    idGroup = gr;
    typeMsg = type;
    idMessage = idM;
    content = c;
    timestamp = ts;
  }
  
  public AgentMessage(int s, int r, int gr, int type, int idM, Object c, long ts){
    super();
    sender = s;
    receiver = new int[1];
    receiver[0] = r;
    idGroup = gr;
    typeMsg = type;
    idMessage = idM;
    content = c;
    timestamp = ts;    
  }  

  public AgentMessage(int idM, Object c){
    super();
    sender = -1;
    receiver = null;
    idGroup = NO_GROUP;
    typeMsg = MULTICAST;
    idMessage = idM;
    content = c;
    timestamp = -1;
    fntMsg = QUESTION;
  }
      
  public void setIdSender(int s){
    sender = s;
  }

  public int getIdSender(){
    return sender;
  }

  public void setIdReceiver(int r[]){
    receiver = r;
  }

  public int[] getIdReceiver(){
    return receiver;
  }

  public void setIdGroup(int i){
    idGroup = i;
  }

  public int getIdGroup(){
    return idGroup;
  }

  public void setTypeMsg(int t){
    typeMsg = t;
  }

  public int getTypeMsg(){
    return typeMsg;
  }

  public void setIdMessage(int i){
    idMessage = i;
  }

  public int getIdMessage(){
    return idMessage;
  }

  public void setContent(Object c){
    content = c;
  }

  public Object getContent(){
    return content;
  }  
  
  public void setTimeStamp(long ts){
    timestamp = ts;
  }
  
  public long getTimeStamp(){
    return timestamp;
  }  
  
  public void setFunctionMessage(int fm){
    fntMsg = fm;
  }
  
  public int getFunctionMessage(){
    return fntMsg;
  }    
    
  public String toString(){
    String tmp = new String();
    tmp = tmp.concat("[ID Sender: "+sender+";");
    tmp = tmp.concat("ID Receivers: ");
    if(receiver!=null){
      for(int i=0; i<receiver.length; i++){
        tmp = tmp.concat(receiver[i]+" ");    
      }
    }
    else{
      tmp = tmp.concat("No");        
    }  
    tmp = tmp.concat(";");    
    tmp = tmp.concat("ID Group: "+((idGroup==NO_GROUP)?"NO_GROUP":Integer.toString(idGroup))+";");    
    tmp = tmp.concat("Type Message: "+((typeMsg==MULTICAST)?"MULTICAST":(typeMsg==BROADCAST)?"BROADCAST":(typeMsg==UNICAST)?"UNICAST":"UNKNOWN")+";");
    tmp = tmp.concat("Function: "+((fntMsg==QUESTION)?"QUESTION":(fntMsg==ANSWER)?"ANSWER":"UNKNOWN")+";");        
    tmp = tmp.concat("ID Message: "+idMessage+";");    
    tmp = tmp.concat("Timestamp: "+timestamp+";");     
    tmp = tmp.concat("Content: "+content+"\n");               
    return tmp;
  }  
  
  public int compareTo(Object o){
    AgentMessage am = (AgentMessage)o;
    if(getIdGroup()==am.getIdGroup()){
      if(getIdSender()==am.getIdSender()){
        if(getIdMessage()==AgentProtocol.QUERY_ATTRIBUTE){      
          Parameter me = (Parameter)getContent();
          Parameter it = (Parameter)am.getContent();
          return me.getName().compareTo(it.getName());
        }
        else{
          return 0;
        }      
      }
      else{
        return (getIdSender()<am.getIdSender())?-1:1;
      }
    }
    else{
      return (getIdGroup()<am.getIdGroup())?-1:1;
    }
  } 
}