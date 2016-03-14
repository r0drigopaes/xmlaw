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

package mages.app.conversor;

import java.util.*;
import java.io.*;

public class Transformation{
  Vector ops = null;
  Vector methods = null;
  
  public Transformation(){
    ops = new Vector();
    methods = new Vector();    
  }
  
  public void setSource(Source t){
    for(int i=0; i<methods.size(); i++){    
      ((Method)methods.elementAt(i)).setSource(t);
    }  
  }
  
  public void addMethod(Method m){
    methods.addElement(m);
  }

  public int getNumMethods(){
    return methods.size();
  }
  
  public Method[] getMethods(){
    Method tmp[] = new Method[methods.size()];
    for(int i=0; i<tmp.length; i++){
      tmp[i] = (Method)ops.elementAt(i);
    }
    return tmp;
  }
    
  public void addOperation(Operation o){
    ops.addElement(o);
  }
  
  public int getNumOperations(){
    return ops.size();
  }  
  
  public Operation[] getOperations(){
    Operation tmp[] = new Operation[ops.size()];
    for(int i=0; i<tmp.length; i++){
      tmp[i] = (Operation)ops.elementAt(i);
    }
    return tmp;
  }
  
  private int getNumCols(){
    Operation ops[]  = getOperations();
    int sum = 0;
    for(int i=0; i<ops.length; i++){
      sum += ops[i].getNumCols();
    }           
    return sum; 
  }
  
  public Source conversion(String[][] source){
    Operation ops[]  = getOperations();
    String target[][] = new String[source.length][getNumCols()];

    int c = 0;
    for(int i=0; i<ops.length; i++){
      int cols[] = ops[i].getCols();
      for(int j=0; j<cols.length; j++){
        ops[i].exec(source,cols[j]);
        format(target,ops[i],c++);
      }
    }
    Source tmp = new Source();
    tmp.setSource(target);
    return tmp;
  }
  
  private void format(String[][] trg, Operation op, int col){
    String values[] = op.getValues();
    if(values!=null){
      for(int i=0; i<values.length; i++){
        trg[i][col] = values[i];
      }
    }
  }
  
  public void save(String path[]) throws IOException{
    for(int i=0; i<methods.size(); i++){    
      ((Method)methods.elementAt(i)).save(path[i]);
    }
  }  
}