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

import java.util.*;

public class Agency extends Thread{
  LinkedList queue;
  Hashtable addresses;
  int idCur = 0;
  int k = 10;
  long curTimeStamp = -1;
  boolean stop = false;
  
  public Agency(){
    queue = new LinkedList();
    addresses = new Hashtable();
    start();
  }
  
  public Agency(int kk){
    queue = new LinkedList();
    addresses = new Hashtable();    
    k = kk;
    start();
  }
    
  public synchronized void send(AgentMessage msg){
    queue.add(msg);
    notify();
  }
  
  private synchronized boolean isAvaliable(){
    return (queue.size()>0)?true:false;
  }
   
  public synchronized void run(){
    while(!stop){
      while(isAvaliable()){
        AgentMessage am = (AgentMessage)queue.removeFirst();
        if(am.getTimeStamp()==getTimeStamp()){
          dispatchMessage(am);
        }  
      }
    
      try{
        wait();
      }
      catch(InterruptedException e){
      }    
    }
  }
    
  public synchronized void reset(){
    queue = new LinkedList();  
  }
    
  private void dispatchMessage(AgentMessage msg){
    switch(msg.getTypeMsg()){
      case AgentMessage.MULTICAST:
        multicast(msg);    
      break;
      
      case AgentMessage.BROADCAST:
        broadcast(msg);          
      break;      
      
      case AgentMessage.UNICAST:
        unicast(msg);          
      break;      
    }
  }
  
  public void multicast(AgentMessage msg){
    Enumeration all = addresses.elements();
    while(all.hasMoreElements()){
      Object obj = all.nextElement();
      if(obj!=null){
        Receiver r = (Receiver)obj;
        if((r.getIdGroup()==msg.getIdGroup()) && (msg.getIdSender()!=r.getIdReceiver())){
          r.dispatchMessage(msg);
        }
      }
    }
  }

  public void broadcast(AgentMessage msg){
    Enumeration all = addresses.elements();
    while(all.hasMoreElements()){
      Object obj = all.nextElement();
      if(obj!=null){
        Receiver r = (Receiver)obj;
        if(msg.getIdSender()!=r.getIdReceiver()){
          r.dispatchMessage(msg);
        }
      }
    }  
  }

  public void unicast(AgentMessage msg){
    int addr[] = msg.getIdReceiver();
    if(addr!=null){
      for(int i=0; i<addr.length; i++){
        Integer key = new Integer(addr[i]+(msg.getIdGroup()*k));
        Receiver r = (Receiver)addresses.get(key);
        if(r!=null){      
          r.dispatchMessage(msg);
        }  
      }
    }  
  }

  public void registry(Receiver rec){
    Integer key = new Integer(rec.getIdReceiver()+(rec.getIdGroup()*k));
    addresses.put(key,rec);
  }
        
  public void registry(Receiver col[]){
    for(int i=0; i<col.length; i++){
      registry(col[i]);   
    }
  }
  
  public synchronized void setTimeStamp(long ts){
    curTimeStamp = ts;
  }
  
  public synchronized long getTimeStamp(){
    return curTimeStamp;
  }  
}