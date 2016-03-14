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

import java.io.*;

public class Tournament implements Serializable{
  String name;
  String description;
  int maxBotsForTeam;
  int maxBotsPtos;
  int maxFlag;
  int maxTeams;

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

  public void setMaxBotsForTeam(int m){
    maxBotsForTeam = m;
  }

  public int getMaxBotsForTeam(){
    return maxBotsForTeam;
  }

  public void setMaxBotsPtos(int m){
    maxBotsPtos = m;
  }

  public int getMaxBotsPtos(){
    return maxBotsPtos;
  }

  public void setMaxFlag(int m){
    maxFlag = m;
  }

  public int getMaxFlag(){
    return maxFlag;
  }

  public void setMaxTeams(int m){
    maxTeams = m;
  }

  public int getMaxTeams(){
    return maxTeams;
  }
  
  public String toString(){
    String tmp = new String();
    tmp = "Name= "+name+"; Description= "+description+"; MaxBotsTeams= "+maxBotsForTeam+
          "; MaxBotsPtos= "+maxBotsPtos+"; MaxFlags= "+maxFlag+"; MaxTeams= "+maxTeams+";";
    return tmp;
  }
}