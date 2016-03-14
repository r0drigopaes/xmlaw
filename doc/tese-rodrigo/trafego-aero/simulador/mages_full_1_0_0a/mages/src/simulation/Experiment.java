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

import java.util.*;

public class Experiment{
  String domainName;
  Vector conditions;  
  Vector replications;
  
  public Experiment(String n){
    domainName = n;
    conditions = new Vector();
    replications = new Vector();
  }
  
  public void setDomainName(String n){
    domainName = n;
  }

  public String getDomainName(){
    return domainName;
  }
    
  public void addCondition(Parameter c){
    conditions.addElement(c);
  }
  
  public Parameter[] getConditions(){
    Parameter tmp[] = new Parameter[conditions.size()];
    for(int i=0; i<tmp.length; i++){
      tmp[i] = (Parameter)conditions.elementAt(i);
    }
    return tmp;
  }    
  
  public void addRun(Run r){
    replications.addElement(r);
  }

  public int getNunReplications(){
    return replications.size();
  }
  
  public Run[] getReplications(){
    Run tmp[] = new Run[replications.size()];
    for(int i=0; i<tmp.length; i++){
      tmp[i] = (Run)replications.elementAt(i);
    }
    return tmp;
  }      
}