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

import mages.gui.*;
import mages.*;
import java.lang.reflect.*;

public class Domain{
  String setupW = null;
  String factory = null;
  String name = null;
  SetupWindow swin = null;
  
  public Domain(String n){
    name = n;
  }
   
  public void run(MagesClient mc, int m) throws ClassNotFoundException,NoSuchMethodException,
  IllegalAccessException,InstantiationException,InvocationTargetException{
    createSetupWindow(mc,m).exec();
  } 
  
  public void setSetupWindow(String sew){
    setupW = sew;
  }

  public SetupWindow getSetupWindow(){
    return swin;
  }
      
  private SetupWindow createSetupWindow(MagesClient mc, int m) throws ClassNotFoundException,
  NoSuchMethodException,IllegalAccessException,InstantiationException,InvocationTargetException{   
    if(swin==null){
      Class cls = Class.forName(setupW);
      Integer i = new Integer(m);          
      Class[] prm = {mc.getClass(),i.getClass()};   
      Constructor con = cls.getDeclaredConstructor(prm);
      Object[] prm2 = {mc, i};
      swin = (SetupWindow)con.newInstance(prm2);
    }  
    return swin;          
  }  
  
  public AgentWindowsFactory createAgentWindowsFactory() throws ClassNotFoundException, InstantiationException,
   IllegalAccessException{
    Class cls = Class.forName(factory);
    AgentWindowsFactory awf = (AgentWindowsFactory)cls.newInstance();
    return awf;
  }
  
  public void setName(String n){
    name = n;
  }
  
  public String getName(){
    return name;
  }    
  
  public String getSetupClassName(){
    return setupW;
  }
  
  public void setAgentWindowsFactory(String awf){
    factory = awf;
  }
  
  public String getAgentWindowsFactoryName(){
   return factory;
  }
}