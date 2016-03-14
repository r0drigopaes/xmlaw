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

package mages.controller;

import clisp.*;
import java.net.*;

public abstract class MagesDaemon extends Daemon{
  int ttt = 0; //Time to try
  long timeComm;

  public MagesDaemon(int p, int ms, int mc) throws ConfigurationException{
    super(p,ms,mc);  
  }
   
  public MagesDaemon(Daemon d, int p, int ms, int mc) throws ConfigurationException{
    super(d,p,ms,mc);
  }
  
  public void setTimesToTry(int t){
    ttt = t;
  }  

  public int getTimesToTry(){
    return ttt;
  }            

  public void setTimeComm(long t){
    timeComm = t;
  }  

  public long getTimeComm(){
    return timeComm;
  }            
          
  public abstract Session managerSession(User u);
  
  public abstract User initUser(Socket s);
    
}