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

package mages.domain.ctf;

import java.util.*;
import java.io.*;

public class Team implements Serializable{
    
  String name;
  String description;
  Vector bots;  
  int color;

  public Team(){
    bots = new Vector();
  }
  
  public void setName(String n){
    name = n;
  }

  public String getName(){
    return name;
  }  
  
  public void setDescription(String d){
    description = d;
  }

  public String getDescription(){
    return description;
  }        
  
  public void addBot(Bot b){
    bots.addElement(b);
  }
  
  public Bot getBot(int id){
    return (Bot)bots.elementAt(id);
  }
  
  public void removeBot(int id){
    bots.removeElementAt(id);
  }
  
  public int getNumBots(){
    return bots.size();
  }
  
  public Vector getBots(){
    return bots;
  }
  
  public void setBots(Vector b){
    bots = b;
  }
       
  public static boolean validate(Team team, Tournament tourn){
    if(team.getNumBots()<=tourn.getMaxBotsForTeam()){
      boolean problem = false;
      for(int i=0; i<team.getNumBots(); i++){
        Bot b = team.getBot(i);
        if(!problem){
          problem = (b.getBotPtos()>tourn.getMaxBotsPtos())?true:false;
        }  
      }
      return !problem;
    }
    return false;
  }
  
  public void setColor(int c){
    color = c;  
  }
  
  public int getColor(){
    return color;  
  }  
}
