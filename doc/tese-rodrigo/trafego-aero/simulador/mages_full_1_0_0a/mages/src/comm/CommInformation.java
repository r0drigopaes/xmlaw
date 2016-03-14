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

import mages.agents.*;
import mages.util.*;
import mages.simulation.*;
import java.io.*;
import java.util.*;

public abstract class CommInformation extends NodeObjectArray implements Serializable, Runnable{
  Vector messages = null;
  Thread thread = null;
  long timeWait = 50;
  boolean stop = false;
  FlagObserver obs = null;
  int idSender;
  int idGroup;
  Object result[] = null;
  int cnt = 0;
  protected CommTable table = null;
  protected int ifNullValues[] = null;
     
  public CommInformation(){    
    messages = new Vector();
    obs = new FlagObserver();
    createIfNullValues();
  }

  public CommInformation(long tw){
    this();
    timeWait = tw;
  }
  
  public abstract void createTable();
  protected abstract void createIfNullValues();  
        
  public void addObserver(Observer o){
    obs.addObserver(o);
  }
  
  public void setTimeWait(long tw){
    timeWait = tw;
  }
  
  public void start(){
    if(thread == null){
      thread = new Thread(this);
      stop = false;
      thread.start();
      thread = null;
    }
  }
 
  public synchronized Object[] value(long timestamp){
    return result;
  }

  public synchronized void setValue(Object[] v){
    if(v!=null){
      result = new Object[v.length];  
      for(int i=0; i<result.length; i++){
        result[i] = v[i];
      }
    }  
  }
  
  public synchronized void addAnswer(AgentMessage am){
    if(result==null){
      result = new Object[table.getLength()];      
    }
    int index = table.getPos(am);
    if(index!=-1){
      if(am.getIdMessage()==AgentProtocol.QUERY_ATTRIBUTE){          
        result[index] = ((Parameter)am.getContent()).getValue();
      }
      else{      
        result[index] = am.getContent();
      }
    }  
  }
    
  public void sendMessages(Agency ag, long ts){
    for(int i = 0; i<messages.size(); i++){
      AgentMessage am = (AgentMessage)messages.elementAt(i);    
      am.setIdSender(idSender);
      am.setIdGroup(idGroup);      
      am.setTimeStamp(ts);
      ag.send(am);
    }    
    wakeUp();
  }
  
  private synchronized void wakeUp(){
    notify();
  }
  
  private synchronized void waitComm(){
    try{
      wait(timeWait);
    }
    catch(InterruptedException e){}        
  }
    
  private synchronized void waitStart(){
    try{
      wait();
    }
    catch(InterruptedException e){}  
  }
  
  public void run(){
    while(!stop){
      waitStart();      
      waitComm();            
      obs.change();
      obs.notifyObservers();
    }
  }
  
  public void stop(){
    stop = true;
  }
  
  public void addMessage(AgentMessage am){
    messages.addElement(am);
  }
    
  public void setIdSender(int is){
    idSender = is;
  }
  
  public void setIdGroup(int ig){
    idGroup = ig;
  }  
  
  public int getIdSender(){
    return idSender;
  }
  
  public int getIdGroup(){
    return idGroup;
  }
      
  public String[] getString(){
    Vector tmp = new Vector();

    for(int i=0; i<result.length; i++){
      if(result[i] instanceof String){
         tmp.addElement(result[i]);
      }
      else{        
        if(result[i] instanceof Action){
	       Action a = (Action)result[i];
	       tmp.addElement(a.toString());             
	     }
	     else{
	       if(result[i]==null){	        
	         int ind = ifNullValues[i];
	         for(int k=0; k<ind; k++){
   	        tmp.addElement("-");	         
	         } 
	       }
	       else{
	         if(result[i] instanceof Object[]){	         
	           Object o[]  = (Object[])result[i];
	           for(int m=0; m<o.length; m++){
	             tmp.addElement(Conversion.toString(o[m]));
	           }
	         }    
	       }
	     }
      }
    }  
    
    String temp[] = new String[tmp.size()];          
    for(int g=0; g<temp.length; g++){
      temp[g] = (String)tmp.elementAt(g);
    }
    return temp;
  }  
    
  private class FlagObserver extends Observable implements Serializable{   
    public FlagObserver(){
      super();
    }
    
    public void change(){
      setChanged();
    }
  }    
}