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

public class Run{
  Vector measPerf;
  long id;
  long seed = -1;
  
  public Run(long i){
    measPerf = new Vector();
    id = i;
  }
  
  public Run(long i, long s){
    measPerf = new Vector();
    id = i;
    seed = s;
  }
    
  public void setId(long i){
    id = i;
  }

  public long getId(){
    return id;
  }
   
  public void setSeed(long s){
    seed = s;
  }

  public long getSeed(){
    return seed;
  }

  public int getNumMeasuresOfPerformance(){
    return measPerf.size();
  }
            
  public void addMeasureOfPerformance(Parameter mp){
    measPerf.addElement(mp);
  }
  
  public Parameter[] getMeasuresOfPerformance(){
    Parameter tmp[] = new Parameter[measPerf.size()];
    for(int i=0; i<tmp.length; i++){
      tmp[i] = (Parameter)measPerf.elementAt(i);
    }
    return tmp;
  }      
}